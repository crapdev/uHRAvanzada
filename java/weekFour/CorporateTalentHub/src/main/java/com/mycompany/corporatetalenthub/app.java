package com.mycompany.corporatetalenthub;

import com.mycompany.corporatetalenthub.model.Developer;
import com.mycompany.corporatetalenthub.model.Employee;
import com.mycompany.corporatetalenthub.model.Manager;
import com.mycompany.corporatetalenthub.model.Promotable;
import com.mycompany.corporatetalenthub.service.EmployeeService;
import com.mycompany.corporatetalenthub.view.ConsoleView;
import java.util.InputMismatchException;
import java.util.Scanner;

public class app {

    private static final int CANTIDAD_TRIMESTRES = 3;
    private static final double NOTA_MINIMA = 0.0;
    private static final double NOTA_MAXIMA = 100.0;

    public static void main(String[] args) {

        var service = new EmployeeService();
        var view = new ConsoleView();

        try (var scanner = new Scanner(System.in)) {
            
            var sistemaActivo = true;

            do {
                
                view.mostrarMenu();
                try {

                    System.out.print("Seleccione una opción: ");

                    var opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        
                        case 0 -> {
                            sistemaActivo = false;
                            view.mostrarMensaje("Sesión finalizada.");
                        }
                            
                        case 1 -> registrarEmpleado(scanner, service, view );

                        case 2 -> mostrarReporte(service,view);

                        case 3 -> view.mostrarCategoriasSalariales();

                        case 4 -> eliminarEmpleado(scanner, service, view );
                        
                        case 5 -> mostrarConfiguracion(service, view);
                            
                        case 6 -> mostrarOrdenEmpleados(service, view);
                        
                        case 7 -> filtrarEmpleadosPorDesempeno(service, view);
                        
                        case 8 -> mostrarReportesMensuales(service, view);
                        
                        case 9 -> mostrarRolesEmpleados(service, view);
                        
                        case 10 -> mostrarBonosPromocion(service, view);
                        
                        default -> view.mostrarMensaje("Opción fuera del menú.");
                    }

                } catch (InputMismatchException excepcion) {

                    view.mostrarMensaje("Entrada inválida. Debe escribir un valor numérico.");

                    scanner.nextLine();
                }

            } while (sistemaActivo);
        }
    }


    // ==============================
    // CAPTURA DE DATOS
    // ==============================

    private static void registrarEmpleado(Scanner scanner, EmployeeService service, ConsoleView view) {

        System.out.print("ID positivo: ");

        var id = scanner.nextInt();
        scanner.nextLine();

        // Validación básica del dato ingresado
        if (id <= 0) {
            view.mostrarMensaje("El ID debe ser mayor que cero.");
            return;
        }

        /*
         * App pregunta al Service y este utiliza EmployeeRepository,
         * que consulta el HashMap usando el ID como clave.
         */
        if (service.existeEmpleado(id)) {
            view.mostrarMensaje("Ya existe un empleado con ese ID.");
            return;
        }

        System.out.print("Nombre: ");

        var nombre = scanner.nextLine().trim();
        // verifica si una cadena de texto está vacía
        if (nombre.isBlank()) {
            view.mostrarMensaje("El nombre no puede estar vacío.");
            return;
        }

        System.out.print("Edad entre 18 y 100: ");

        var edadIngresada = scanner.nextInt();

        if (edadIngresada < 18 || edadIngresada > 100) {
            view.mostrarMensaje("La edad está fuera del rango permitido.");
            scanner.nextLine();
            return;
        }

        /*
         * Scanner entrega un int.
         * Después de validar el rango se convierte a byte.
         */
        var edad = (byte) edadIngresada;

        System.out.print("Salario mayor que cero: ");

        var salario = scanner.nextDouble();

        if (salario <= 0) {
            view.mostrarMensaje("El salario debe ser mayor que cero.");
            scanner.nextLine();
            return;
        }

        /*
         * Cada Employee conserva ahora su propio arreglo de calificaciones.
         *
         * Ya no se utiliza una matriz double[][] global asociada por posición
         * con un arreglo Employee[].
         *
         * Como la cantidad de trimestres es fija, se mantiene un double[] de
         * tamaño CANTIDAD_TRIMESTRES.
         */
        var calificaciones = new double[CANTIDAD_TRIMESTRES];

        for (var trimestre = 0; trimestre < CANTIDAD_TRIMESTRES; trimestre++) {

            System.out.printf("Calificación del trimestre %d (0 a 100): ", trimestre + 1);

            var calificacion = scanner.nextDouble();

            if (calificacion < NOTA_MINIMA || calificacion > NOTA_MAXIMA) {
                view.mostrarMensaje("La calificación está fuera del rango permitido.");
                scanner.nextLine();
                return;
            }

            calificaciones[trimestre] = calificacion;
        }

        scanner.nextLine();
        
        System.out.println("Tipo de empleado:\n1. Developer\n2. Manager");

        System.out.print("Seleccione el tipo: ");

        var tipoEmpleado = scanner.nextInt();
        scanner.nextLine();
        
        Employee employee;
        
        if (tipoEmpleado == 1) {

            System.out.print("Ingrese el lenguaje principal: ");
            var mainLanguage = scanner.nextLine().trim();

            employee = new Developer( id, nombre, edad, salario, calificaciones, mainLanguage );
            
        } else if (tipoEmpleado == 2) {
            
            System.out.print("Presupuesto mensual: ");
            var monthlyBudget = scanner.nextDouble();
            scanner.nextLine();

            employee = new Manager( id, nombre, edad, salario, calificaciones, monthlyBudget);

        } else {
            view.mostrarMensaje("Tipo de empleado inválido.");
            return;
        }
        

        var promedio = service.calcularPromedio(calificaciones);

        employee.setPromedioDesempeno(promedio);

        // Devuelve true o un false para saber si se guardo o no
        // Pasa por service y repository para ejecutar esta acción
        var registrado = service.registrarEmpleado(employee);

        if (registrado) {
            view.mostrarMensaje("Empleado registrado correctamente.");
        } else {
            view.mostrarMensaje("No fue posible registrar el empleado.");
        }
    }


    // ==============================
    // COORDINACIÓN DEL REPORTE
    // ==============================

    private static void mostrarReporte(EmployeeService service, ConsoleView view) {

        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("Todavía no hay empleados registrados.");
            return;
        }
        
        view.mostrarTituloReporte();


        for (var empleado : empleados) {


            var promedio = empleado.getPromedioDesempeno();

            /*
             * Casting explícito de double a int.
             *
             * Se elimina la parte decimal:
             * 89.99 -> 89
             *
             * Esto implica pérdida de precisión.
             */
            var puntajeSimplificado = (int) promedio;

            var estadoPromocion = service.obtenerEstadoPromocion(promedio);
            var categoria = service.obtenerCategoriaSalarial(empleado.getSalario());

            view.mostrarEmpleado(empleado, promedio, puntajeSimplificado, estadoPromocion, categoria);
        }
    }
    
    private static void mostrarReportesMensuales( EmployeeService service, ConsoleView view ) {

        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados para generar reportes.");
            return;
        }

        view.mostrarMensaje("\nREPORTES MENSUALES DE DESEMPEÑO");

        for (var empleado : empleados) {
            
            var reporte = service.generarReporteDesempeno(empleado);
            
            view.mostrarReporteDesempeno(reporte);
        }
    }
    
    // ==============================
    // ELIMINACIÓN DE EMPLEADOS
    // ==============================

    private static void eliminarEmpleado(Scanner scanner, EmployeeService service, ConsoleView view) {

        System.out.print("Ingrese el ID del empleado a eliminar: ");

        var id = scanner.nextInt();
        scanner.nextLine();

        /*
         * App no manipula directamente las colecciones.
         *
         * EmployeeService solicita la eliminación al Repository,
         * donde se actualizan tanto la List<Employee> como el HashMap.
         */
        var eliminado = service.eliminarEmpleado(id);

        if (eliminado) {
            view.mostrarMensaje("Empleado eliminado correctamente.");
        } else {
            view.mostrarMensaje("No existe un empleado con ese ID.");
        }
    }
    
    // ==============================
    // CONFIGURACIÓN DEL SISTEMA
    // ==============================

    private static void mostrarConfiguracion(EmployeeService service, ConsoleView view) {

        view.mostrarTecnologias(service.obtenerTecnologias());
        view.mostrarSedes(service.obtenerSedes());
    }
    
    // ==============================
    // SEQUENCED COLLECTIONS - JAVA 21
    // ==============================

    private static void mostrarOrdenEmpleados(EmployeeService service, ConsoleView view) {
        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados.");
            return;
        }

        var primerEmpleado = service.obtenerPrimerEmpleado();
        var ultimoEmpleado = service.obtenerUltimoEmpleado();
        var empleadosInvertidos = service.listarEmpleadosInvertidos();

        view.mostrarMensaje("\nPrimer empleado:");
        view.mostrarEmpleadoSimple(primerEmpleado);

        view.mostrarMensaje("\nÚltimo empleado:");
        view.mostrarEmpleadoSimple(ultimoEmpleado);

        view.mostrarMensaje("\nEmpleados en orden inverso:");
        view.mostrarListaEmpleados(empleadosInvertidos);
    }
    
    // ==============================
    // FILTRADO POR DESEMPEÑO
    // ==============================

    private static void filtrarEmpleadosPorDesempeno(EmployeeService service, ConsoleView view) {

        var empleados = service.listarEmpleados();

        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados para filtrar.");
            return;
        }

        /*
         * La regla del sistema establece que un empleado debe tener
         * un promedio igual o superior a 60 para permanecer.
         *
         * EmployeeService define la regla de negocio y EmployeeRepository
         * utiliza removeIf() para eliminar los empleados que no la cumplen.
         */
        service.filtrarEmpleadosPorDesempeno();

        var empleadosFiltrados = service.listarEmpleados();

        view.mostrarMensaje("\nEmpleados con desempeño igual o superior a " + service.obtenerDesempenoMinimo() + ":");

        if (empleadosFiltrados.isEmpty()) {
            view.mostrarMensaje("Ningún empleado cumple con el desempeño mínimo.");
            return;
        }

        view.mostrarListaEmpleados(empleadosFiltrados);
    }
    
    private static void mostrarRolesEmpleados(EmployeeService service, ConsoleView view){
        
        var empleados = service.listarEmpleados();
        
        if (empleados.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados");
            return;
        }
        
        for (var empleado : empleados) {
            view.mostrarEmpleadoSimple(empleado);
            var informacion = service.obtenerInformacionRol(empleado);
            view.mostrarMensaje(informacion);
        }
        
    }
    
    private static void mostrarBonosPromocion(EmployeeService service, ConsoleView view){
        var employees = service.listarEmpleados();
        
        if (employees.isEmpty()) {
            view.mostrarMensaje("No hay empleados registrados. ");
            return;
        }
        
        for (Employee employee : employees) {
            
            if (employee instanceof Promotable promotable) {
                
                var bono = service.calcularBonoPromocionable(promotable);
                
                view.mostrarMensaje("Empleado id: " + employee.getId()
                                        + " | Nombre: " + employee.getNombre()
                                        + " | Bono de ascenso: $" + bono
               );
            }
            
        }
        
    }
}