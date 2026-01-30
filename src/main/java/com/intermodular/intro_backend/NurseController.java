package com.intermodular.intro_backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.intermodular.intro_backend.repository.NurseRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NurseRepository nurseRepository;



    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        try {
            Map<String, String> response = new HashMap<>();
            
            if (nurseRepository.existsByEmailIgnoreCase(request.email())) {
                response.put("error", "El email ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!validateEmail(request.email())) {
                response.put("Error", "Invalid parameters. Email must contain '@' and a valid domain (e.g., user@example.com).");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!validatePassword(request.password())) {
                response.put("Error", "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Nurse newNurse = new Nurse();
            newNurse.setFirstName(request.first_name());
            newNurse.setLastName(request.last_name());
            newNurse.setEmail(request.email());
            newNurse.setPassword(passwordEncoder.encode(request.password()));
            newNurse.setProfilePicture(request.profile_picture());

            Nurse savedNurse = nurseRepository.save(newNurse);

            Map<String, Object> nurseResponse = new HashMap<>();
            nurseResponse.put("nurse_id", savedNurse.getId());
            nurseResponse.put("first_name", savedNurse.getFirstName());
            nurseResponse.put("last_name", savedNurse.getLastName());
            nurseResponse.put("email", savedNurse.getEmail());
            nurseResponse.put("profile_picture", savedNurse.getProfilePicture());

            return ResponseEntity.status(HttpStatus.CREATED).body(nurseResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    public record NurseRegisterRequest(String first_name, String last_name, String email, String password, byte[] profile_picture) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");
        boolean authenticated = false;

        Nurse nurse = nurseRepository.findByEmail(email);

        if (nurse != null) {
            if (passwordEncoder.matches(password, nurse.getPassword())) {
                authenticated = true;
                session.setAttribute("user", nurse.getFirstName());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authenticated);

        if (authenticated) {
            response.put("nurse_id", nurse.getId());
            response.put("first_name", nurse.getFirstName());
            response.put("last_name", nurse.getLastName());
            response.put("email", nurse.getEmail());
            response.put("profile_picture", nurse.getProfilePicture());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping("/index")
    public ResponseEntity<List<Nurse>> getAllNurses() {
        return ResponseEntity.ok(nurseRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> readNurse(@PathVariable int id) {
        return nurseRepository.findById(id)
                .map(nurse -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("nurse_id", nurse.getId());
                    result.put("first_name", nurse.getName());
                    result.put("last_name", nurse.getLastName());
                    result.put("email", nurse.getEmail());
                    result.put("password", nurse.getPassword());
                    result.put("profile_picture", nurse.getProfilePicture());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateNurse(@PathVariable int id,@RequestBody Nurse newNurse) {
        Map<String, String> response = new HashMap<>();
        Optional<Nurse> oldNurse = nurseRepository.findById(id);

        if (oldNurse.isEmpty()) {
            response.put("Error", "Nurse not found with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (!validateEmail(newNurse.getEmail())) {
            response.put("Error", "Invalid parameters. Email must contain '@' and a valid domain (e.g., user@example.com).");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!validatePassword(newNurse.getPassword())) {
            response.put("Error", "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        oldNurse.get().setFirstName(newNurse.getFirstName());
        oldNurse.get().setLastName(newNurse.getLastName());
        oldNurse.get().setEmail(newNurse.getEmail());
        oldNurse.get().setPassword(passwordEncoder.encode(newNurse.getPassword()));
        oldNurse.get().setProfilePicture(newNurse.getProfilePicture());

        nurseRepository.save(oldNurse.get());
        response.put("Success", "Nurse with id: " + id + " successfully updated");
        return ResponseEntity.ok().body(response);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNurse(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        if (!nurseRepository.existsById(id)) {
            response.put("Error", "Nurse not found with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        nurseRepository.deleteById(id);
        response.put("Success", "Nurse successfuly deleted with id " + id);
        return ResponseEntity.ok(response);
    }




    //VALIDATIONS

    public boolean validateEmail(String email){
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    public boolean validatePassword(String password){
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }


}
