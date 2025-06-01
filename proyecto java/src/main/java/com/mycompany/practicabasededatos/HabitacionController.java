package com.mycompany.practicabasededatos;

import com.mycompany.practicabasededatos.modelo.EstadoHabitacion;
import com.mycompany.practicabasededatos.modelo.Habitacion;
import com.mycompany.practicabasededatos.modelo.Modelo;
import com.mycompany.practicabasededatos.modelo.TipoHabitacion;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

/**
 * Controlador para la gestión de habitaciones.
 * Permite crear, modificar, eliminar y visualizar habitaciones.
 */
public class HabitacionController {
    // Campo de texto para el número de habitación
    @FXML
    private TextField txtNumeroHabitacion;
    // ComboBox para seleccionar el tipo de habitación
    @FXML
    private ComboBox<TipoHabitacion> comboTipoHabitacion;
    // Campo de texto para la capacidad de la habitación
    @FXML
    private TextField txtCapacidad;
    // ComboBox para seleccionar el estado de la habitación
    @FXML
    private ComboBox<EstadoHabitacion> comboEstado;
    // Área de texto para la descripción de la habitación
    @FXML
    private TextArea txtDescripcion;
    // Campo de texto para el precio por noche en alojamiento y desayuno
    @FXML
    private TextField txtPrecioNocheAD;
    // Campo de texto para el precio por noche en media pensión
    @FXML
    private TextField txtPrecioNocheMP;
    // ListView para mostrar la lista de habitaciones
    @FXML
    private ListView<Habitacion> listHabitaciones;
    // Botón para crear una nueva habitación
    @FXML
    private Button btnCrear;
    // Botón para guardar cambios en una habitación existente
    @FXML
    private Button btnGuardarCambios;
    // Botón para eliminar una habitación
    @FXML
    private Button btnEliminar;
    // Botón para limpiar los campos del formulario
    @FXML
    private Button btnLimpiar;

    // Instancia del modelo para acceder a la lógica de negocio y datos
    private Modelo modelo = new Modelo();

    /**
     * Inicializa la vista, configurando los ComboBox, ListView y listeners.
     */
    @FXML
    public void initialize() {
        comboTipoHabitacion.getItems().setAll(TipoHabitacion.values());
        comboEstado.getItems().setAll(EstadoHabitacion.values());
        cargarHabitacionesEnLista();

        // Listener para alternar botones al seleccionar un elemento de la lista
        listHabitaciones.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mostrarDetallesHabitacion(newValue);
                alternarBotones(false); // Mostrar "Guardar Cambios" y "Eliminar", ocultar "Crear"
            }
        });

        // Listener para alternar botones al escribir en los campos
        agregarListenersDeEscritura();
    }

    /**
     * Carga las habitaciones en el ListView.
     */
    private void cargarHabitacionesEnLista() {
        ObservableList<Habitacion> habitaciones = modelo.obtenerHabitaciones();
        listHabitaciones.setItems(habitaciones);
    
        listHabitaciones.setCellFactory(param -> new ListCell<Habitacion>() {
            @Override
            protected void updateItem(Habitacion habitacion, boolean empty) {
                super.updateItem(habitacion, empty);
                if (empty || habitacion == null) {
                    setText(null);
                } else {
                    // Mostrar el estado directamente desde el campo "estado"
                    String estadoTexto = habitacion.getEstado().toString();
                    setText("Habitación " + habitacion.getNumero_habitacion() + " - " + habitacion.getTipo() + " (" + estadoTexto + ")");
                }
            }
        });
    }

    /**
     * Muestra los detalles de la habitación seleccionada en los campos del formulario.
     */
    private void mostrarDetallesHabitacion(Habitacion habitacion) {
        if (habitacion != null) {
            txtNumeroHabitacion.setText(habitacion.getNumero_habitacion());
            comboTipoHabitacion.setValue(habitacion.getTipo());
            txtCapacidad.setText(String.valueOf(habitacion.getCapacidad()));
            comboEstado.setValue(habitacion.getEstado());
            txtDescripcion.setText(habitacion.getDescripcion());
            txtPrecioNocheAD.setText(String.valueOf(habitacion.getPrecio_noche_ad()));
            txtPrecioNocheMP.setText(String.valueOf(habitacion.getPrecio_noche_mp()));
        } else {
            limpiarCampos();
        }
    }

    /**
     * Limpia los campos y restablece el formulario.
     */
    @FXML
    private void limpiarCamposAction() {
        limpiarCampos();
        listHabitaciones.getSelectionModel().clearSelection();
        alternarBotones(true);
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarCampos() {
        txtNumeroHabitacion.clear();
        comboTipoHabitacion.setValue(null);
        txtCapacidad.clear();
        comboEstado.setValue(null);
        txtDescripcion.clear();
        txtPrecioNocheAD.clear();
        txtPrecioNocheMP.clear();
    }

    /**
     * Alterna la visibilidad de los botones según el contexto.
     * @param mostrarCrear true para mostrar "Crear", false para mostrar "Guardar Cambios" y "Eliminar"
     */
    private void alternarBotones(boolean mostrarCrear) {
        btnCrear.setVisible(mostrarCrear);
        btnGuardarCambios.setVisible(!mostrarCrear);
        btnEliminar.setVisible(!mostrarCrear);
    }

    /**
     * Agrega listeners para detectar escritura en los campos y alternar botones.
     */
    private void agregarListenersDeEscritura() {
        txtNumeroHabitacion.setOnKeyTyped(this::manejarEscritura);
        txtCapacidad.setOnKeyTyped(this::manejarEscritura);
        txtDescripcion.setOnKeyTyped(this::manejarEscritura);
        txtPrecioNocheAD.setOnKeyTyped(this::manejarEscritura);
        txtPrecioNocheMP.setOnKeyTyped(this::manejarEscritura);
    }

    /**
     * Maneja la escritura en los campos para alternar los botones.
     */
    private void manejarEscritura(KeyEvent event) {
        if (listHabitaciones.getSelectionModel().getSelectedItem() == null) {
            alternarBotones(true);
        }
    }

    /**
     * Crea una nueva habitación a partir de los datos del formulario.
     */
    @FXML
    private void crearHabitacion() {
        try {
            String numeroHabitacion = txtNumeroHabitacion.getText();
            TipoHabitacion tipoHabitacion = comboTipoHabitacion.getValue();
            int capacidad = Integer.parseInt(txtCapacidad.getText());
            EstadoHabitacion estado = comboEstado.getValue();
            String descripcion = txtDescripcion.getText();
            double precioNocheAD = Double.parseDouble(txtPrecioNocheAD.getText());
            double precioNocheMP = Double.parseDouble(txtPrecioNocheMP.getText());
    
            Habitacion habitacion = new Habitacion(0, numeroHabitacion, tipoHabitacion, capacidad, estado, descripcion, precioNocheAD, precioNocheMP);
            int idHabitacion = modelo.crearHabitacion(habitacion);
    
            if (idHabitacion > 0) {
                mostrarAlertaInformacion("Éxito", "Habitación creada correctamente.");
                cargarHabitacionesEnLista();
                limpiarCampos();
                alternarBotones(false);
            } else {
                mostrarAlertaError("Error", "No se pudo crear la habitación.");
            }
        } catch (NumberFormatException e) {
            mostrarAlertaError("Error", "Por favor, ingresa valores válidos en los campos.");
        }
    }

    /**
     * Guarda los cambios realizados en una habitación seleccionada.
     */
    @FXML
    private void guardarCambios() {
        Habitacion habitacionSeleccionada = listHabitaciones.getSelectionModel().getSelectedItem();
        if (habitacionSeleccionada == null) {
            mostrarAlertaError("Error", "Por favor, selecciona una habitación.");
            return;
        }
    
        try {
            String numeroHabitacion = txtNumeroHabitacion.getText();
            TipoHabitacion tipoHabitacion = comboTipoHabitacion.getValue();
            int capacidad = Integer.parseInt(txtCapacidad.getText());
            EstadoHabitacion estado = comboEstado.getValue();
            String descripcion = txtDescripcion.getText();
            double precioNocheAD = Double.parseDouble(txtPrecioNocheAD.getText());
            double precioNocheMP = Double.parseDouble(txtPrecioNocheMP.getText());
    
            Habitacion habitacionActualizada = new Habitacion(
                habitacionSeleccionada.getId_habitacion(),
                numeroHabitacion,
                tipoHabitacion,
                capacidad,
                estado,
                descripcion,
                precioNocheAD,
                precioNocheMP
            );
    
            boolean actualizada = modelo.actualizarHabitacion(habitacionActualizada);
    
            if (actualizada) {
                mostrarAlertaInformacion("Éxito", "Habitación actualizada correctamente.");
                cargarHabitacionesEnLista();
                limpiarCampos();
                alternarBotones(true);
            } else {
                mostrarAlertaError("Error", "No se pudo actualizar la habitación.");
            }
        } catch (NumberFormatException e) {
            mostrarAlertaError("Error", "Por favor, ingresa valores válidos en los campos.");
        }
    }

    /**
     * Elimina la habitación seleccionada tras confirmación del usuario.
     */
    @FXML
    private void eliminarHabitacion() {
        Habitacion habitacionSeleccionada = listHabitaciones.getSelectionModel().getSelectedItem();
        if (habitacionSeleccionada == null) {
            mostrarAlertaError("Error", "Por favor, selecciona una habitación.");
            return;
        }

        // Mostrar mensaje de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta habitación?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            boolean eliminada = modelo.eliminarHabitacion(habitacionSeleccionada.getId_habitacion());

            if (eliminada) {
                mostrarAlertaInformacion("Éxito", "Habitación eliminada correctamente.");
                cargarHabitacionesEnLista();
                limpiarCampos();
                alternarBotones(false);
            } else {
                mostrarAlertaError("Error", "No se pudo eliminar la habitación.");
            }
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
