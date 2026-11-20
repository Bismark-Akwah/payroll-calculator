package com.rosewood.payroll;

import java.util.Objects;

public class Employee {

    private final String id;
    private final String name;

    public Employee(String id, String name) {
        this.id = Objects.requireNonNull(id, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Employee name cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be blank");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be blank");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}