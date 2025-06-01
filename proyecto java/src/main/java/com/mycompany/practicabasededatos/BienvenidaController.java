/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.practicabasededatos;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * Controlador para la pantalla de bienvenida.
 * Permite cambiar entre diferentes vistas (personas, tareas, habitaciones, reservas, servicios).
 */
public class BienvenidaController  {

    // Panel central donde se cargan las diferentes vistas
    @FXML
    AnchorPane anchorPaneCentre;
    // Panel principal (puede usarse para otras funcionalidades)
    @FXML
    AnchorPane anchorPanePrincipal;
    
    // Cambia la vista al formulario de alta de personas
    @FXML
    private void cambiarAltaPersonas() throws IOException {
        switchFxml("primary.fxml");
    }

    // Cambia la vista al formulario de modificación de personas
    @FXML
    private void cambiarModificarPersonas() throws IOException {
        switchFxml("modificarUsuario.fxml");
    }
    
    // Cambia la vista al formulario de creación de tareas
    @FXML
    private void cambiarCrearTarea() throws IOException {
        switchFxml("crearTarea.fxml");
    }

    // Cambia la vista al formulario de asignación de tareas
    @FXML
    private void cambiarAsignarTarea() throws IOException {
        switchFxml("asignarTarea.fxml");
    }
    
    // Cambia la vista al formulario de gestión de habitaciones
    @FXML
    private void cambiarHabitacion() throws IOException {
        switchFxml("habitacion.fxml");
    }

    // Cambia la vista al formulario de gestión de reservas
    @FXML
    private void cambiarReservas() throws IOException {
        switchFxml("reserva.fxml");
    }

    // Cambia la vista al formulario de servicios
    @FXML
    private void cambiarServicios() throws IOException{
        switchFxml("servicios.fxml");
    }
    
    /**
     * Método auxiliar para cambiar el contenido del panel central por el FXML indicado.
     * @param nomFxml nombre del archivo FXML a cargar
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void switchFxml(String nomFxml) throws IOException {
        anchorPaneCentre.getChildren().clear();
        FXMLLoader fxmlPersones = new FXMLLoader(getClass().getResource(nomFxml));
        AnchorPane vistaAcarregar = fxmlPersones.load();

        anchorPaneCentre.getChildren().add(vistaAcarregar);
    }
}
