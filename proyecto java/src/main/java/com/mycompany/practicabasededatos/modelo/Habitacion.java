package com.mycompany.practicabasededatos.modelo;

/**
 * Clase que representa una habitación en el sistema.
 */
public class Habitacion {
    // Identificador único de la habitación
    private int id_habitacion;
    // Número de la habitación (ej: "101")
    private String numero_habitacion;
    // Tipo de habitación (ej: individual, doble, suite)
    private TipoHabitacion tipo;
    // Capacidad máxima de personas
    private int capacidad;
    // Estado actual de la habitación (ej: disponible, ocupada)
    private EstadoHabitacion estado; 
    // Descripción adicional de la habitación
    private String descripcion;
    // Precio por noche en alojamiento y desayuno
    private double precio_noche_ad;
    // Precio por noche en media pensión
    private double precio_noche_mp;

    /**
     * Constructor completo para crear una habitación con todos los atributos.
     */
    public Habitacion(int id_habitacion, String numero_habitacion, TipoHabitacion tipo, int capacidad,
                      EstadoHabitacion estado, String descripcion, double precio_noche_ad, double precio_noche_mp) {
        this.id_habitacion = id_habitacion;
        this.numero_habitacion = numero_habitacion;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado;
        this.descripcion = descripcion;
        this.precio_noche_ad = precio_noche_ad;
        this.precio_noche_mp = precio_noche_mp;
    }
    
    /**
     * Constructor alternativo (no implementado) para mostrar habitaciones usando tipo como String.
     * Lanza una excepción si se utiliza.
     */
    public Habitacion(int id_habitacion, String numero_habitacion, String tipo, int capacidad, EstadoHabitacion estado, String descripcion, double precio_noche_ad, double precio_noche_mp) {
        throw new UnsupportedOperationException("No soportado Habitacion"); 
    }

    // Getters

    /**
     * Obtiene el ID de la habitación.
     */
    public int getId_habitacion() {
        return id_habitacion;
    }

    /**
     * Obtiene el número de la habitación.
     */
    public String getNumero_habitacion() {
        return numero_habitacion;
    }

    /**
     * Obtiene el tipo de la habitación.
     */
    public TipoHabitacion getTipo() {
        return tipo;
    }

    /**
     * Obtiene la capacidad máxima de la habitación.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Obtiene el estado actual de la habitación.
     */
    public EstadoHabitacion getEstado() {
        return estado;
    }

    /**
     * Obtiene la descripción de la habitación.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el precio por noche en alojamiento y desayuno.
     */
    public double getPrecio_noche_ad() {
        return precio_noche_ad;
    }

    /**
     * Obtiene el precio por noche en media pensión.
     */
    public double getPrecio_noche_mp() {
        return precio_noche_mp;
    }
    
    // Setters

    /**
     * Establece el ID de la habitación.
     */
    public void setId_habitacion(int id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    /**
     * Establece el número de la habitación.
     */
    public void setNumero_habitacion(String numero_habitacion) {
        this.numero_habitacion = numero_habitacion;
    }

    /**
     * Establece el tipo de la habitación.
     */
    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    /**
     * Establece la capacidad máxima de la habitación.
     */
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Establece el estado de la habitación.
     */
    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    /**
     * Establece la descripción de la habitación.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Establece el precio por noche en alojamiento y desayuno.
     */
    public void setPrecio_noche_ad(double precio_noche_ad) {
        this.precio_noche_ad = precio_noche_ad;
    }

    /**
     * Establece el precio por noche en media pensión.
     */
    public void setPrecio_noche_mp(double precio_noche_mp) {
        this.precio_noche_mp = precio_noche_mp;
    }
}
