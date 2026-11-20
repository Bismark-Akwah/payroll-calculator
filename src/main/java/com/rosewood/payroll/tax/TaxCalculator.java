package com.rosewood.payroll.tax;

import java.math.BigDecimal;

public interface TaxCalculator {

    BigDecimal calculateTax(BigDecimal grossPay);
}