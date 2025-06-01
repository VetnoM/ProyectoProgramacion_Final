package com.mycompany.practicabasededatos.modelo;

import java.util.Date;

/**
 * Clase que representa una tarea en el sistema.
 */
public class Tarea {
    // Identificador único de la tarea
    private int id_tarea;
    // Descripción de la tarea
    private String descripcion;
    // Estado actual de la tarea (PENDIENTE, EN_PROCESO, COMPLETADA, etc.)
    private EstadoTarea estadoTarea;
    // Fecha de creación de la tarea
    private Date fecha_creacion;
    // Fecha de ejecución programada de la tarea
    private Date fecha_ejecucion;

    /**
     * Constructor completo para crear una tarea con todos los atributos.
     */
    public Tarea(int id_tarea, String descripcion, EstadoTarea estadoTarea, Date fecha_creacion, Date fecha_ejecucion) {
        this.id_tarea = id_tarea;
        this.descripcion = descripcion;
        this.estadoTarea = estadoTarea;
        this.fecha_creacion = fecha_creacion;
        this.fecha_ejecucion = fecha_ejecucion;
    }

    /**
     * Constructor vacío.
     */
    public Tarea() {
    }

    // Getters y Setters

    /**
     * Obtiene el ID de la tarea.
     */
    public int getId_tarea() {
        return id_tarea;
    }

    /**
     * Establece el ID de la tarea.
     */
    public void setId_tarea(int id_tarea) {
        this.id_tarea = id_tarea;
    }

    /**
     * Obtiene la descripción de la tarea.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la tarea.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el estado actual de la tarea.
     */
    public EstadoTarea getEstadoTarea() {
        return estadoTarea;
    }

    /**
     * Establece el estado actual de la tarea.
     */
    public void setEstadoTarea(EstadoTarea estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    /**
     * Obtiene la fecha de creación de la tarea.
     */
    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    /**
     * Establece la fecha de creación de la tarea.
     */
    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    /**
     * Obtiene la fecha de ejecución de la tarea.
     */
    public Date getFecha_ejecucion() {
        return fecha_ejecucion;
    }

    /**
     * Establece la fecha de ejecución de la tarea.
     */
    public void setFecha_ejecucion(Date fecha_ejecucion) {
        this.fecha_ejecucion = fecha_ejecucion;
    }

    /**
     * Devuelve una representación en texto de la tarea.
     */
    @Override
    public String toString() {
        return descripcion + " | Estado: " + estadoTarea;
    }
}