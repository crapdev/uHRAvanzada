package com.riwi.talent;

import com.riwi.talent.controller.EmployeeController;
import com.riwi.talent.model.EmployeeService;
import com.riwi.talent.view.ConsoleView;

public class app {

    public static void main(String[] args) {

        EmployeeService service = new EmployeeService();
        ConsoleView view = new ConsoleView();

        EmployeeController controller = new EmployeeController(service, view);

        controller.iniciar();
    }
}