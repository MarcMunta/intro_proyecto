package com.intermodular.intro_backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
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

import com.intermodular.intro_backend.repository.NurseRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    private static final String NURSE_JSON_PATH = "src/main/resources/data/nurse.json";

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private NurseRepository nurseRepository;

    private JSONArray getListNurses() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(NURSE_JSON_PATH)));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void saveAllNurses(JSONArray nurses) throws IOException {
        Files.writeString(Paths.get(NURSE_JSON_PATH), nurses.toString(2));
    }

    private boolean existsById(int id) throws IOException {
        JSONArray nurses = getListNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getInt("nurse_id") == id) {
                return true;
            }
        }
        return false;
    }

    private boolean existsByEmail(String email) throws IOException {
        JSONArray nurses = getListNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getString("email").equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        try {
            Map<String, String> response = new HashMap<>();
            if (existsById(request.nurse_id())) {
                response.put("error", "El id ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existsByEmail(request.email())) {
                response.put("error", "El email ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            JSONObject newNurse = new JSONObject();
            newNurse.put("nurse_id", request.nurse_id());
            newNurse.put("first_name", request.first_name());
            newNurse.put("last_name", request.last_name());
            newNurse.put("email", request.email());
            newNurse.put("password", passwordEncoder.encode(request.password()));

            JSONArray nurses = getListNurses();
            nurses.put(newNurse);
            saveAllNurses(nurses);

            return ResponseEntity.status(HttpStatus.CREATED).body(newNurse.toMap());
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
        JSONArray listNurses = getListNurses();
        String firstName = body.get("first_name");
        String password = body.get("password");
        boolean authenticated = false;

        for (int i = 0; i < listNurses.length(); i++) {
            JSONObject nurse = listNurses.getJSONObject(i);
            if (nurse.getString("first_name").equalsIgnoreCase(firstName) &&
                    passwordEncoder.matches(password, nurse.getString("password"))) {
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
        return ResponseEntity.ok(getListNurses().toList());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> findByName(@PathVariable String name) {
        return nurseRepository.findByFirstNameIgnoreCase(name)
                .map(nurse -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("nurse_id", nurse.getId());
                    result.put("first_name", nurse.getName());
                    result.put("last_name", nurse.getLastName());
                    result.put("email", nurse.getEmail());
                    result.put("password", nurse.getPassword());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
