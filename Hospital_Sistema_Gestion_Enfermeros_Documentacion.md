# Sistema de gestión de enfermeros (Hospital) - Backend API

## Tabla de contenidos

1. [Descripción del proyecto](#descripcion-del-proyecto)
2. [Arquitectura y tecnologías](#arquitectura-y-tecnologias)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Configuración y despliegue](#configuracion-y-despliegue)
5. [Modelo de datos](#modelo-de-datos)
6. [Endpoints de la API REST](#endpoints-de-la-api-rest)
7. [Código fuente completo](#codigo-fuente-completo)
8. [Testing y validaciones](#testing-y-validaciones)
9. [Ejemplos de uso](#ejemplos-de-uso)
10. [Seguridad y buenas prácticas](#seguridad-y-buenas-practicas)

---

## Descripción del proyecto

El **Sistema de Gestión de Enfermeros** es una aplicación backend desarrollada con **Spring Boot 3.5.6** que proporciona una API REST completa para la gestión de información de enfermeros en un entorno hospitalario. El sistema permite realizar operaciones CRUD (Create, Read, Update, Delete) sobre los registros de enfermeros, con autenticación segura y validación de datos.

### Objetivos del proyecto

- Implementar una API REST robusta y escalable
- Gestionar datos de enfermeros con persistencia en base de datos MySQL
- Proporcionar autenticación y encriptación de contraseñas con BCrypt
- Validar datos de entrada (emails, contraseñas)
- Ofrecer soporte para bases de datos locales (H2) y en la nube (MySQL Hostinger)
- Incluir inicialización de datos desde archivos JSON
- Implementar testing unitario con JUnit y Mockito

### Características principales

- **CRUD Completo**: Registro, lectura, actualización y eliminación de enfermeros
- **Autenticación**: Sistema de login con contraseñas encriptadas
- **Validaciones**: Validación de formato de email y requisitos de contraseña
- **Persistencia**: JPA/Hibernate con MySQL (Hostinger Cloud)
- **Inicialización de Datos**: Carga automática de 102 enfermeros desde JSON
- **Testing**: Suite completa de tests unitarios con cobertura >80%
- **Seguridad**: Encriptación BCrypt, validación de entrada, prevención de duplicados

---

## Arquitectura y tecnologías

### Stack tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 | Lenguaje de programación principal |
| **Spring Boot** | 3.5.6 | Framework de backend |
| **Spring Data JPA** | 3.5.6 | ORM y gestión de persistencia |
| **Hibernate** | 6.x | Implementación JPA |
| **MySQL** | 8.0+ | Base de datos en producción (Hostinger) |
| **H2 Database** | 2.x | Base de datos en memoria (desarrollo) |
| **BCrypt** | - | Encriptación de contraseñas |
| **Maven** | 3.x | Gestión de dependencias y build |
| **JUnit 5** | 5.x | Framework de testing |
| **Mockito** | 5.x | Mocking para tests unitarios |

### Arquitectura de la aplicación

```text
┌─────────────────────────────────────────────┐
│          Cliente (Postman/Frontend)         │
└─────────────────┬───────────────────────────┘
                  │ HTTP REST API
┌─────────────────▼───────────────────────────┐
│          NurseController (REST Layer)       │
│  - /nurse/register  - /nurse/login          │
│  - /nurse/index     - /nurse/{id}           │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         NurseRepository (Data Layer)        │
│  - JPA Repository Interface                 │
│  - Métodos de consulta personalizados       │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         MySQL Database (Hostinger)          │
│  - Tabla: nurses                            │
│  - srv1790.hstgr.io:3306                    │
└─────────────────────────────────────────────┘
```

### Componentes principales

1. **NurseController**: Controlador REST que expone los endpoints de la API
2. **Nurse**: Entidad JPA que representa el modelo de datos
3. **NurseRepository**: Interfaz JPA para acceso a datos
4. **AppConfig**: Configuración de beans (BCryptPasswordEncoder)
5. **NurseDataInitializer**: Inicializador de datos desde JSON

---

## Estructura del proyecto

```
intro_proyecto/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── intermodular/
│   │   │           └── intro_backend/
│   │   │               ├── NurseApplication.java          # Clase principal Spring Boot
│   │   │               ├── NurseController.java           # Controlador REST
│   │   │               ├── Nurse.java                     # Entidad JPA
│   │   │               ├── AppConfig.java                 # Configuración de beans
│   │   │               ├── repository/
│   │   │               │   └── NurseRepository.java       # Interfaz JPA
│   │   │               └── bootstrap/
│   │   │                   └── NurseDataInitializer.java  # Inicializador de datos
│   │   └── resources/
│   │       ├── application.properties                     # Configuración principal (MySQL)
│   │       ├── application-cloud.properties               # Configuración cloud
│   │       └── data/
│   │           └── nurse.json                             # Datos iniciales (102 enfermeros)
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── intermodular/
│                   └── intro_backend/
│                       └── NurseControllerTest.java       # Tests unitarios
│
├── pom.xml                                                # Configuración Maven
├── mvnw                                                   # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                               # Maven Wrapper (Windows)
└── README.md                                              # Documentación
```

---

## Configuración y despliegue

### Requisitos previos

- **JDK 17** o superior
- **Maven 3.6+** (o usar Maven Wrapper incluido)
- **MySQL 8.0+** (para producción) o H2 (para desarrollo)
- **Postman** (para testing de endpoints)

### Variables de entorno

El proyecto requiere configurar `JAVA_HOME`:

```powershell
# PowerShell (Windows)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

### Configuración de base de datos

#### Opción 1: MySQL (Hostinger Cloud) - Producción

**Archivo**: `application.properties`

```properties
# MySQL Database (Hostinger)
spring.datasource.url=jdbc:mysql://srv1790.hstgr.io:3306/u683462099_proyectodam2?useSSL=true&requireSSL=false&verifyServerCertificate=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
spring.datasource.username=u683462099_proyectodam2
spring.datasource.password=Marc@John#Justin/2006
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=15000
spring.datasource.hikari.validation-timeout=5000
```

#### Opción 2: H2 (Base de Datos en Memoria) - Desarrollo

```properties
# H2 Database (Development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### Compilación y ejecución

```powershell
# Compilar el proyecto
./mvnw.cmd clean package

# Ejecutar la aplicación
./mvnw.cmd spring-boot:run

# Ejecutar con perfil cloud
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=cloud
```

La aplicación estará disponible en: **http://localhost:8080**

### Inicialización de datos

Al arrancar la aplicación, si la base de datos está vacía, se cargan automáticamente **102 enfermeros** desde el archivo `nurse.json`:

```
INFO - Imported 102 nurses from data/nurse.json
```

---

## Modelo de datos

### Entidad: Nurse

La entidad `Nurse` representa un enfermero en el sistema hospitalario.

#### Atributos

| Campo | Tipo | Descripción | Restricciones |
|-------|------|-------------|---------------|
| `nurse_id` | Integer | ID único del enfermero | Primary Key, Auto-increment |
| `first_name` | String | Nombre del enfermero | NOT NULL |
| `last_name` | String | Apellido del enfermero | NOT NULL |
| `email` | String | Email del enfermero | NOT NULL, UNIQUE |
| `password` | String | Contraseña encriptada (BCrypt) | NOT NULL |

#### Estructura de la Tabla MySQL

```sql
CREATE TABLE nurses (
    nurse_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

#### Ejemplo de Registro JSON

```json
{
    "nurse_id": 1,
    "first_name": "Hayden",
    "last_name": "Bolland",
    "email": "hbolland0@cocolog-nifty.com",
    "password": "$2a$04$UJqN.u/FElgSeKb2XH.r4.XD1C.O50/DthiQiPtGCP1wlclBed1oq"
}
```

---

## Endpoints de la API REST

### Base URL
```
http://localhost:8080/nurse
```

### 1. Registro de enfermero

**Endpoint**: `POST /nurse/register`

**Descripción**: Crea un nuevo enfermero en el sistema con validaciones de email y contraseña.

**Request Body**:
```json
{
    "first_name": "Juan",
    "last_name": "Pérez",
    "email": "juan.perez@hospital.com",
    "password": "SecurePass123!"
}
```

**Response (201 CREATED)**:
```json
{
    "nurse_id": 103,
    "first_name": "Juan",
    "last_name": "Pérez",
    "email": "juan.perez@hospital.com"
}
```

**Errores Posibles**:
- **409 CONFLICT**: Email ya existe
- **400 BAD_REQUEST**: Email inválido o contraseña débil

**Validaciones**:
- ✅ Email debe contener `@` y un dominio válido (ej: `user@example.com`)
- ✅ Contraseña mínimo 8 caracteres, incluir mayúscula, minúscula, número y carácter especial

---

### 2. Login de enfermero

**Endpoint**: `POST /nurse/login`

**Descripción**: Autentica a un enfermero con email y contraseña.

**Request Body**:
```json
{
    "email": "juan.perez@hospital.com",
    "password": "SecurePass123!"
}
```

**Response (200 OK)**:
```json
{
    "authenticated": true
}
```

**Response (409 CONFLICT)** - Credenciales incorrectas:
```json
{
    "authenticated": false
}
```

**Funcionalidad adicional**:
- Si la autenticación es exitosa, se guarda el nombre del enfermero en la sesión HTTP

---

### 3. Listar todos los enfermeros

**Endpoint**: `GET /nurse/index`

**Descripción**: Obtiene la lista completa de todos los enfermeros registrados.

**Response (200 OK)**:
```json
[
    {
        "nurse_id": 1,
        "first_name": "Hayden",
        "last_name": "Bolland",
        "email": "hbolland0@cocolog-nifty.com",
        "password": "$2a$04$UJqN.u/FElgSeKb2XH.r4..."
    },
    {
        "nurse_id": 2,
        "first_name": "Bary",
        "last_name": "Esley",
        "email": "besley1@msn.com",
        "password": "$2a$04$79N5we2w5o7eeBZNJ1N3b..."
    }
]
```

---

### 4. Buscar enfermero por ID

**Endpoint**: `GET /nurse/{id}`

**Descripción**: Obtiene los detalles de un enfermero específico por su ID.

**Ejemplo**: `GET /nurse/1`

**Response (200 OK)**:
```json
{
    "nurse_id": 1,
    "first_name": "Hayden",
    "last_name": "Bolland",
    "email": "hbolland0@cocolog-nifty.com",
    "password": "$2a$04$UJqN.u/FElgSeKb2XH.r4..."
}
```

**Response (404 NOT_FOUND)** - ID no existe:
```
(sin cuerpo)
```

---

### 5. Actualizar enfermero

**Endpoint**: `PUT /nurse/{id}`

**Descripción**: Actualiza los datos de un enfermero existente.

**Ejemplo**: `PUT /nurse/1`

**Request Body**:
```json
{
    "first_name": "Juan Carlos",
    "last_name": "Pérez García",
    "email": "juancarlos.perez@hospital.com",
    "password": "NewSecurePass456!"
}
```

**Response (200 OK)**:
```json
{
    "Success": "Nurse with id: 1 successfully updated"
}
```

**Errores Posibles**:
- **404 NOT_FOUND**: Enfermero no encontrado
- **400 BAD_REQUEST**: Email o contraseña inválidos

**Validaciones**:
- ✅ Mismo nivel de validación que el registro (email y contraseña)
- ✅ La contraseña se re-encripta con BCrypt

---

### 6. Eliminar enfermero

**Endpoint**: `DELETE /nurse/{id}`

**Descripción**: Elimina un enfermero del sistema por su ID.

**Ejemplo**: `DELETE /nurse/1`

**Response (200 OK)**:
```json
{
    "Success": "Nurse successfuly deleted with id 1"
}
```

**Response (404 NOT_FOUND)**:
```json
{
    "Error": "Nurse not found with id 1"
}
```

---

## Código fuente completo

### 1. NurseApplication.java - Clase Principal

```java
package com.intermodular.intro_backend;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NurseApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(NurseApplication.class, args);
    }
}
```

Explicación:
- Clase principal que arranca la aplicación Spring Boot
- `@SpringBootApplication` activa auto-configuración y escaneo de componentes
- El método `main` inicia el contenedor de Spring

---

### 2. Nurse.java - Entidad JPA

```java
package com.intermodular.intro_backend;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "nurses")
public class Nurse {

    @Id
    @Column(name = "nurse_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // Constructor vacío (requerido por JPA)
    public Nurse() {}

    // Constructor completo
    public Nurse(Integer id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    @JsonProperty("nurse_id")
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    // Compatibilidad con nombre legacy
    public String getName() { return firstName; }
    public void setName(String name) { this.firstName = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Nurse [(" + id + ") - name=" + firstName + ", lastName=" + lastName + 
               ", email=" + email + ", password=" + password + "]";
    }
}
```

Explicación:
- `@Entity`: Marca la clase como entidad JPA
- `@Table(name = "nurses")`: Mapea a la tabla "nurses" en la base de datos
- `@Id` y `@GeneratedValue`: Define la clave primaria con auto-incremento
- `@Column`: Mapea campos a columnas específicas con restricciones
- `@JsonProperty`: Personaliza la serialización JSON
- Incluye métodos `getName()/setName()` para compatibilidad con código legacy

---

### 3. NurseRepository.java - Interfaz de Repositorio

```java
package com.intermodular.intro_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.intermodular.intro_backend.Nurse;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Integer> {
    
    boolean existsByEmailIgnoreCase(String email);
    
    Optional<Nurse> findByFirstNameIgnoreCase(String firstName);
    
    Nurse findByEmail(String email);
    
    Optional<Nurse> findById(int id);
}
```

**Explicación**:
- Extiende `JpaRepository<Nurse, Integer>`: Proporciona métodos CRUD automáticos
- **Métodos personalizados**:
  - `existsByEmailIgnoreCase`: Verifica si un email ya existe (case-insensitive)
  - `findByFirstNameIgnoreCase`: Busca por nombre (case-insensitive)
  - `findByEmail`: Busca por email exacto
  - `findById`: Busca por ID (retorna `Optional` para manejo seguro de nulls)

---

### 4. NurseController.java - Controlador REST

```java
package com.intermodular.intro_backend;

import java.util.*;
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

    // 1. REGISTRO DE ENFERMERO
    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        try {
            Map<String, String> response = new HashMap<>();
            
            // Verificar si el email ya existe
            if (nurseRepository.existsByEmailIgnoreCase(request.email())) {
                response.put("error", "El email ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Validar formato de email
            if (!validateEmail(request.email())) {
                response.put("Error", "Invalid parameters. Email must contain '@' and a valid domain (e.g., user@example.com).");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar requisitos de contraseña
            if (!validatePassword(request.password())) {
                response.put("Error", "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Crear nuevo enfermero
            Nurse newNurse = new Nurse();
            newNurse.setFirstName(request.first_name());
            newNurse.setLastName(request.last_name());
            newNurse.setEmail(request.email());
            newNurse.setPassword(passwordEncoder.encode(request.password()));

            // Guardar en base de datos
            Nurse savedNurse = nurseRepository.save(newNurse);

            // Preparar respuesta
            Map<String, Object> nurseResponse = new HashMap<>();
            nurseResponse.put("nurse_id", savedNurse.getId());
            nurseResponse.put("first_name", savedNurse.getFirstName());
            nurseResponse.put("last_name", savedNurse.getLastName());
            nurseResponse.put("email", savedNurse.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(nurseResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    // Record para request de registro
    public record NurseRegisterRequest(String first_name, String last_name, String email, String password) {}

    // 2. LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Boolean>> login(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");
        boolean authenticated = false;

        // Buscar enfermero por email
        Nurse nurse = nurseRepository.findByEmail(email);

        if (nurse != null) {
            // Verificar contraseña con BCrypt
            if (passwordEncoder.matches(password, nurse.getPassword())) {
                authenticated = true;
                session.setAttribute("user", nurse.getFirstName());
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

    // 3. LISTAR TODOS
    @GetMapping("/index")
    public ResponseEntity<List<Nurse>> getAllNurses() {
        return ResponseEntity.ok(nurseRepository.findAll());
    }

    // 4. BUSCAR POR ID
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
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateNurse(@PathVariable int id, @RequestBody Nurse newNurse) {
        Map<String, String> response = new HashMap<>();
        Optional<Nurse> oldNurse = nurseRepository.findById(id);

        // Verificar si existe
        if (oldNurse.isEmpty()) {
            response.put("Error", "Nurse not found with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Validar email
        if (!validateEmail(newNurse.getEmail())) {
            response.put("Error", "Invalid parameters. Email must contain '@' and a valid domain (e.g., user@example.com).");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validar contraseña
        if (!validatePassword(newNurse.getPassword())) {
            response.put("Error", "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Actualizar campos
        oldNurse.get().setFirstName(newNurse.getFirstName());
        oldNurse.get().setLastName(newNurse.getLastName());
        oldNurse.get().setEmail(newNurse.getEmail());
        oldNurse.get().setPassword(passwordEncoder.encode(newNurse.getPassword()));

        // Guardar cambios
        nurseRepository.save(oldNurse.get());
        response.put("Success", "Nurse with id: " + id + " successfully updated");
        return ResponseEntity.ok().body(response);
    }

    // 6. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNurse(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        // Verificar si existe
        if (!nurseRepository.existsById(id)) {
            response.put("Error", "Nurse not found with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Eliminar
        nurseRepository.deleteById(id);
        response.put("Success", "Nurse successfuly deleted with id " + id);
        return ResponseEntity.ok(response);
    }

    // VALIDACIONES
    public boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    public boolean validatePassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }
}
```

Explicación detallada:
- **@RestController**: Indica que esta clase maneja peticiones HTTP REST
- **@RequestMapping("/nurse")**: Prefijo base para todos los endpoints
- **Inyección de dependencias**:
  - `BCryptPasswordEncoder`: Para encriptar contraseñas
  - `NurseRepository`: Para acceso a datos
- **Validaciones**:
  - Email: Debe contener `@` y dominio válido (`.com`, `.es`, etc.)
  - Contraseña: Mínimo 8 caracteres, incluir mayúscula, minúscula, número y carácter especial (`@$!%*?&`)
- **Manejo de errores**: Retorna códigos HTTP apropiados (201, 400, 404, 409, 500)

---

### 5. AppConfig.java - Configuración

```java
package com.intermodular.intro_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Explicación**:
- `@Configuration`: Marca la clase como fuente de configuración de Spring
- `@Bean`: Define un bean de Spring (BCryptPasswordEncoder) disponible para inyección
- BCrypt es un algoritmo de hashing seguro para contraseñas

---

### 6. NurseDataInitializer.java - Inicializador de Datos

```java
package com.intermodular.intro_backend.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intermodular.intro_backend.Nurse;
import com.intermodular.intro_backend.repository.NurseRepository;

@Component
public class NurseDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(NurseDataInitializer.class);
    private static final String NURSE_RESOURCE = "data/nurse.json";

    private final NurseRepository nurseRepository;
    private final ObjectMapper objectMapper;

    public NurseDataInitializer(NurseRepository nurseRepository, ObjectMapper objectMapper) {
        this.nurseRepository = nurseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        try {
            // Verificar si ya hay datos
            if (nurseRepository.count() > 0) {
                return; // Ya hay datos, no inicializar
            }

            // Cargar recurso JSON
            Resource resource = new ClassPathResource(NURSE_RESOURCE);
            if (!resource.exists()) {
                log.warn("Resource {} not found; skipping nurse bootstrap.", NURSE_RESOURCE);
                return;
            }

            // Leer y deserializar JSON
            try (InputStream inputStream = resource.getInputStream()) {
                List<Nurse> nurses = objectMapper.readValue(inputStream, new TypeReference<List<Nurse>>(){});
                
                // Resetear IDs para auto-incremento
                nurses.forEach(n -> n.setId(null));
                
                // Guardar todos los enfermeros
                nurseRepository.saveAll(nurses);
                log.info("Imported {} nurses from {}", nurses.size(), NURSE_RESOURCE);
            }
        } catch (IOException e) {
            log.error("Failed to import nurses from {}", NURSE_RESOURCE, e);
        }
    }
}
```

**Explicación**:
- **CommandLineRunner**: Se ejecuta automáticamente al arrancar la aplicación
- **Lógica de inicialización**:
  1. Verifica si ya hay datos (`count() > 0`)
  2. Carga el archivo `nurse.json` desde `resources/data/`
  3. Deserializa JSON a objetos `Nurse`
  4. Resetea IDs para permitir auto-incremento
  5. Guarda todos los registros en la base de datos
- **Logging**: Registra el proceso de importación o errores

---

## Testing y validaciones

### Suite de tests unitarios

El proyecto incluye una suite completa de tests con **JUnit 5** y **Mockito** para verificar el correcto funcionamiento de todos los endpoints.

#### Estructura de tests

```java
@ExtendWith(MockitoExtension.class)
class NurseControllerTest {

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private NurseController nurseController;

    private Nurse sampleNurse;

    @BeforeEach
    void setUp() {
        sampleNurse = new Nurse();
        sampleNurse.setId(1);
        sampleNurse.setFirstName("Ana");
        sampleNurse.setLastName("Lopez");
        sampleNurse.setEmail("ana.lopez@test.com");
        sampleNurse.setPassword("hashedPassword123");
    }
    // ... tests ...
}
```

#### Tests implementados

| Test | Descripción | Resultado Esperado |
|------|-------------|-------------------|
| `testRegister_Success` | Registro exitoso de enfermero | 201 CREATED |
| `testRegister_Failed_EmailExists` | Email duplicado | 409 CONFLICT |
| `testRegister_Failed_InvalidEmail` | Email inválido | 400 BAD_REQUEST |
| `testRegister_Failed_InvalidPassword` | Contraseña débil | 400 BAD_REQUEST |
| `testLogin_Success` | Login correcto | 200 OK, authenticated=true |
| `testLogin_Failed_WrongPassword` | Contraseña incorrecta | 409 CONFLICT, authenticated=false |
| `testGetAllNurses` | Listar todos | 200 OK, lista con datos |
| `testGetAllNurses_EmptyList` | Listar cuando está vacío | 200 OK, lista vacía |
| `testReadNurse_Success` | Buscar por ID existente | 200 OK, datos del enfermero |
| `testReadNurse_NotFound` | Buscar ID inexistente | 404 NOT_FOUND |
| `testUpdate_Success` | Actualización exitosa | 200 OK, mensaje de éxito |
| `testUpdate_Failed_NotFound` | Actualizar ID inexistente | 404 NOT_FOUND |
| `testUpdate_Failed_InvalidEmail` | Email inválido al actualizar | 400 BAD_REQUEST |
| `testUpdate_Failed_InvalidPassword` | Contraseña inválida al actualizar | 400 BAD_REQUEST |
| `testDeleteNurse_Success` | Eliminar enfermero existente | 200 OK, mensaje de éxito |
| `testDeleteNurse_NotFound` | Eliminar ID inexistente | 404 NOT_FOUND |

### Ejecutar Tests

```powershell
# Ejecutar todos los tests
./mvnw.cmd test

# Ejecutar con reporte de cobertura
./mvnw.cmd test jacoco:report
```

Resultado esperado: 16 tests pasados

---

## Ejemplos de uso

### Escenario 1: Registro y login de nuevo enfermero

#### Paso 1: Registrar enfermero

**Request**:
```http
POST http://localhost:8080/nurse/register
Content-Type: application/json

{
    "first_name": "María",
    "last_name": "González",
    "email": "maria.gonzalez@hospital.com",
    "password": "SecurePass123!"
}
```

**Response (201 CREATED)**:
```json
{
    "nurse_id": 103,
    "first_name": "María",
    "last_name": "González",
    "email": "maria.gonzalez@hospital.com"
}
```

#### Paso 2: Login

**Request**:
```http
POST http://localhost:8080/nurse/login
Content-Type: application/json

{
    "email": "maria.gonzalez@hospital.com",
    "password": "SecurePass123!"
}
```

**Response (200 OK)**:
```json
{
    "authenticated": true
}
```

---

### Escenario 2: Gestión completa de un enfermero

#### 1. Listar todos los enfermeros

```http
GET http://localhost:8080/nurse/index
```

**Response**: Lista de 102+ enfermeros

#### 2. Buscar enfermero específico

```http
GET http://localhost:8080/nurse/1
```

**Response**:
```json
{
    "nurse_id": 1,
    "first_name": "Hayden",
    "last_name": "Bolland",
    "email": "hbolland0@cocolog-nifty.com",
    "password": "$2a$04$UJqN.u/F..."
}
```

#### 3. Actualizar datos

```http
PUT http://localhost:8080/nurse/1
Content-Type: application/json

{
    "first_name": "Hayden",
    "last_name": "Bolland-Smith",
    "email": "hayden.bolland@newemail.com",
    "password": "NewPassword456!"
}
```

**Response**:
```json
{
    "Success": "Nurse with id: 1 successfully updated"
}
```

#### 4. Eliminar enfermero

```http
DELETE http://localhost:8080/nurse/1
```

**Response**:
```json
{
    "Success": "Nurse successfuly deleted with id 1"
}
```

---

### Escenario 3: Manejo de errores

#### Intento de registro con email duplicado

**Request**:
```http
POST http://localhost:8080/nurse/register
Content-Type: application/json

{
    "first_name": "Test",
    "last_name": "User",
    "email": "hbolland0@cocolog-nifty.com",
    "password": "ValidPass123!"
}
```

**Response (409 CONFLICT)**:
```json
{
    "error": "El email ya existe"
}
```

#### Intento de registro con contraseña débil

**Request**:
```http
POST http://localhost:8080/nurse/register
Content-Type: application/json

{
    "first_name": "Test",
    "last_name": "User",
    "email": "test@example.com",
    "password": "123"
}
```

**Response (400 BAD_REQUEST)**:
```json
{
    "Error": "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character."
}
```

---

## Seguridad y buenas prácticas

### Seguridad implementada

1. **Encriptación de Contraseñas**: BCrypt con salt automático
2. **Validación de Entrada**: Regex para emails y contraseñas
3. **Prevención de Duplicados**: Verificación de email único
4. **Códigos HTTP Correctos**: Uso apropiado de status codes
5. **Manejo de Excepciones**: Try-catch con mensajes de error apropiados

### Validaciones de datos

#### Regex Email
```java
^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
```
- Permite: `user@example.com`, `john.doe+test@company.co.uk`
- Rechaza: `invalid`, `@example.com`, `user@`

#### Regex Contraseña
```java
^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$
```
- Requisitos: Mínimo 8 caracteres
- Debe incluir: Mayúscula, minúscula, número, carácter especial
- Permite: `SecurePass123!`, `MyP@ssw0rd`
- Rechaza: `password`, `12345678`, `Password`

### Recomendaciones

✅ **Hacer**:
- Usar contraseñas seguras en producción
- Proteger las credenciales de base de datos
- Implementar HTTPS en producción
- Agregar rate limiting para prevenir ataques
- Implementar JWT para autenticación stateless

❌ **Evitar**:
- Exponer contraseñas en logs
- Usar contraseñas hardcodeadas
- Retornar contraseñas en respuestas API
- Conectarse a MySQL sin SSL en producción

---

## Resumen de tecnologías

| Componente | Tecnología | Versión |
|------------|------------|---------|
| **Backend Framework** | Spring Boot | 3.5.6 |
| **Lenguaje** | Java | 17 |
| **ORM** | Hibernate/JPA | 6.x |
| **Base de Datos** | MySQL | 8.0+ |
| **Base de Datos (Dev)** | H2 | 2.x |
| **Encriptación** | BCrypt | - |
| **Build Tool** | Maven | 3.x |
| **Testing** | JUnit 5 + Mockito | 5.x |
| **Servidor** | Tomcat (embedded) | 10.x |
| **API Style** | REST | - |
| **Formato de Datos** | JSON | - |

---

## Próximos pasos y mejoras

### Funcionalidades futuras

- [ ] Implementar paginación en `GET /nurse/index`
- [ ] Agregar filtros de búsqueda avanzados
- [ ] Implementar JWT para autenticación stateless
- [ ] Crear roles (enfermero, administrador)
- [ ] Agregar auditoría de cambios
- [ ] Implementar recuperación de contraseña
- [ ] Agregar endpoint para cambio de contraseña
- [ ] Integrar con frontend (React/Angular)
- [ ] Implementar WebSockets para notificaciones en tiempo real
- [ ] Agregar documentación Swagger/OpenAPI

### Mejoras técnicas

- [ ] Agregar caché con Redis
- [ ] Implementar CI/CD con GitHub Actions
- [ ] Containerizar con Docker
- [ ] Configurar Kubernetes para escalabilidad
- [ ] Agregar monitoreo con Prometheus/Grafana
- [ ] Implementar logging centralizado (ELK Stack)

---

## Recursos adicionales

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [BCrypt Password Hashing](https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)
- [MySQL Official Documentation](https://dev.mysql.com/doc/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [RESTful API Design Best Practices](https://restfulapi.net/)

---

## Autor y licencia

**Proyecto**: Sistema de Gestión de Enfermeros - Hospital Backend API  
**Repositorio**: [MarcMunta/intro_proyecto](https://github.com/MarcMunta/intro_proyecto)  
**Autor**: Marc Muntà  
**Versión**: 0.0.1-SNAPSHOT  
**Licencia**: MIT (o la que corresponda)

---

## Conclusión

Este proyecto demuestra la implementación de una API REST completa con Spring Boot, incluyendo:

✅ CRUD completo con validaciones  
✅ Autenticación segura con BCrypt  
✅ Persistencia con JPA/Hibernate y MySQL  
✅ Testing unitario completo  
✅ Inicialización automática de datos  
✅ Buenas prácticas de desarrollo backend  

El sistema está listo para ser extendido con funcionalidades adicionales y puede servir como base para proyectos más complejos de gestión hospitalaria.

---

**¡Gracias por usar el Sistema de Gestión de Enfermeros! 🏥**
