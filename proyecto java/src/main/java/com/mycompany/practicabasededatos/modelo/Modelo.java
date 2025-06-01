package com.mycompany.practicabasededatos.modelo;

// Importa las clases necesarias para manejar la conexión a la base de datos y las clases del modelo
import com.mycompany.practicabasededatos.database.ClienteDAO;
import com.mycompany.practicabasededatos.database.DatabaseConnection;
import com.mycompany.practicabasededatos.database.EmpleadoDAO;
import com.mycompany.practicabasededatos.database.HabitacionDAO;
import com.mycompany.practicabasededatos.database.PersonaDAO;
import com.mycompany.practicabasededatos.database.ReservaDAO;
import com.mycompany.practicabasededatos.database.TareaDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Clase Modelo que centraliza la lógica de negocio y acceso a datos de la aplicación.
 * Gestiona clientes, empleados, personas, tareas, habitaciones y reservas.
 */
public class Modelo {

    // Logger para registrar mensajes de error y depuración
    private static final Logger LOGGER = Logger.getLogger(Modelo.class.getName());

    // Instancias de los DAOs para interactuar con la base de datos
    private PersonaDAO personaDAO = new PersonaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private TareaDAO tareaDAO = new TareaDAO();
    private HabitacionDAO habitacionDAO = new HabitacionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    // ===================== CLIENTES =====================

    /**
     * Inserta un cliente en la base de datos.
     * @param idPersona ID de la persona asociada.
     * @param cliente Objeto Cliente a insertar.
     */
    public void insertarCliente(int idPersona, Cliente cliente) {
        try {
            if (idPersona != -1) {
                cliente.setId_persona(idPersona);
                clienteDAO.insertarCliente(cliente);
            } else {
                LOGGER.log(Level.SEVERE, "Error al insertar la persona para el cliente");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al enviar los datos al clienteDAO", ex);
        }
    }

    /**
     * Obtiene la lista de clientes como ObservableList.
     */
    public ObservableList<Cliente> obtenerClientes() {
        try {
            List<Cliente> listaClientes = clienteDAO.obtenerClientes();
            return FXCollections.observableArrayList(listaClientes);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los clientes", e);
        }
        return FXCollections.observableArrayList();
    }

    /**
     * Obtiene el ID de un cliente a partir de su DNI.
     */
    public int obtenerIdClientePorDni(String dni) {
        try {
            return clienteDAO.obtenerIdClientePorDni(dni);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el ID del cliente por DNI", e);
        }
        return -1;
    }

    // ===================== EMPLEADOS =====================

    /**
     * Inserta un empleado en la base de datos.
     * @param idPersona ID de la persona asociada.
     * @param empleado Objeto Empleado a insertar.
     */
    public void insertarEmpleado(int idPersona, Empleado empleado) {
        try {
            if (idPersona != -1) {
                empleadoDAO.insertarEmpleado(idPersona, empleado);
            } else {
                LOGGER.log(Level.SEVERE, "Error al insertar la persona para el empleado");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al enviar los datos al empleadoDAO", ex);
        }
    }

    /**
     * Obtiene la lista de empleados.
     */
    public List<Empleado> obtenerEmpleados() {
        try {
            return empleadoDAO.obtenerEmpleados();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los empleados", e);
            return new ArrayList<>();
        }
    }

    // ===================== PERSONAS =====================

    /**
     * Inserta una persona en la base de datos.
     */
    public int insertarPersona(Persona persona) {
        return personaDAO.insertarPersona(persona);
    }

    /**
     * Obtiene una lista observable de personas únicas (clientes o empleados).
     */
    public ObservableList<String> obtenerPersonasUnicas() {
        ObservableList<String> personas = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT p.documento_identidad, p.nombre, p.apellido, " +
                "CASE WHEN c.id_cliente IS NOT NULL THEN 'Cliente' ELSE 'Empleado' END AS tipo " +
                "FROM persona p " +
                "LEFT JOIN cliente c ON p.id_persona = c.id_persona " +
                "LEFT JOIN empleado e ON p.id_persona = e.id_persona";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                String persona = rs.getString("documento_identidad") + " - " +
                        rs.getString("nombre") + " " +
                        rs.getString("apellido") + " (" + tipo + ")";
                personas.add(persona);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personas;
    }

    /**
     * Obtiene la lista de personas.
     */
    public ArrayList<Persona> obtenerPersonas() {
        return personaDAO.obtenerPersonas();
    }

    /**
     * Carga la lista de personas en un ListView.
     */
    public void cargarListaPersonas(ListView<String> listPersonas) {
        ArrayList<Persona> personas = obtenerPersonas();
        listPersonas.getItems().clear();
        for (Persona p : personas) {
            listPersonas.getItems().add(p.getNombre() + " " + p.getApellido());
        }
    }

    // ===================== TAREAS =====================

    /**
     * Obtiene las tareas pendientes.
     */
    public LinkedList<Tarea> obtenerTareasPendientes() {
        return tareaDAO.obtenerTareasPendientes();
    }

    /**
     * Obtiene las tareas en proceso.
     */
    public LinkedList<Tarea> obtenerTareasEnProceso() {
        return tareaDAO.obtenerTareasEnProceso();
    }

    /**
     * Asigna una tarea a un empleado si no está ya asignada.
     */
    public boolean asignarTareaAEmpleado(Tarea tarea, Empleado empleado) {
        if (tarea == null || empleado == null) return false;
        if (tareaDAO.tieneTarea(tarea.getId_tarea(), empleado.getId_empleado())) {
            return false; // Ya asignada, no continuar
        }
        return tareaDAO.asignarTareaAEmpleado(tarea.getId_tarea(), empleado.getId_empleado());
    }

    /**
     * Crea una nueva tarea.
     */
    public int crearTarea(String descripcion, Date fechaCreacion, Date fechaEjecucion) throws SQLException {
        TareaDAO dao = new TareaDAO();
        return dao.crearTarea(descripcion, fechaCreacion, fechaEjecucion, "PENDIENTE");
    }

    /**
     * Obtiene las tareas asignadas a un empleado.
     */
    public List<Tarea> obtenerTareasAsignadasAEmpleado(int idEmpleado) {
        return tareaDAO.obtenerTareasAsignadasAEmpleado(idEmpleado);
    }

    /**
     * Marca una tarea de un empleado como completada.
     */
    public boolean marcarTareaEmpleadoComoCompletada(int idEmpleado, int idTarea) {
        return tareaDAO.marcarTareaEmpleadoComoCompletada(idEmpleado, idTarea);
    }

    /**
     * Verifica si una tarea ya está asignada a un empleado.
     */
    public boolean yaTieneAsignado(Tarea tarea, Empleado empleado) {
        if (tareaDAO.yaTieneAsingado(tarea.getId_tarea(), empleado.getId_empleado())) {
            return false; // ya asignada
        }
        return tareaDAO.asignarTareaAEmpleado(tarea.getId_tarea(), empleado.getId_empleado());
    }

    /**
     * Obtiene los empleados que tienen tareas asignadas.
     */
    public ObservableList<Empleado> obtenerEmpleadosConTareas() {
        try {
            List<Empleado> lista = tareaDAO.obtenerEmpleadosConTareas();
            return FXCollections.observableArrayList(lista);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener empleados con tareas", e);
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Asigna una tarea a un empleado por documento e id de tarea.
     */
    public boolean asignarTarea(String documentoEmpleado, int idTarea) {
        try {
            int idEmpleado = tareaDAO.obtenerIdEmpleadoPorDocumento(documentoEmpleado);
            return tareaDAO.asignarTareaAEmpleado(idTarea, idEmpleado);
        } catch (SQLException e) {
            System.err.println("Error al asignar tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista de todas las tareas.
     */
    public ObservableList<Tarea> obtenerTareas() {
        List<Tarea> listaTareas = tareaDAO.obtenerTareas();
        return FXCollections.observableArrayList(listaTareas);
    }

    /**
     * Actualiza una tarea.
     */
    public boolean actualizarTarea(Tarea tarea) {
        try {
            tareaDAO.actualizarTarea(tarea);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la tarea", e);
        }
        return false;
    }

    /**
     * Elimina una tarea.
     */
    public boolean eliminarTarea(int idTarea) {
        try {
            tareaDAO.eliminarTarea(idTarea);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la tarea", e);
        }
        return false;
    }

    /**
     * Obtiene una lista combinada de tareas pendientes y en proceso.
     */
    public LinkedList<Tarea> obtenerTareasPendientesYEnProceso() {
        LinkedList<Tarea> tareasPendientes = new LinkedList<>();
        LinkedList<Tarea> tareasEnProceso = new LinkedList<>();

        List<Tarea> todasTareas = tareaDAO.obtenerTareas();
        for (Tarea tarea : todasTareas) {
            if (tarea.getEstadoTarea() == EstadoTarea.PENDIENTE) {
                tareasPendientes.add(tarea);
            } else if (tarea.getEstadoTarea() == EstadoTarea.EN_PROCESO) {
                tareasEnProceso.add(tarea);
            }
        }

        LinkedList<Tarea> tareasClasificadas = new LinkedList<>();
        tareasClasificadas.addAll(tareasPendientes);
        tareasClasificadas.addAll(tareasEnProceso);

        return tareasClasificadas;
    }

    // ===================== HABITACIONES =====================

    /**
     * Obtiene la lista de habitaciones.
     */
    public ObservableList<Habitacion> obtenerHabitaciones() {
        try {
            List<Habitacion> listaHabitaciones = habitacionDAO.obtenerHabitaciones();
            return FXCollections.observableArrayList(listaHabitaciones);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las habitaciones", e);
        }
        return FXCollections.observableArrayList();
    }

    /**
     * Obtiene la lista de habitaciones disponibles (sin reserva).
     */
    public ObservableList<String> obtenerHabitacionesSinReserva() {
        ObservableList<String> habitaciones = FXCollections.observableArrayList();
        String sql = "SELECT h.numero_habitacion " +
                "FROM habitacion h " +
                "WHERE h.estado = 'DISPONIBLE'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                habitaciones.add(rs.getString("numero_habitacion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habitaciones;
    }

    /**
     * Crea una nueva habitación.
     */
    public int crearHabitacion(Habitacion habitacion) {
        try {
            return habitacionDAO.crearHabitacion(habitacion);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear la habitación", e);
        }
        return -1;
    }

    /**
     * Actualiza una habitación existente.
     */
    public boolean actualizarHabitacion(Habitacion habitacion) {
        try {
            habitacionDAO.actualizarHabitacion(habitacion);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la habitación", e);
        }
        return false;
    }

    /**
     * Elimina una habitación.
     */
    public boolean eliminarHabitacion(int idHabitacion) {
        try {
            habitacionDAO.eliminarHabitacion(idHabitacion);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la habitación", e);
        }
        return false;
    }

    /**
     * Actualiza el estado de una habitación.
     */
    public boolean actualizarEstadoHabitacion(int idHabitacion, String nuevoEstado) {
        ReservaDAO reservaDAO = new ReservaDAO(); // Instanciar el DAO
        try {
            reservaDAO.actualizarEstadoHabitacion(idHabitacion, nuevoEstado);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== RESERVAS =====================

    /**
     * Obtiene la lista de reservas.
     */
    public ObservableList<Reserva> obtenerReservas() {
        try {
            List<Reserva> listaReservas = reservaDAO.obtenerReservas();
            return FXCollections.observableArrayList(listaReservas);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las reservas desde la base de datos", e);
        }
        return FXCollections.observableArrayList();
    }

    /**
     * Obtiene el próximo ID de reserva disponible.
     */
    public int obtenerProximoIdReserva() {
        try {
            return reservaDAO.obtenerProximoIdReserva();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el último ID de reserva", e);
        }
        return 0;
    }

    /**
     * Actualiza una reserva.
     */
    public boolean actualizarReserva(Reserva reserva) {
        try {
            reservaDAO.actualizarReserva(reserva);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la reserva", e);
        }
        return false;
    }

    /**
     * Actualiza una reserva (simulación).
     */
    public boolean actualitzarReserva(Reserva reserva) {
        // Lógica para actualizar la reserva en la base de datos
        System.out.println("Reserva actualizada: " + reserva.getId_reserva());
        return true; // Simulación de éxito
    }

    /**
     * Actualiza el estado de una reserva.
     */
    public void actualizarEstadoReserva(int idReserva, String nuevoEstado) throws SQLException {
        String sql = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            stmt.executeUpdate();
        }
    }

    /**
     * Elimina una reserva
     */
 public boolean eliminarReservaFacturada(int idReserva) {
    ReservaDAO reservaDAO = new ReservaDAO();
    return reservaDAO.eliminarReservaSiEstaFacturada(idReserva);
}


    /**
     * Crea una reserva 
     */
    public boolean crearReserva(Reserva reserva) {
        // Lógica para insertar la reserva en la base de datos
        System.out.println("Reserva creada: " + reserva.getId_reserva());
        return true; // Simulación de éxito
    }

    /**
     * Obtiene todos los clientes como lista observable de String.
     */
    public ObservableList<String> obtenerTodosLosClientes() {
        List<String> lista = reservaDAO.obtenerClientesConNombre();
        return FXCollections.observableArrayList(lista);
    }

    /**
     * Devuelve el IVA según el tipo de cliente.
     */
    public double obtenerIvaPorTipoCliente(String tipoCliente) {
        switch (tipoCliente) {
            case "Cliente":
                return 21.0;
            case "Empleado":
                return 7.0;
            case "Ambos":
                return 10.0;
            default:
                return 0.0;
        }
    }

    /**
     * Obtiene el id_cliente a partir del documento.
     */
    public int obtenerIdClientePorDocumento(String documento) {
        return reservaDAO.obtenerIdClientePorDocumento(documento);
    }

    /**
     * Obtiene el tipo de cliente por id_cliente.
     */
    public String obtenerTipoClientePorId(int idCliente) {
        return reservaDAO.obtenerTipoClientePorId(idCliente);
    }

    /**
     * Obtiene el id de la habitación a partir de su número.
     */
    public int obtenerIdHabitacionPorNumero(String numeroHabitacion) {
        ReservaDAO reservaDAO = new ReservaDAO();
        return reservaDAO.obtenerIdHabitacionPorNumero(numeroHabitacion);
    }

    /**
     * Crea una reserva y devuelve el id generado.
     */
    public int crearReservaConExito(Reserva reserva) {
        try {
            int idReserva = reservaDAO.crearReserva(reserva);
            return idReserva;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear la reserva", e);
        }
        return -1;
    }

    /**
     * Registra una reserva y actualiza el estado de la habitación.
     */
    public boolean registrarReserva(Reserva reserva) {
        try {
            int idReserva = crearReservaConExito(reserva);
            if (idReserva > 0) {
                reservaDAO.actualizarEstadoHabitacion(reserva.getId_habitacion(), "OCUPADA");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar la reserva", e);
        }
        return false;
    }

    /**
     * Verifica si existe solapamiento de reservas para una habitación y fechas dadas.
     */
    public boolean existeSolapamientoReserva(int idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaDAO.existeSolapamientoReserva(idHabitacion, fechaInicio, fechaFin);
    }

    /**
     * Obtiene habitaciones con la última fecha de reserva.
     */
    public ObservableList<String> obtenerHabitacionesConUltimaFecha() throws SQLException {
        ObservableList<String> habitacionesConEstado = reservaDAO.obtenerHabitacionesConEstado();
        ObservableList<String> habitacionesConUltimaFecha = FXCollections.observableArrayList();

        for (String item : habitacionesConEstado) {
            String numero = item.split(" - ")[0];
            int idHab = reservaDAO.obtenerIdHabitacionPorNumero(numero);
            LocalDate ultimaFecha = reservaDAO.obtenerUltimaFechaFinReserva(idHab);
            String texto = (ultimaFecha != null) ? numero + " - Última reserva hasta: " + ultimaFecha.toString()
                                                : numero + " - Sin reservas";
            habitacionesConUltimaFecha.add(texto);
        }
        return habitacionesConUltimaFecha;
    }

    /**
     * Actualiza el estado de una reserva a "Facturado".
     * @param idReserva ID de la reserva a actualizar.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarEstadoReservaAFacturada(int idReserva) {
        return reservaDAO.actualizarEstadoReserva(idReserva, "Facturado");
    }

    /**
     * Obtiene una lista de días ocupados para una habitación específica.
     * @param idHabitacion ID de la habitación.
     * @return Lista de fechas ocupadas.
     * @throws SQLException si ocurre un error en la consulta.
     */
    public List<LocalDate> obtenerDiasOcupados(int idHabitacion) throws SQLException {
        return reservaDAO.obtenerDiasOcupados(idHabitacion);
    }

    /**
     * Obtiene los rangos de fechas reservadas para una habitación.
     * @param idHabitacion ID de la habitación.
     * @return Lista de arreglos de fechas (inicio y fin) reservadas.
     */
    public List<LocalDate[]> obtenerFechasReservadasHabitacion(int idHabitacion) {
        try {
            return reservaDAO.obtenerRangosFechasReservadas(idHabitacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ===================== FIN RESERVAS =====================
}
