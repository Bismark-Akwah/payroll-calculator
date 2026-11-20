
package com.rosewood.payroll;

import com.rosewood.payroll.tax.BandBasedTaxCalculator;
import com.rosewood.payroll.tax.TaxBand;
import com.rosewood.payroll.tax.TaxCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PayrollIntegrationTest {

    @Test
    void calculatesPayrollUsingRealComponents() {

        Employee employee = new Employee("e1", "Efua Sarpong");

        EmployeeRepository repository =
                new InMemoryEmployeeRepository(List.of(employee));

        List<TaxBand> taxBands = List.of(
                new TaxBand(
                        "LOW",
                        new BigDecimal("0"),
                        new BigDecimal("1000"),
                        new BigDecimal("0.05")
                ),
                new TaxBand(
                        "MEDIUM",
                        new BigDecimal("1000"),
                        new BigDecimal("5000"),
                        new BigDecimal("0.15")
                ),
                new TaxBand(
                        "HIGH",
                        new BigDecimal("5000"),
                        null,
                        new BigDecimal("0.25")
                )
        );

        GrossPayCalculator grossPayCalculator =
                new GrossPayCalculator();

        TaxCalculator taxCalculator =
                new BandBasedTaxCalculator(taxBands);

        PayrollCalculator payrollCalculator = new PayrollCalculator(
                repository,
                grossPayCalculator,
                taxCalculator
        );

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
    }

    private void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "Expected " + expected + " but got " + actual
        );
    }
}
