package com.mycompany.practicabasededatos.database;

import com.mycompany.practicabasededatos.modelo.Empleado;
import com.mycompany.practicabasededatos.modelo.EstadoTarea;
import com.mycompany.practicabasededatos.modelo.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TareaDAO {

    // Crear una nueva tarea y devolver su ID
    public int crearTarea(String descripcion, Date fechaCreacion, Date fechaEjecucion, String estado)
            throws SQLException {
        // SQL para insertar una nueva tarea en la base de datos
        String sql = "INSERT INTO tarea (descripcion, fecha_creacion, fecha_ejecucion, estado) VALUES (?, ?, ?, ?)";

        // Establecer conexión y preparar la declaración
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignar valores a los parámetros de la consulta
            stmt.setString(1, descripcion);
            stmt.setDate(2, fechaCreacion);

            // Manejar el caso en que la fecha de ejecución sea nula
            if (fechaEjecucion != null) {
                stmt.setDate(3, fechaEjecucion);
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, estado);

            // Ejecutar la actualización y verificar si se afectaron filas
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }

            // Obtener la clave generada para la nueva tarea
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
        }
    }

    // Método para obtener tareas asignadas a un empleado
    public List<Tarea> obtenerTareasPorEmpleado(String documentoEmpleado) {
        List<Tarea> tareas = new ArrayList<>();

        // Consulta SQL para obtener tareas asociadas a un empleado específico
        String query = "SELECT t.id_tarea, t.descripcion, t.estado, t.fecha_creacion, t.fecha_ejecucion " +
                "FROM tarea t " +
                "JOIN empleado e ON t.id_empleado = e.id_empleado " +
                "JOIN persona p ON e.id_persona = p.id_persona " +
                "WHERE p.documento = ?";

        // Establecer conexión y preparar la declaración
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            // Asignar el documento del empleado a la consulta
            ps.setString(1, documentoEmpleado);

            // Ejecutar la consulta y procesar los resultados
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idTarea = rs.getInt("id_tarea");
                    String descripcion = rs.getString("descripcion");
                    String estadoStr = rs.getString("estado");
                    Date fechaCreacion = rs.getDate("fecha_creacion");
                    Date fechaEjecucion = rs.getDate("fecha_ejecucion");

                    // Convertir el estado de la tarea a un objeto EstadoTarea
                    EstadoTarea estadoTarea = EstadoTarea.valueOf(estadoStr.toUpperCase());

                    // Crear una nueva tarea y agregarla a la lista
                    Tarea tarea = new Tarea(idTarea, descripcion, estadoTarea, fechaCreacion, fechaEjecucion);
                    tareas.add(tarea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tareas; // Retornar la lista de tareas
    }

    // Método para obtener empleados con tareas asignadas
    public List<Empleado> obtenerEmpleadosConTareas() throws SQLException {
        List<Empleado> empleadosConTareas = new ArrayList<>();
        // Consulta SQL para obtener empleados que tienen tareas asignadas
        String sql = "SELECT DISTINCT e.id_empleado, p.nombre, p.apellido FROM empleado e " +
                     "JOIN persona p ON e.id_persona = p.id_persona " +
                     "JOIN realizar_tarea rt ON e.id_empleado = rt.id_empleado";

        // Establecer conexión y preparar la declaración
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Procesar los resultados y crear objetos Empleado
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId_empleado(rs.getInt("id_empleado"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellido(rs.getString("apellido"));
                empleadosConTareas.add(empleado);
            }
        }
        return empleadosConTareas; // Retornar la lista de empleados
    }

    // Método para asignar una tarea a un empleado
    public boolean asignarTareaAEmpleado(Tarea tarea, Empleado empleado) {
        String sql = "INSERT INTO realizar_tarea (id_empleado, id_tarea) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Asignar los valores de empleado y tarea a la consulta
            stmt.setInt(1, empleado.getId_empleado());
            stmt.setInt(2, tarea.getId_tarea());
            stmt.executeUpdate(); // Ejecutar la inserción
            return true; // Retornar verdadero si la asignación fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retornar falso en caso de error
        }
    }

    // Método para verificar si un empleado ya tiene asignada una tarea
    public boolean tieneTarea(int idTarea, int idEmpleado) {
        String sql = "SELECT COUNT(*) FROM realizar_tarea WHERE id_tarea = ? AND id_empleado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Asignar los parámetros a la consulta
            stmt.setInt(1, idTarea);
            stmt.setInt(2, idEmpleado);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retornar verdadero si hay tareas asignadas
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Retornar falso si no hay tareas asignadas
    }

    // Método para obtener todas las tareas
    public List<Tarea> obtenerTareas() {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            // Procesar los resultados y crear objetos Tarea
            while (rs.next()) {
                Tarea tarea = new Tarea(
                        rs.getInt("id_tarea"),
                        rs.getString("descripcion"),
                        EstadoTarea.valueOf(rs.getString("estado")),
                        rs.getDate("fecha_creacion"),
                        rs.getDate("fecha_ejecucion"));
                tareas.add(tarea); // Agregar tarea a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas; // Retornar la lista de tareas
    }

    // Método para obtener tareas asignadas a un empleado específico
    public List<Tarea> obtenerTareasAsignadasAEmpleado(int idEmpleado) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.id_tarea, t.descripcion, t.fecha_creacion, t.fecha_ejecucion, " +
                     "rt.estado AS estado_individual " +
                     "FROM tarea t " +
                     "JOIN realizar_tarea rt ON t.id_tarea = rt.id_tarea " +
                     "WHERE rt.id_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEmpleado); // Asignar el ID del empleado a la consulta
            ResultSet rs = stmt.executeQuery();

            // Procesar los resultados y crear objetos Tarea
            while (rs.next()) {
                Tarea tarea = new Tarea();
                tarea.setId_tarea(rs.getInt("id_tarea"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setFecha_creacion(rs.getDate("fecha_creacion"));
                tarea.setFecha_ejecucion(rs.getDate("fecha_ejecucion"));

                // Usar el estado individual de la asignación
                String estadoStr = rs.getString("estado_individual");
                tarea.setEstadoTarea(EstadoTarea.valueOf(estadoStr.toUpperCase()));

                tareas.add(tarea); // Agregar tarea a la lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tareas; // Retornar la lista de tareas
    }

    // Método para obtener tareas pendientes
    public LinkedList<Tarea> obtenerTareasPendientes() {
        LinkedList<Tarea> tareasPendientes = new LinkedList<>();
        String query = "SELECT * FROM tarea WHERE estado = 'PENDIENTE'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            // Procesar los resultados y crear objetos Tarea
            while (rs.next()) {
                Tarea tarea = new Tarea(
                        rs.getInt("id_tarea"),
                        rs.getString("descripcion"),
                        EstadoTarea.PENDIENTE,
                        rs.getDate("fecha_creacion"),
                        rs.getDate("fecha_ejecucion"));
                tareasPendientes.add(tarea); // Agregar tarea a la lista de pendientes
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tareasPendientes; // Retornar la lista de tareas pendientes
    }

    // Método para obtener tareas en proceso
    public LinkedList<Tarea> obtenerTareasEnProceso() {
        LinkedList<Tarea> tareasEnProceso = new LinkedList<>();
        String query = "SELECT * FROM tarea WHERE estado = 'EN_PROCESO'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            // Procesar los resultados y crear objetos Tarea
            while (rs.next()) {
                Tarea tarea = new Tarea(
                        rs.getInt("id_tarea"),
                        rs.getString("descripcion"),
                        EstadoTarea.EN_PROCESO,
                        rs.getDate("fecha_creacion"),
                        rs.getDate("fecha_ejecucion"));
                tareasEnProceso.add(tarea); // Agregar tarea a la lista de en proceso
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tareasEnProceso; // Retornar la lista de tareas en proceso
    }

    // Método para asignar tarea a empleado insertando en realizar_tarea
    public boolean asignarTareaAEmpleado(int idTarea, int idEmpleado) {
        String sql = "INSERT INTO realizar_tarea (id_empleado, id_tarea) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado); // Asignar el ID del empleado
            ps.setInt(2, idTarea); // Asignar el ID de la tarea
            return ps.executeUpdate() > 0; // Retornar verdadero si la inserción fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retornar falso en caso de error
        }
    }

    // Método auxiliar para obtener id_empleado desde documento
    public int obtenerIdEmpleadoPorDocumento(String documentoEmpleado) throws SQLException {
        String sql = "SELECT e.id_empleado FROM empleado e " +
                "JOIN persona p ON e.id_persona = p.id_persona " +
                "WHERE p.documento = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documentoEmpleado); // Asignar el documento a la consulta
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_empleado"); // Retornar el ID del empleado
                } else {
                    throw new SQLException("Empleado no encontrado con documento: " + documentoEmpleado);
                }
            }
        }
    }

    // Método para marcar tarea de un empleado como completada
    public boolean marcarTareaEmpleadoComoCompletada(int idEmpleado, int idTarea) {
        String updateEstadoIndividual = "UPDATE realizar_tarea SET estado = 'COMPLETADA' " +
                                        "WHERE id_empleado = ? AND id_tarea = ?";
        
        String checkPendientes = "SELECT COUNT(*) FROM realizar_tarea " +
                                 "WHERE id_tarea = ? AND estado != 'COMPLETADA'";

        String updateEstadoGlobal = "UPDATE tarea SET estado = 'COMPLETADA', fecha_ejecucion = CURRENT_DATE " +
                                    "WHERE id_tarea = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Desactivar el autocommit para manejar transacciones

            // 1. Marcar tarea como completada por el empleado
            try (PreparedStatement stmt1 = conn.prepareStatement(updateEstadoIndividual)) {
                stmt1.setInt(1, idEmpleado);
                stmt1.setInt(2, idTarea);
                stmt1.executeUpdate();
            }

            // 2. Verificar si todos la completaron
            boolean todosCompletaron = false;
            try (PreparedStatement stmt2 = conn.prepareStatement(checkPendientes)) {
                stmt2.setInt(1, idTarea);
                ResultSet rs = stmt2.executeQuery();
                if (rs.next()) {
                    int pendientes = rs.getInt(1);
                    todosCompletaron = pendientes == 0; // Verificar si no hay tareas pendientes
                }
            }

            // 3. Si todos completaron, actualizar el estado global
            if (todosCompletaron) {
                try (PreparedStatement stmt3 = conn.prepareStatement(updateEstadoGlobal)) {
                    stmt3.setInt(1, idTarea);
                    stmt3.executeUpdate();
                }
            }

            conn.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retornar falso en caso de error
        }
    }

    // Método para verificar si un empleado ya tiene asignada una tarea
    public boolean yaTieneAsingado(int idTarea, int idEmpleado) {
        String sql = "SELECT COUNT(*) FROM realizar_tarea WHERE id_tarea = ? AND id_empleado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTarea);
            stmt.setInt(2, idEmpleado);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retornar verdadero si ya tiene asignada la tarea
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejar el error
        }
        return false; // Retornar falso si no tiene asignada la tarea
    }

    // Método para actualizar una tarea existente
    public void actualizarTarea(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET descripcion = ?, estado = ?, fecha_creacion = ?, fecha_ejecucion = ? WHERE id_tarea = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignar los valores de la tarea a la consulta
            stmt.setString(1, tarea.getDescripcion());
            stmt.setString(2, tarea.getEstadoTarea().toString());
            stmt.setDate(3, new java.sql.Date(tarea.getFecha_creacion().getTime()));
            stmt.setDate(4, tarea.getFecha_ejecucion() != null ? new java.sql.Date(tarea.getFecha_ejecucion().getTime())
                    : null);
            stmt.setInt(5, tarea.getId_tarea());

            stmt.executeUpdate(); // Ejecutar la actualización
        }
    }

    // Método para eliminar una tarea por su ID
    public void eliminarTarea(int idTarea) throws SQLException {
        String sql = "DELETE FROM tarea WHERE id_tarea = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTarea); // Asignar el ID de la tarea a eliminar
            stmt.executeUpdate(); // Ejecutar la eliminación
        }
    }
}
