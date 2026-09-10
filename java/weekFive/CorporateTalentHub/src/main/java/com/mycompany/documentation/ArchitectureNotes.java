/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.documentation;

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
    /**
     * 
 * TASK 1 - Migración a ArrayList y HashMap:
 *
 * - El arreglo fijo Employee[] fue reemplazado por una List<Employee>
 *   implementada mediante ArrayList.
 *
 * - ArrayList permite agregar y eliminar empleados dinámicamente sin
 *   establecer previamente una cantidad máxima.
 *
 * - Se utiliza HashMap<String, Employee> para relacionar el ID del empleado
 *   con el objeto Employee.
 *
 * - El ArrayList funciona como la colección principal para almacenar,
 *   listar y recorrer los empleados.
 *
 * - El HashMap se utiliza para consultar y buscar empleados utilizando
 *   directamente su ID como clave.
 *
 * - El mismo objeto Employee puede estar referenciado desde el ArrayList
 *   y desde el HashMap; no se crea una copia del empleado.
 *
 * - Al eliminar un empleado deben actualizarse ambas colecciones para
 *   mantener los datos sincronizados.
 *
 * - EmployeeRepository concentra las operaciones relacionadas con
 *   almacenamiento, búsqueda, listado y eliminación.

 * TASK 2 - Factory Methods List.of() y Map.of():
 *
 * - List.of() y Map.of() permiten crear colecciones no modificables
 *   utilizando una sintaxis compacta.
 *
 * - En el proyecto se utiliza List.of() para almacenar las tecnologías
 *   disponibles y Map.of() para relacionar códigos de sede con sus nombres.
 *
 * - Estas colecciones son apropiadas para datos de configuración porque
 *   sus elementos se definen desde el inicio y no deberían modificarse
 *   durante la ejecución.
 *
 * - A diferencia de un ArrayList o HashMap tradicional, las colecciones
 *   creadas con List.of() y Map.of() no permiten operaciones como
 *   add(), remove() o put().
 *
 * - Esto reduce el riesgo de modificar accidentalmente datos que deberían
 *   mantenerse constantes durante la ejecución del sistema.
 
  * TASK 3 - Sequenced Collections en Java 21:
 *
 * - Java 21 incorporó una API común para trabajar con colecciones que
 *   tienen un orden de encuentro definido.
 *
 * - List forma parte de esta jerarquía, por lo que nuestro List<Employee>
 *   puede utilizar getFirst(), getLast() y reversed().
 *
 * - En versiones Legacy, para obtener el primer elemento se utilizaba:
 *   empleados.get(0);
 *
 * - Para obtener el último elemento era necesario calcular manualmente
 *   el índice:
 *   empleados.get(empleados.size() - 1);
 *
 * - En Java 21 podemos utilizar directamente:
 *   empleados.getFirst();
 *   empleados.getLast();
 *
 * - Esto mejora la legibilidad porque el nombre del método expresa
 *   directamente lo que queremos obtener y evita calcular índices
 *   manualmente.
 *
 * - reversed() permite obtener una vista de la colección en el orden
 *   contrario al original sin implementar manualmente un algoritmo
 *   para recorrerla al revés.
 *
 * - reversed() no ordena por nombre, ID, salario u otro atributo.
 *   Simplemente invierte el orden de encuentro actual.
 */
}
