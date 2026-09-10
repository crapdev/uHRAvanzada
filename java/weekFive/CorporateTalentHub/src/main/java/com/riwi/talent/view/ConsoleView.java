
package com.riwi.talent.view;

import com.riwi.talent.model.Employee;
import com.riwi.talent.model.PerformanceReport;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public int pedirOpcion() {
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        return opcion;
    }

    public int pedirId() {
        System.out.print("ID positivo: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        return id;
    }

    public String pedirNombre() {
        System.out.print("Nombre: ");
        return scanner.nextLine().trim();
    }

    public int pedirEdad() {
        System.out.print("Edad entre 18 y 100: ");
        int edad = scanner.nextInt();
        scanner.nextLine();
        return edad;
    }

    public double pedirSalario() {
        System.out.print("Salario mayor que cero: ");
        double salario = scanner.nextDouble();
        scanner.nextLine();
        return salario;
    }

    public double[] pedirCalificaciones(int cantidad) {
        double[] calificaciones = new double[cantidad];

        for (int i = 0; i < cantidad; i++) {
            System.out.printf("Calificación del trimestre %d (0 a 100): ", i + 1);
            calificaciones[i] = scanner.nextDouble();
        }

        scanner.nextLine();
        return calificaciones;
    }

    public int pedirTipoEmpleado() {
        System.out.println("Tipo de empleado:\n1. Developer\n2. Manager");
        System.out.print("Seleccione el tipo: ");
        int tipo = scanner.nextInt();
        scanner.nextLine();
        return tipo;
    }

    public String pedirLenguajePrincipal() {
        System.out.print("Ingrese el lenguaje principal: ");
        return scanner.nextLine().trim();
    }

    public double pedirPresupuestoMensual() {
        System.out.print("Presupuesto mensual: ");
        double presupuesto = scanner.nextDouble();
        scanner.nextLine();
        return presupuesto;
    }

    public int pedirIdEliminar() {
        System.out.print("Ingrese el ID del empleado a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        return id;
    }

    public void mostrarMenu() {
        System.out.println("""
                
                =====================================
                     CORPORATE TALENT HUB
                =====================================
                1. Registrar empleado y calificaciones
                2. Mostrar reporte de desempeño
                3. Consultar categorías salariales
                4. Eliminar empleado
                5. Consultar tecnologías y sedes
                6. Consultar orden de empleados
                7. Filtrar empleados por desempeño mínimo
                8. Generar reportes mensuales
                9. Consultar roles de empleados
               10. Mostrar bonos de promoción
                0. Salir
                
                """);
    }

    public void mostrarTituloReporte() {
        System.out.println("\nREPORTE DE DESEMPEÑO");
    }

    public void mostrarListaEmpleados(List<Employee> empleados) {
        for (Employee empleado : empleados) {
            mostrarEmpleadoSimple(empleado);
        }
    }

    public void mostrarTecnologias(List<String> tecnologias) {
        System.out.println("\nTecnologías:");

        for (String tecnologia : tecnologias) {
            System.out.println("- " + tecnologia);
        }
    }

    public void mostrarSedes(Map<String, String> sedes) {
        System.out.println("\nSedes:");

        for (var sede : sedes.entrySet()) {
            System.out.println("- " + sede.getKey() + ": " + sede.getValue());
        }
    }

    public void mostrarCategoriasSalariales() {
        System.out.println("""
                Categorías:
                - Menos de $2.000.000: JUNIOR
                - Desde $2.000.000 y menos de $4.000.000: SEMISENIOR
                - Desde $4.000.000 y menos de $7.000.000: SENIOR
                - Desde $7.000.000: LÍDER
                """);
    }

    public void mostrarEmpleado(Employee empleado, double promedio, int puntajeSimplificado, String estado, String categoria) {
        System.out.printf("ID: %d | Nombre: %s | Promedio: %.2f | Simplificado: %d | Estado: %s | Categoría: %s%n",
                empleado.getId(), empleado.getNombre(), promedio, puntajeSimplificado, estado, categoria);
    }

    public void mostrarEmpleadoSimple(Employee empleado) {
        System.out.printf("ID: %d | Nombre: %s | Salario: %.2f | Promedio: %.2f%n",
                empleado.getId(), empleado.getNombre(), empleado.getSalario(), empleado.getPromedioDesempeno());
    }

    public void mostrarReporteDesempeno(PerformanceReport reporte) {
        System.out.printf("ID: %d | Promedio: %.2f | Feedback: %s%n",
                reporte.idEmployee(), reporte.average(), reporte.feedback());
    }

    public void mostrarReporteFinal(PerformanceReport reporte) {

        String formato = """
            ==================================
               REPORTE DE DESEMPEÑO
            ==================================
            ID Empleado: %d
            Promedio: %.2f
            Feedback: %s
            ==================================
            """;

        System.out.printf(
                        formato,
                        reporte.idEmployee(),
                        reporte.average(),
                        reporte.feedback()
        );
    }
}
