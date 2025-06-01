/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.practicabasededatos.modelo;

/**
 * Enum que representa los diferentes estados que puede tener una habitación
 * en el sistema de gestión del hotel.
 * 
 * Los estados posibles son:
 * - DISPONIBLE: La habitación está libre para ser ocupada.
 * - OCUPADA: La habitación está actualmente ocupada por un cliente.
 * - LIMPIEZA: La habitación está en proceso de limpieza.
 * - RESERVADA: La habitación ha sido reservada y está reservada para un cliente.
 * 
 * @author eric_
 */
public enum EstadoHabitacion {
    DISPONIBLE, // Habitación libre
    OCUPADA,    // Habitación ocupada
    LIMPIEZA,   // Habitación en limpieza
    RESERVADA;  // Habitación reservada
    
}
