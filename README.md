

# Rosewood Payroll Calculator

## 1. Overview

The Rosewood Payroll Calculator is a Java application that calculates employee gross pay, tax deductions, and net pay.

The project began as a tightly coupled payroll calculator with hardcoded tax rules and limited input validation. It has been refactored to improve maintainability, testability, and extensibility while preserving the original payroll calculation behaviour.

## 2. Features

- Calculates employee gross pay, including overtime.
- Applies configurable tax bands.
- Calculates tax deductions and net pay.
- Retrieves employee information through an employee repository.
- Validates payroll inputs and tax configurations.
- Uses meaningful exceptions to handle invalid input and missing employees.
- Includes unit and integration tests using JUnit 5 and Mockito.

## 3. Payroll Rules

### Gross pay

- Standard working hours: 40 hours per week.
- Hours above 40 receive an additional 50% overtime premium.
- Overtime is calculated on top of the normal hourly rate.

**Example:**

An employee works 50 hours at GHS 30 per hour.

- Normal pay: 50 × 30 = GHS 1,500
- Overtime premium: 10 × 30 × 0.5 = GHS 150
- Gross pay: GHS 1,650

### Tax calculation

Tax is calculated as a flat percentage of the employee's entire gross pay, based on the applicable tax band.

| Gross pay | Tax rate |
|---|---:|
| Below GHS 1,000 | 5% |
| GHS 1,000 to below GHS 5,000 | 15% |
| GHS 5,000 and above | 25% |

The tax bands are configurable, allowing new bands or different rates to be introduced without changing the tax calculation logic.

## 4. Project Structure

```text
code-quality-c-rosewood-payroll/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/rosewood/payroll/
│   │           ├── exception/
│   │           │   ├── EmployeeNotFoundException.java
│   │           │   ├── InvalidPayrollInputException.java
│   │           │   └── InvalidTaxConfigurationException.java
│   │           ├── tax/
│   │           │   ├── BandBasedTaxCalculator.java
│   │           │   ├── TaxBand.java
│   │           │   └── TaxCalculator.java
│   │           ├── Demo.java
│   │           ├── Employee.java
│   │           ├── EmployeeRepository.java
│   │           ├── GrossPayCalculator.java
│   │           ├── InMemoryEmployeeRepository.java
│   │           ├── PayrollCalculator.java
│   │           └── PayrollResult.java
│   └── test/
│       └── java/
│           └── com/rosewood/payroll/
│               ├── tax/
│               │   └── BandBasedTaxCalculatorTest.java
│               ├── EmployeeTest.java
│               ├── GrossPayCalculatorTest.java
│               ├── InMemoryEmployeeRepositoryTest.java
│               ├── PayrollCalculatorTest.java
│               └── PayrollIntegrationTest.java
├── Analysis.md
├── DesignRationale.md
├── README.md
└── pom.xml
```

## 5. Technologies

- **Java 21** – Main programming language.
- **Maven** – Project and dependency management.
- **JUnit 5** – Unit and integration testing.
- **Mockito** – Mocking dependencies in unit tests.
- **BigDecimal** – Accurate decimal arithmetic for financial calculations.
- **IntelliJ IDEA** – Development environment.

## 6. Running the Application

The application can be run directly from IntelliJ IDEA.

1. Open the project in IntelliJ IDEA.
2. Navigate to `src/main/java/com/rosewood/payroll/Demo.java`.
3. Open `Demo.java`.
4. Click the green Run icon beside the `main` method, or right-click the file and select **Run 'Demo.main()'**.
5. View the output in the Run console.

**Prerequisite:** Ensure that JDK 21 is configured for the project.

The `Demo` class provides sample payroll calculations using the in-memory employee repository and configured tax bands.

## 7. Running the Tests

The project includes six test classes covering individual components and the overall payroll calculation flow.

To run all tests using IntelliJ IDEA:

1. Open the project in IntelliJ IDEA.
2. Locate the `src/test/java` directory in the Project panel.
3. Right-click the test directory.
4. Select **Run 'All Tests'**.
5. View the test results in the Run window.

### Test coverage

| Test class | What it verifies |
|---|---|
| `EmployeeTest` | Employee creation and input validation |
| `GrossPayCalculatorTest` | Gross pay and overtime calculations |
| `InMemoryEmployeeRepositoryTest` | Employee lookup behaviour |
| `PayrollCalculatorTest` | Payroll orchestration, dependency behaviour, and exceptions |
| `BandBasedTaxCalculatorTest` | Tax band selection, tax calculation, and configuration validation |
| `PayrollIntegrationTest` | Integration of the payroll components |

## 8. Documentation

The project includes two additional documents:

- **Analysis.md** – Describes the problems, code smells, design weaknesses, and testing issues identified in the original implementation.
- **DesignRationale.md** – Explains the design decisions made during the refactoring, including separation of responsibilities, dependency injection, configurable tax bands, exception handling, and the use of `BigDecimal`.

## 9. Key Design Improvements

The refactoring introduces the following improvements:

- **Separation of responsibilities:** Gross pay calculation, tax calculation, employee retrieval, and payroll orchestration are handled by separate components.
- **Dependency injection:** `PayrollCalculator` receives its dependencies through its constructor rather than creating them internally.
- **Abstraction:** Interfaces such as `EmployeeRepository` and `TaxCalculator` make implementations easier to replace.
- **Extensibility:** Tax rules are represented by configurable `TaxBand` objects.
- **Input validation:** Invalid payroll inputs and tax configurations are rejected instead of silently producing incorrect results.
- **Meaningful exceptions:** Specific exceptions communicate problems such as missing employees and invalid payroll inputs.
- **Financial accuracy:** `BigDecimal` replaces `double` for monetary calculations.
- **Automated testing:** Unit and integration tests verify expected behaviour and important edge cases.

