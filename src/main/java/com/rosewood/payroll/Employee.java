
package com.rosewood.payroll;

import com.rosewood.payroll.exception.InvalidPayrollInputException;

import java.util.Objects;

public class Employee {

    private final String id;
    private final String name;

    public Employee(String id, String name) {
        Objects.requireNonNull(id, "Employee ID cannot be null");
        Objects.requireNonNull(name, "Employee name cannot be null");

        if (id.isBlank()) {
            throw new InvalidPayrollInputException(
                    "Employee ID cannot be blank"
            );
        }

        if (name.isBlank()) {
            throw new InvalidPayrollInputException(
                    "Employee name cannot be blank"
            );
        }

        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
