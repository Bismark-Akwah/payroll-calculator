package com.rosewood.payroll.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String employeeId) {
        super("Employee not found: " + employeeId);
    }
}