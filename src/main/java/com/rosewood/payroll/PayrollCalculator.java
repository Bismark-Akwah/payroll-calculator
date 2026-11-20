package com.rosewood.payroll;

import com.rosewood.payroll.exception.EmployeeNotFoundException;
import com.rosewood.payroll.exception.InvalidPayrollInputException;
import com.rosewood.payroll.tax.TaxCalculator;

import java.math.BigDecimal;
import java.util.Objects;

public class PayrollCalculator {

    private final EmployeeRepository employeeRepository;
    private final GrossPayCalculator grossPayCalculator;
    private final TaxCalculator taxCalculator;

    public PayrollCalculator(
            EmployeeRepository employeeRepository,
            GrossPayCalculator grossPayCalculator,
            TaxCalculator taxCalculator) {

        this.employeeRepository = Objects.requireNonNull(
                employeeRepository, "Employee repository cannot be null");

        this.grossPayCalculator = Objects.requireNonNull(
                grossPayCalculator, "Gross pay calculator cannot be null");

        this.taxCalculator = Objects.requireNonNull(
                taxCalculator, "Tax calculator cannot be null");
    }

    public PayrollResult calculateNetPay(
            String employeeId,
            BigDecimal hoursWorked,
            BigDecimal hourlyRate) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new InvalidPayrollInputException(
                    "Employee ID cannot be null or blank");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        BigDecimal grossPay = grossPayCalculator.calculateGrossPay(
                hoursWorked, hourlyRate);

        BigDecimal taxAmount = taxCalculator.calculateTax(grossPay);

        BigDecimal netPay = grossPay.subtract(taxAmount);

        return new PayrollResult(
                employee.getId(),
                employee.getName(),
                grossPay,
                taxAmount,
                netPay);
    }
}