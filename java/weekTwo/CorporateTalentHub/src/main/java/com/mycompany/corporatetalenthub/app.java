package com.mycompany.corporatetalenthub;

import com.mycompany.corporatetalenthub.model.Employee;
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

                    /*
                     * Switch tradicional compatible con Java 8.
                     *
                     * La responsabilidad del switch es decidir qué acción ejecutar.
                     *
                     * Las validaciones propias de cada proceso ya no se realizan
                     * directamente dentro de cada case.
                     *
                     * Por ejemplo:
                     * - registrarEmpleado() valida los datos ingresados.
                     * - EmployeeService valida o aplica reglas relacionadas con empleados.
                     * - EmployeeRepository se encarga del acceso y modificación de los datos.
                     *
                     * Esto permite que el main se encargue principalmente de coordinar
                     * el flujo del programa y no de realizar toda la lógica.
                     *
                     * Si olvidamos el break puede ocurrir fall-through,
                     * haciendo que se ejecute también el siguiente case.
                     *
                     * En una Switch Expression moderna con -> este comportamiento
                     * no ocurre por defecto.
                     */
                    switch (opcion) {

                        case 0:
                            sistemaActivo = false;
                            view.mostrarMensaje("Sesión finalizada.");
                            break;
                            
                        case 1:
                            registrarEmpleado(scanner, service, view );
                            break;

                        case 2:
                            mostrarReporte(service,view);
                            break;

                        case 3:
                            view.mostrarCategoriasSalariales();
                            break;

                        case 4:
                            eliminarEmpleado(scanner, service, view );
                            break;
                        
                        case 5:
                            mostrarConfiguracion(service, view);
                            break;
                            
                        case 6:
                            mostrarOrdenEmpleados(service, view);
                            break;
                        case 7:
                            filtrarEmpleadosPorDesempeno(service, view);
                            break;
                        default:
                            view.mostrarMensaje("Opción fuera del menú.");
                            break;
                    }

                } catch (InputMismatchException excepcion) {

                    view.mostrarMensaje("Entrada inválida. Debe escribir un valor numérico.");

                    scanner.nextLine();

                    /*
                     * Java moderno mejoró algunos diagnósticos de excepciones,
                     * especialmente Helpful NullPointerExceptions desde Java 14.
                     *
                     * Esto no significa que todas las excepciones tengan
                     * automáticamente mensajes más detallados.
                     */
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
         * La validación de ID repetido ya no recorre manualmente un arreglo.
         *
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

        /*
         * Se construye un Employee con todos sus datos,
         * incluyendo su propio arreglo de calificaciones.
         */
        var employee = new Employee(id, nombre, edad, salario, calificaciones);

        /*
         * El cálculo del promedio corresponde a lógica del negocio,
         * por eso se delega a EmployeeService.
         */
        var promedio = service.calcularPromedio(calificaciones);

        employee.setPromedioDesempeno(promedio);

        /*
         * App no guarda directamente en ArrayList ni HashMap.
         *
         * Solicita el registro al Service y este delega el almacenamiento
         * al EmployeeRepository.
         */
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

        /*
         * App ya no recibe un Employee[] ni una cantidadEmpleados.
         *
         * Solicita la colección al Service, que obtiene los empleados
         * almacenados en EmployeeRepository.
         */
        var empleados = service.listarEmpleados();

        /*
         * Con List podemos consultar directamente si la colección está vacía,
         * sin mantener manualmente una variable cantidadEmpleados.
         */
        if (empleados.isEmpty()) {
            view.mostrarMensaje("Todavía no hay empleados registrados.");
            return;
        }

        view.mostrarTituloReporte();

        /*
         * Ya no recorremos empleados mediante índices.
         *
         * El enhanced for permite recorrer directamente cada Employee
         * almacenado en la List.
         */
        for (var empleado : empleados) {

            /*
             * El promedio ya pertenece al Employee porque fue calculado
             * durante su registro.
             */
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

            /*
             * Las reglas de promoción y categoría salarial continúan
             * perteneciendo a EmployeeService.
             */
            var estadoPromocion = service.obtenerEstadoPromocion(promedio);
            var categoria = service.obtenerCategoriaSalarial(empleado.getSalario());

            /*
             * ConsoleView se encarga únicamente de presentar
             * los datos al usuario.
             */
            view.mostrarEmpleado(empleado, promedio, puntajeSimplificado, estadoPromocion, categoria);
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

        /*
         * Las tecnologías y sedes se crean mediante List.of() y Map.of().
         *
         * Son datos de configuración definidos desde el inicio y no deberían
         * modificarse durante la ejecución del programa.
         */
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
        /*
         * En versiones Legacy se accedía manualmente mediante índices:
         *
         * Primer empleado:
         * empleados.get(0);
         *
         * Último empleado:
         * empleados.get(empleados.size() - 1);
         *
         * Java 21 permite expresar directamente la intención mediante
         * getFirst(), getLast() y reversed().
         */
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
}