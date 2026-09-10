/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.corporatetalenthub.documentation;

/**
 * Notas de arquitectura del proyecto Corporate Talent Hub.
 *
 * Java 8 (enfoque Legacy):
 * - Las entidades de datos se implementan normalmente con clases tradicionales.
 * - El desarrollador escribe constructor, getters, setters, equals, hashCode y toString.
 * - No existen Records ni Text Blocks.
 * - Las NullPointerException suelen mostrar una ubicación, pero no explican con
 *   precisión qué referencia de una expresión era null.
 *
 * Java 17/21 (enfoque LTS moderno):
 * - Permite Records para representar datos de forma breve e inmutable.
 * - Permite Text Blocks para escribir texto de varias líneas legible.
 * - Incluye Helpful NullPointerExceptions, que describen mejor la causa del null.
 * - Mantiene compatibilidad con clases tradicionales cuando el dominio requiere
 *   estado mutable o comportamiento adicional.
 *
 * JVM, Heap y Garbage Collector:
 * - javac compila el código fuente a bytecode (.class).
 * - La JVM carga y ejecuta ese bytecode en el sistema operativo correspondiente.
 * - Los objetos creados con new se administran normalmente en el Heap.
 * - Las variables locales pueden contener referencias que permiten acceder a esos objetos.
 * - Cuando un objeto deja de ser alcanzable desde referencias activas, queda elegible
 *   para recolección. El Garbage Collector recupera su memoria automáticamente.
 * - El programador no libera objetos manualmente ni puede garantizar el instante
 *   exacto en el que el Garbage Collector los eliminará.
 */
public class ArchitectureNotes {
    
}
