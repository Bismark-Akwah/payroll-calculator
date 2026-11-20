package com.rosewood.payroll;

import com.rosewood.payroll.exception.InvalidPayrollInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void createsEmployeeWithValidDetails() {
        Employee employee = new Employee("e1", "Efua Sarpong");

        assertEquals("e1", employee.getId());
        assertEquals("Efua Sarpong", employee.getName());
    }

    @Test
    void rejectsNullEmployeeId() {
        assertThrows(
                NullPointerException.class,
                () -> new Employee(null, "Efua Sarpong")
        );
    }

    @Test
    void rejectsBlankEmployeeId() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> new Employee(" ", "Efua Sarpong")
        );
    }

    @Test
    void rejectsNullEmployeeName() {
        assertThrows(
                NullPointerException.class,
                () -> new Employee("e1", null)
        );
    }

    @Test
    void rejectsBlankEmployeeName() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> new Employee("e1", " ")
        );
    }
}
