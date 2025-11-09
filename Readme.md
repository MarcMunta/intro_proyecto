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

### Usage (How to Run)

This project uses the Maven Wrapper (mvnw), so you don't need Maven installed globally.
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

## API Endpoints

All endpoints use the `/nurse` prefix:

- **POST** `/nurse/register` → Registers a new nurse.
- **POST** `/nurse/login` → Authenticates a nurse.
- **GET** `/nurse/index` → Returns a list of all nurses.
- **GET** `/nurse/{id}` → Returns a nurse by their ID.
- **PUT** `/nurse/{id}` → Updates a nurse by their ID.
- **DELETE** `/nurse/{id}` → Deletes a nurse by their ID.
