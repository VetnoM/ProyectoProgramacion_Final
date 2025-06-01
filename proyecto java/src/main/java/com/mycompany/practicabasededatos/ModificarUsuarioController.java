package com.mycompany.practicabasededatos;

import com.mycompany.practicabasededatos.database.ClienteDAO;
import com.mycompany.practicabasededatos.database.EmpleadoDAO;
import com.mycompany.practicabasededatos.database.PersonaDAO;
import com.mycompany.practicabasededatos.modelo.Cliente;
import com.mycompany.practicabasededatos.modelo.Empleado;
import com.mycompany.practicabasededatos.modelo.EstadoLaboral;
import com.mycompany.practicabasededatos.modelo.Persona;
import com.mycompany.practicabasededatos.modelo.TipoCliente;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la vista de modificación de usuarios (personas, clientes y empleados).
 * Permite buscar, mostrar y actualizar datos de usuarios.
 */
public class ModificarUsuarioController {

    // Campo de texto para el documento de identidad
    @FXML private TextField txtDocumento;
    // Campo de texto para la dirección
    @FXML private TextField txtDireccion;
    // Campo de texto para el correo electrónico
    @FXML private TextField txtCorreo;
    // ComboBox para seleccionar el tipo de cliente
    @FXML private ComboBox<TipoCliente> cmbTipoCliente;
    // Campo de texto para el salario (empleado)
    @FXML private TextField txtSalario;
    // Campo de texto para el lugar de trabajo (empleado)
    @FXML private TextField txtLugarTrabajo;
    // ComboBox para seleccionar el estado laboral (empleado)
    @FXML private ComboBox<EstadoLaboral> cmbEstadoLaboral;
    // ListView para mostrar coincidencias de usuarios por documento
    @FXML private ListView<String> listViewUsuarios;
    // Botón para buscar usuario
    @FXML private Button btnBuscar;

    // DAOs para acceder a la base de datos
    private PersonaDAO personaDAO = new PersonaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    // Instancias actuales de persona, cliente y empleado seleccionados
    private Persona personaActual;
    private Cliente clienteActual;
    private Empleado empleadoActual;

    /**
     * Inicializa la vista, cargando los valores de los ComboBox y configurando el ListView.
     */
    @FXML
    public void initialize() {
        // Cargar valores en los ComboBox
        cmbTipoCliente.setItems(FXCollections.observableArrayList(TipoCliente.values()));
        cmbEstadoLaboral.setItems(FXCollections.observableArrayList(EstadoLaboral.values()));

        // Manejar selección en ListView
        listViewUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtDocumento.setText(newVal.split(" - ")[0]);  // Extrae el documento del formato "Documento - Nombre"
                buscarUsuario();
            }
        });
    }

    /**
     * Busca un usuario por documento y muestra sus datos si existe.
     */
    @FXML
    public void buscarUsuario() {
        String documento = txtDocumento.getText().trim();
        
        if (documento.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un documento válido.");
            return;
        }
    
        try {
            // Buscar la persona en la base de datos usando el método optimizado
            personaActual = personaDAO.obtenerPersonaPorDocumento(documento);
            
            if (personaActual == null) {
                mostrarAlerta("Usuario no encontrado", "El documento ingresado no está registrado.");
                limpiarCampos();
                return;
            }
    
            // Mostrar datos de persona
            txtDireccion.setText(personaActual.getDireccion());
            txtCorreo.setText(personaActual.getEmail());
    
            // Buscar si es cliente
            clienteActual = clienteDAO.obtenerClientePorIdPersona(personaActual.getId_persona());
            if (clienteActual != null) {
                cmbTipoCliente.setValue(clienteActual.getTipoCliente());
            } else {
                cmbTipoCliente.setValue(null);
            }
    
            // Buscar si es empleado
            empleadoActual = empleadoDAO.obtenerEmpleadoPorIdPersona(personaActual.getId_persona());
            if (empleadoActual != null) {
                txtSalario.setText(String.valueOf(empleadoActual.getSalario_bruto()));
                txtLugarTrabajo.setText(empleadoActual.getLugar_trabajo());
                cmbEstadoLaboral.setValue(empleadoActual.getEstadolaboral());
            } else {
                txtSalario.clear();
                txtLugarTrabajo.clear();
                cmbEstadoLaboral.setValue(null);
            }
    
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo realizar la búsqueda.");
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda los cambios realizados en los datos del usuario.
     */
    @FXML
    public void guardarCambios() {
        if (personaActual == null) {
            mostrarAlerta("Error", "Debe buscar un usuario antes de guardar.");
            return;
        }

        try {
            // Actualizar datos de Persona
            personaActual.setDireccion(txtDireccion.getText());
            personaActual.setEmail(txtCorreo.getText());
            personaDAO.actualizarPersona(personaActual);

            // Actualizar datos de Cliente si existe
            if (clienteActual != null) {
                clienteActual.setTipoCliente(cmbTipoCliente.getValue());
                clienteDAO.actualizarCliente(clienteActual);
            }

            // Actualizar datos de Empleado si existe
            if (empleadoActual != null) {
                empleadoActual.setSalario_bruto(Double.parseDouble(txtSalario.getText()));
                empleadoActual.setLugar_trabajo(txtLugarTrabajo.getText());
                empleadoActual.setEstadolaboral(cmbEstadoLaboral.getValue());
                empleadoDAO.actualizarEmpleado(empleadoActual);
            }

            mostrarAlerta("Éxito", "Los datos han sido actualizados correctamente.");

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar la información.");
            e.printStackTrace();
        }
    }

    /**
     * Busca usuarios por coincidencia parcial de documento y los muestra en el ListView.
     */
    @FXML
    public void buscarUsuariosPorDocumento() {
        String documento = txtDocumento.getText().trim();

        if (documento.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un documento válido para buscar.");
            return;
        }

        try {
            List<Persona> personas = personaDAO.buscarPersonasPorDocumentoParcial(documento);
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Persona p : personas) {
                items.add(p.getDocumento_identidad() + " - " + p.getNombre());
            }

            listViewUsuarios.setItems(items);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo realizar la búsqueda.");
            e.printStackTrace();
        }
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarCampos() {
        txtDireccion.clear();
        txtCorreo.clear();
        txtSalario.clear();
        txtLugarTrabajo.clear();
        cmbTipoCliente.setValue(null);
        cmbEstadoLaboral.setValue(null);
    }

    /**
     * Muestra una alerta informativa.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
