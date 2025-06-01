package com.mycompany.practicabasededatos.modelo;

import java.time.LocalDate;

/**
 * Clase que representa una factura en el sistema.
 */
public class Factura {
    // Atributos de la factura
    private int id_factura; // Identificador único de la factura
    private LocalDate fecha_emision; // Fecha en que se emite la factura
    private String metodo_pago; // Método de pago utilizado
    private double base_imponible; // Base imponible de la factura
    private double iva; // IVA aplicado
    private double total; // Total de la factura
    private int id_reserva; // Identificador de la reserva asociada

    /**
     * Constructor para crear una factura sin ID (por ejemplo, antes de guardar en BD).
     */
    public Factura(LocalDate fecha_emision, String metodo_pago, double base_imponible, double iva, double total, int id_reserva) {
        this.fecha_emision = fecha_emision;
        this.metodo_pago = metodo_pago;
        this.base_imponible = base_imponible;
        this.iva = iva;
        this.total = total;
        this.id_reserva = id_reserva;
    }

    /**
     * Constructor para crear una factura con ID (por ejemplo, al recuperar de BD).
     */
    public Factura(int id_factura, LocalDate fecha_emision, String metodo_pago, double base_imponible, double iva, double total, int id_reserva) {
        this.id_factura = id_factura;
        this.fecha_emision = fecha_emision;
        this.metodo_pago = metodo_pago;
        this.base_imponible = base_imponible;
        this.iva = iva;
        this.total = total;
        this.id_reserva = id_reserva;
    }

    // Getters y Setters

    /**
     * Obtiene el ID de la factura.
     */
    public int getId_factura() {
        return id_factura;
    }

    /**
     * Establece el ID de la factura.
     */
    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    /**
     * Obtiene la fecha de emisión de la factura.
     */
    public LocalDate getFecha_emision() {
        return fecha_emision;
    }

    /**
     * Establece la fecha de emisión de la factura.
     */
    public void setFecha_emision(LocalDate fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    /**
     * Obtiene el método de pago.
     */
    public String getMetodo_pago() {
        return metodo_pago;
    }

    /**
     * Establece el método de pago.
     */
    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    /**
     * Obtiene la base imponible.
     */
    public double getBase_imponible() {
        return base_imponible;
    }

    /**
     * Establece la base imponible.
     */
    public void setBase_imponible(double base_imponible) {
        this.base_imponible = base_imponible;
    }

    /**
     * Obtiene el IVA.
     */
    public double getIva() {
        return iva;
    }

    /**
     * Establece el IVA.
     */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /**
     * Obtiene el total de la factura.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el total de la factura.
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Obtiene el ID de la reserva asociada.
     */
    public int getId_reserva() {
        return id_reserva;
    }

    /**
     * Establece el ID de la reserva asociada.
     */
    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }
}