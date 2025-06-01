package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Reserva;
import com.mycompany.practicabasededatos.modelo.TipoReserva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase DAO para gestionar las operaciones CRUD y consultas
 * relacionadas con las reservas en la base de datos.
 * 
 * Esta clase permite obtener reservas, crear, actualizar y eliminar reservas,
 * así como consultar información relacionada con clientes, habitaciones,
 * verificar solapamientos de fechas y actualizar estados tanto de reservas
 * como de habitaciones.
 */
public class ReservaDAO {

    /**
     * Obtiene todas las reservas de la base de datos con información de habitación.
     */
    public ObservableList<Reserva> obtenerReservas() {
        ObservableList<Reserva> reservas = FXCollections.observableArrayList();
        String sql = "SELECT r.id_reserva, r.tipo_reserva, r.fecha_inicio, r.fecha_fin, r.fecha_reserva, " +
                     "r.precio_total, r.id_cliente, r.id_habitacion, r.estado, h.numero_habitacion " +
                     "FROM reserva r " +
                     "LEFT JOIN habitacion h ON r.id_habitacion = h.id_habitacion";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reserva reserva = new Reserva(
                    rs.getInt("id_reserva"),
                    TipoReserva.valueOf(rs.getString("tipo_reserva")),
                    rs.getDate("fecha_inicio").toLocalDate(),
                    rs.getDate("fecha_fin").toLocalDate(),
                    rs.getDate("fecha_reserva").toLocalDate(),
                    rs.getDouble("precio_total"),
                    rs.getInt("id_cliente"),
                    rs.getInt("id_habitacion"),
                    rs.getString("estado"),
                    rs.getString("numero_habitacion") // Obtener el número de habitación
                );

                reservas.add(reserva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    /**
     * Obtiene una lista de clientes con su documento y nombre completo.
     */
    public List<String> obtenerClientesConNombre() {
        List<String> personas = new ArrayList<>();
        String sql = "SELECT p.documento, p.nombre, p.apellido " +
                     "FROM persona p " +
                     "JOIN cliente c ON p.id_persona = c.id_persona";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String documento = rs.getString("documento");
                String nombre = rs.getString("nombre") + " " + rs.getString("apellido");
                personas.add(documento + " - " + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personas;
    }

    /**
     * Crea una nueva reserva en la base de datos.
     */
    public int crearReserva(Reserva reserva) throws SQLException {
        int nuevoId = obtenerProximoIdReserva(); // Obtener el nuevo ID manualmente
        reserva.setId_reserva(nuevoId); // Asignar el ID a la reserva
        String sql = "INSERT INTO reserva (id_reserva, tipo_reserva, fecha_inicio, fecha_fin, fecha_reserva, precio_total, id_cliente, id_habitacion, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reserva.getId_reserva());
            stmt.setString(2, reserva.getTipo_reserva().name());
            stmt.setDate(3, java.sql.Date.valueOf(reserva.getFecha_inicio()));
            stmt.setDate(4, java.sql.Date.valueOf(reserva.getFecha_fin()));
            stmt.setDate(5, java.sql.Date.valueOf(reserva.getFecha_reserva()));
            stmt.setDouble(6, reserva.getPrecio_total());
            stmt.setInt(7, reserva.getId_cliente());
            stmt.setInt(8, reserva.getId_habitacion()); // Asignar el id_habitacion
            stmt.setString(9, "Pendiente de facturar"); // Estado inicial

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la reserva. Ninguna fila fue afectada.");
            }

            return reserva.getId_reserva();
        }
    }

    /**
     * Actualiza los datos de una reserva existente.
     */
    public void actualizarReserva(Reserva reserva) throws SQLException {
        String sql = "UPDATE reserva SET tipo_reserva = ?, fecha_inicio = ?, fecha_fin = ?, fecha_reserva = ?, precio_total = ?, id_cliente = ?, id_habitacion = ?, estado = ? WHERE id_reserva = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reserva.getTipo_reserva().toString());
            stmt.setDate(2, java.sql.Date.valueOf(reserva.getFecha_inicio()));
            stmt.setDate(3, java.sql.Date.valueOf(reserva.getFecha_fin()));
            stmt.setDate(4, java.sql.Date.valueOf(reserva.getFecha_reserva()));
            stmt.setDouble(5, reserva.getPrecio_total());
            stmt.setInt(6, reserva.getId_cliente());
            stmt.setInt(7, reserva.getId_habitacion());
            stmt.setString(8, reserva.getEstado()); // Actualizar el estado
            stmt.setInt(9, reserva.getId_reserva());

            stmt.executeUpdate();
        }
    }

    /**
     * Elimina una reserva según su ID.
     */
    public void eliminarReserva(int idReserva) throws SQLException {
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.executeUpdate();
        }
    }

    /**
     * Actualiza el estado de una habitación según su ID.
     */
    public void actualizarEstadoHabitacion(int idHabitacion, String nuevoEstado) throws SQLException {
        String sql = "UPDATE habitacion SET estado = ? WHERE id_habitacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idHabitacion);

            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene el ID del cliente a partir de su documento.
     */
    public int obtenerIdClientePorDocumento(String documento) {
        int idCliente = -1; // Valor por defecto si no se encuentra

        String sql = "SELECT c.id_cliente " +
                     "FROM cliente c " +
                     "JOIN persona p ON c.id_persona = p.id_persona " +
                     "WHERE p.documento = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idCliente = rs.getInt("id_cliente");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idCliente;
    }

    /**
     * Devuelve el tipo de cliente según id_cliente ("Cliente", "Empleado", "Ambos", "Otros").
     */
    public String obtenerTipoClientePorId(int idCliente) {
        String tipo = "Otros";
        String sqlCliente = "SELECT 1 FROM cliente WHERE id_cliente = ?";
        String sqlEmpleado = "SELECT 1 FROM empleado WHERE id_persona = (SELECT id_persona FROM cliente WHERE id_cliente = ?)";

        try {
            // Verificamos si es cliente
            try (PreparedStatement psCliente = DatabaseConnection.getConnection().prepareStatement(sqlCliente)) {
                psCliente.setInt(1, idCliente);
                ResultSet rsCliente = psCliente.executeQuery();
                boolean esCliente = rsCliente.next();
                rsCliente.close();

                // Verificamos si es empleado también
                try (PreparedStatement psEmpleado = DatabaseConnection.getConnection().prepareStatement(sqlEmpleado)) {
                    psEmpleado.setInt(1, idCliente);
                    ResultSet rsEmpleado = psEmpleado.executeQuery();
                    boolean esEmpleado = rsEmpleado.next();
                    rsEmpleado.close();

                    if (esCliente && esEmpleado) {
                        tipo = "Ambos";
                    } else if (esCliente) {
                        tipo = "Cliente";
                    } else if (esEmpleado) {
                        tipo = "Empleado";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipo;
    }

    /**
     * Obtiene el ID de la habitación a partir de su número.
     */
    public int obtenerIdHabitacionPorNumero(String numeroHabitacion) {
        int idHabitacion = -1; // Valor por defecto si no se encuentra

        String sql = "SELECT id_habitacion FROM habitacion WHERE numero_habitacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroHabitacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idHabitacion = rs.getInt("id_habitacion");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idHabitacion;
    }

    /**
     * Verifica si existe solapamiento de fechas para una reserva en una habitación dada.
     */
    public boolean existeSolapamientoReserva(int idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_habitacion = ? AND " +
                     "((? BETWEEN fecha_inicio AND fecha_fin) OR " +
                     "(? BETWEEN fecha_inicio AND fecha_fin) OR " +
                     "(fecha_inicio BETWEEN ? AND ?) OR " +
                     "(fecha_fin BETWEEN ? AND ?))";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHabitacion);
            stmt.setDate(2, java.sql.Date.valueOf(fechaInicio));
            stmt.setDate(3, java.sql.Date.valueOf(fechaFin));
            stmt.setDate(4, java.sql.Date.valueOf(fechaInicio));
            stmt.setDate(5, java.sql.Date.valueOf(fechaFin));
            stmt.setDate(6, java.sql.Date.valueOf(fechaInicio));
            stmt.setDate(7, java.sql.Date.valueOf(fechaFin));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene el próximo ID disponible para la tabla reserva.
     */
    public int obtenerProximoIdReserva() throws SQLException {
        String sql = "SELECT MAX(id_reserva) AS max_id FROM reserva";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1; // Incrementa el valor máximo en 1
            } else {
                return 1; // Si no hay registros, comienza desde 1
            }
        }
    }

    /**
     * Obtiene la última fecha de fin de reserva de una habitación.
     */
    public LocalDate obtenerUltimaFechaFinReserva(int idHabitacion) throws SQLException {
        String sql = "SELECT MAX(fecha_fin) AS ultima_fecha_fin FROM reserva WHERE id_habitacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDate("ultima_fecha_fin") != null) {
                return rs.getDate("ultima_fecha_fin").toLocalDate();
            }
        }
        return null;
    }

    /**
     * Obtiene todos los días ocupados de una habitación según sus reservas.
     */
    public List<LocalDate> obtenerDiasOcupados(int idHabitacion) throws SQLException {
        List<LocalDate> diasOcupados = new ArrayList<>();
        List<LocalDate[]> rangos = obtenerRangosFechasReservadas(idHabitacion);
        for (LocalDate[] rango : rangos) {
            LocalDate inicio = rango[0];
            LocalDate fin = rango[1];
            long dias = ChronoUnit.DAYS.between(inicio, fin);
            for (int i = 0; i <= dias; i++) {
                diasOcupados.add(inicio.plusDays(i));
            }
        }
        return diasOcupados;
    }

    /**
     * Obtiene una lista de pares de fechas [inicio, fin] de reservas de una habitación.
     */
    public List<LocalDate[]> obtenerRangosFechasReservadas(int idHabitacion) throws SQLException {
        List<LocalDate[]> rangos = new ArrayList<>();
        String sql = "SELECT fecha_inicio, fecha_fin FROM reserva WHERE id_habitacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rangos.add(new LocalDate[] {
                    rs.getDate("fecha_inicio").toLocalDate(),
                    rs.getDate("fecha_fin").toLocalDate()
                });
            }
        }
        return rangos;
    }


/**
 * Obtiene una lista observable con las habitaciones, mostrando su número y la palabra "Habitación".
 */
public ObservableList<String> obtenerHabitacionesConEstado() throws SQLException {
    ObservableList<String> resultado = FXCollections.observableArrayList();

    String sql = "SELECT numero_habitacion FROM habitacion";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String numero = rs.getString("numero_habitacion");
            resultado.add(numero + " - Habitación");
        }
    }

    return resultado;
}

/**
 * Actualiza el estado de una reserva según su ID.
 */
public boolean actualizarEstadoReserva(int idReserva, String nuevoEstado) {
    String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, nuevoEstado);
        stmt.setInt(2, idReserva);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean eliminarReservaSiEstaFacturada(int idReserva) {
    String sql = 
        "DELETE FROM reserva "+ 
        "WHERE id_reserva = ? "+
        "AND EXISTS ( "+
            "SELECT 1 FROM factura WHERE factura.id_reserva = reserva.id_reserva"+
        ");"
        ;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idReserva);
        int filasAfectadas = stmt.executeUpdate();
        return filasAfectadas > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}



}