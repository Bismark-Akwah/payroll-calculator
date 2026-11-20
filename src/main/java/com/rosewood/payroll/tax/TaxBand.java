package com.rosewood.payroll.tax;

import com.rosewood.payroll.exception.InvalidTaxConfigurationException;

import java.math.BigDecimal;
import java.util.Objects;

public record TaxBand(
        String name,
        BigDecimal lowerBound,
        BigDecimal upperBound,
        BigDecimal taxRate) {

    public TaxBand {
        Objects.requireNonNull(name, "Tax band name cannot be null");
        Objects.requireNonNull(lowerBound, "Lower bound cannot be null");
        Objects.requireNonNull(taxRate, "Tax rate cannot be null");

        if (name.isBlank()) {
            throw new InvalidTaxConfigurationException(
                    "Tax band name cannot be blank");
        }

        if (lowerBound.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTaxConfigurationException(
                    "Lower bound cannot be negative");
        }

        if (upperBound != null
                && upperBound.compareTo(lowerBound) <= 0) {
            throw new InvalidTaxConfigurationException(
                    "Upper bound must be greater than lower bound");
        }

        if (taxRate.compareTo(BigDecimal.ZERO) < 0
                || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidTaxConfigurationException(
                    "Tax rate must be between 0 and 1");
        }
    }

    public boolean appliesTo(BigDecimal grossPay) {
        boolean aboveLowerBound =
                grossPay.compareTo(lowerBound) >= 0;

        boolean belowUpperBound =
                upperBound == null
                        || grossPay.compareTo(upperBound) < 0;

        return aboveLowerBound && belowUpperBound;
    }
}
