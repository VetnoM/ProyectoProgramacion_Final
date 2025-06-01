package com.mycompany.practicabasededatos.modelo;

import java.sql.Date;

// Clase que representa a un empleado, que hereda de la clase Persona
public class Empleado extends Persona {
    
    // Atributos específicos de la clase Empleado
    private int id_empleado; // Identificador único del empleado
    private String lugar_trabajo; // Lugar de trabajo del empleado
    private double salario_bruto; // Salario bruto del empleado
    private EstadoLaboral estadolaboral; // Estado laboral del empleado (activo, baja, etc.)
    private Date fecha_contratacion; // Fecha de contratación del empleado

    // Constructor completo que inicializa todos los atributos
    public Empleado(int id_persona, String lugar_trabajo, double salario_bruto, EstadoLaboral estadolaboral, Date fecha_contratacion) {
        super(id_persona); // Llamar al constructor de la clase base (Persona)
        this.lugar_trabajo = lugar_trabajo; // Inicializar lugar de trabajo
        this.salario_bruto = salario_bruto; // Inicializar salario bruto
        this.estadolaboral = estadolaboral; // Inicializar estado laboral
        this.fecha_contratacion = fecha_contratacion; // Inicializar fecha de contratación
    }

    // Constructor que permite inicializar atributos de Persona y otros atributos de Empleado
    public Empleado(int id_persona, String documento, String direccion, String email, 
                    double salario_bruto, String lugarTrabajo, EstadoLaboral estadoLaboral) {
        super(id_persona, documento, direccion, email); // Llamar al constructor de la clase base (Persona)
        this.salario_bruto = salario_bruto; // Inicializar salario bruto
        this.lugar_trabajo = lugarTrabajo; // Inicializar lugar de trabajo
        this.estadolaboral = estadoLaboral; // Inicializar estado laboral
    }

    // Constructor vacío
    public Empleado() {
    }

    // Getters para acceder a los atributos privados
    public int getId_empleado() {
        return id_empleado; // Retornar el ID del empleado
    }

    public String getLugar_trabajo() {
        return lugar_trabajo; // Retornar el lugar de trabajo
    }

    public double getSalario_bruto() {
        return salario_bruto; // Retornar el salario bruto
    }

    public EstadoLaboral getEstadolaboral() {
        return estadolaboral; // Retornar el estado laboral
    }

    public Date getFecha_contratacion() {
        return fecha_contratacion; // Retornar la fecha de contratación
    }

    // Setters para modificar los atributos privados
    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado; // Establecer el ID del empleado
    }

    public void setLugar_trabajo(String lugar_trabajo) {
        this.lugar_trabajo = lugar_trabajo; // Establecer el lugar de trabajo
    }

    public void setSalario_bruto(double salario_bruto) {
        this.salario_bruto = salario_bruto; // Establecer el salario bruto
    }

    public void setEstadolaboral(EstadoLaboral estadolaboral) {
        this.estadolaboral = estadolaboral; // Establecer el estado laboral
    }

    public void setFecha_contratacion(Date fecha_contratacion) {
        this.fecha_contratacion = fecha_contratacion; // Establecer la fecha de contratación
    }

    // Método toString para representar al empleado como una cadena
    @Override
    public String toString() {
        return (getDocumento_identidad() != null ? getDocumento_identidad() : "Sin Documento") + " - " +
               (getApellido() != null ? getApellido() : "Apellido Desconocido") + ", " +
               (getNombre() != null ? getNombre() : "Nombre Desconocido");
    }
}
