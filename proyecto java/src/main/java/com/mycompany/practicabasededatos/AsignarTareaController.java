package com.mycompany.practicabasededatos;

import com.mycompany.practicabasededatos.modelo.Modelo;
import com.mycompany.practicabasededatos.modelo.Empleado;
import com.mycompany.practicabasededatos.modelo.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controlador para la vista de asignación de tareas a empleados.
 * Permite asignar tareas, filtrar empleados y tareas, y marcar tareas como completadas.
 */
public class AsignarTareaController {

    // ListView para mostrar tareas pendientes
    @FXML
    private ListView<Tarea> listTareasPendientes;
    // ListView para mostrar empleados disponibles
    @FXML
    private ListView<Empleado> listEmpleados;
    // ListView para mostrar empleados con tareas asignadas
    @FXML
    private ListView<Empleado> listEmpleadosConTareas;
    // ListView para mostrar tareas asignadas a un empleado seleccionado
    @FXML
    private ListView<Tarea> listTareasAsignadas;
    // Botón para marcar una tarea como completada
    @FXML
    private Button btnMarcarCompletada;
    // Campo de texto para buscar empleados por documento, nombre o apellido
    @FXML
    private TextField tfBuscarDocumento;
    // ComboBox para filtrar tareas
    @FXML
    private ComboBox<Tarea> comboFiltrarTareas;
    // ListView para mostrar todas las tareas (no siempre usado)
    @FXML
    private ListView<Tarea> listTareasTodas;
    // Botón para asignar tarea a empleado
    @FXML
    private Button btnAsignar;

    // Instancia del modelo para acceder a la lógica de negocio y datos
    private Modelo modelo = new Modelo();

    // Lista observable de empleados filtrados
    private ObservableList<Empleado> empleadosfiltrado;
    // Lista observable de tareas pendientes original
    private ObservableList<Tarea> tareasPendientesOriginal;

    /**
     * Inicializa la vista y carga los datos necesarios.
     */
    public void initialize() {
        cargarTareasPendientes();
        cargarEmpleados();
        configurarFiltroEmpleados();
        configurarFiltroTareas();
        btnAsignar.setOnAction(e -> asignarTarea());
        listEmpleados.setItems(FXCollections.observableArrayList(modelo.obtenerEmpleados()));

        // Configura el ListView de empleados con tareas
        listEmpleadosConTareas.setItems(modelo.obtenerEmpleadosConTareas());
        listEmpleadosConTareas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        });

        // Cuando seleccionas un empleado, carga sus tareas asignadas
        listEmpleadosConTareas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarTareasAsignadas(newVal);
            }
        });
    }

    /**
     * Carga las tareas pendientes en el ListView correspondiente.
     */
    private void cargarTareasPendientes() {
        List<Tarea> pendientes = modelo.obtenerTareasPendientes();
        tareasPendientesOriginal = FXCollections.observableArrayList(pendientes);
        listTareasPendientes.setItems(tareasPendientesOriginal);

        listTareasPendientes.setCellFactory(lv -> new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);
                if (empty || tarea == null) {
                    setText(null);
                } else {
                    setText("📌 " + tarea.getDescripcion() + " | Estado: " + tarea.getEstadoTarea());
                }
            }
        });
    }

    /**
     * Carga los empleados en los ListView correspondientes.
     */
    public void cargarEmpleados() {
        try {
            List<Empleado> empleados = modelo.obtenerEmpleados();
            if (empleados == null) {
                empleados = List.of(); // nunca null
            }

            empleadosfiltrado = FXCollections.observableArrayList(empleados);
            listEmpleados.setItems(empleadosfiltrado);
            listEmpleadosConTareas.setItems(FXCollections.observableArrayList(modelo.obtenerEmpleadosConTareas()));

        } catch (Exception e) {
            e.printStackTrace();
            empleadosfiltrado = FXCollections.observableArrayList(); // defensivo
            listEmpleados.setItems(empleadosfiltrado);
        }
    }

    /**
     * Configura el filtro de empleados por documento, nombre o apellido.
     */
    private void configurarFiltroEmpleados() {
        tfBuscarDocumento.textProperty().addListener((obs, oldText, newText) -> {
            if (empleadosfiltrado == null)
                return;

            if (newText == null || newText.isBlank()) {
                // Mostrar todos los empleados si el campo está vacío
                listEmpleados.setItems(empleadosfiltrado);
            } else {
                String filtro = newText.toLowerCase();

                ObservableList<Empleado> filtrados = empleadosfiltrado
                        .filtered(emp -> (emp.getDocumento_identidad() != null
                                && emp.getDocumento_identidad().toLowerCase().contains(filtro)) ||
                                (emp.getNombre() != null && emp.getNombre().toLowerCase().contains(filtro)) ||
                                (emp.getApellido() != null && emp.getApellido().toLowerCase().contains(filtro)));

                listEmpleados.setItems(filtrados);
            }
        });
    }

    /**
     * Configura el filtro de tareas mediante el ComboBox.
     */
    private void configurarFiltroTareas() {
        comboFiltrarTareas.valueProperty().addListener((obs, oldTarea, nuevaTarea) -> {
            if (nuevaTarea == null) {
                listTareasPendientes.setItems(tareasPendientesOriginal);
            } else {
                ObservableList<Tarea> filtradas = tareasPendientesOriginal
                        .filtered(t -> t.getId_tarea() == nuevaTarea.getId_tarea());
                listTareasPendientes.setItems(filtradas);
            }
        });

        // Llenar el comboBox con tareas pendientes
        comboFiltrarTareas.setItems(FXCollections.observableArrayList(tareasPendientesOriginal));
        comboFiltrarTareas.getItems().add(0, null);
        comboFiltrarTareas.setPromptText("Filtrar tareas");

        comboFiltrarTareas.setCellFactory(cb -> new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);
                if (empty || tarea == null) {
                    setText("Todas las tareas");
                } else {
                    setText(tarea.getDescripcion());
                }
            }
        });

        comboFiltrarTareas.setButtonCell(new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);
                if (empty || tarea == null) {
                    setText("Todas las tareas");
                } else {
                    setText(tarea.getDescripcion());
                }
            }
        });
    }

    /**
     * Asigna la tarea seleccionada al empleado seleccionado.
     */
    private void asignarTarea() {
        Tarea tareaSeleccionada = listTareasPendientes.getSelectionModel().getSelectedItem();
        Empleado empleadoSeleccionado = listEmpleados.getSelectionModel().getSelectedItem();

        if (tareaSeleccionada == null) {
            mostrarAlertaError("Error", "Por favor, selecciona una tarea pendiente.");
            return;
        }
        if (empleadoSeleccionado == null) {
            mostrarAlertaError("Error", "Por favor, selecciona un empleado.");
            return;
        }

        boolean exito = modelo.asignarTareaAEmpleado(tareaSeleccionada, empleadoSeleccionado);

        if (exito) {
            mostrarAlertaInformacion("Éxito", "Tarea asignada correctamente.");
            cargarTareasPendientes();
            cargarTareasAsignadas(empleadoSeleccionado); 
        } else {
            mostrarAlertaError("Error", "No se pudo asignar la tarea. Es posible que ya esté asignada a este empleado.");
        }
    }

    /**
     * Carga las tareas asignadas a un empleado seleccionado.
     */
    private void cargarTareasAsignadas(Empleado empleado) {
        try {
            List<Tarea> tareas = modelo.obtenerTareasAsignadasAEmpleado(empleado.getId_empleado());
            listTareasAsignadas.setItems(FXCollections.observableArrayList(tareas));
            listTareasAsignadas.setCellFactory(lv -> new ListCell<Tarea>() {
                @Override
                protected void updateItem(Tarea tarea, boolean empty) {
                    super.updateItem(tarea, empty);
                    if (empty || tarea == null) {
                        setText(null);
                    } else {
                        setText(tarea.getDescripcion() + " | Estado: " + tarea.getEstadoTarea());
                    }
                }
            });

        } catch (Exception e) {
            mostrarAlertaError("Error", "No se pudieron cargar las tareas.");
            e.printStackTrace();
        }
    }

    /**
     * Marca la tarea seleccionada como completada para el empleado seleccionado.
     */
    @FXML
    private void marcarTareaComoCompletada() {
        Empleado empleado = listEmpleadosConTareas.getSelectionModel().getSelectedItem();
        Tarea tarea = listTareasAsignadas.getSelectionModel().getSelectedItem();

        if (empleado == null || tarea == null) {
            mostrarAlertaError("Error", "Selecciona un empleado y una tarea.");
            return;
        }

        try {
            // Marca la tarea como completada para el empleado
            boolean completadaGlobal = modelo.marcarTareaEmpleadoComoCompletada(empleado.getId_empleado(), tarea.getId_tarea());

            // Mostrar mensaje
            if (completadaGlobal) {
                mostrarAlertaInformacion("Éxito", "Tarea marcada como completada.");
            } else {
                mostrarAlertaInformacion("Éxito", "Has marcado tu parte como completada.");
            }

            // Refrescar listas
            cargarTareasAsignadas(empleado);    // Lista del empleado
            cargarTareasPendientes();           // Refresca las tareas pendientes
            comboFiltrarTareas.setItems(FXCollections.observableArrayList(tareasPendientesOriginal)); // Actualiza opciones del ComboBox

        } catch (Exception e) {
            mostrarAlertaError("Error", "No se pudo actualizar la tarea.");
            e.printStackTrace();
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
    public static void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
