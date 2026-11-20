package com.rosewood.payroll.tax;

import com.rosewood.payroll.exception.InvalidTaxConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BandBasedTaxCalculatorTest {

    private TaxCalculator taxCalculator;

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

        taxCalculator = new BandBasedTaxCalculator(taxBands);
    }

    @Test
    void calculatesTaxForLowTaxBand() {
        BigDecimal tax = taxCalculator.calculateTax(
                new BigDecimal("800")
        );

        assertMoneyEquals("40", tax);
    }

    @Test
    void calculatesTaxForMediumTaxBand() {
        BigDecimal tax = taxCalculator.calculateTax(
                new BigDecimal("2000")
        );

        assertMoneyEquals("300", tax);
    }

    @Test
    void calculatesTaxForHighTaxBand() {
        BigDecimal tax = taxCalculator.calculateTax(
                new BigDecimal("6000")
        );

        assertMoneyEquals("1500", tax);
    }

    @Test
    void usesMediumBandAtExactlyOneThousand() {
        BigDecimal tax = taxCalculator.calculateTax(
                new BigDecimal("1000")
        );

        assertMoneyEquals("150", tax);
    }

    @Test
    void usesHighBandAtExactlyFiveThousand() {
        BigDecimal tax = taxCalculator.calculateTax(
                new BigDecimal("5000")
        );

        assertMoneyEquals("1250", tax);
    }

    @Test
    void rejectsNegativeGrossPay() {
        assertThrows(
                IllegalArgumentException.class,
                () -> taxCalculator.calculateTax(
                        new BigDecimal("-100")
                )
        );
    }

    @Test
    void rejectsNullGrossPay() {
        assertThrows(
                NullPointerException.class,
                () -> taxCalculator.calculateTax(null)
        );
    }

    @Test
    void rejectsEmptyTaxBandConfiguration() {
        assertThrows(
                InvalidTaxConfigurationException.class,
                () -> new BandBasedTaxCalculator(List.of())
        );
    }

    @Test
    void rejectsOverlappingTaxBands() {
        List<TaxBand> overlappingBands = List.of(
                new TaxBand("LOW",
                        new BigDecimal("0"),
                        new BigDecimal("2000"),
                        new BigDecimal("0.05")),

                new TaxBand("HIGH",
                        new BigDecimal("1000"),
                        null,
                        new BigDecimal("0.25"))
        );

        assertThrows(
                InvalidTaxConfigurationException.class,
                () -> new BandBasedTaxCalculator(overlappingBands)
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