
package com.mycompany.corporatetalenthub.view;
import com.mycompany.corporatetalenthub.model.Employee;
import com.mycompany.corporatetalenthub.model.PerformanceReport;
import java.util.List;
import java.util.Map;

// Metodos antes en app: mostrarMenu() y mostrarCategoriasSalariales(). Trasladadas acá al separar la captura, logica del negocio y la presentacion
public class ConsoleView {
    
    
    
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
    
    public void mostrarTituloReporte() {
        System.out.println( "\nREPORTE DE DESEMPEÑO" );
    }
    
    public void mostrarListaEmpleados(List<Employee> empleados) {
        for (var empleado : empleados) {
            mostrarEmpleadoSimple(empleado);
        }
    }
    
    public void mostrarTecnologias(List<String> tecnologias) {
        System.out.println("\nTecnologías:");

        for (var tecnologia : tecnologias) {
            System.out.println("- " + tecnologia);
        }
    }
    
    public void mostrarSedes(Map<String, String> sedes) {
        System.out.println("\nSedes:");

        for (var sede : sedes.entrySet()) {
            System.out.println("- " + sede.getKey() + ": " + sede.getValue());
        }
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
                   10. Mostrar los bonos de promocion para cada empleado
                    0. Salir
                
                """);
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

    public void mostrarEmpleado( Employee empleado, double promedio, int puntajeSimplificado, String estado, String categoria) {

        System.out.printf( "ID: %d | Nombre: %s | Promedio: %.2f | "
                        + "Simplificado: %d | Estado: %s | Categoría: %s%n",
                empleado.getId(),
                empleado.getNombre(),
                promedio,
                puntajeSimplificado,
                estado,
                categoria
        );
    }
    
    
    public void mostrarEmpleadoSimple(Employee empleado) {
        System.out.printf(
                "ID: %d | Nombre: %s | Salario: %.2f | Promedio: %.2f%n",
                empleado.getId(),
                empleado.getNombre(),
                empleado.getSalario(),
                empleado.getPromedioDesempeno()
        );
    }
    
    public void mostrarReporteDesempeno(PerformanceReport reporte) {
        System.out.printf( "ID: %d | Promedio: %.2f | Feedback: %s%n",
                            reporte.idEmployee(),
                            reporte.average(),
                            reporte.feedback()
        );
    }
}
