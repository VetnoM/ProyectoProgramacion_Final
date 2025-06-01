package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Habitacion;
import com.mycompany.practicabasededatos.modelo.EstadoHabitacion;
import com.mycompany.practicabasededatos.modelo.TipoHabitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase HabitacionDAO encargada de gestionar todas las operaciones
 * de acceso y manipulación de datos para la tabla 'habitacion'
 * en la base de datos. Permite obtener, crear, actualizar y eliminar habitaciones.
 */
public class HabitacionDAO {

    // Busca una habitación en la base de datos por su ID
    public Habitacion obtenerHabitacionPorId(int id) {
        Habitacion habitacion = null;
        String consulta = "SELECT * FROM habitacion WHERE id_habitacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    // Lee todos los campos para construir el objeto Habitacion
                    int id_habitacion = rs.getInt("id_habitacion");
                    String numero_habitacion = rs.getString("numero_habitacion");
                    TipoHabitacion tipo = TipoHabitacion.valueOf(rs.getString("tipo").toUpperCase());
                    int capacidad = rs.getInt("capacidad");
                    EstadoHabitacion estado = EstadoHabitacion.valueOf(rs.getString("estado").toUpperCase());
                    String descripcion = rs.getString("descripcion");
                    double precio_noche_ad = rs.getDouble("precio_noche_ad");
                    double precio_noche_mp = rs.getDouble("precio_noche_mp");

                    habitacion = new Habitacion(id_habitacion, numero_habitacion, tipo, capacidad, estado, descripcion, precio_noche_ad, precio_noche_mp);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener habitación por ID: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Si el valor en BD no coincide con los valores de los enums
            System.out.println("Error: EstadoHabitacion o TipoHabitacion no válido en la base de datos.");
        }

        return habitacion;
    }

    // Obtiene todas las habitaciones almacenadas en la base de datos
    public List<Habitacion> obtenerHabitaciones() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM habitacion";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Construye una lista con todas las habitaciones encontradas
            while (rs.next()) {
                Habitacion habitacion = new Habitacion(
                        rs.getInt("id_habitacion"),
                        rs.getString("numero_habitacion"),
                        TipoHabitacion.valueOf(rs.getString("tipo").toUpperCase()),
                        rs.getInt("capacidad"),
                        EstadoHabitacion.valueOf(rs.getString("estado").toUpperCase()),
                        rs.getString("descripcion"),
                        rs.getDouble("precio_noche_ad"),
                        rs.getDouble("precio_noche_mp")
                );
                habitaciones.add(habitacion);
            }
        }
        return habitaciones;
    }

    // Inserta una nueva habitación en la base de datos y devuelve su ID generado
    public int crearHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "INSERT INTO habitacion (numero_habitacion, tipo, capacidad, estado, descripcion, precio_noche_ad, precio_noche_mp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignación de valores a la consulta
            stmt.setString(1, habitacion.getNumero_habitacion());
            stmt.setString(2, habitacion.getTipo().name());
            stmt.setInt(3, habitacion.getCapacidad());
            stmt.setString(4, habitacion.getEstado().name());
            stmt.setString(5, habitacion.getDescripcion());
            stmt.setDouble(6, habitacion.getPrecio_noche_ad());
            stmt.setDouble(7, habitacion.getPrecio_noche_mp());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la habitación. Ninguna fila fue afectada.");
            }

            // Obtener el ID generado automáticamente tras la inserción
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo crear la habitación. No se obtuvo el ID.");
                }
            }
        }
    }

    // Actualiza los datos de una habitación existente
    public void actualizarHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "UPDATE habitacion SET numero_habitacion = ?, tipo = ?, capacidad = ?, estado = ?, descripcion = ?, precio_noche_ad = ?, precio_noche_mp = ? " +
                     "WHERE id_habitacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignar nuevos valores para actualizar
            stmt.setString(1, habitacion.getNumero_habitacion());
            stmt.setString(2, habitacion.getTipo().name());
            stmt.setInt(3, habitacion.getCapacidad());
            stmt.setString(4, habitacion.getEstado().name());
            stmt.setString(5, habitacion.getDescripcion());
            stmt.setDouble(6, habitacion.getPrecio_noche_ad());
            stmt.setDouble(7, habitacion.getPrecio_noche_mp());
            stmt.setInt(8, habitacion.getId_habitacion());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo actualizar la habitación. Ninguna fila fue afectada.");
            }
        }
    }

    // Elimina una habitación por su ID
    public void eliminarHabitacion(int idHabitacion) throws SQLException {
        String sql = "DELETE FROM habitacion WHERE id_habitacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idHabitacion);
            stmt.executeUpdate();
        }
    }
}
