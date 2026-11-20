package com.rosewood.payroll.tax;

import com.rosewood.payroll.exception.InvalidPayrollInputException;
import com.rosewood.payroll.exception.InvalidTaxConfigurationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BandBasedTaxCalculator implements TaxCalculator {

    private final List<TaxBand> taxBands;

    public BandBasedTaxCalculator(List<TaxBand> taxBands) {
        Objects.requireNonNull(taxBands, "Tax bands cannot be null");

        if (taxBands.isEmpty()) {
            throw new InvalidTaxConfigurationException(
                    "At least one tax band must be configured");
        }

        this.taxBands = taxBands.stream()
                .sorted((first, second) ->
                        first.lowerBound().compareTo(second.lowerBound()))
                .toList();

        validateTaxBands();
    }

    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        Objects.requireNonNull(grossPay, "Gross pay cannot be null");

        if (grossPay.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPayrollInputException(
                    "Gross pay cannot be negative");
        }

        TaxBand applicableBand = taxBands.stream()
                .filter(band -> band.appliesTo(grossPay))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidTaxConfigurationException(
                                "No tax band configured for gross pay: "
                                        + grossPay));

        return grossPay.multiply(applicableBand.taxRate());
    }

    private void validateTaxBands() {
        for (int i = 0; i < taxBands.size() - 1; i++) {
            TaxBand current = taxBands.get(i);
            TaxBand next = taxBands.get(i + 1);

            if (current.upperBound() == null
                    || current.upperBound().compareTo(next.lowerBound()) != 0) {

                throw new InvalidTaxConfigurationException(
                        "Tax bands must be continuous and non-overlapping: "
                                + current.name() + " and " + next.name());
            }
        }
    }
}
