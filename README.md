# intro_proyecto

## Requisitos
- Java 17
- Maven
- MySQL Server


## Configuración y creación completa de la base de datos

Ejecuta todo este bloque en MySQL Workbench o tu cliente favorito para dejar la base de datos y el usuario listos:

```sql
-- Crea la base de datos
CREATE DATABASE IF NOT EXISTS hospital;
USE hospital;

-- Crea el usuario (si no existe) y dale permisos
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON hospital.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- (Opcional) Crea la tabla manualmente si quieres probar antes de arrancar la app
CREATE TABLE IF NOT EXISTS enfermeros (
   nurse_id INT PRIMARY KEY,
   first_name VARCHAR(100) NOT NULL,
   last_name VARCHAR(100) NOT NULL,
   email VARCHAR(150) NOT NULL UNIQUE,
   password VARCHAR(500) NOT NULL
);
```

### Configuración en `src/main/resources/application.properties`

```
spring.datasource.url=jdbc:mysql://localhost:3306/hospital
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

## Estructura de tablas (Hibernate/JPA)
Hibernate creará automáticamente la tabla `enfermeros` según la entidad `Nurse`:

```sql
CREATE TABLE enfermeros (
    nurse_id INT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL
);
```

## Inicialización de datos
Al iniciar la aplicación, si la tabla está vacía, se importarán los datos de `src/main/resources/data/nurse.json` automáticamente.

## Cómo arrancar el proyecto

1. Instala dependencias y compila:
   ```
   ./mvnw.cmd clean install
   ```
2. Arranca la aplicación:
   ```
   ./mvnw.cmd spring-boot:run
   ```
   o desde tu IDE ejecutando la clase `NurseApplication`.

## Probar la API
- `GET http://localhost:8080/nurse/index` — Lista todas las enfermeras
- `POST http://localhost:8080/nurse/register` — Registra una nueva enfermera
- `POST http://localhost:8080/nurse/login` — Login de enfermera

## Ver la tabla y datos en MySQL

Conéctate a MySQL y ejecuta:
```sql
USE hospital;
SELECT * FROM enfermeros;
```

---

**¡Listo! Hibernate y Spring Boot gestionan la creación y actualización de la base de datos automáticamente.**
