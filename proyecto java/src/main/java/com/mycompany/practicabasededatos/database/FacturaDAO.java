package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase encargada de gestionar las operaciones de base de datos relacionadas con la entidad Factura
public class FacturaDAO {

    // Inserta una nueva factura en la base de datos y devuelve su ID generado
    public int crearFactura(Factura factura) throws SQLException {
        String sql = "INSERT INTO factura (fecha_emision, metodo_pago, base_imponible, iva, total, id_reserva) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Se asignan los valores a cada parámetro del statement
            stmt.setDate(1, Date.valueOf(factura.getFecha_emision()));
            stmt.setString(2, factura.getMetodo_pago());
            stmt.setDouble(3, factura.getBase_imponible());
            stmt.setDouble(4, factura.getIva());
            stmt.setDouble(5, factura.getTotal());
            stmt.setInt(6, factura.getId_reserva());

            // Ejecuta la inserción
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la factura. Ninguna fila fue afectada.");
            }

            // Obtiene el ID generado automáticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la factura creada.");
                }
            }
        }
    }

    // Recupera todas las facturas de la base de datos y las devuelve en una lista
    public List<Factura> obtenerFacturas() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM factura";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Recorre el resultado y construye objetos Factura
            while (rs.next()) {
                Factura factura = new Factura(
                        rs.getInt("id_factura"),
                        rs.getDate("fecha_emision").toLocalDate(),
                        rs.getString("metodo_pago"),
                        rs.getDouble("base_imponible"),
                        rs.getDouble("iva"),
                        rs.getDouble("total"),
                        rs.getInt("id_reserva")
                );
                facturas.add(factura);
            }
        }
        return facturas;
    }

    // Actualiza una factura existente en la base de datos
    public void actualizarFactura(Factura factura) throws SQLException {
        String sql = "UPDATE factura SET fecha_emision = ?, metodo_pago = ?, base_imponible = ?, iva = ?, total = ?, id_reserva = ? " +
                     "WHERE id_factura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(factura.getFecha_emision()));
            stmt.setString(2, factura.getMetodo_pago());
            stmt.setDouble(3, factura.getBase_imponible());
            stmt.setDouble(4, factura.getIva());
            stmt.setDouble(5, factura.getTotal());
            stmt.setInt(6, factura.getId_reserva());
            stmt.setInt(7, factura.getId_factura());

            stmt.executeUpdate();
        }
    }

    // Elimina una factura de la base de datos según su ID
    public void eliminarFactura(int idFactura) throws SQLException {
        String sql = "DELETE FROM FACTURA WHERE id_factura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFactura);
            stmt.executeUpdate();
        }
    }

    // Cambia el estado de una reserva, útil después de emitir una factura
    public boolean actualizarEstadoReserva(int idReserva, String nuevoEstado) {
        String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // En un entorno real se debería usar logging
            return false;
        }
    }

    // Calcula el total a pagar por una reserva según su tipo (AD o MP)
    public double calcularTotalEstadia(int idReserva) throws SQLException {
        String sql = "SELECT CASE " +
                     "WHEN r.tipo_reserva = 'AD' THEN DATEDIFF(r.fecha_fin, r.fecha_inicio) * h.precio_noche_ad " +
                     "WHEN r.tipo_reserva = 'MP' THEN DATEDIFF(r.fecha_fin, r.fecha_inicio) * h.precio_noche_mp " +
                     "ELSE 0 END AS total_estadia " +
                     "FROM reserva r " +
                     "JOIN habitacion h ON r.id_habitacion = h.id_habitacion " +
                     "WHERE r.id_reserva = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReserva);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_estadia");
                } else {
                    throw new SQLException("No se encontró la reserva con ID: " + idReserva);
                }
            }
        }
    }
}
