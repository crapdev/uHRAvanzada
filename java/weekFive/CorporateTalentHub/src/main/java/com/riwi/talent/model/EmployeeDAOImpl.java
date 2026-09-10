package com.riwi.talent.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public boolean insertar(Employee employee) {

        String sql = """
                INSERT INTO employees
                (id, nombre, edad, salario, calificaciones, promedio_desempeno, tipo, main_lenguaje, monthly_budget)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getNombre());
            statement.setShort(3, employee.getEdad());
            statement.setDouble(4, employee.getSalario());

            double[] notas = employee.getCalificaciones();
            Double[] notasParaSQL = new Double[notas.length];

            for (int i = 0; i < notas.length; i++) {
                notasParaSQL[i] = notas[i];
            }

            statement.setArray(5, connection.createArrayOf("float8", notasParaSQL));
            statement.setDouble(6, employee.getPromedioDesempeno());

            if (employee instanceof Developer developer) {
                statement.setString(7, "DEVELOPER");
                statement.setString(8, developer.getMainLenguaje());
                statement.setNull(9, Types.DOUBLE);

            } else if (employee instanceof Manager manager) {
                statement.setString(7, "MANAGER");
                statement.setNull(8, Types.VARCHAR);
                statement.setDouble(9, manager.getMonthlyBudget());
            }
            // Si fue exitosa la consulta devuelve 1, y 1 > 0 es true. Y este metodo devuelve un boolean
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar empleado: " + e.getMessage());
            return false;
        }

    }

    @Override
    public List<Employee> listar() {
        List<Employee> empleados = new ArrayList<>();

        String sql = "SELECT * FROM employees ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                byte edad = resultSet.getByte("edad");
                double salario = resultSet.getDouble("salario");
                double promedio = resultSet.getDouble("promedio_desempeno");
                String tipo = resultSet.getString("tipo");

                Double[] notasSQL = (Double[]) resultSet.getArray("calificaciones").getArray();
                double[] calificaciones = new double[notasSQL.length];
                for (int i = 0; i < notasSQL.length; i++) {
                    calificaciones[i] = notasSQL[i];
                }

                Employee employee;
                // si es del tipo que le pongo, entonces traeme su atributo que tiene como hijo y se instancia
                if (tipo.equals("DEVELOPER")) {
                    String mainLenguaje = resultSet.getString("main_lenguaje");
                    employee = new Developer(id, nombre, edad, salario, calificaciones, mainLenguaje);

                } else if (tipo.equals("MANAGER")) {
                    double monthlyBudget = resultSet.getDouble("monthly_budget");
                    employee = new Manager(id, nombre, edad, salario, calificaciones, monthlyBudget);

                } else {
                    continue;
                }

                employee.setPromedioDesempeno(promedio);

                empleados.add(employee);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }

        return empleados;
    }

    @Override
    public boolean actualizar(Employee employee) {
        String sql = """
            UPDATE employees SET nombre = ?, edad = ?, salario = ?, calificaciones = ?,
                promedio_desempeno = ?, tipo = ?, main_lenguaje = ?, monthly_budget = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getNombre());
            statement.setShort(2, employee.getEdad());
            statement.setDouble(3, employee.getSalario());

            double[] notas = employee.getCalificaciones();
            Double[] notasParaSQL = new Double[notas.length];

            for (int i = 0; i < notas.length; i++) {
                notasParaSQL[i] = notas[i];
            }

            statement.setArray(4, connection.createArrayOf("float8", notasParaSQL));
            statement.setDouble(5, employee.getPromedioDesempeno());

            if (employee instanceof Developer developer) {
                statement.setString(6, "DEVELOPER");
                statement.setString(7, developer.getMainLenguaje());
                statement.setNull(8, Types.DOUBLE);

            } else if (employee instanceof Manager manager) {
                statement.setString(6, "MANAGER");
                statement.setNull(7, Types.VARCHAR);
                statement.setDouble(8, manager.getMonthlyBudget());
            }

            statement.setInt(9, employee.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<PerformanceReport> generarReporteDesempeno() {

        List<PerformanceReport> reportes = new ArrayList<>();

        String sql = """
            SELECT id, promedio_desempeno, CASE
                       WHEN promedio_desempeno >= 80 THEN 'Excelente desempeño'
                       ELSE 'Debe continuar mejorando'
                   END AS feedback
            FROM employees
            ORDER BY id
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                PerformanceReport reporte = new PerformanceReport(
                        resultSet.getInt("id"),
                        resultSet.getDouble("promedio_desempeno"),
                        resultSet.getString("feedback")
                );

                reportes.add(reporte);
            }

        } catch (SQLException e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
        }

        return reportes;
    }
}
