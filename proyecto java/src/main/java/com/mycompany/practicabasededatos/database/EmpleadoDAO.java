package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Empleado;
import com.mycompany.practicabasededatos.modelo.EstadoLaboral;
import com.mycompany.practicabasededatos.modelo.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) encargada de manejar todas las operaciones relacionadas
 * con la tabla 'empleado' en la base de datos.
 */
public class EmpleadoDAO {

    /**
     * Inserta un nuevo empleado en la base de datos.
     * @param idPersona ID de la persona asociada al empleado.
     * @param empleado Objeto Empleado con los datos a insertar.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public void insertarEmpleado(int idPersona, Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleado (id_persona, lugar_trabajo, salario, estado_laboral, fecha_contratacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            stmt.setString(2, empleado.getLugar_trabajo());
            stmt.setDouble(3, empleado.getSalario_bruto());
            stmt.setString(4, empleado.getEstadolaboral().name());
            stmt.setDate(5, new java.sql.Date(empleado.getFecha_contratacion().getTime()));

            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene una lista con todos los empleados junto con los datos personales asociados.
     * @return Lista de objetos Empleado.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    public List<Empleado> obtenerEmpleados() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();

        String sql = 
           "SELECT e.id_empleado, e.lugar_trabajo, e.salario, e.estado_laboral, e.fecha_contratacion, "+
                  "p.id_persona, p.documento, p.nombre, p.apellido, p.fecha_nacimiento, p.telefono, p.email, p.direccion "+
           "FROM empleado e "+
           "JOIN persona p ON e.id_persona = p.id_persona";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId_empleado(rs.getInt("id_empleado"));
                e.setLugar_trabajo(rs.getString("lugar_trabajo"));
                e.setSalario_bruto(rs.getDouble("salario"));
                e.setEstadolaboral(EstadoLaboral.valueOf(rs.getString("estado_laboral")));
                e.setFecha_contratacion(rs.getDate("fecha_contratacion"));

                // Datos heredados de la tabla persona
                e.setId_persona(rs.getInt("id_persona"));
                e.setDocumento_identidad(rs.getString("documento"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                e.setTelefono(rs.getString("telefono"));
                e.setEmail(rs.getString("email"));
                e.setDireccion(rs.getString("direccion"));

                empleados.add(e);
            }
        }

        return empleados;
    }

    /**
     * Devuelve el ID de un empleado a partir del documento de identidad.
     * @param conn Conexión ya establecida a la base de datos.
     * @param documento Documento de identidad de la persona.
     * @return ID del empleado si se encuentra, -1 si no existe.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public int obtenerIdEmpleadoPorDocumento(Connection conn, String documento) throws SQLException {
        // ⚠️ ERROR: Nombres de tabla incorrectos (deberían ser 'empleado' y 'persona')
        String query = "SELECT empleado.id_empleado FROM empleado e " +
                       "JOIN personas p ON e.idPersona = p.idPersona " +
                       "WHERE p.documento = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_empleado");
            }
        }
        return -1;
    }

    /**
     * Actualiza los datos de un empleado en la base de datos.
     * @param empleado Objeto Empleado con los datos modificados.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public void actualizarEmpleado(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleado SET lugar_trabajo = ?, salario = ?, estado_laboral = ?, fecha_contratacion = ? WHERE id_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empleado.getLugar_trabajo());
            stmt.setDouble(2, empleado.getSalario_bruto());
            stmt.setString(3, empleado.getEstadolaboral().name());
            stmt.setDate(4, new java.sql.Date(empleado.getFecha_contratacion().getTime()));
            stmt.setInt(5, empleado.getId_empleado());

            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un empleado de la base de datos por su ID.
     * @param idEmpleado ID del empleado a eliminar.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public void eliminarEmpleado(int idEmpleado) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmpleado);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene un empleado a partir de su ID de persona.
     * @param idPersona ID de la persona asociada.
     * @return Objeto Empleado si se encuentra, null si no existe.
     */
    public Empleado obtenerEmpleadoPorIdPersona(int idPersona) {
        String sql = "SELECT e.*, p.documento, p.nombre, p.apellido, p.fecha_nacimiento, p.telefono, p.email, p.direccion " +
                     "FROM empleado e " +
                     "JOIN persona p ON e.id_persona = p.id_persona " +
                     "WHERE e.id_persona = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                EstadoLaboral estadoLaboral = EstadoLaboral.valueOf(rs.getString("estado_laboral").toUpperCase());
                Empleado empleado = new Empleado();
                empleado.setId_empleado(rs.getInt("id_empleado"));
                empleado.setId_persona(rs.getInt("id_persona"));
                empleado.setDocumento_identidad(rs.getString("documento"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellido(rs.getString("apellido"));
                empleado.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                empleado.setTelefono(rs.getString("telefono"));
                empleado.setEmail(rs.getString("email"));
                empleado.setDireccion(rs.getString("direccion"));
                empleado.setSalario_bruto(rs.getDouble("salario"));
                empleado.setLugar_trabajo(rs.getString("lugar_trabajo"));
                empleado.setEstadolaboral(estadoLaboral);
                empleado.setFecha_contratacion(rs.getDate("fecha_contratacion"));
                return empleado;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
