# Corporate Talent Hub

Sistema de gestión de empleados desarrollado en **Java 21**, utilizando **Maven**, **JDBC**, **PostgreSQL** y el patrón **MVC (Modelo - Vista - Controlador)**.

Permite registrar, listar, actualizar y eliminar empleados, generar reportes de desempeño y conservar la información de forma persistente en PostgreSQL.

---

## 📁 Estructura del proyecto

```text
src/main/java/
└── com/riwi/talent/
    ├── App.java
    ├── model/
    │   ├── Person.java
    │   ├── Employee.java
    │   ├── Developer.java
    │   ├── Manager.java
    │   ├── ExternalConsultant.java
    │   ├── Promotable.java
    │   ├── PerformanceReport.java
    │   ├── DatabaseConnection.java
    │   ├── EmployeeDAO.java
    │   ├── EmployeeDAOImpl.java
    │   └── EmployeeService.java
    ├── controller/
    │   └── EmployeeController.java
    └── view/
        └── ConsoleView.java
```

---

## 📦 Descripción de carpetas y módulos

### `model`

Contiene las entidades, reglas principales y acceso a datos.

- `Person`: clase base de la jerarquía.
- `Employee`: clase abstracta con información común de empleados.
- `Developer`: empleado desarrollador.
- `Manager`: empleado gerente.
- `ExternalConsultant`: consultor externo.
- `Promotable`: contrato para empleados que pueden recibir bono de promoción.
- `PerformanceReport`: `record` utilizado para transportar datos de reportes.
- `DatabaseConnection`: crea conexiones JDBC hacia PostgreSQL.
- `EmployeeDAO`: define las operaciones CRUD.
- `EmployeeDAOImpl`: implementa las operaciones CRUD con JDBC.
- `EmployeeService`: contiene reglas de negocio y coordina el acceso al DAO.

### `controller`

`EmployeeController` actúa como mediador entre la vista y el modelo. Recibe la información capturada por la vista, coordina las acciones, llama al servicio y devuelve los resultados a la vista.

### `view`

`ConsoleView` contiene toda la interacción directa con el usuario mediante consola. Aquí se encuentra el `Scanner`, además de los métodos para mostrar menús, mensajes, empleados y reportes.

### `App`

Es el punto de entrada del programa. Su responsabilidad principal es crear los objetos necesarios e iniciar el controlador.

---

## 🔄 Flujo del sistema

```text
Usuario
   ↓
ConsoleView
   ↓
EmployeeController
   ↓
EmployeeService
   ↓
EmployeeDAO
   ↓
EmployeeDAOImpl
   ↓
JDBC
   ↓
PostgreSQL
```

Ejemplo de registro:

```text
Usuario ingresa datos
        ↓
ConsoleView los captura
        ↓
EmployeeController coordina
        ↓
EmployeeService procesa reglas
        ↓
EmployeeDAO solicita persistencia
        ↓
EmployeeDAOImpl ejecuta INSERT
        ↓
PostgreSQL almacena los datos
```

---

## 💾 Persistencia con JDBC

JDBC permite la comunicación entre Java y PostgreSQL.

La conexión se obtiene mediante:

```java
Connection connection = DatabaseConnection.getConnection();
```

Los recursos JDBC se gestionan con `try-with-resources`:

```java
try (Connection connection = DatabaseConnection.getConnection();
     PreparedStatement statement = connection.prepareStatement(sql);
     ResultSet resultSet = statement.executeQuery()) {

    // Procesamiento
}
```

Esto permite cerrar automáticamente recursos como:

- `Connection`
- `PreparedStatement`
- `ResultSet`

En enfoques Legacy era común cerrar estos recursos manualmente dentro de `finally`. `try-with-resources` reduce el riesgo de dejar recursos JDBC abiertos.

---

## 🔐 PreparedStatement

Todas las consultas CRUD utilizan `PreparedStatement`.

Ejemplo:

```java
String sql = "DELETE FROM employees WHERE id = ?";

try (Connection connection = DatabaseConnection.getConnection();
     PreparedStatement statement = connection.prepareStatement(sql)) {

    statement.setInt(1, id);
    return statement.executeUpdate() > 0;
}
```

El uso de parámetros `?` evita concatenar directamente datos dentro del SQL y ayuda a proteger frente a inyección SQL.

---

## 🗃️ CRUD

| Operación | Método |
|---|---|
| CREATE | `insertar()` |
| READ | `listar()` |
| UPDATE | `actualizar()` |
| DELETE | `eliminar()` |

`EmployeeDAO` define el contrato y `EmployeeDAOImpl` contiene la implementación concreta con JDBC.

---

## 🧩 Records

El proyecto utiliza `PerformanceReport` para mapear información proveniente de consultas SQL.

```java
public record PerformanceReport(int idEmployee, double average, String feedback) {
}
```

Los records generan automáticamente constructor, accesores, `equals()`, `hashCode()` y `toString()`.

Acceso tradicional:

```java
reporte.getAverage();
```

Acceso con record:

```java
reporte.average();
```

Esto reduce código repetitivo frente a un POJO tradicional.

---

## 📝 Text Blocks

Se utilizan Text Blocks para mostrar información multilínea de forma más legible.

```java
String formato = """
        ==================================
           REPORTE DE DESEMPEÑO
        ==================================
        ID Empleado: %d
        Promedio: %.2f
        Feedback: %s
        ==================================
        """;
```

---

## 🧬 Conceptos de Java aplicados

- Encapsulación
- Herencia
- Abstracción
- Interfaces
- Polimorfismo
- Pattern Matching
- Sealed Classes
- Records
- List y ArrayList
- HashMap
- DAO
- MVC
- JDBC
- PreparedStatement
- ResultSet
- Try-with-resources
- Text Blocks

---

## 🛠️ Tecnologías usadas

- Java 21
- Maven
- PostgreSQL
- JDBC
- PostgreSQL JDBC Driver
- SQL
- Git / GitHub
- Ubuntu

---

# 📖 Manual de usuario

## ✅ Requisitos del sistema

- Java JDK 21
- Maven
- PostgreSQL
- PostgreSQL JDBC Driver administrado por Maven
- PostgreSQL ejecutándose localmente
- IDE compatible con Java, como NetBeans, IntelliJ IDEA o VS Code

El proyecto fue desarrollado en Ubuntu.

---

## 🗄️ Base de datos

Base utilizada:

```text
corporate_talent_hub
```

Tabla principal:

```sql
CREATE TABLE employees (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(250) NOT NULL,
    edad SMALLINT NOT NULL,
    salario DECIMAL(12,2) NOT NULL,
    calificaciones DOUBLE PRECISION[] NOT NULL,
    promedio_desempeno DECIMAL(8,2) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    main_lenguaje VARCHAR(100),
    monthly_budget DECIMAL(12,2)
);
```

---

## 🔌 Configuración de conexión

Ejemplo de configuración:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/corporate_talent_hub";
private static final String USER = "postgres";
private static final String PASSWORD = "TU_CONTRASEÑA";
```

> No se recomienda subir contraseñas reales a repositorios públicos.

---

## ▶️ Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone URL_DEL_REPOSITORIO
cd CorporateTalentHub
```

### 2. Verificar Java

```bash
java --version
```

Debe utilizar Java 21.

### 3. Verificar Maven

```bash
mvn --version
```

### 4. Verificar PostgreSQL en Ubuntu

```bash
sudo systemctl status postgresql
```

Si está detenido:

```bash
sudo systemctl start postgresql
```

### 5. Crear la base de datos

```bash
psql -U postgres
```

```sql
CREATE DATABASE corporate_talent_hub;
\c corporate_talent_hub
```

Después ejecutar el `CREATE TABLE employees` mostrado anteriormente.

### 6. Compilar

```bash
mvn clean compile
```

### 7. Ejecutar

Ejecutar la clase principal:

```text
com.riwi.talent.App
```

También puede ejecutarse desde el IDE si está configurada como clase principal.

---

## 🖥️ Funcionalidades

1. Registrar empleados y calificaciones.
2. Mostrar reportes de desempeño.
3. Consultar categorías salariales.
4. Eliminar empleados.
5. Consultar tecnologías y sedes.
6. Consultar orden de empleados.
7. Filtrar empleados por desempeño mínimo.
8. Generar reportes mensuales.
9. Consultar información específica según el rol.
10. Mostrar bonos de promoción.

---

## 👨‍💻 Registrar un empleado

Seleccionar:

```text
1. Registrar empleado y calificaciones
```

El sistema solicitará:

- ID
- Nombre
- Edad
- Salario
- Calificaciones
- Tipo de empleado

Para `Developer` solicita lenguaje principal.

Para `Manager` solicita presupuesto mensual.

Los datos se guardan en PostgreSQL mediante JDBC.

---

## 📊 Mostrar reporte de desempeño

Seleccionar:

```text
2. Mostrar reporte de desempeño
```

El sistema muestra información como ID, nombre, promedio, estado de promoción y categoría salarial.

---

## 🗑️ Eliminar un empleado

Seleccionar:

```text
4. Eliminar empleado
```

Ingresar el ID del empleado. El sistema ejecuta un `DELETE` mediante `PreparedStatement`.

---

## 📋 Generar reportes

Seleccionar:

```text
8. Generar reportes mensuales
```

Flujo:

```text
PostgreSQL
   ↓
SELECT
   ↓
ResultSet
   ↓
PerformanceReport
   ↓
ConsoleView
```

Ejemplo:

```text
==================================
   REPORTE DE DESEMPEÑO
==================================
ID Empleado: 1
Promedio: 88.33
Feedback: Excelente desempeño
==================================
```

---

## 👤 Consultar información de roles

Seleccionar:

```text
9. Consultar roles de empleados
```

El sistema utiliza Pattern Matching para diferenciar entre:

```text
Developer → lenguaje principal
Manager   → presupuesto mensual
```

---

## 🎯 Bonos de promoción

Seleccionar:

```text
10. Mostrar bonos de promoción
```

El sistema utiliza la interfaz `Promotable` para calcular el bono según el tipo real del empleado.

---

# 🏗️ Arquitectura MVC

```text
Vista
→ entrada y salida de información

Controlador
→ coordinación del flujo

Modelo
→ entidades, lógica y persistencia
```

La vista no ejecuta SQL. El controlador coordina las acciones. El DAO concentra el acceso a PostgreSQL.

---

# 📌 Persistencia de datos

Antes, los empleados podían almacenarse en estructuras en memoria como `ArrayList` y `HashMap`.

La versión actual utiliza PostgreSQL:

```text
Registrar empleado
        ↓
PostgreSQL
        ↓
Cerrar aplicación
        ↓
Abrir nuevamente
        ↓
El empleado continúa almacenado
```

---

# 👨‍💻 Autor - CRISTIAN ALBOR

Proyecto académico desarrollado para practicar Java 21, JDBC, PostgreSQL, CRUD, DAO, MVC y características modernas del lenguaje Java.
