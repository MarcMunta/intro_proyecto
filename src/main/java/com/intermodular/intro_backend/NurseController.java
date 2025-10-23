package com.intermodular.intro_backend;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/nurse")

public class NurseController {

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (existsById(request.nurse_id())) {
                response.put("error", "El id ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existsByEmail(request.email())) {
                response.put("error", "El email ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            Nurse newNurse = new Nurse(
                    request.nurse_id(),
                    request.first_name(),
                    request.last_name(),
                    request.email(),
                    passwordEncoder.encode(request.password()));

            nurseRepository.save(newNurse);
            return ResponseEntity.status(HttpStatus.CREATED).body(toMap(newNurse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    public record NurseRegisterRequest(int nurse_id, String first_name, String last_name, String email,
            String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Boolean>> login(@RequestBody Map<String, String> body, HttpSession session) {
        List<Nurse> nurses = nurseRepository.findAll();
        String firstName = body.get("first_name");
        String password = body.get("password");
        boolean authenticated = false;

        for (Nurse nurse : nurses) {
            if (nurse.getFirstName().equalsIgnoreCase(firstName) &&
                    passwordEncoder.matches(password, nurse.getPassword())) {
                authenticated = true;
                session.setAttribute("user", firstName);
                break;
            }
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("authenticated", authenticated);

        if (authenticated) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping("/index")
    public ResponseEntity<List<Object>> getAllNurses() {
        List<Nurse> nurses = nurseRepository.findAll();
        List<Object> result = nurses.stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> findByName(@PathVariable String name) {
        List<Nurse> nurses = nurseRepository.findAll();
        for (Nurse n : nurses) {
            if (n.getFirstName().equalsIgnoreCase(name)) {
                return ResponseEntity.ok(toMap(n));
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean existsById(int id) {
        return nurseRepository.findById(id).isPresent();
    }

    private boolean existsByEmail(String email) {
        return nurseRepository.findAll().stream()
                .anyMatch(n -> n.getEmail().equalsIgnoreCase(email));
    }

    private Map<String, Object> toMap(Nurse nurse) {
        Map<String, Object> map = new HashMap<>();
        map.put("nurse_id", nurse.getId());
    map.put("first_name", nurse.getFirstName());
        map.put("last_name", nurse.getLastName());
        map.put("email", nurse.getEmail());
        map.put("password", nurse.getPassword());
        return map;
    }
}
