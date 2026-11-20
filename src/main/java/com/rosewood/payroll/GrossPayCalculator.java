package com.rosewood.payroll;

import com.rosewood.payroll.exception.InvalidPayrollInputException;

import java.math.BigDecimal;

public class GrossPayCalculator {

    private static final BigDecimal STANDARD_WORKING_HOURS =
            new BigDecimal("40");

    private static final BigDecimal OVERTIME_PREMIUM =
            new BigDecimal("0.5");

    public BigDecimal calculateGrossPay(
            BigDecimal hoursWorked,
            BigDecimal hourlyRate) {

        validateInputs(hoursWorked, hourlyRate);

        BigDecimal grossPay = hoursWorked.multiply(hourlyRate);

        if (hoursWorked.compareTo(STANDARD_WORKING_HOURS) > 0) {

            BigDecimal overtimeHours =
                    hoursWorked.subtract(STANDARD_WORKING_HOURS);

            BigDecimal overtimePay = overtimeHours
                    .multiply(hourlyRate)
                    .multiply(OVERTIME_PREMIUM);

            grossPay = grossPay.add(overtimePay);
        }

        return grossPay;
    }

    private void validateInputs(
            BigDecimal hoursWorked,
            BigDecimal hourlyRate) {

        if (hoursWorked == null) {
            throw new InvalidPayrollInputException(
                    "Hours worked cannot be null");
        }

        if (hourlyRate == null) {
            throw new InvalidPayrollInputException(
                    "Hourly rate cannot be null");
        }

        if (hoursWorked.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPayrollInputException(
                    "Hours worked cannot be negative");
        }

        if (hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPayrollInputException(
                    "Hourly rate cannot be negative");
        }
    }
}