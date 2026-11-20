package com.rosewood.payroll;

import com.rosewood.payroll.exception.InvalidPayrollInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GrossPayCalculatorTest {

    private GrossPayCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new GrossPayCalculator();
    }

    @Test
    void calculatesRegularPayForFortyHours() {
        BigDecimal result = calculator.calculateGrossPay(
                new BigDecimal("40"),
                new BigDecimal("30")
        );

        assertMoneyEquals("1200", result);
    }

    @Test
    void calculatesOvertimePayForHoursAboveForty() {
        BigDecimal result = calculator.calculateGrossPay(
                new BigDecimal("50"),
                new BigDecimal("30")
        );

        assertMoneyEquals("1650", result);
    }

    @Test
    void calculatesPayCorrectlyForFiveOvertimeHours() {
        BigDecimal result = calculator.calculateGrossPay(
                new BigDecimal("45"),
                new BigDecimal("30")
        );

        assertMoneyEquals("1425", result);
    }

    @Test
    void returnsZeroWhenNoHoursAreWorked() {
        BigDecimal result = calculator.calculateGrossPay(
                new BigDecimal("0"),
                new BigDecimal("30")
        );

        assertMoneyEquals("0", result);
    }

    @Test
    void rejectsNegativeHoursWorked() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> calculator.calculateGrossPay(
                        new BigDecimal("-5"),
                        new BigDecimal("30")
                )
        );
    }

    @Test
    void rejectsNegativeHourlyRate() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> calculator.calculateGrossPay(
                        new BigDecimal("40"),
                        new BigDecimal("-30")
                )
        );
    }

    @Test
    void rejectsNullHoursWorked() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> calculator.calculateGrossPay(
                        null,
                        new BigDecimal("30")
                )
        );
    }

    @Test
    void rejectsNullHourlyRate() {
        assertThrows(
                InvalidPayrollInputException.class,
                () -> calculator.calculateGrossPay(
                        new BigDecimal("40"),
                        null
                )
        );
    }

    private void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "Expected " + expected + " but got " + actual
        );
    }
}