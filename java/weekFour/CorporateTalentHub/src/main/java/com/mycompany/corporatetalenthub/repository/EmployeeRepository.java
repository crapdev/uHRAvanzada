package com.mycompany.corporatetalenthub.repository;

import com.mycompany.corporatetalenthub.model.Employee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeRepository {
    
    private final List<Employee> empleados;
    private final HashMap<String, Employee> empleadosPorId;
    
    // Constructor
    public EmployeeRepository() {
        this.empleados = new ArrayList<>();
        this.empleadosPorId = new HashMap<>();
    }
    
    //Methods
    
     public boolean guardar(Employee employee) {
         
        var id = String.valueOf(employee.getId());
        if (empleadosPorId.containsKey(id)) {
            return false;
        }

        empleados.add(employee);
        empleadosPorId.put(id, employee);

        return true;
    }


    public List<Employee> listar() {
        return new ArrayList<>(empleados);
    }

    public Employee buscarPorId(int id) {

        var clave = String.valueOf(id);
        return empleadosPorId.get(clave);
    }

    public boolean existePorId(int id) {

        var clave = String.valueOf(id);
        return empleadosPorId.containsKey(clave);
    }

    public boolean eliminarPorId(int id) {

        var clave = String.valueOf(id);
        var employee = empleadosPorId.remove(clave);

        if (employee == null) {
            return false;
        }

        empleados.remove(employee);

        return true;
    }
    
    
    public Employee obtenerPrimero() {
        if (empleados.isEmpty()) {
            return null;
        }
        return empleados.getFirst();
    }


    public Employee obtenerUltimo() {

        if (empleados.isEmpty()) {
            return null;
        }
        return empleados.getLast();
    }

    public List<Employee> listarInvertido() {
        return new ArrayList<>(empleados.reversed());
    }
    
    public void eliminarPorDesempeno(double promedioMinimo) {
        empleados.removeIf(empleado -> empleado.getPromedioDesempeno() < promedioMinimo);

        empleadosPorId.entrySet().removeIf(
                entrada -> entrada.getValue().getPromedioDesempeno() < promedioMinimo
        );
    }
    
}
