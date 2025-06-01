package com.mycompany.practicabasededatos;

import com.mycompany.practicabasededatos.modelo.Reserva;
import com.mycompany.practicabasededatos.modelo.TipoReserva;
import com.mycompany.practicabasededatos.modelo.Modelo;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javafx.util.Callback;

/**
 * Controlador para la gestión de reservas.
 * Permite crear, visualizar, facturar y eliminar reservas, así como gestionar la disponibilidad de habitaciones.
 */
public class ReservaController {

    // Selectores de fecha para la reserva, inicio y fin de la estancia
    @FXML
    private DatePicker dateFechaReserva;
    @FXML
    private DatePicker dateFechaInicio;
    @FXML
    private DatePicker dateFechaFin;
    // ComboBox para seleccionar el tipo de reserva (AD, MP)
    @FXML
    private ComboBox<String> comboTipoReserva;
    // Campo de texto para mostrar el tipo de IVA aplicado
    @FXML
    private TextField txtTipoIVA;
    // Campo de texto para mostrar el precio total de la reserva
    @FXML
    private TextField txtPrecioTotalReserva;
    // ListView para mostrar las reservas existentes
    @FXML
    private ListView<Reserva> listReservas;
    // ListView para mostrar habitaciones disponibles
    @FXML
    private ListView<String> listHabitacionesSinReserva;
    // ListView para mostrar personas/clientes disponibles
    @FXML
    private ListView<String> listPersonas;
    // Botones para crear, guardar, eliminar y facturar reservas
    @FXML
    private Button btnCrearReserva;
    @FXML
    private Button btnGuardarCambios;
    @FXML
    private Button btnEliminarReserva;
    @FXML
    private Button btnFacturar;

    // Instancia del modelo para acceder a la lógica de negocio y datos
    private Modelo modelo = new Modelo();

    /**
     * Inicializa la vista, configurando ComboBox, ListView y listeners.
     */
    @FXML
    public void initialize() {
        btnFacturar.setDisable(true);

        comboTipoReserva.getItems().addAll("AD", "MP");
        cargarReservasEnLista();
        cargarHabitacionesSinReserva();
        cargarClientes();

        // Hacer que los campos txtTipoIVA y txtPrecioTotalReserva no sean editables
        txtTipoIVA.setEditable(false);
        txtPrecioTotalReserva.setEditable(false);

        // Listener para marcar días ocupados al seleccionar una habitación
        listHabitacionesSinReserva.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String numeroHabitacion = newVal.split(" ")[0];
                int idHabitacion = modelo.obtenerIdHabitacionPorNumero(numeroHabitacion);
                if (idHabitacion != -1) {
                    marcarDiasOcupadosEnDatePicker(idHabitacion);
                }
            }
        });

        // Listener para actualizar IVA al seleccionar una persona
        listPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String documentoIdentidad = newVal.split(" - ")[0];
                if (!documentoIdentidad.isEmpty()) {
                    int idCliente = modelo.obtenerIdClientePorDocumento(documentoIdentidad);
                    if (idCliente != -1) {
                        String tipoCliente = modelo.obtenerTipoClientePorId(idCliente);
                        double iva = modelo.obtenerIvaPorTipoCliente(tipoCliente);
                        txtTipoIVA.setText(String.valueOf(iva));
                    } else {
                        txtTipoIVA.setText("");
                    }
                }
            } else {
                txtTipoIVA.setText("");
            }
        });

        // Listener para habilitar/deshabilitar el botón de facturar según el estado de la reserva
        listReservas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String estado = newVal.getEstado();
                if ("Facturado".equalsIgnoreCase(estado)) {
                    btnFacturar.setDisable(true);
                } else {
                    btnFacturar.setDisable(false);
                }
            } else {
                btnFacturar.setDisable(true);
            }
        });
    }

    /**
     * Crea una nueva reserva validando los datos del formulario.
     */
    @FXML
    private void crearReserva() {
        try {
            // Validar selección de habitación
            String habitacionSeleccionada = listHabitacionesSinReserva.getSelectionModel().getSelectedItem();
            if (habitacionSeleccionada == null) {
                 mostrarError("Error", "Por favor, selecciona una habitación.");
                return;
            }
            String numeroHabitacion = habitacionSeleccionada.split(" ")[0];
            int idHabitacion = modelo.obtenerIdHabitacionPorNumero(numeroHabitacion);
            if (idHabitacion == -1) {
                 mostrarError("Error", "No se encontró la habitación en la base de datos.");
                return;
            }

            // Validar selección de persona
            String personaSeleccionada = listPersonas.getSelectionModel().getSelectedItem();
            if (personaSeleccionada == null) {
                 mostrarError("Error", "Por favor, selecciona una persona.");
                return;
            }
            String documentoIdentidad = personaSeleccionada.split(" - ")[0];
            if (documentoIdentidad.isEmpty()) {
                 mostrarError("Error", "No se pudo obtener el documento de identidad del cliente.");
                return;
            }
            int idCliente = modelo.obtenerIdClientePorDocumento(documentoIdentidad);
            if (idCliente == -1) {
                 mostrarError("Error", "No se encontró el cliente en la base de datos.");
                return;
            }

            // Validar tipo de reserva
            String tipoReserva = comboTipoReserva.getValue();
            if (tipoReserva == null || tipoReserva.isEmpty()) {
                 mostrarError("Error", "Por favor, selecciona un tipo de reserva.");
                return;
            }

            // Validar fechas
            if (dateFechaInicio.getValue() == null || dateFechaFin.getValue() == null
                    || dateFechaReserva.getValue() == null) {
                 mostrarError("Error", "Por favor, selecciona todas las fechas.");
                return;
            }
            if (dateFechaInicio.getValue().isAfter(dateFechaFin.getValue())) {
                 mostrarError("Error", "La fecha de inicio no puede ser posterior a la fecha de fin.");
                return;
            }

            // Verificar solapamiento de reservas
            LocalDate fechaInicio = dateFechaInicio.getValue();
            LocalDate fechaFin = dateFechaFin.getValue();
            if (modelo.existeSolapamientoReserva(idHabitacion, fechaInicio, fechaFin)) {
                 mostrarError("Error", "La habitación ya está reservada durante ese período.");
                return;
            }

            // Calcular precio base de la reserva
            double precioBase = calcularPrecioBase(dateFechaInicio.getValue(), dateFechaFin.getValue(), tipoReserva);
            double precioTotal = precioBase;

            // Crear objeto Reserva
            Reserva nuevaReserva = new Reserva(
                    0,
                    TipoReserva.valueOf(tipoReserva),
                    dateFechaInicio.getValue(),
                    dateFechaFin.getValue(),
                    dateFechaReserva.getValue(),
                    precioTotal,
                    idCliente,
                    idHabitacion,
                    "Pendiente de facturar",
                    habitacionSeleccionada);

            // Registrar la reserva en la base de datos
            boolean exito = modelo.registrarReserva(nuevaReserva);
            if (exito) {
                modelo.actualizarEstadoHabitacion(idHabitacion, "Ocupada");
                mostrarAlertaInformacion("Éxito", "Reserva creada correctamente.");
                cargarReservasEnLista();
                cargarHabitacionesSinReserva();
            } else {
                 mostrarError("Error", "No se pudo crear la reserva.");
            }

        } catch (Exception e) {
             mostrarError("Error", "Ocurrió un error al crear la reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcula el precio base de la reserva según el tipo y la duración.
     */
    private double calcularPrecioBase(LocalDate fechaInicio, LocalDate fechaFin, String tipoReserva) {
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        double precioPorDia;
        switch (tipoReserva) {
            case "AD":
                precioPorDia = 50.0;
                break;
            case "MP":
                precioPorDia = 75.0;
                break;
            default:
                precioPorDia = 0.0;
                break;
        }
        return dias * precioPorDia;
    }

    /**
     * Carga las reservas en el ListView.
     */
    private void cargarReservasEnLista() {
        ObservableList<Reserva> reservas = modelo.obtenerReservas();
        listReservas.setItems(reservas);

        listReservas.setCellFactory(param -> new ListCell<Reserva>() {
            @Override
            protected void updateItem(Reserva reserva, boolean empty) {
                super.updateItem(reserva, empty);
                if (empty || reserva == null) {
                    setText(null);
                } else {
                    String numeroHabitacion = reserva.getNumeroHabitacion() != null ? reserva.getNumeroHabitacion()
                            : "Sin asignar";
                    setText("Habitación: " + numeroHabitacion + " | Tipo: " + reserva.getTipo_reserva()
                            + " | Fechas: " + reserva.getFecha_inicio() + " a " + reserva.getFecha_fin()
                            + " | Estado: " + reserva.getEstado());
                }
            }
        });
    }

    /**
     * Carga las habitaciones disponibles en el ListView.
     */
    private void cargarHabitacionesSinReserva() {
        try {
            ObservableList<String> habitaciones = modelo.obtenerHabitacionesConUltimaFecha();
            listHabitacionesSinReserva.setItems(habitaciones);
        } catch (Exception e) {
             mostrarError("Error", "No se pudieron cargar las habitaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los clientes/personas en el ListView.
     */
    private void cargarClientes() {
        ObservableList<String> personas = modelo.obtenerTodosLosClientes();
        listPersonas.setItems(personas);
    }

    /**
     * Abre la ventana de facturación para la reserva seleccionada.
     */
    @FXML
    private void abrirVentanaFacturacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("factura.fxml"));
            Parent root = loader.load();

            FacturaController facturaController = loader.getController();

            Reserva reservaSeleccionada = listReservas.getSelectionModel().getSelectedItem();
            if (reservaSeleccionada == null) {
                 mostrarError("Error", "Por favor, selecciona una reserva para facturar.");
                return;
            }

            // Determinar el tipo de cliente y el IVA correspondiente
            String tipoCliente = modelo.obtenerTipoClientePorId(reservaSeleccionada.getId_cliente());
            double iva;
            if (tipoCliente == null) {
                 mostrarError("Error", "No se pudo obtener el tipo de cliente.");
                return;
            }

            switch (tipoCliente) {
                case "Cliente":
                    iva = 21.0;
                    break;
                case "Empleado":
                    iva = 7.0;
                    break;
                case "Ambos":
                    iva = 10.0;
                    break;
                default:
                     mostrarError("Error", "No se pudo determinar el tipo de cliente.");
                    return;
            }

            facturaController.setReserva(reservaSeleccionada, iva);

            if (facturaController.isFacturacionCompletada()) {
                actualizarTablaReservas();
            }

            Stage stage = new Stage();
            stage.setTitle("Facturación");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Actualizar estado de la reserva a "Facturado" si corresponde
            if (facturaController.isFacturacionCompletada()) {
                cargarReservasEnLista();
            }

        } catch (IOException e) {
             mostrarError("Error", "No se pudo abrir la ventana de facturación: " + e.getMessage());
            e.printStackTrace();
        }
        Reserva seleccionada = listReservas.getSelectionModel().getSelectedItem();
        if (seleccionada != null && "Facturado".equalsIgnoreCase(seleccionada.getEstado())) {
             mostrarError("Error", "Esta reserva ya fue facturada.");
            return;
        }
    }

    /**
     * Actualiza la tabla de reservas en la vista.
     */
    private void actualizarTablaReservas() {
        List<Reserva> reservas = modelo.obtenerReservas();
        listReservas.getItems().setAll(reservas);
    }

    /**
     * Marca los días ocupados en los DatePicker según las reservas existentes de la habitación.
     */
    private void marcarDiasOcupadosEnDatePicker(int idHabitacion) {
        List<LocalDate[]> fechasOcupadas = modelo.obtenerFechasReservadasHabitacion(idHabitacion);

        Callback<DatePicker, DateCell> dayCellFactory = (DatePicker picker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                for (LocalDate[] rango : fechasOcupadas) {
                    LocalDate inicio = rango[0];
                    LocalDate fin = rango[1];
                    if (!empty && (date.isEqual(inicio) || date.isEqual(fin)
                            || (date.isAfter(inicio) && date.isBefore(fin)))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ff6666;");
                    }
                }
            }
        };

        dateFechaInicio.setDayCellFactory(dayCellFactory);
        dateFechaFin.setDayCellFactory(dayCellFactory);
        dateFechaReserva.setDayCellFactory(dayCellFactory);
    }

    /**
     * Elimina la reserva seleccionada si está facturada.
     */
@FXML
private void eliminarReservaFacturada() {
    Reserva reservaSeleccionada = listReservas.getSelectionModel().getSelectedItem();
    if (reservaSeleccionada == null) {
         mostrarError("Error", "Selecciona una reserva para eliminar.");
        return;
    }

    boolean confirmacion = mostrarConfirmacion("Confirmar", "¿Estás seguro de eliminar esta reserva facturada?");
    if (!confirmacion) return;

    boolean eliminada = modelo.eliminarReservaFacturada(reservaSeleccionada.getId_reserva());
    if (eliminada) {
        mostrarAlerta("Éxito", "Reserva eliminada correctamente.");
        cargarReservasEnLista(); // Método que recargue la lista
    } else {
        mostrarAlerta("Error", "No se pudo eliminar. La reserva no está facturada o ocurrió un error.");
    }
}

    // Métodos para mostrar alertas

    /**
     * Muestra una alerta de información.
     */
    public static void mostrarAlertaInformacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de error.
     */
    public static void  mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}

private boolean mostrarConfirmacion(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
}

}
