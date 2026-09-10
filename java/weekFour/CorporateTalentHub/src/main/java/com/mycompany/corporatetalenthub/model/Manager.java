/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.corporatetalenthub.model;

/**
 *
 * @author cristian
 */
public final class Manager extends Employee implements Promotable{
    private double monthlyBudget;
    private double percentageIncrease = 0.10;
    
    public Manager( int id, String nombre, byte edad, double salario, double[] calificaciones, double monthlyBudget) {
        super(id, nombre, edad, salario, calificaciones);
        this.monthlyBudget = monthlyBudget;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }
    
    @Override
    public double calcularBonoAscenso(){
        return calcularPorcentajeSalario(percentageIncrease);
    }
            
}
