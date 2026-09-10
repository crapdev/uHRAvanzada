package com.riwi.talent.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Legacy:
 * Antes de try-with-resources, los recursos JDBC se cerraban manualmente
 * normalmente dentro de un bloque finally, verificando que el recurso no
 * fuera null y controlando posibles SQLException durante close().
 *
 * try-with-resources, disponible desde Java 7 y utilizado en Java moderno,
 * cierra automáticamente los recursos que implementan AutoCloseable,
 * reduciendo el riesgo de conexiones o recursos JDBC que queden abiertos.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5433/corporate_talent_hub";
    private static final String USER = "cristian";
    private static final String PASSWORD = "123.qwe**";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*
 * El cierre automático ayuda a prevenir fugas de recursos (resource leaks),
 * como conexiones, statements o result sets que permanezcan abiertos.
 * Esto evita mantener recursos innecesarios en Java y PostgreSQL y reduce
 * problemas como el agotamiento de conexiones disponibles.
 */

//jdbc:postgresql://localhost:5432/corporate_talent_hub
//        │       │            │                    │
//        │       │            │                    └── base de datos
//│       │       │            └── puerto PostgreSQL
//│       │       └── servidor
//│       └── motor
//└── JDBC

//        Connection
//→ representa la conexión con PostgreSQL
//
//        PreparedStatement
//→ prepara y ejecuta SQL de forma segura
//
//        ResultSet
//→ contiene los datos devueltos por un SELECT
//
//try-with-resources
//→ cierra automáticamente los recursos JDBC