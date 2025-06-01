package com.mycompany.practicabasededatos.database;

// Importaciones necesarias para manejar la conexión a la base de datos
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL.
 * Proporciona un método estático para obtener una conexión activa con la base de datos del proyecto.
 */
public class DatabaseConnection {

    // URL de conexión a la base de datos (incluye la dirección del servidor, puerto y nombre de la base de datos)
    private static final String URL = "jdbc:mysql://localhost:3306/dam_bd";

    // Usuario con permisos de acceso a la base de datos
    private static final String USER = "usuariodam";

    // Contraseña correspondiente al usuario definido anteriormente
    private static final String PASSWORD = "usuariodam";

    /**
     * Método estático que establece y devuelve una conexión a la base de datos.
     * @return Objeto Connection con la conexión activa.
     * @throws SQLException Si ocurre algún error al intentar conectarse a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        // Establece y retorna una conexión a la base de datos utilizando los parámetros configurados
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
