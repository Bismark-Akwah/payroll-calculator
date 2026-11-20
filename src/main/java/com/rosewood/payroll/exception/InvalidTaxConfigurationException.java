package com.rosewood.payroll.exception;

public class InvalidTaxConfigurationException
        extends IllegalStateException {

    public InvalidTaxConfigurationException(String message) {
        super(message);
    }
}