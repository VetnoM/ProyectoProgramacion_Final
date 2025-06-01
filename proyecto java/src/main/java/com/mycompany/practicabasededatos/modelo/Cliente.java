package com.mycompany.practicabasededatos.modelo;

import java.sql.Date;

// Clase que representa a un cliente, que hereda de la clase Persona
public class Cliente extends Persona {

    // Atributos específicos de la clase Cliente
    private int id_cliente; // Identificador único del cliente
    private Date fecha_registro; // Fecha de registro del cliente
    private String tarjeta_credito; // Número de tarjeta de crédito del cliente
    private TipoCliente tipoCliente; // Tipo de cliente (Regular, VIP, etc.)

    // Constructor completo que inicializa todos los atributos
    public Cliente(int id_persona, Date fecha_registro, String tarjeta_credito, TipoCliente tipoCliente) {
        super(id_persona); // Llamar al constructor de la clase base (Persona)
        this.fecha_registro = fecha_registro; // Inicializar fecha de registro
        this.tarjeta_credito = tarjeta_credito; // Inicializar tarjeta de crédito
        this.tipoCliente = tipoCliente; // Inicializar tipo de cliente
    }

    // Constructor que permite inicializar atributos de Persona y tipo de cliente
    public Cliente(int id_persona, String documento, String direccion, String email, TipoCliente tipoCliente) {
        super(id_persona, documento, direccion, email); // Llamar al constructor de la clase base (Persona)
        this.tipoCliente = tipoCliente; // Inicializar tipo de cliente
    }

    // Constructor vacío
    public Cliente() {
    }

    // Getters para acceder a los atributos privados
    public int getId_cliente() {
        return id_cliente; // Retornar el ID del cliente
    }

    public Date getFecha_registro() {
        return fecha_registro; // Retornar la fecha de registro
    }

    public String getTarjeta_credito() {
        return tarjeta_credito; // Retornar el número de tarjeta de crédito
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente; // Retornar el tipo de cliente
    }

    // Setters para modificar los atributos privados
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente; // Establecer el ID del cliente
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro; // Establecer la fecha de registro
    }

    public void setTarjeta_credito(String tarjeta_credito) {
        this.tarjeta_credito = tarjeta_credito; // Establecer el número de tarjeta de crédito
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente; // Establecer el tipo de cliente
    }
}
