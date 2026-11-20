package com.rosewood.payroll.exception;

public class InvalidPayrollInputException
        extends IllegalArgumentException {

    public InvalidPayrollInputException(String message) {
        super(message);
    }
}