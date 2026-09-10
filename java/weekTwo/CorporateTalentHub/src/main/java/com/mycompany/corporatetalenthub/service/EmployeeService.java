package com.mycompany.corporatetalenthub.service;

import com.mycompany.corporatetalenthub.model.Employee;
import com.mycompany.corporatetalenthub.repository.EmployeeRepository;
import java.util.List;
import java.util.Map;


public class EmployeeService {
    
    private static final int CANTIDAD_TRIMESTRES = 3;
    private static final double PROMEDIO_PARA_PROMOCION = 80.0;
    private static final double DESEMPENO_MINIMO = 60.0;
    private final EmployeeRepository repository;
    
    private static final List<String> TECNOLOGIAS = List.of( "Java", "Spring Boot", "PostgreSQL");
    private static final Map<String, String> SEDES = Map.of( "BAQ", "Barranquilla", "BOG", "Bogotá", "MED", "Medellín");
    
    //contructor
    public EmployeeService() {
        this.repository = new EmployeeRepository();
    }
    
    // Getters
    public List<String> obtenerTecnologias() {
        return TECNOLOGIAS;
    }

    public Map<String, String> obtenerSedes() {
        return SEDES;
    }
    
    
    public boolean registrarEmpleado(Employee employee) {
        return repository.guardar(employee);
    }
    
    public boolean existeEmpleado(int id) {
        return repository.existePorId(id);
    }
    
    public Employee buscarEmpleado(int id) {
        return repository.buscarPorId(id);
    }

    public List<Employee> listarEmpleados() {
        return repository.listar();
    }


    public boolean eliminarEmpleado(int id) {
        return repository.eliminarPorId(id);
    }
    
    public double calcularPromedio(double[] calificaciones) {
        var suma = 0.0;

        for (var calificacion : calificaciones) {
            suma += calificacion;
        }
        
        return suma / CANTIDAD_TRIMESTRES;
    }
    

    public String obtenerEstadoPromocion(double promedio) {
        // Operador ternario solicitado por la tarea.
        return promedio >= PROMEDIO_PARA_PROMOCION ? "PROMOVIDO" : "NO PROMOVIDO";
    }

    public String obtenerCategoriaSalarial(double salario) {
        var rango = determinarRangoSalarial(salario);

        return switch (rango) {
            case 1 -> "JUNIOR";
            case 2 -> "SEMISENIOR";
            case 3 -> "SENIOR";
            case 4 -> "LÍDER";
            default -> throw new IllegalArgumentException("Rango salarial no reconocido: " + rango);
        };
    }

    private int determinarRangoSalarial(double salario) {

        if (salario < 2_000_000.0) {
            return 1;

        } else if (salario < 4_000_000.0) {
            return 2;

        } else if (salario < 7_000_000.0) {
            return 3;

        } else {
            return 4;
        }
    }
    
    public Employee obtenerPrimerEmpleado() {
        return repository.obtenerPrimero();
    }

    public Employee obtenerUltimoEmpleado() {
        return repository.obtenerUltimo();
    }

    public List<Employee> listarEmpleadosInvertidos() {
        return repository.listarInvertido();
    }
    
    public void filtrarEmpleadosPorDesempeno() {
        repository.eliminarPorDesempeno(DESEMPENO_MINIMO);
    }


    public double obtenerDesempenoMinimo() {
        return DESEMPENO_MINIMO;
    }


    public int obtenerTotalEmpleados() {
        return repository.listar().size();
    }


    public double calcularPromedioSalarios() {

        var empleados = repository.listar();

        if (empleados.isEmpty()) {
            return 0.0;
        }

        var sumaSalarios = 0.0;

        for (var empleado : empleados) {
            sumaSalarios += empleado.getSalario();
        }

        return sumaSalarios / empleados.size();
    }
    
}
