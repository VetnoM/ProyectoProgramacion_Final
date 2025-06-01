package com.mycompany.practicabasededatos.modelo;

import java.sql.Date;

/**
 * Clase que representa una persona en el sistema.
 */
public class Persona {

    // Identificador único de la persona
    private int id_persona;
    // Documento de identidad (DNI, pasaporte, etc.)
    private String documento_identidad;
    // Nombre de la persona
    private String nombre;
    // Apellido de la persona
    private String apellido;
    // Fecha de nacimiento
    private Date fecha_nacimiento;
    // Teléfono de contacto
    private String telefono;
    // Correo electrónico
    private String email;
    // Dirección de residencia
    private String direccion;

    /**
     * Constructor completo para crear una persona con todos los atributos.
     */
    public Persona(int id_persona, String documento_identidad, String nombre, String apellido, Date fecha_nacimiento, String telefono, String email, String direccion) {
        this.id_persona = id_persona;
        this.documento_identidad = documento_identidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha_nacimiento = fecha_nacimiento;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    /**
     * Constructor con solo id_persona, documento_identidad, dirección y email.
     */
    public Persona(int id_persona, String documento_identidad, String direccion, String email) {
        this.id_persona = id_persona;
        this.documento_identidad = documento_identidad;
        this.direccion = direccion;
        this.email = email;
    }

    /**
     * Constructor vacío.
     */
    public Persona() {
    }

    /**
     * Constructor con solo id_persona.
     */
    public Persona(int id_persona) {
        this.id_persona = id_persona;
    }

    // Getters y Setters

    /**
     * Obtiene el ID de la persona.
     */
    public int getId_persona() {
        return id_persona;
    }

    /**
     * Establece el ID de la persona.
     */
    public void setId_persona(int id_persona) {
        this.id_persona = id_persona;
    }

    /**
     * Obtiene el documento de identidad.
     */
    public String getDocumento_identidad() {
        return documento_identidad;
    }

    /**
     * Establece el documento de identidad.
     */
    public void setDocumento_identidad(String documento_identidad) {
        this.documento_identidad = documento_identidad;
    }

    /**
     * Obtiene el nombre de la persona.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la persona.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido de la persona.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido de la persona.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene la fecha de nacimiento.
     */
    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    /**
     * Establece la fecha de nacimiento.
     */
    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    /**
     * Obtiene el teléfono de la persona.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono de la persona.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el email de la persona.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email de la persona.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la dirección de la persona.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la persona.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
