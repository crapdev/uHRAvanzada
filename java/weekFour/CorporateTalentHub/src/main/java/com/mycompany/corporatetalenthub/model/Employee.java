
package com.mycompany.corporatetalenthub.model;

/**
 * Modelo tradicional compatible con la sintaxis de Java 8.
 *
 * Esta clase es más verbosa que un Record porque declara campos, constructor,
 * getters, setter y métodos explícitamente. Esa verbosidad es útil cuando el
 * objeto necesita estado mutable, como bonoMensual o nombre.
 */

public abstract non-sealed class Employee extends Person{

    
    private double salario;
    private double[] calificaciones;
    private double promedioDesempeno;

    public Employee(int id, String nombre, byte edad, double salario, double [] calificaciones) {
        super(id, nombre, edad);
        
        this.salario = salario;
        this.calificaciones = calificaciones;
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
    
    //Uso de protected | Task 4
    protected double calcularPorcentajeSalario(double porcentage){
        return getSalario() * porcentage;
    }
}
