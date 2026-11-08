# Ruta avanzada: Java & Spring Boot

Repositorio de progreso semanal de la ruta avanzada en Riwi. Cada carpeta es una semana de trabajo y muestra cómo evoluciona un mismo proyecto a medida que se suman conceptos.

## 📚 Contenido

### `java/` — Corporate Talent Hub (Java SE)

Sistema de gestión de talento corporativo construido semana a semana:

| Semana | Tema principal |
| --- | --- |
| weekOne | Modelado inicial con `record` y clases |
| weekTwo | Capas de vista, servicio y repositorio en memoria |
| weekThree | Colecciones (`List`, `Map`, Sequenced Collections de Java 21) |
| weekFour | POO avanzada: herencia, interfaces (`Promotable`), polimorfismo |
| weekFive | Persistencia con JDBC, patrón DAO y MVC |

### `springboot/` — Eventify (Spring Boot)

API de gestión de eventos que crece semana a semana:

| Semana | Tema principal |
| --- | --- |
| weekOne | Primer API REST: controller / service / repository, seeders y OpenAPI |
| weekTwo | Spring Data JPA + PostgreSQL, DTOs y mappers |
| weekThree | Vistas con Thymeleaf |
| weekFour | Migraciones con Flyway |
| weekFive | MapStruct y validaciones personalizadas |

## 🛠️ Stack

Java 21 · Spring Boot 3 · Spring Data JPA · PostgreSQL · Flyway · MapStruct · Lombok · Bean Validation · springdoc-openapi · Thymeleaf · Maven

## ▶️ Cómo ejecutar un proyecto Spring Boot

```bash
cd springboot/weekFive/EventifyT
# Ajusta la conexión a PostgreSQL en src/main/resources/application.properties
./mvnw spring-boot:run
```

La documentación de la API queda en `http://localhost:8080/swagger-ui.html`.

## 👤 Autor

**Cristian Albor** — [@crapdev](https://github.com/crapdev)
