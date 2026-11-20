package com.rosewood.payroll;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    public InMemoryEmployeeRepository(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "Employees cannot be null");

        for (Employee employee : employees) {
            Objects.requireNonNull(employee, "Employee cannot be null");

            Employee existingEmployee =
                    this.employees.putIfAbsent(employee.getId(), employee);

            if (existingEmployee != null) {
                throw new IllegalArgumentException(
                        "Duplicate employee ID: " + employee.getId()
                );
            }
        }
    }

    @Override
    public Optional<Employee> findById(String id) {
        Objects.requireNonNull(id, "Employee ID cannot be null");

        return Optional.ofNullable(employees.get(id));
    }
}
