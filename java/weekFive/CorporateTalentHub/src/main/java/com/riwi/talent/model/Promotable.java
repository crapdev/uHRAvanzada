
package com.riwi.talent.model;

public interface Promotable {
    
    double calcularBonoAscenso();
    
    // Esto lo que nos permite a que este metodo no sea obligatorio implementar en 
    default void registrarLogPromocion(){
        System.out.println("Operacion de promocion registrada. ");
    }
}
