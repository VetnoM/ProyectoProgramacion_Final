package com.mycompany.practicabasededatos.modelo;

import java.time.LocalDate;

/**
 * Clase que representa una reserva en el sistema.
 */
public class Reserva {
    // Identificador único de la reserva
    private int id_reserva;
    // Tipo de reserva (por ejemplo, individual, grupal, etc.)
    private TipoReserva tipo_reserva;
    // Fecha de inicio de la reserva
    private LocalDate fecha_inicio;
    // Fecha de fin de la reserva
    private LocalDate fecha_fin;
    // Fecha en la que se realizó la reserva
    private LocalDate fecha_reserva;
    // Precio total de la reserva
    private double precio_total;
    // Documento de identidad del cliente (opcional, para algunos constructores)
    private String documento_identidad;
    // ID del cliente asociado a la reserva
    private int id_cliente;
    // ID de la habitación reservada
    private int id_habitacion;
    // Estado de la reserva (ej: activa, cancelada, facturada)
    private String estado;
    // Número de la habitación reservada (campo adicional para mostrar información)
    private String numeroHabitacion;

    /**
     * Constructor completo para crear una reserva con ID de cliente.
     */
    public Reserva(int id_reserva, TipoReserva tipo_reserva, LocalDate fecha_inicio, LocalDate fecha_fin,
            LocalDate fecha_reserva, double precio_total, int id_cliente, int id_habitacion,
            String estado, String numeroHabitacion) {
        this.id_reserva = id_reserva;
        this.tipo_reserva = tipo_reserva;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.fecha_reserva = fecha_reserva;
        this.precio_total = precio_total;
        this.id_cliente = id_cliente;
        this.id_habitacion = id_habitacion;
        this.estado = estado;
        this.numeroHabitacion = numeroHabitacion;
    }

    /**
     * Constructor alternativo para crear una reserva con documento de identidad.
     */
    public Reserva(int id_reserva, TipoReserva tipo_reserva, LocalDate fecha_inicio, LocalDate fecha_fin,
            LocalDate fecha_reserva, double precio_total, String documento_identidad, int id_habitacion,
            String estado, String numeroHabitacion) {
        this.id_reserva = id_reserva;
        this.tipo_reserva = tipo_reserva;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.fecha_reserva = fecha_reserva;
        this.precio_total = precio_total;
        this.documento_identidad = documento_identidad;
        this.id_habitacion = id_habitacion;
        this.estado = estado;
        this.numeroHabitacion = numeroHabitacion;
    }

    // Getters y Setters

    /**
     * Obtiene el ID de la reserva.
     */
    public int getId_reserva() {
        return id_reserva;
    }

    /**
     * Establece el ID de la reserva.
     */
    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    /**
     * Obtiene el tipo de reserva.
     */
    public TipoReserva getTipo_reserva() {
        return tipo_reserva;
    }

    /**
     * Establece el tipo de reserva.
     */
    public void setTipo_reserva(TipoReserva tipo_reserva) {
        this.tipo_reserva = tipo_reserva;
    }

    /**
     * Obtiene la fecha de inicio de la reserva.
     */
    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }

    /**
     * Establece la fecha de inicio de la reserva.
     */
    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    /**
     * Obtiene la fecha de fin de la reserva.
     */
    public LocalDate getFecha_fin() {
        return fecha_fin;
    }

    /**
     * Establece la fecha de fin de la reserva.
     */
    public void setFecha_fin(LocalDate fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    /**
     * Obtiene la fecha en la que se realizó la reserva.
     */
    public LocalDate getFecha_reserva() {
        return fecha_reserva;
    }

    /**
     * Establece la fecha en la que se realizó la reserva.
     */
    public void setFecha_reserva(LocalDate fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    /**
     * Obtiene el precio total de la reserva.
     */
    public double getPrecio_total() {
        return precio_total;
    }

    /**
     * Establece el precio total de la reserva.
     */
    public void setPrecio_total(double precio_total) {
        this.precio_total = precio_total;
    }

    /**
     * Obtiene el documento de identidad del cliente.
     */
    public String getDocumento_identidad() {
        return documento_identidad;
    }

    /**
     * Establece el documento de identidad del cliente.
     */
    public void setDocumento_identidad(String documento_identidad) {
        this.documento_identidad = documento_identidad;
    }

    /**
     * Establece el ID del cliente asociado a la reserva.
     */
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    /**
     * Obtiene el ID de la habitación reservada.
     */
    public int getId_habitacion() {
        return id_habitacion;
    }

    /**
     * Establece el ID de la habitación reservada.
     */
    public void setId_habitacion(int id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    /**
     * Obtiene el estado de la reserva.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la reserva.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el ID del cliente asociado a la reserva.
     */
    public int getId_cliente() {
        return id_cliente;
    }

    /**
     * Obtiene el número de la habitación reservada.
     */
    public String getNumeroHabitacion() {
        return numeroHabitacion;
    }

    /**
     * Establece el número de la habitación reservada.
     */
    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }
}
