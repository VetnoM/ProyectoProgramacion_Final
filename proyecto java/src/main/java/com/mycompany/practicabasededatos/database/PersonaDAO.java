package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Persona;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PersonaDAO encargada de realizar operaciones CRUD
 * sobre la tabla 'persona' en la base de datos. 
 * Permite obtener, insertar, actualizar, eliminar y buscar personas.
 */
public class PersonaDAO {

    // Obtiene todas las personas almacenadas en la base de datos
    public ArrayList<Persona> obtenerPersonas() {
        ArrayList<Persona> personas = new ArrayList<>();
        String query = "SELECT * FROM persona";

        // Abrimos conexión con la base de datos, creamos un Statement y ejecutamos la consulta
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Iteramos sobre cada registro devuelto
            while (rs.next()) {
                // Creamos un objeto Persona con los datos del registro actual
                Persona persona = new Persona(
                        rs.getInt("id_persona"),
                        rs.getString("documento"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                );
                // Añadimos la persona a la lista
                personas.add(persona);
            }
        } catch (SQLException e) {
            // Imprime mensaje de error si ocurre un problema con la consulta
            System.out.println("Error al obtener personas: " + e.getMessage());
        }
        // Devuelve la lista con todas las personas encontradas
        return personas;
    }

    // Inserta una persona nueva en la base de datos y devuelve el ID generado
    public int insertarPersona(Persona persona) {
        String query = "INSERT INTO persona (documento, nombre, apellido, fecha_nacimiento, telefono, email, direccion) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int idGenerado = -1;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            // Validación simple para asegurar que campos obligatorios no estén vacíos
            if (persona.getDocumento_identidad() == null || persona.getNombre() == null || persona.getApellido() == null) {
                System.out.println("Faltan datos obligatorios para la persona.");
                return idGenerado;
            }

            // Asignamos los valores a los parámetros de la consulta
            pst.setString(1, persona.getDocumento_identidad());
            pst.setString(2, persona.getNombre());
            pst.setString(3, persona.getApellido());
            // Convertimos la fecha java.util.Date a java.sql.Date para la base de datos
            pst.setDate(4, new java.sql.Date(persona.getFecha_nacimiento().getTime()));
            pst.setString(5, persona.getTelefono());
            pst.setString(6, persona.getEmail());
            pst.setString(7, persona.getDireccion());

            // Ejecutamos la inserción en la base de datos
            int filasAfectadas = pst.executeUpdate();

            // Si la inserción fue exitosa, obtenemos el ID generado automáticamente
            if (filasAfectadas > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            // Muestra mensaje de error si la inserción falla
            System.out.println("Error al insertar persona: " + e.getMessage());
        }
        // Devuelve el ID generado o -1 si no se insertó
        return idGenerado;
    }

    // Obtiene el ID de una persona a partir de su documento de identidad
    public int obtenerID_persona(Persona p) {
        String query = "SELECT id_persona FROM persona WHERE documento = ?";
        int idGenerado = -1;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            // Asignamos el documento para buscar
            pst.setString(1, p.getDocumento_identidad());
            
            // Ejecutamos la consulta y verificamos resultados
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    idGenerado = rs.getInt("id_persona");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID de persona: " + e.getMessage());
        }
        // Retorna el ID encontrado o -1 si no existe
        return idGenerado;
    }

    // Actualiza los datos de una persona existente en la base de datos (sin modificar el documento)
    public boolean actualizarPersona(Persona persona) {
        String query = "UPDATE persona SET nombre = ?, apellido = ?, fecha_nacimiento = ?, telefono = ?, email = ?, direccion = ? " +
                       "WHERE id_persona = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            // Asignamos los nuevos valores para la actualización (sin documento)
            pst.setString(1, persona.getNombre());
            pst.setString(2, persona.getApellido());
            pst.setDate(3, new java.sql.Date(persona.getFecha_nacimiento().getTime()));
            pst.setString(4, persona.getTelefono());
            pst.setString(5, persona.getEmail());
            pst.setString(6, persona.getDireccion());
            pst.setInt(7, persona.getId_persona());

            // Ejecutamos la actualización
            int rowsAffected = pst.executeUpdate();
            
            // Retorna true si al menos una fila fue actualizada
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar persona: " + e.getMessage());
        }
        // Retorna false si hubo error o no se actualizó nada
        return false;
    }

    // Elimina una persona de la base de datos usando su ID
    public boolean eliminarPersona(int idPersona) {
        String query = "DELETE FROM persona WHERE id_persona = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            // Asignamos el ID de la persona a eliminar
            pst.setInt(1, idPersona);

            // Ejecutamos la eliminación
            int rowsAffected = pst.executeUpdate();

            // Retorna true si se eliminó correctamente
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar persona: " + e.getMessage());
        }
        // Retorna false si hubo error
        return false;
    }

    // Método privado reutilizable para obtener una persona por cualquier campo único
    private Persona obtenerPersonaPorCampo(String campo, Object valor) {
        String query = "SELECT * FROM persona WHERE " + campo + " = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setObject(1, valor);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Persona(
                        rs.getInt("id_persona"),
                        rs.getString("documento"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener persona por " + campo + ": " + e.getMessage());
        }
        return null;
    }

    // Obtiene un objeto Persona buscando por su ID
    public Persona obtenerPersonaPorId(int idPersona) {
        return obtenerPersonaPorCampo("id_persona", idPersona);
    }

    // Obtiene una persona usando su documento de identidad
    public Persona obtenerPersonaPorDocumento(String documento) {
        return obtenerPersonaPorCampo("documento", documento);
    }

    // Busca personas cuyo documento comience con el texto dado
    public List<Persona> buscarPersonasPorDocumentoParcial(String documento) throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM persona WHERE documento LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // El símbolo '%' permite buscar cualquier cosa que comience con el texto indicado
            stmt.setString(1, documento + "%");
            ResultSet rs = stmt.executeQuery();

            // Por cada resultado se crea un objeto Persona y se añade a la lista
            while (rs.next()) {
                Persona persona = new Persona();
                persona.setId_persona(rs.getInt("id_persona"));
                persona.setDocumento_identidad(rs.getString("documento"));
                persona.setNombre(rs.getString("nombre"));
                persona.setDireccion(rs.getString("direccion"));
                persona.setEmail(rs.getString("email"));
                personas.add(persona);
            }
        }
        // Retorna la lista con las personas que coinciden con la búsqueda parcial
        return personas;
    }

}
