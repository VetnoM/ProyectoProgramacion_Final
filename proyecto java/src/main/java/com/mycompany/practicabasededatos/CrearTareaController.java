package com.mycompany.practicabasededatos;

import com.mycompany.practicabasededatos.modelo.Modelo;
import com.mycompany.practicabasededatos.modelo.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Controlador para la vista de creación de tareas.
 * Permite crear nuevas tareas y visualizar las existentes en una tabla.
 */
public class CrearTareaController {

    // Campo de texto para la descripción de la tarea
    @FXML
    private TextField txtDescripcionTarea;

    // Selector de fecha para la creación de la tarea
    @FXML
    private DatePicker dpFechaCreacionTarea;

    // Selector de fecha para la ejecución de la tarea
    @FXML
    private DatePicker dpFechaEjecucionTarea;

    // Tabla para mostrar las tareas existentes
    @FXML
    private TableView<Tarea> tableTareas;

    // Columna de la tabla para la descripción
    @FXML
    private TableColumn<Tarea, String> colDescripcion;

    // Columna de la tabla para el estado de la tarea
    @FXML
    private TableColumn<Tarea, String> colEstado;

    // Columna de la tabla para la fecha de creación
    @FXML
    private TableColumn<Tarea, String> colFechaCreacion;

    // Instancia del modelo para acceder a la lógica de negocio y datos
    Modelo modelo = new Modelo();

    /**
     * Inicializa la vista, configurando las columnas y cargando las tareas.
     */
    @FXML
    public void initialize() {
        // Configurar las columnas del TableView
        colDescripcion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDescripcion()));

        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstadoTarea().toString()));

        colFechaCreacion.setCellValueFactory(cellData -> {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = cellData.getValue().getFecha_creacion() != null
                    ? formatter.format(cellData.getValue().getFecha_creacion())
                    : "";
            return new javafx.beans.property.SimpleStringProperty(fechaStr);
        });

        cargarTareasEnTabla();
    }

    /**
     * Carga las tareas existentes en la tabla.
     */
    private void cargarTareasEnTabla() {
        try {
            ObservableList<Tarea> tareas = FXCollections.observableArrayList(modelo.obtenerTareas());
            tableTareas.setItems(tareas);
        } catch (Exception e) {
            mostrarAlertaError("Error", "No se pudieron cargar las tareas.");
            e.printStackTrace();
        }
    }

    /**
     * Crea una nueva tarea a partir de los datos introducidos en el formulario.
     */
    @FXML
    private void crearTarea() {
        String descripcion = txtDescripcionTarea.getText();
        Date fechaCreacion = (dpFechaCreacionTarea.getValue() != null)
                ? Date.valueOf(dpFechaCreacionTarea.getValue())
                : Date.valueOf(LocalDate.now());
        Date fechaEjecucion = (dpFechaEjecucionTarea.getValue() != null)
                ? Date.valueOf(dpFechaEjecucionTarea.getValue())
                : null;


        if (descripcion.isEmpty()) {
            mostrarAlertaError("Error", "Por favor, completa la descripción de la tarea.");
            return;
        }

        try {
            int idTarea = modelo.crearTarea(descripcion, fechaCreacion, fechaEjecucion);
            if (idTarea > 0) {
                mostrarAlertaInformacion("Éxito", "Tarea creada correctamente.");
                cargarTareasEnTabla();
            } else {
                mostrarAlertaError("Error", "No se pudo crear la tarea.");
            }
        } catch (Exception e) {
            mostrarAlertaError("Error", "Error al crear la tarea: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
    public static void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
