# Rosewood Payroll — Design Rationale

## 1. Overview

The refactoring of the Rosewood Payroll application focused on improving maintainability, extensibility, testability and the reliability of payroll calculations.

The design decisions were guided by the problems identified in the original codebase and the need to make future changes safer without unnecessarily complicating the application.

This document explains the main decisions made during the refactoring and the reasoning behind them.

## 2. Separation of Responsibilities

### Decision

The original `PayrollCalculator` handled employee retrieval, gross-pay calculation, tax calculation, exception handling and output presentation.

These responsibilities were separated into focused components:

- `EmployeeRepository` handles employee retrieval.
- `GrossPayCalculator` calculates gross pay, including overtime.
- `TaxCalculator` defines the tax calculation contract.
- `BandBasedTaxCalculator` implements tax calculation using configured tax bands.
- `PayrollCalculator` coordinates the payroll calculation.
- `PayrollResult` represents the outcome of a payroll calculation.
- `Demo` handles application setup and presentation of results.

### Rationale

Separating responsibilities makes each component easier to understand, test and modify.

For example, changing the overtime calculation should not require modifying the tax calculation logic.

This design follows the Single Responsibility Principle (SRP) and reduces the risk of unrelated functionality being affected by changes.

## 3. Dependency Injection 

### Decision

`PayrollCalculator` receives its dependencies through its constructor:

- `EmployeeRepository`
- `GrossPayCalculator`
- `TaxCalculator`

It depends on the `EmployeeRepository` and `TaxCalculator` interfaces rather than concrete implementations.

### Rationale

The original implementation created its own repository, tightly coupling payroll calculation to `InMemoryEmployeeRepository`.

Constructor injection removes this dependency creation from `PayrollCalculator`.

This allows different implementations to be supplied without changing the payroll calculation logic.

It also makes testing easier because dependencies can be replaced with Mockito mocks when testing individual components.

This design supports the Dependency Inversion Principle (DIP).

## 4. Extensible Tax Calculation

### Decision

Tax calculation was separated behind the `TaxCalculator` interface.

The `BandBasedTaxCalculator` implementation uses `TaxBand` objects to represent tax rules.

Each tax band contains:

- A name.
- A lower gross-pay boundary.
- An optional upper gross-pay boundary.
- A tax rate.

The calculator selects the applicable band and applies its rate to the entire gross-pay amount.

### Rationale

The original tax rules were hard-coded into conditional statements inside `PayrollCalculator`.

This meant that introducing another tax band required modifying existing payroll logic.

The new design allows tax bands to be added through configuration without changing the main payroll calculation or the tax calculator's selection algorithm.

This supports the Open/Closed Principle (OCP).

The implementation preserves the original flat-tax behaviour: the selected tax rate is applied to the entire gross-pay amount, rather than applying different rates progressively to portions of income.

## 5. Use of Records for Data Representation

### Decision

Java records were used for `TaxBand` and `PayrollResult`.

### Rationale

Both classes primarily represent data rather than complex behaviour.

Records provide:

- Automatically generated accessors.
- Value-based equality.
- A generated `toString()` implementation.
- Final record components.

This reduces boilerplate and makes the purpose of these classes clear.

`Employee` remains a regular class because its existing class-based design is sufficient and does not require conversion into a record merely to reduce code.

## 6. Employee Lookup with Optional

### Decision

The `EmployeeRepository.findById()` method returns `Optional<Employee>` instead of returning `null` when an employee cannot be found.

### Rationale

Returning `Optional` makes the possibility of a missing employee explicit in the method's contract.

`PayrollCalculator` handles the missing result by throwing an `EmployeeNotFoundException`.

This avoids relying on callers to remember null checks and provides a meaningful failure when payroll is requested for an unknown employee.

## 7. Meaningful Exceptions and Input Validation

### Decision

Specific exceptions were introduced:

- `EmployeeNotFoundException`
- `InvalidPayrollInputException`
- `InvalidTaxConfigurationException`

Validation is performed at appropriate boundaries, including employee construction, gross-pay calculation and tax configuration.

### Rationale

The original implementation caught broad exceptions and returned zero, hiding failures.

The refactored implementation allows expected failures to be represented explicitly and propagated to the appropriate caller.

This prevents an unsuccessful payroll calculation from being confused with a legitimate zero payment.

Validation also prevents invalid data and configurations from being accepted into the system.

## 8. Monetary Calculations with BigDecimal

### Decision

`BigDecimal` replaced `double` for monetary values and payroll calculations.

### Rationale

Binary floating-point types such as `double` cannot represent every decimal fraction exactly.

This can introduce precision errors in financial calculations.

`BigDecimal` provides decimal arithmetic that is more appropriate for monetary values.

Values are constructed from strings, for example:

`new BigDecimal("0.05")`

This avoids introducing binary floating-point approximation during construction.

The demo formats displayed amounts to two decimal places using `RoundingMode.HALF_UP`. This is presentation formatting and should not be confused with a complete business rounding policy for payroll calculations.

## 9. Testing Strategy

### Decision

The test suite uses JUnit 5 and Mockito and includes both unit tests and an integration test.

Unit tests focus on individual components:

- `EmployeeTest`
- `GrossPayCalculatorTest`
- `InMemoryEmployeeRepositoryTest`
- `BandBasedTaxCalculatorTest`
- `PayrollCalculatorTest`

`PayrollCalculatorTest` uses Mockito to isolate the repository dependency and verify payroll coordination.

`PayrollIntegrationTest` uses real implementations of the collaborating components to verify that they work together correctly.

### Rationale

Unit tests make it easier to identify problems within individual components.

Integration tests verify that the components cooperate correctly to deliver the expected payroll result.

The suite covers normal calculations, tax-band boundaries, invalid inputs, missing employees and invalid tax configurations.

Assertions verify meaningful results and expected exceptions rather than relying only on interactions between mocks.

## 10. Additional Improvement: Financial Calculation Reliability

The use of `BigDecimal` was selected as an additional improvement beyond the structural refactoring.

This was justified by the financial nature of the application.

Payroll calculations need reliable decimal arithmetic because small precision errors can affect employee payments.

Using `BigDecimal` reduces the risk of binary floating-point representation errors and provides better control over decimal precision and rounding.

## 11. Design Trade-offs and Limitations

The refactoring deliberately avoids introducing unnecessary architectural complexity.

For example:

- `InMemoryEmployeeRepository` remains a simple in-memory implementation because persistent storage was not required by the assignment.
- `Demo` remains a simple console entry point rather than introducing a separate application framework.
- The tax calculation implementation supports configurable bands without introducing a more complicated tax-rule engine.
- The current implementation does not define a complete currency-specific rounding policy. Such a policy would need to be established as a business requirement.

These decisions keep the implementation focused on the assignment's requirements while leaving room for future extension.

## 12. Conclusion

The refactoring improves the original payroll application by separating responsibilities, reducing coupling, introducing extensible tax calculation, validating inputs, handling failures explicitly and improving monetary precision.

The resulting design is easier to test, maintain and extend.

Most importantly, the payroll calculation components now have clearer responsibilities, and their behaviour is verified through meaningful automated tests.