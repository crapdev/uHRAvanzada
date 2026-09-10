package com.riwi.talent.model;


import java.util.List;

public interface EmployeeDAO {
    boolean insertar(Employee employee);

    List<Employee> listar();

    boolean actualizar(Employee employee);

    boolean eliminar(int id);

    List<PerformanceReport> generarReporteDesempeno();

}
