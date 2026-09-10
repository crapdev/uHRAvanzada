package com.riwi.talent.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeService {

    private static final int CANTIDAD_TRIMESTRES = 3;
    private static final double PROMEDIO_PARA_PROMOCION = 80.0;
    private static final double DESEMPENO_MINIMO = 60.0;

    private static final List<String> TECNOLOGIAS = List.of("Java", "Spring Boot", "PostgreSQL");
    private static final Map<String, String> SEDES = Map.of("BAQ", "Barranquilla", "BOG", "Bogotá", "MED", "Medellín");

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAOImpl();
    }


    public List<String> obtenerTecnologias() {
        return TECNOLOGIAS;
    }
    public Map<String, String> obtenerSedes() {
        return SEDES;
    }

    public String obtenerEstadoPromocion(double promedio) {
        return promedio >= PROMEDIO_PARA_PROMOCION ? "PROMOVIDO" : "NO PROMOVIDO";
    }

    public double calcularPromedio(double[] calificaciones) {
        double suma = 0;
        for (double calificacion : calificaciones) {
            suma += calificacion;
        }
        return suma / CANTIDAD_TRIMESTRES;
    }

    public String obtenerCategoriaSalarial(double salario) {
        int rango = determinarRangoSalarial(salario);

        return switch (rango) {
            case 1 -> "JUNIOR";
            case 2 -> "SEMISENIOR";
            case 3 -> "SENIOR";
            case 4 -> "LÍDER";
            default -> throw new IllegalArgumentException("Rango salarial no reconocido");
        };
    }

    private int determinarRangoSalarial(double salario) {
        if (salario < 2_000_000) return 1;
        if (salario < 4_000_000) return 2;
        if (salario < 7_000_000) return 3;
        return 4;
    }

    public double obtenerDesempenoMinimo() {
        return DESEMPENO_MINIMO;
    }


    public Employee obtenerPrimerEmpleado() {
        List<Employee> empleados = employeeDAO.listar();
        return empleados.isEmpty() ? null : empleados.getFirst();
    }

    public Employee obtenerUltimoEmpleado() {
        List<Employee> empleados = employeeDAO.listar();
        return empleados.isEmpty() ? null : empleados.getLast();
    }

    public List<Employee> listarEmpleadosInvertidos() {
        return new ArrayList<>(employeeDAO.listar().reversed());
    }

    public void filtrarEmpleadosPorDesempeno() {
        List<Employee> empleados = employeeDAO.listar();

        for (Employee employee : empleados) {
            if (employee.getPromedioDesempeno() < DESEMPENO_MINIMO) {
                employeeDAO.eliminar(employee.getId());
            }
        }
    }

    public PerformanceReport generarReporteDesempeno(Employee employee) {
        double promedio = employee.getPromedioDesempeno();
        String feedback = promedio >= PROMEDIO_PARA_PROMOCION ? "Excelente desempeño" : "Debe continuar mejorando";

        return new PerformanceReport(employee.getId(), promedio, feedback);
    }

    public String obtenerInformacionRol(Employee employee) {
        if (employee instanceof Developer developer) {
            return "Lenguaje principal: " + developer.getMainLenguaje();
        }

        if (employee instanceof Manager manager) {
            return "Presupuesto mensual: " + manager.getMonthlyBudget();
        }

        return "Tipo de empleado no conocido";
    }

    public double calcularBonoPromocionable(Promotable promotable) {
        promotable.registrarLogPromocion();
        return promotable.calcularBonoAscenso();
    }

    //CRUD
    public boolean registrarEmpleado(Employee employee) {
        return employeeDAO.insertar(employee);
    }

    public List<Employee> listarEmpleados() {
        return employeeDAO.listar();
    }

    public boolean eliminarEmpleado(int id) {
        return employeeDAO.eliminar(id);
    }

    public boolean actualizarEmpleado(Employee employee) {
        return employeeDAO.actualizar(employee);
    }

    public boolean existeEmpleado(int id) {
        return buscarEmpleado(id) != null;
    }

    public Employee buscarEmpleado(int id) {
        for (Employee employee : employeeDAO.listar()) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    public List<PerformanceReport> obtenerReportesDesempeno() {
        return employeeDAO.generarReporteDesempeno();
    }
}
