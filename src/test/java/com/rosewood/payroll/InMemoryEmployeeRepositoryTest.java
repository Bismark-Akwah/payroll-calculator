package com.rosewood.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEmployeeRepositoryTest {

    private InMemoryEmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmployeeRepository();
    }

    @Test
    void findsExistingEmployee() {
        var employee = repository.findById("e1");

        assertTrue(employee.isPresent());
        assertEquals("Efua Sarpong", employee.get().getName());
    }

    @Test
    void returnsEmptyForUnknownEmployee() {
        var employee = repository.findById("unknown");

        assertTrue(employee.isEmpty());
    }

    @Test
    void rejectsNullEmployeeId() {
        assertThrows(
                NullPointerException.class,
                () -> repository.findById(null)
        );
    }
}