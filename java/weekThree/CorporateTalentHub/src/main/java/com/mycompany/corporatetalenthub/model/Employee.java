
package com.mycompany.corporatetalenthub.model;

/**
 * Modelo tradicional compatible con la sintaxis de Java 8.
 *
 * Esta clase es más verbosa que un Record porque declara campos, constructor,
 * getters, setter y métodos explícitamente. Esa verbosidad es útil cuando el
 * objeto necesita estado mutable, como bonoMensual o nombre.
 */

public class Employee {
    private final int id;
    private final String nombre;
    private final byte edad;
    private final double salario;
    private final double[] calificaciones;
    private double promedioDesempeno;

    public Employee(int id, String nombre, byte edad, double salario, double [] calificaciones) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.salario = salario;
        this.calificaciones = calificaciones;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public byte getEdad() {
        return edad;
    }

    public double getSalario() {
        return salario;
    }

    public double getPromedioDesempeno() {
        return promedioDesempeno;
    }
    public double[] getCalificaciones() {
        return calificaciones;
    }
    
    public void setPromedioDesempeno(double promedioDesempeno) {
        this.promedioDesempeno = promedioDesempeno;
    }
    
}
