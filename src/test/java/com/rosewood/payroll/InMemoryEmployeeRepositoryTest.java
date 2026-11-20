package com.rosewood.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEmployeeRepositoryTest {

    private InMemoryEmployeeRepository repository;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("e1", "Efua Sarpong");
        repository = new InMemoryEmployeeRepository(List.of(employee));
    }

    @Test
    void findsExistingEmployee() {
        var result = repository.findById("e1");

        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
    }

    @Test
    void returnsEmptyForUnknownEmployee() {
        var result = repository.findById("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void rejectsNullEmployeeId() {
        assertThrows(
                NullPointerException.class,
                () -> repository.findById(null)
        );
    }

    @Test
    void rejectsNullEmployeeCollection() {
        assertThrows(
                NullPointerException.class,
                () -> new InMemoryEmployeeRepository(null)
        );
    }

    @Test
    void rejectsNullEmployeeInCollection() {
        assertThrows(
                NullPointerException.class,
                () -> new InMemoryEmployeeRepository(
                        Arrays.asList(employee, null)
                )
        );
    }

    @Test
    void acceptsDifferentEmployees() {
        Employee secondEmployee =
                new Employee("e2", "Kwame Owusu");

        InMemoryEmployeeRepository customRepository =
                new InMemoryEmployeeRepository(
                        List.of(employee, secondEmployee)
                );

        assertTrue(customRepository.findById("e1").isPresent());
        assertTrue(customRepository.findById("e2").isPresent());
    }

    @Test
    void rejectsDuplicateEmployeeIds() {
        Employee duplicate =
                new Employee("e1", "Kwame Owusu");

        assertThrows(
                IllegalArgumentException.class,
                () -> new InMemoryEmployeeRepository(
                        List.of(employee, duplicate)
                )
        );
    }
}
