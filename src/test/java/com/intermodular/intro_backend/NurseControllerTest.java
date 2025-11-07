package com.intermodular.intro_backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.intermodular.intro_backend.repository.NurseRepository;

import jakarta.servlet.http.HttpSession;

// Enable Mockito extension
@ExtendWith(MockitoExtension.class)
class NurseControllerTest {

    // Mock dependencies
    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session; 

    // Inject mocks into the controller
    @InjectMocks
    private NurseController nurseController;

    private Nurse sampleNurse;

    // Create a sample nurse before each test
    @BeforeEach
    void setUp() {
        sampleNurse = new Nurse();
        sampleNurse.setId(1);
        sampleNurse.setFirstName("Ana");
        sampleNurse.setLastName("Lopez");
        sampleNurse.setEmail("ana.lopez@test.com");
        sampleNurse.setPassword("hashedPassword123");
    }

    // --- Login Tests ---

    @Test
    void testLogin_Success() {
        // Arrange
        Map<String, String> loginRequest = Map.of("email", "ana.lopez@test.com", "password", "pass123");
        
        // Define mock behavior
        when(nurseRepository.findByEmail("ana.lopez@test.com")).thenReturn(sampleNurse);
        when(passwordEncoder.matches("pass123", "hashedPassword123")).thenReturn(true);

        // Act (call the method)
        ResponseEntity<Map<String, Boolean>> response = nurseController.login(loginRequest, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("authenticated"));
        // Verify session attribute was set
        verify(session).setAttribute("user", "Ana");
    }

    @Test
    void testLogin_Failed_WrongPassword() {
        // Arrange
        Map<String, String> loginRequest = Map.of("email", "ana.lopez@test.com", "password", "wrong");
        
        when(nurseRepository.findByEmail("ana.lopez@test.com")).thenReturn(sampleNurse);
        when(passwordEncoder.matches("wrong", "hashedPassword123")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Boolean>> response = nurseController.login(loginRequest, session);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(false, response.getBody().get("authenticated"));
        // Verify session was never touched
        verify(session, never()).setAttribute(any(), any());
    }

    // --- Get All Nurses Tests ---

    @Test
    void testGetAllNurses() {
        // Arrange
        when(nurseRepository.findAll()).thenReturn(List.of(sampleNurse));

        // Act
        ResponseEntity<List<Nurse>> response = nurseController.getAllNurses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size()); // Check list size is 1
        assertEquals("ana.lopez@test.com", response.getBody().get(0).getEmail());
    }

    @Test
    void testGetAllNurses_EmptyList() {
        // Arrange
        when(nurseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Nurse>> response = nurseController.getAllNurses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size()); // Check list is empty
    }

    // --- Get By ID Tests ---

    @Test
    void testReadNurse_Success() {
        // Arrange
        // Mock: repository finds the nurse
        when(nurseRepository.findById(1)).thenReturn(Optional.of(sampleNurse));

        // Act
        ResponseEntity<Map<String, Object>> response = nurseController.readNurse(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().get("nurse_id"));
        assertEquals("ana.lopez@test.com", response.getBody().get("email"));
        
    }

    @Test
    void testReadNurse_NotFound() {
        // Arrange
        // Mock: repository finds nothing
        when(nurseRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = nurseController.readNurse(99);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- Delete Tests ---

    @Test
    void testDeleteNurse_Success() {
        // Arrange
        when(nurseRepository.existsById(1)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.deleteNurse(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nurse successfuly deleted with id 1", response.getBody().get("Success"));
        
        // Verify deleteById was called once
        verify(nurseRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNurse_NotFound() {
        // Arrange
        when(nurseRepository.existsById(99)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.deleteNurse(99);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Nurse not found with id 99", response.getBody().get("Error"));
        
        // Verify deleteById was never called
        verify(nurseRepository, never()).deleteById(99);
    }
}