package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Cliente;
import com.mycompany.practicabasededatos.modelo.TipoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) encargada de gestionar todas las operaciones
 * relacionadas con la tabla 'cliente' de la base de datos del hotel.
 * Incluye métodos para insertar, consultar, actualizar y eliminar clientes.
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo cliente en la tabla 'cliente'.
     * Este método asume que la persona ya ha sido insertada previamente en la tabla 'persona'.
     * 
     * @param cliente Objeto Cliente con los datos necesarios para la inserción.
     */
    public void insertarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (id_persona, tipo_cliente, tarjeta_credito, fecha_registro) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId_persona());
            stmt.setString(2, cliente.getTipoCliente().toString());
            stmt.setString(3, cliente.getTarjeta_credito());
            stmt.setDate(4, new java.sql.Date(cliente.getFecha_registro().getTime()));
            stmt.executeUpdate();
        }
    }

    /**
     * Recupera todos los clientes registrados en la base de datos.
     * Además de los datos específicos de cliente, también carga los datos personales heredados de la tabla 'persona'.
     * 
     * @return Lista de objetos Cliente con toda la información relevante.
     */
    public List<Cliente> obtenerClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        // Hacemos JOIN con persona para obtener todos los datos
        String sql = "SELECT c.*, p.documento, p.nombre, p.apellido, p.fecha_nacimiento, p.telefono, p.email, p.direccion " +
                     "FROM cliente c " +
                     "JOIN persona p ON c.id_persona = p.id_persona";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();

                // Datos específicos del cliente
                cliente.setId_cliente(rs.getInt("id_cliente"));
                cliente.setFecha_registro(rs.getDate("fecha_registro"));
                cliente.setTarjeta_credito(rs.getString("tarjeta_credito"));
                cliente.setTipoCliente(TipoCliente.valueOf(rs.getString("tipo_cliente")));
                cliente.setId_persona(rs.getInt("id_persona"));

                // Datos heredados de la tabla 'persona'
                cliente.setDocumento_identidad(rs.getString("documento"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setDireccion(rs.getString("direccion"));

                clientes.add(cliente);
            }
        }
        return clientes;
    }

    /**
     * Obtiene el ID de un cliente a partir de su DNI (documento de identidad).
     * Útil para validar si una persona ya es cliente del hotel.
     *
     * @param dni Documento identificativo del cliente (ej. DNI, pasaporte).
     * @return ID del cliente si existe, o -1 si no se encuentra.
     */
    public int obtenerIdClientePorDni(String dni) throws SQLException {
        String sql = "SELECT id_cliente FROM cliente c JOIN persona p ON c.id_persona = p.id_persona WHERE p.dni = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_cliente") : -1;
            }
        }
    }

    /**
     * Actualiza los datos específicos de un cliente ya existente.
     * Este método no modifica los datos personales (tabla 'persona'), solo los de la tabla 'cliente'.
     * 
     * @param cliente Objeto Cliente con los datos actualizados.
     */
    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET tipo_cliente = ?, tarjeta_credito = ?, fecha_registro = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getTipoCliente().toString());
            stmt.setString(2, cliente.getTarjeta_credito());
            stmt.setDate(3, new java.sql.Date(cliente.getFecha_registro().getTime()));
            stmt.setInt(4, cliente.getId_cliente());
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un cliente de la base de datos, dado su ID.
     * 
     * @param idCliente Identificador del cliente a eliminar.
     */
    public void eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }

    /**
     * Recupera un cliente a partir del ID de su persona asociada.
     * Carga también los datos personales accediendo al DAO de Persona.
     * 
     * @param idPersona Identificador de la persona asociada.
     * @return Objeto Cliente completo o null si no se encuentra.
     */
    public Cliente obtenerClientePorIdPersona(int idPersona) {
        String sql = "SELECT c.*, p.documento, p.nombre, p.apellido, p.fecha_nacimiento, p.telefono, p.email, p.direccion " +
                     "FROM cliente c " +
                     "JOIN persona p ON c.id_persona = p.id_persona " +
                     "WHERE c.id_persona = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPersona);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Se construye el objeto Cliente combinando los datos de persona y cliente
                TipoCliente tipoCliente = TipoCliente.valueOf(rs.getString("tipo_cliente").toUpperCase());
                Cliente cliente = new Cliente(
                    rs.getInt("id_persona"),
                    rs.getString("documento"),
                    rs.getString("direccion"),
                    rs.getString("email"),
                    tipoCliente
                );
                cliente.setId_cliente(rs.getInt("id_cliente"));
                cliente.setFecha_registro(rs.getDate("fecha_registro"));
                cliente.setTarjeta_credito(rs.getString("tarjeta_credito"));
                // Puedes añadir más setters si necesitas más datos
                return cliente;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // En producción sería recomendable registrar este error con un logger
        }
        return null;
    }
}
