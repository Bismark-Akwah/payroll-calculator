package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.Objects;

public record PayrollResult(
        String employeeId,
        String employeeName,
        BigDecimal grossPay,
        BigDecimal taxAmount,
        BigDecimal netPay) {

    public PayrollResult {
        Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        Objects.requireNonNull(employeeName, "Employee name cannot be null");
        Objects.requireNonNull(grossPay, "Gross pay cannot be null");
        Objects.requireNonNull(taxAmount, "Tax amount cannot be null");
        Objects.requireNonNull(netPay, "Net pay cannot be null");
    }
}