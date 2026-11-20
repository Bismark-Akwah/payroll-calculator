
package com.rosewood.payroll;

import com.rosewood.payroll.exception.EmployeeNotFoundException;
import com.rosewood.payroll.exception.InvalidPayrollInputException;
import com.rosewood.payroll.tax.BandBasedTaxCalculator;
import com.rosewood.payroll.tax.TaxBand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private PayrollCalculator payrollCalculator;

    @BeforeEach
    void setUp() {
        List<TaxBand> taxBands = List.of(
                new TaxBand("LOW",
                        new BigDecimal("0"),
                        new BigDecimal("1000"),
                        new BigDecimal("0.05")),

                new TaxBand("MEDIUM",
                        new BigDecimal("1000"),
                        new BigDecimal("5000"),
                        new BigDecimal("0.15")),

                new TaxBand("HIGH",
                        new BigDecimal("5000"),
                        null,
                        new BigDecimal("0.25"))
        );

        payrollCalculator = new PayrollCalculator(
                employeeRepository,
                new GrossPayCalculator(),
                new BandBasedTaxCalculator(taxBands)
        );
    }

    @Test
    void calculatesNetPayForAnExistingEmployee() {
        Employee employee = new Employee("e1", "Efua Sarpong");

        when(employeeRepository.findById("e1"))
                .thenReturn(Optional.of(employee));

        PayrollResult result = payrollCalculator.calculateNetPay(
                "e1",
                new BigDecimal("40"),
                new BigDecimal("20")
        );

        assertEquals("e1", result.employeeId());
        assertEquals("Efua Sarpong", result.employeeName());

        assertMoneyEquals("800", result.grossPay());
        assertMoneyEquals("40", result.taxAmount());
        assertMoneyEquals("760", result.netPay());

        verify(employeeRepository).findById("e1");
    }

    @Test
    void throwsExceptionWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> payrollCalculator.calculateNetPay(
                        "unknown",
                        new BigDecimal("40"),
                        new BigDecimal("20")
                )
        );

        verify(employeeRepository).findById("unknown");
    }

    @Test
    void rejectsBlankEmployeeId() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> payrollCalculator.calculateNetPay(
                        " ",
                        new BigDecimal("40"),
                        new BigDecimal("20")
                )
        );

        verifyNoInteractions(employeeRepository);
    }

    @Test
    void rejectsNegativeHoursWorked() {
        Employee employee = new Employee("e1", "Efua Sarpong");

        when(employeeRepository.findById("e1"))
                .thenReturn(Optional.of(employee));

        assertThrows(
                InvalidPayrollInputException.class,
                () -> payrollCalculator.calculateNetPay(
                        "e1",
                        new BigDecimal("-5"),
                        new BigDecimal("20")
                )
        );

        verify(employeeRepository).findById("e1");
    }

    private void assertMoneyEquals(
            String expected,
            BigDecimal actual) {

        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "Expected " + expected + " but got " + actual
        );
    }
}
