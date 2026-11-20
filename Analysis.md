# Rosewood Payroll — Code Quality Analysis

## 1. Overview

The inherited payroll application compiles and its tests pass, but the implementation has several design and testing weaknesses that make it difficult to maintain, extend and trust.

The main concern is that payroll calculations can silently fail or produce misleading results. This is particularly serious because payroll is a financial system where incorrect calculations can affect employee payments.

The following analysis identifies the main problems before refactoring.

## 2. Problems identified in the production code

### 2.1 Broad exception handling and swallowed failures

**Location:** `PayrollCalculator`

```text
catch (Exception e) {
    return 0;
}
```

The application catches every exception and returns zero, regardless of the actual failure.

This hides the original cause of the problem and makes it impossible for callers to distinguish a legitimate zero payment from a failed calculation.

For example, when an employee does not exist, `findById()` returns null. Calling `employee.getName()` then causes a `NullPointerException`, which is caught and silently converted into zero.

**Principles affected:** Fail Fast, meaningful exception handling and Clean Code.

**Proposed improvement:** Introduce meaningful exceptions for expected business failures, validate inputs and avoid swallowing unexpected exceptions.

### 2.2 Multiple responsibilities in PayrollCalculator

**Location:** `PayrollCalculator`

The class currently:

- Retrieves employee information.
- Calculates gross pay.
- Calculates overtime.
- Determines tax bands.
- Calculates net pay.
- Prints payroll information.
- Handles exceptions.

These responsibilities make the class difficult to understand, test and modify safely.

**Principle affected:** Single Responsibility Principle (SRP).

**Proposed improvement:** Separate employee retrieval, gross pay calculation, tax calculation and payroll coordination into appropriately focused components. Move presentation outside the business logic.

### 2.3 Hard-coded dependency

**Location:** `PayrollCalculator`

```text
private final EmployeeRepository repository =
        new InMemoryEmployeeRepository();
```

The calculator creates its own repository implementation instead of receiving its dependency externally.

This tightly couples the calculator to `InMemoryEmployeeRepository`, making it difficult to replace the implementation or supply a mock during testing.

**Principle affected:** Dependency Inversion Principle (DIP).

**Proposed improvement:** Inject `EmployeeRepository` through the constructor so that the calculator depends on the abstraction rather than a concrete implementation.

### 2.4 Tax rules are difficult to extend

**Location:** `PayrollCalculator`

```text
if (gross < 1000) {
    taxRate = 0.05;
} else if (gross < 5000) {
    taxRate = 0.15;
} else {
    taxRate = 0.25;
}
```

Tax rules are embedded directly inside the payroll calculation.

The business expects to introduce another tax band. Adding it would require modifying existing conditional logic, increasing the risk of affecting existing calculations.

**Principles affected:** Open/Closed Principle (OCP) and separation of concerns.

**Proposed improvement:** Introduce an extensible tax-rule abstraction so new tax bands can be added without modifying the main payroll calculation.

### 2.5 Presentation mixed with business logic

**Location:** `PayrollCalculator`

```text
System.out.println(
    "Net pay for " + employee.getName() + ": GHS " + net
);
```

The calculator is responsible for both calculating payroll and displaying the result.

This makes it less reusable because different application interfaces may need to present the same calculation differently.

**Proposed improvement:** Return the calculation result and let the application or presentation layer decide how to display it.

### 2.6 Missing input validation

**Locations:** `Employee` and `PayrollCalculator`

The `Employee` constructor accepts null or blank identifiers and names.

Similarly, the payroll calculation does not validate negative working hours or invalid hourly rates.

This allows invalid data to enter the system and potentially produce incorrect payroll results.

**Proposed improvement:** Introduce appropriate constructor and calculation input validation.

### 2.7 Ambiguous employee lookup result

**Location:** `EmployeeRepository`

```text
Employee findById(String id);
```

The implementation returns null when an employee does not exist.

This means callers must remember to perform null checks, and the possibility of a missing employee is not clearly represented in the method contract.

**Proposed improvement:** Consider using `Optional<Employee>` to represent the possibility that an employee may not exist, and translate that situation into a meaningful `EmployeeNotFoundException` at the appropriate application boundary.

### 2.8 Monetary calculations use double

**Location:** `PayrollCalculator`

The application uses `double` for hourly rates, gross pay, tax and net pay.

Floating-point arithmetic cannot represent every decimal fraction exactly, which can introduce precision issues in financial calculations.

**Proposed improvement:** Use `BigDecimal` for monetary values and establish an explicit rounding policy.

## 3. Problems identified in the AI-generated tests

### 3.1 calculatesNetPay()

```text
assertTrue(net > 0);
```

The test executes the real calculator, but its assertion is too weak.

It only checks whether the returned amount is positive. An incorrect amount would still pass.

For the supplied example:

- Hours worked: 40
- Hourly rate: GHS 20
- Gross pay: GHS 800
- Tax rate: 5%
- Expected net pay: GHS 760

**Proposed improvement:** Assert the exact expected amount using an appropriate floating-point tolerance, or use an exact monetary assertion after introducing `BigDecimal`.

### 3.2 calculatesOvertimePay()

```text
PayrollCalculator calculator =
        mock(PayrollCalculator.class);

when(calculator.calculateNetPay("e2", 50, 30))
        .thenReturn(1402.5);
```

This test mocks the actual class whose behaviour should be tested.

It instructs the mock to return a value and then checks whether that same value is returned.

The real payroll calculation never executes, meaning the test does not verify the overtime calculation or tax rules.

**Proposed improvement:** Instantiate the real calculator and mock its dependencies instead.

### 3.3 returnsPayForAnEmployee()

```text
assertNotNull(net);
```

The variable `net` is a primitive `double`.

Primitive values cannot be null, so this assertion cannot meaningfully fail.

The test therefore does not verify whether the returned payroll amount is correct.

**Proposed improvement:** Assert the expected net pay for the employee.

## 4. SOLID assessment

| Principle | Finding |
|---|---|
| Single Responsibility | Violated: PayrollCalculator handles several unrelated responsibilities. |
| Open/Closed | Weakness: introducing another tax band requires modifying existing conditional logic. |
| Liskov Substitution | No direct violation established in the inherited implementation. |
| Interface Segregation | No direct violation established in the inherited implementation. |
| Dependency Inversion | Violated: PayrollCalculator creates a concrete repository implementation internally. |

The absence of a demonstrated violation of LSP or ISP is intentional. The refactoring should address actual problems rather than invent violations to cover every SOLID principle.

## 5. Most serious problem

The most serious problem is the broad exception handling that silently converts failures into zero.

Payroll calculations must be trustworthy. Returning zero when an employee is missing or an unexpected programming error occurs hides the actual problem and can lead to incorrect financial outcomes.

The weak tests make this even more dangerous because they provide false confidence that the application is working correctly.

## 6. Refactoring direction

The refactoring will focus on:

1. Separating payroll calculation, gross pay calculation, tax calculation and employee retrieval.
2. Injecting dependencies through constructors.
3. Introducing an extensible tax-rule design to accommodate new tax bands.
4. Validating employee data and payroll inputs.
5. Handling expected failures explicitly without swallowing unexpected exceptions.
6. Replacing weak tests with meaningful JUnit 5 and Mockito tests.
7. Using `BigDecimal` for monetary calculations as an additional financial-safety improvement.

The objective is to produce a maintainable, extensible and testable payroll system whose behaviour can be trusted.