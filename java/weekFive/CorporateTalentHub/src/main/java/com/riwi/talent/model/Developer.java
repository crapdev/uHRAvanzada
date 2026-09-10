package com.riwi.talent.model;

/**
 *
 * @author cristian
 */
public final class Developer extends Employee implements Promotable{
    private String mainLenguaje;
    private double percentageIncrease = 0.15;
    
    public Developer(int id, String nombre, byte edad, double salario, double[] calificaciones, String mainLenguaje) {
        super(id, nombre, edad, salario, calificaciones);
        this.mainLenguaje = mainLenguaje;
    }

    public String getMainLenguaje() {
        return mainLenguaje;
    }

    public void setMainLenguaje(String mainLenguaje) {
        this.mainLenguaje = mainLenguaje;
    }
    
    @Override
    public double calcularBonoAscenso(){
        return calcularPorcentajeSalario(percentageIncrease);   
    }
}
