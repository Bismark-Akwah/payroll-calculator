package com.rosewood.payroll.tax;

import java.math.BigDecimal;
import java.util.List;

public final class TaxBandFactory {

    private TaxBandFactory() {
        // Utility class; prevent instantiation.
    }

    public static List<TaxBand> createDefaultTaxBands() {
        return List.of(
                new TaxBand(
                        "LOW",
                        BigDecimal.ZERO,
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
    }
}
