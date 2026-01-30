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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.intermodular.intro_backend.NurseController.NurseRegisterRequest;
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

    // --- Register Tests ---

    @Test
    void testRegister_Success() {
        // Arrange
        // Use a valid password to pass validation
        NurseRegisterRequest request = new NurseRegisterRequest("Jon", "Jina", "jon@test.com", "ValidPass123!", new byte[1]);
        
        // Mock: email does NOT exist
        when(nurseRepository.existsByEmailIgnoreCase("jon@test.com")).thenReturn(false);
        // Mock: password encoding
        when(passwordEncoder.encode("ValidPass123!")).thenReturn("hashedPass");
        
        // Mock: saving the new nurse returns an object with an ID
        Nurse savedNurse = new Nurse();
        savedNurse.setId(2);
        savedNurse.setFirstName("Jon");
        savedNurse.setLastName("Jina");
        savedNurse.setEmail("jon@test.com");
        when(nurseRepository.save(any(Nurse.class))).thenReturn(savedNurse);

        // Act
        ResponseEntity<?> response = nurseController.registerNurse(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Check that the response body contains the new ID
        assertTrue(response.getBody().toString().contains("nurse_id=2"));
        // Verify 'save' was called
        verify(nurseRepository).save(any(Nurse.class));
    }

    @Test
    void testRegister_Failed_EmailExists() {
        // Arrange
        NurseRegisterRequest request = new NurseRegisterRequest("Jon", "Jina", "jon@test.com", "ValidPass123!", new byte[1]);
        // Mock: email ALREADY exists
        when(nurseRepository.existsByEmailIgnoreCase("jon@test.com")).thenReturn(true);

        // Act
        ResponseEntity<?> response = nurseController.registerNurse(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("El email ya existe"));
        // Verify 'save' was never called
        verify(nurseRepository, never()).save(any(Nurse.class));
    }

    @Test
    void testRegister_Failed_InvalidEmail() {
        // Arrange
        // Use an invalid email
        NurseRegisterRequest request = new NurseRegisterRequest("Jon", "Jina", "bad-email", "ValidPass123!", new byte[1]);
        // Mock: email does not exist (to pass the first check)
        when(nurseRepository.existsByEmailIgnoreCase("bad-email")).thenReturn(false);

        // Act
        ResponseEntity<?> response = nurseController.registerNurse(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid parameters. Email must contain"));
        verify(nurseRepository, never()).save(any(Nurse.class));
    }

    @Test
    void testRegister_Failed_InvalidPassword() {
        // Arrange
        // Use an invalid (short) password
        NurseRegisterRequest request = new NurseRegisterRequest("Jon", "Jina", "jon@test.com", "123", new byte[1]);
        // Mock: email does not exist
        when(nurseRepository.existsByEmailIgnoreCase("jon@test.com")).thenReturn(false);

        // Act
        ResponseEntity<?> response = nurseController.registerNurse(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid parameters. Password must be at least 8 characters"));
        verify(nurseRepository, never()).save(any(Nurse.class));
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

    // --- Update Tests ---

    @Test
    void testUpdate_Success() {
        // Arrange
        Nurse updateRequest = new Nurse();
        updateRequest.setFirstName("AnaUpdated");
        updateRequest.setLastName("LopezUpdated");
        updateRequest.setEmail("ana.updated@test.com");
        updateRequest.setPassword("NewValidPass123!"); // Valid password

        // Mock: find the existing nurse
        when(nurseRepository.findById(1)).thenReturn(Optional.of(sampleNurse));
        // Mock: password encoding
        when(passwordEncoder.encode("NewValidPass123!")).thenReturn("newHashedPass");

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.updateNurse(1, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nurse with id: 1 successfully updated", response.getBody().get("Success"));
        
        // Verify 'save' was called
        verify(nurseRepository).save(any(Nurse.class));
        
        // Optional: Verify the object was updated before saving
        ArgumentCaptor<Nurse> nurseCaptor = ArgumentCaptor.forClass(Nurse.class);
        verify(nurseRepository).save(nurseCaptor.capture());
        assertEquals("AnaUpdated", nurseCaptor.getValue().getFirstName());
        assertEquals("newHashedPass", nurseCaptor.getValue().getPassword());
    }

    @Test
    void testUpdate_Failed_NotFound() {
        // Arrange
        Nurse updateRequest = new Nurse(); // Data doesn't matter
        // Mock: repository finds nothing
        when(nurseRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.updateNurse(99, updateRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Nurse not found with id 99", response.getBody().get("Error"));
        verify(nurseRepository, never()).save(any(Nurse.class));
    }

    @Test
    void testUpdate_Failed_InvalidEmail() {
        // Arrange
        Nurse updateRequest = new Nurse();
        updateRequest.setEmail("bad-email"); // Invalid email
        updateRequest.setPassword("ValidPass123!");

        // Mock: find the existing nurse
        when(nurseRepository.findById(1)).thenReturn(Optional.of(sampleNurse));

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.updateNurse(1, updateRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid parameters. Email must contain"));
        verify(nurseRepository, never()).save(any(Nurse.class));
    }

    @Test
    void testUpdate_Failed_InvalidPassword() {
        // Arrange
        Nurse updateRequest = new Nurse();
        updateRequest.setEmail("ana.updated@test.com");
        updateRequest.setPassword("123"); // Invalid password

        // Mock: find the existing nurse
        when(nurseRepository.findById(1)).thenReturn(Optional.of(sampleNurse));

        // Act
        ResponseEntity<Map<String, String>> response = nurseController.updateNurse(1, updateRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid parameters. Password must be at least 8 characters"));
        verify(nurseRepository, never()).save(any(Nurse.class));
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