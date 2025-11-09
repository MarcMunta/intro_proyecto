# Nurse API Project (Spring Boot)

![CI](https://github.com/MarcMunta/intro_proyecto/actions/workflows/ci.yml/badge.svg)

## Purpose

This project is an introduction to backend development. The goal is to learn and practice key technologies, by building an API that simulates a small part of a hospital smanager, focusing on managing a "Nurse" entity.

To build the API, we used **Spring Boot** with **Spring Data JPA (Hibernate)** to manage the database.

The project implements 6 endpoints from the `NurseController`:
* **Authentication:**
    * `POST /nurse/register`
    * `POST /nurse/login`
* **CRUD Operations:**
    * `GET /nurse/index` (Read all)
    * `GET /nurse/{id}` (Read by ID)
    * `PUT /nurse/{id}` (Update)
    * `DELETE /nurse/{id}` (Delete)

Finally, to test the functionalities, the application was tested using:
* **JUnit (with Mockito)** for unit tests.
* **Postman** for manual endpoint testing.
* A **GitHub Actions pipeline** for Continuous Integration (CI).

## Installation

Follow these steps to set up the project on your local machine.

### Clone the repository

```
git clone https://github.com/MarcMunta/intro_proyecto.git
cd intro_proyecto
```

### How to Run

Run the application:
```
# On macOS/Linux
./mvnw spring-boot:run

# On Windows
./mvnw.cmd spring-boot:run
```
The API will be available at:

`http://localhost:8080`

Run the tests:
```
# On macOS/Linux
./mvnw clean test

# On Windows
./mvnw.cmd clean test
```

## Uses / API Endpoints

All endpoints use the `/nurse` prefix.

### **POST /nurse/register**
**Description:** Registers a new nurse.

**Requires JSON body:**
```json
{ 
  "first_name": "...", 
  "last_name": "...", 
  "email": "...", 
  "password": "..." 
}
```

**Returns (201 Created):** The newly created nurse's information (password not included).  
**Returns (Error):**
- `409 Conflict` → The email already exists. Example:
```
{ "error": "El email ya existe" }
```
- `400 Bad Request` → Validation failed. Possible messages:
```
{ "Error": "Invalid parameters. Email must contain '@' and a valid domain (e.g., user@example.com)." }
```
or
```
{ "Error": "Invalid parameters. Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character." }
```

---

### **POST /nurse/login**
**Description:** Authenticates a nurse.

**Requires (JSON Body):**
```
{ 
  "email": "...", 
  "password": "..." 
}
```

**Returns (200 OK):**
```
{ 
  "authenticated": true 
}
```

**Returns (Error) `409 Conflict`:**
```
{ 
  "authenticated": false 
}
```

---

### **GET /nurse/index**
**Description:** Returns a list of all nurses.

**Returns (200 OK):**
```
[
  { ... },
  { ... }
]
```

---

### **GET /nurse/{id}**
**Description:** Returns a specific nurse by their ID.

**Returns (200 OK):** A JSON object with nurse details.  
**Returns (Error) `404 Not Found`:** The nurse with that ID does not exist.
```
{
  "Error": "Nurse not found with id ..."
}
```

---

### **PUT /nurse/{id}**
**Description:** Updates a nurse by their ID.

**Requires (JSON Body):**
```json
{ 
  "first_name": "...", 
  "last_name": "...", 
  "email": "...", 
  "password": "..." 
}
```

**Returns (200 OK):**
```
{ 
  "Success": "Nurse with id: ... successfully updated" 
}
```

**Returns (Error):**
- `404 Not Found` → Nurse does not exist.
- `400 Bad Request` → Validation issues.

---

### **DELETE /nurse/{id}**
**Description:** Deletes a nurse by their ID.

**Returns (200 OK):**
```
{ 
  "Success": "Nurse successfully deleted with id ..." 
}
```

**Returns (Error) `404 Not Found`:** The nurse with that ID does not exist.ç
```
{
  "Error": "Nurse not found with id ..."
}
```

