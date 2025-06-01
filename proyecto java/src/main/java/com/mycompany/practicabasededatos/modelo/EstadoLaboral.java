/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.practicabasededatos.modelo;

/**
 * Enum que representa los distintos estados laborales que puede tener un empleado
 * dentro del sistema de gestión del hotel.
 * 
 * Los posibles estados son:
 * - ACTIVO: El empleado está trabajando activamente.
 * - BAJA: El empleado se encuentra de baja, por enfermedad u otra causa.
 * - PERMISO: El empleado está con permiso o licencia temporal.
 * 
 * @author eric_
 */
public enum EstadoLaboral {
    ACTIVO,  // Empleado activo
    BAJA,    // Empleado de baja
    PERMISO; // Empleado con permiso o licencia temporal
    
}
