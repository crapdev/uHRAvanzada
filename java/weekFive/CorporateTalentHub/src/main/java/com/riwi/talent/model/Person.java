
package com.riwi.talent.model;

/*
Las sealed clases ofrecen más seguridad porque estas obligado a decir quien hereda de quien
Así no hay herencias abiertas y no se dejan puertas a que cualquier clase externa extienda el código.
y hacerlo abstracta y sealed hago que solo puedan instanciarse personas a traves de empleados y consultor externo ( que es la forma como se busca ).

 Las Sealed Classes hacen más seguro el diseño de una API porque evitan que
 clases no autorizadas extiendan tipos importantes del dominio y permiten
 conocer de forma controlada cuáles son sus subtipos permitidos.
*/
public abstract sealed class Person permits Employee, ExternalConsultant{
    
    private int id;
    private String nombre;
    private byte edad;

    public Person(int id, String nombre, byte edad) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
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
    
    
}
