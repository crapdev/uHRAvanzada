package com.riwi.talent.controller;

import com.riwi.talent.model.Developer;
import com.riwi.talent.model.Employee;
import com.riwi.talent.model.EmployeeService;
import com.riwi.talent.model.Manager;
import com.riwi.talent.model.Promotable;
import com.riwi.talent.view.ConsoleView;
import java.util.InputMismatchException;

public class EmployeeController {
    private static final int CANTIDAD_TRIMESTRES = 3;
    private static final double NOTA_MINIMA = 0;
    private static final double NOTA_MAXIMA = 100;

    private final EmployeeService service;
    private final ConsoleView view;

    public EmployeeController(EmployeeService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void iniciar() {
        boolean sistemaActivo = true;

        while (sistemaActivo) {
            try {
                view.mostrarMenu();

                int opcion = view.pedirOpcion();

                switch (opcion) {
                    case 0 -> {
                        sistemaActivo = false;
                        view.mostrarMensaje("Sesión finalizada.");
                    }
                    case 1 -> registrarEmpleado();
                    case 2 -> mostrarReporte();
                    case 3 -> view.mostrarCategoriasSalariales();
                    case 4 -> eliminarEmpleado();
                    case 5 -> mostrarConfiguracion();
                    case 6 -> mostrarOrdenEmpleados();
                    case 7 -> filtrarEmpleadosPorDesempeno();
                    case 8 -> mostrarReportesMensuales();
                    case 9 -> mostrarRolesEmpleados();
                    case 10 -> mostrarBonosPromocion();
                    default -> view.mostrarMensaje("Opción fuera del menú.");
                }

            } catch (InputMismatchException e) {
                view.mostrarMensaje("Entrada inválida. Debe escribir un valor numérico.");
                return;
            }
        }
    }

    private void registrarEmpleado() {

        int id = view.pedirId();

        if (id <= 0) {
            view.mostrarMensaje("El ID debe ser mayor que cero.");
            return;
        }

        if (service.existeEmpleado(id)) {
            view.mostrarMensaje("Ya existe un empleado con ese ID.");
            return;
        }

        String nombre = view.pedirNombre();

        if (nombre.isBlank()) {
            view.mostrarMensaje("El nombre no puede estar vacío.");
            return;
        }

        int edadIngresada = view.pedirEdad();

        if (edadIngresada < 18 || edadIngresada > 100) {
            view.mostrarMensaje("La edad está fuera del rango permitido.");
            return;
        }

        byte edad = (byte) edadIngresada;

        double salario = view.pedirSalario();

        if (salario <= 0) {
            view.mostrarMensaje("El salario debe ser mayor que cero.");
            return;
        }

        double[] calificaciones = view.pedirCalificaciones(CANTIDAD_TRIMESTRES);

        for (double calificacion : calificaciones) {
            if (calificacion < NOTA_MINIMA || calificacion > NOTA_MAXIMA) {
                view.mostrarMensaje("La calificación está fuera del rango permitido.");
                return;
            }
        }

        int tipoEmpleado = view.pedirTipoEmpleado();

        Employee employee;

        if (tipoEmpleado == 1) {
            String mainLanguage = view.pedirLenguajePrincipal();
            employee = new Developer(id, nombre, edad, salario, calificaciones, mainLanguage);

        } else if (tipoEmpleado == 2) {
            double monthlyBudget = view.pedirPresupuestoMensual();
            employee = new Manager(id, nombre, edad, salario, calificaciones, monthlyBudget);

        } else {
            view.mostrarMensaje("Tipo de empleado inválido.");
            return;
        }

        double promedio = service.calcularPromedio(calificaciones);
        employee.setPromedioDesempeno(promedio);

        boolean registrado = service.registrarEmpleado(employee);

        view.mostrarMensaje(registrado ? "Empleado registrado correctamente." : "No fue posible registrar el empleado.");
    }

    private void mostrarReporte() {
        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("Todavía no hay empleados registrados.");
            return;
        }

        view.mostrarTituloReporte();

        for (Employee empleado : empleados) {
            double promedio = empleado.getPromedioDesempeno();
            int puntajeSimplificado = (int) promedio;
            String estado = service.obtenerEstadoPromocion(promedio);
            String categoria = service.obtenerCategoriaSalarial(empleado.getSalario());

            view.mostrarEmpleado(empleado, promedio, puntajeSimplificado, estado, categoria);
        }
    }

    private void eliminarEmpleado() {
        int id = view.pedirIdEliminar();

        boolean eliminado = service.eliminarEmpleado(id);

        view.mostrarMensaje(eliminado ? "Empleado eliminado correctamente." : "No existe un empleado con ese ID.");
    }

    private void mostrarConfiguracion() {
        view.mostrarTecnologias(service.obtenerTecnologias());
        view.mostrarSedes(service.obtenerSedes());
    }

    private void mostrarOrdenEmpleados() {
        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados.");
            return;
        }

        view.mostrarMensaje("\nPrimer empleado:");
        view.mostrarEmpleadoSimple(service.obtenerPrimerEmpleado());

        view.mostrarMensaje("\nÚltimo empleado:");
        view.mostrarEmpleadoSimple(service.obtenerUltimoEmpleado());

        view.mostrarMensaje("\nEmpleados en orden inverso:");
        view.mostrarListaEmpleados(service.listarEmpleadosInvertidos());
    }

    private void filtrarEmpleadosPorDesempeno() {
        if (service.listarEmpleados().isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados para filtrar.");
            return;
        }

        service.filtrarEmpleadosPorDesempeno();

        var empleados = service.listarEmpleados();

        view.mostrarMensaje("\nEmpleados con desempeño igual o superior a " + service.obtenerDesempenoMinimo() + ":");

        if (empleados.isEmpty()) {
            view.mostrarMensaje("Ningún empleado cumple con el desempeño mínimo.");
            return;
        }

        view.mostrarListaEmpleados(empleados);
    }

    private void mostrarReportesMensuales() {

        var reportes = service.obtenerReportesDesempeno();

        if (reportes.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados para generar reportes.");
            return;
        }

        for (var reporte : reportes) {
            view.mostrarReporteFinal(reporte);
        }
    }

    private void mostrarRolesEmpleados() {
        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados.");
            return;
        }

        for (Employee empleado : empleados) {
            view.mostrarEmpleadoSimple(empleado);
            view.mostrarMensaje(service.obtenerInformacionRol(empleado));
        }
    }

    private void mostrarBonosPromocion() {
        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados.");
            return;
        }

        for (Employee employee : empleados) {
            if (employee instanceof Promotable promotable) {
                double bono = service.calcularBonoPromocionable(promotable);

                view.mostrarMensaje("Empleado id: " + employee.getId()
                        + " | Nombre: " + employee.getNombre()
                        + " | Bono de ascenso: $" + bono);
            }
        }
    }
}
