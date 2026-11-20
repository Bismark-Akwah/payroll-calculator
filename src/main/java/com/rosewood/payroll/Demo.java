package com.rosewood.payroll;

import com.rosewood.payroll.exception.EmployeeNotFoundException;
import com.rosewood.payroll.tax.BandBasedTaxCalculator;
import com.rosewood.payroll.tax.TaxBandFactory;
import com.rosewood.payroll.tax.TaxCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Demo {

    public static void main(String[] args) {

        EmployeeRepository employeeRepository =
                new InMemoryEmployeeRepository(
                        List.of(
                                new Employee("e1", "Efua Sarpong"),
                                new Employee("e2", "Kwame Owusu")
                        )
                );

        GrossPayCalculator grossPayCalculator =
                new GrossPayCalculator();

        TaxCalculator taxCalculator =
                new BandBasedTaxCalculator(
                        TaxBandFactory.createDefaultTaxBands()
                );

        PayrollCalculator payrollCalculator = new PayrollCalculator(
                employeeRepository,
                grossPayCalculator,
                taxCalculator
        );

        calculateAndDisplayPayroll(
                payrollCalculator,
                "e1",
                new BigDecimal("40"),
                new BigDecimal("20")
        );

        calculateAndDisplayPayroll(
                payrollCalculator,
                "e2",
                new BigDecimal("50"),
                new BigDecimal("30")
        );

        calculateAndDisplayPayroll(
                payrollCalculator,
                "unknown",
                new BigDecimal("40"),
                new BigDecimal("20")
        );
    }

    private static void calculateAndDisplayPayroll(
            PayrollCalculator calculator,
            String employeeId,
            BigDecimal hoursWorked,
            BigDecimal hourlyRate) {

        try {
            PayrollResult result = calculator.calculateNetPay(
                    employeeId,
                    hoursWorked,
                    hourlyRate
            );

            System.out.println("\nPayroll for " + result.employeeName());
            System.out.println("Employee ID: " + result.employeeId());
            System.out.println("Gross pay: GHS " + formatMoney(result.grossPay()));
            System.out.println("Tax: GHS " + formatMoney(result.taxAmount()));
            System.out.println("Net pay: GHS " + formatMoney(result.netPay()));

        } catch (EmployeeNotFoundException exception) {
            System.out.println("\nPayroll could not be calculated: "
                    + exception.getMessage());
        }
    }

    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
