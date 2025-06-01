/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.practicabasededatos.modelo;

/**
 * Enum que representa los diferentes estados que puede tener una tarea
 * dentro del sistema de gestión del hotel.
 * 
 * Los estados posibles son:
 * - PENDIENTE: La tarea está pendiente de ser iniciada.
 * - EN_PROCESO: La tarea está actualmente en ejecución.
 * - COMPLETADA: La tarea ha sido finalizada con éxito.
 * - CANCELADA: La tarea ha sido cancelada y no se completará.
 * 
 * @author eric_
 */
public enum EstadoTarea {
    PENDIENTE,  // Tarea pendiente
    EN_PROCESO, // Tarea en ejecución
    COMPLETADA, // Tarea finalizada
    CANCELADA   // Tarea cancelada
}
