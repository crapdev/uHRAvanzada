/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.corporatetalenthub.model;

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
