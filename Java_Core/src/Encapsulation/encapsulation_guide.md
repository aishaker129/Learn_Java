# Advanced Java Encapsulation

## Deep-Dive Notes with Examples (Java 17+)

**Level:** Advanced\
**Objective:** Understand encapsulation as a way to protect object
invariants and control state transitions---not merely as `private`
fields and getters/setters.

## Table of Contents

1.  Meaning of Encapsulation
2.  Encapsulation, Data Hiding, and Abstraction
3.  Object Invariants
4.  Access Modifiers
5.  Why Setters Can Be Dangerous
6.  Validation and Normalization
7.  Immutability
8.  Defensive Copying
9.  Aliasing and Mutable References
10. Collections
11. Inheritance and Encapsulation
12. Composition
13. Thread Safety
14. Java Memory Model
15. Records
16. Domain-Driven Design
17. Encapsulation in Spring Boot Applications
18. Common Mistakes
19. Refactoring Strategy
20. Complete Bank Account Example
21. Advanced Questions
22. Design Checklist
23. Final Mental Model

------------------------------------------------------------------------

## 1. Meaning of Encapsulation

Encapsulation is the object-oriented design principle of keeping state
and the behavior that controls that state together, while restricting
uncontrolled access to internal implementation.

An object should be responsible for maintaining its own correctness.

Weak design:

``` java
public class BankAccount {
    public double balance;
}
```

Any caller can now assign an invalid value:

``` java
BankAccount account = new BankAccount();
account.balance = -5000;
```

A stronger design exposes meaningful operations:

``` java
public class BankAccount {

    private BigDecimal balance;

    public BankAccount(BigDecimal initialBalance) {
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new IllegalArgumentException("Invalid initial balance");
        }
        this.balance = initialBalance;
    }

    public void deposit(BigDecimal amount) {
        validatePositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validatePositive(amount);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

The caller asks the object to perform an operation instead of directly
changing its state.

**Key principle:** True encapsulation gives a class control over how its
state is observed and changed.

A class with private fields and unrestricted setters may hide its fields
syntactically while still failing to protect its invariants.

------------------------------------------------------------------------

## 2. Encapsulation, Data Hiding, and Abstraction

  --------------------------------------------------------------------------
  Concept                 Main purpose            Example
  ----------------------- ----------------------- --------------------------
  Encapsulation           Keep state and behavior `BankAccount.withdraw()`
                          together; control state 
                          changes                 

  Data hiding             Restrict direct access  `private balance`
                          to implementation       
                          details                 

  Abstraction             Expose essential        `PaymentService.pay()`
                          behavior and hide       
                          unnecessary complexity  
  --------------------------------------------------------------------------

Example:

``` java
public class PaymentService {

    public void pay(BigDecimal amount) {
        validate(amount);
        checkLimit(amount);
        processPayment(amount);
        saveTransaction(amount);
    }

    private void validate(BigDecimal amount) { }
    private void checkLimit(BigDecimal amount) { }
    private void processPayment(BigDecimal amount) { }
    private void saveTransaction(BigDecimal amount) { }
}
```

The public method is the abstraction. Private methods hide
implementation details. The class encapsulates the workflow.

------------------------------------------------------------------------

## 3. Object Invariants: The Heart of Encapsulation

An **invariant** is a condition that must remain true for an object
throughout its valid lifetime.

Examples:

-   Account balance must not be negative.
-   An order must contain at least one item before submission.
-   An employee's salary must not be negative.
-   A date range's start must not be after its end.
-   A confirmed order cannot be edited.

``` java
public class Order {

    private final List<String> items = new ArrayList<>();
    private boolean confirmed;

    public void addItem(String item) {
        if (confirmed) {
            throw new IllegalStateException("Confirmed order cannot be changed");
        }
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("Item is required");
        }
        items.add(item);
    }

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an empty order");
        }
        confirmed = true;
    }
}
```

Without encapsulation, every caller must remember every rule. This
causes duplicated validation and inconsistent behavior.

**Design rule:** Put a rule as close as possible to the state it
protects.

------------------------------------------------------------------------

## 4. Access Modifiers and Their Design Meaning

  -----------------------------------------------------------------------------
  Modifier              Same class   Same package    Subclass in Other packages
                                                         another 
                                                         package 
  ----------------- -------------- -------------- -------------- --------------
  `private`                    Yes             No             No             No

  package-private              Yes            Yes             No             No

  `protected`                  Yes            Yes   Yes, subject             No
                                                  to Java access 
                                                           rules 

  `public`                     Yes            Yes            Yes            Yes
  -----------------------------------------------------------------------------

### `private`

Use it for implementation details that should not be directly accessible
outside the class.

``` java
public class Employee {
    private BigDecimal salary;
}
```

### Package-private

When no modifier is written, access is limited to the same package.

``` java
class SalaryCalculator {
    BigDecimal calculate(BigDecimal base) {
        return base.multiply(new BigDecimal("1.10"));
    }
}
```

Useful for internal collaboration within a package without making a type
part of the public API.

### `protected`

`protected` is often overused. It exposes implementation details to
subclasses and package peers.

``` java
public class BaseAccount {
    protected BigDecimal balance;
}
```

A subclass can now bypass validation by changing `balance` directly.
Prefer private state with carefully designed protected operations.

### `public`

Every public member can become part of a class's API. Other code may
depend on it, making future changes expensive.

**Rule:** Choose the narrowest access level that supports the intended
use.

------------------------------------------------------------------------

## 5. Why Setters Can Be Dangerous

A setter is not automatically bad. The problem is exposing arbitrary
state changes when the domain requires controlled transitions.

Weak design:

``` java
public class Order {
    private OrderStatus status;

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
```

A caller could mark an unpaid order as delivered.

A stronger design models valid transitions:

``` java
enum OrderStatus {
    CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
}

public class Order {

    private OrderStatus status = OrderStatus.CREATED;

    public void markPaid() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Invalid payment transition");
        }
        status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be shipped");
        }
        status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be delivered");
        }
        status = OrderStatus.DELIVERED;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
```

The API communicates intent and prevents invalid state transitions.

### When can setters be appropriate?

-   DTOs used for data binding.
-   Simple data carriers without meaningful invariants.
-   Properties that are genuinely independent.
-   Framework integration where mutability is an explicit trade-off.

Do not confuse framework convenience with good domain modeling.

------------------------------------------------------------------------

## 6. Validation and Normalization at Object Boundaries

Validate data when it enters the object.

``` java
public final class User {

    private final String email;

    public User(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        this.email = email.trim().toLowerCase(Locale.ROOT);
    }

    public String getEmail() {
        return email;
    }
}
```

Validation checks whether input is acceptable. Normalization converts
acceptable input into a consistent representation.

Examples: - Trim surrounding whitespace. - Normalize case when the
domain treats case as irrelevant. - Convert phone numbers to a canonical
representation. - Use `BigDecimal` for monetary values instead of
`double`.

Normalization must follow domain rules. Blindly lowercasing every
identifier can be incorrect.

If multiple public methods can change a field, each must preserve the
invariant. Centralize validation or prefer immutable state where
appropriate.

------------------------------------------------------------------------

## 7. Immutability and Encapsulation

An immutable object cannot be observably changed after construction.

Benefits: - Easier reasoning. - Safer sharing between components. -
Fewer accidental state changes. - Simpler concurrent use when the
reachable object graph is immutable. - Stable values for keys and domain
concepts.

``` java
public final class Money {

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.amount = amount;
        this.currency = Objects.requireNonNull(currency);
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }
}
```

A well-designed immutable class usually: 1. Is `final`, or otherwise
prevents unsafe subclassing. 2. Uses private final fields. 3.
Initializes required state in its constructor. 4. Has no mutators. 5.
Does not leak mutable internal references. 6. Uses immutable referenced
objects or defensive copies.

**Important:** `final` on a reference does not make the referenced
object immutable.

``` java
private final List<String> names = new ArrayList<>();
```

The reference cannot be reassigned, but the list can still be modified.

------------------------------------------------------------------------

## 8. Defensive Copying

Defensive copying prevents callers from changing internal mutable state
through a shared reference.

Broken example:

``` java
public class Team {

    private final List<String> members;

    public Team(List<String> members) {
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }
}
```

Both the caller and `Team` hold a reference to the same list.

``` java
List<String> input = new ArrayList<>();
input.add("A");

Team team = new Team(input);
input.add("B"); // Mutates Team's internal list.
team.getMembers().clear(); // Also mutates Team's internal list.
```

Safer design:

``` java
public class Team {

    private final List<String> members;

    public Team(List<String> members) {
        this.members = new ArrayList<>(Objects.requireNonNull(members));
    }

    public List<String> getMembers() {
        return List.copyOf(members);
    }
}
```

### Shallow copy vs deep copy

A shallow copy duplicates the collection structure, not the objects
stored inside it.

``` java
List<Employee> copy = new ArrayList<>(employees);
```

Both lists still refer to the same `Employee` objects.

A deep copy duplicates mutable elements too. The implementation depends
on the element type and domain.

`List.copyOf` returns an unmodifiable collection structure; it does not
deep-copy mutable elements.

------------------------------------------------------------------------

## 9. Aliasing and Mutable References

Aliasing occurs when multiple references point to the same object.

``` java
Address address = new Address("Dhaka");
Person person = new Person(address);

address.setCity("Chattogram");
```

If `Person` stores the original mutable reference, its internal state
changes without calling a method on `Person`.

### Preferred solution: immutable value object

``` java
public record Address(String city) {
    public Address {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
    }
}
```

Alternatively, copy mutable objects on input and output.

Whenever a class accepts or returns an object reference, ask:

> Can the caller use this reference to mutate state that this class is
> responsible for protecting?

------------------------------------------------------------------------

## 10. Encapsulation with Collections

Three common choices:

``` java
// 1. Exposes the internal mutable collection: usually unsafe.
public List<String> getRoles() {
    return roles;
}

// 2. Read-only live view: callers cannot mutate through this view,
// but changes made internally remain visible.
public List<String> getRolesView() {
    return Collections.unmodifiableList(roles);
}

// 3. Unmodifiable snapshot: later internal collection changes
// are not reflected in the returned list.
public List<String> getRolesSnapshot() {
    return List.copyOf(roles);
}
```

Controlled mutation:

``` java
public void addRole(String role) {
    if (role == null || role.isBlank()) {
        throw new IllegalArgumentException("Role is required");
    }
    if (!roles.contains(role)) {
        roles.add(role);
    }
}
```

  -----------------------------------------------------------------------
  Approach            Caller can mutate through   Reflects later internal
                                returned value?                  changes?
  ------------------- ------------------------- -------------------------
  Internal list                             Yes                       Yes

  Unmodifiable live                          No                       Yes
  view                                          

  Unmodifiable                               No                        No
  snapshot                                      
  -----------------------------------------------------------------------

These approaches do not automatically make mutable elements immutable.

------------------------------------------------------------------------

## 11. Inheritance and Encapsulation

Inheritance can weaken encapsulation when subclasses receive direct
access to internal state.

Weak design:

``` java
public class Account {
    protected BigDecimal balance;
}

public class SavingsAccount extends Account {
    public void resetBalance() {
        balance = BigDecimal.ZERO;
    }
}
```

The subclass bypasses the parent's rules.

Better:

``` java
public class Account {

    private BigDecimal balance;

    protected void credit(BigDecimal amount) {
        validatePositive(amount);
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

A subclass can use a deliberate extension point without directly
modifying the field.

### Avoid overridable calls from constructors

``` java
class Parent {
    Parent() {
        initialize(); // Risky if overridden
    }

    void initialize() {}
}
```

An override may execute before subclass initialization has completed,
exposing partially initialized state.

**Rule:** Keep fields private and expose the smallest safe set of
extension operations.

------------------------------------------------------------------------

## 12. Composition and Encapsulation

Composition often provides better control than exposing implementation
through inheritance.

``` java
public class OrderService {

    private final PricingPolicy pricingPolicy;

    public OrderService(PricingPolicy pricingPolicy) {
        this.pricingPolicy = Objects.requireNonNull(pricingPolicy);
    }

    public BigDecimal calculateTotal(Order order) {
        return pricingPolicy.calculate(order);
    }
}
```

The collaborator is injected through an interface, while the service
controls how it is used.

Encapsulation does not mean every field must be hidden from every
collaborator. It means access should be intentional and controlled.

------------------------------------------------------------------------

## 13. Encapsulation and Thread Safety

`private` does not make a field thread-safe.

This class is not thread-safe:

``` java
public class Counter {
    private int count;

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

`count++` is a read-modify-write operation. Concurrent calls can lose
updates.

### Option 1: Synchronization

``` java
public class Counter {
    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

### Option 2: Atomic type

``` java
public class Counter {
    private final AtomicInteger count = new AtomicInteger();

    public void increment() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}
```

### Protect compound invariants

If a balance check and balance update must happen together, synchronize
the entire state transition, not just individual field access.

``` java
public class LimitedAccount {

    private BigDecimal balance;
    private final BigDecimal withdrawalLimit;

    public synchronized void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount.compareTo(withdrawalLimit) > 0) {
            throw new IllegalArgumentException("Limit exceeded");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance = balance.subtract(amount);
    }
}
```

This is simplified; a real daily limit needs transaction history and a
defined reset period.

-   Encapsulation controls access to state.
-   Thread safety controls correctness under concurrent access.
-   Synchronization, locks, atomics, and immutability are concurrency
    tools.
-   A private field alone gives no concurrency guarantee.

------------------------------------------------------------------------

## 14. Encapsulation and the Java Memory Model

The Java Memory Model defines how threads interact through memory.

### Final fields

When an object is correctly constructed and does not escape during
construction, final fields receive special initialization-safety
guarantees.

``` java
public final class Configuration {
    private final String environment;

    public Configuration(String environment) {
        this.environment = Objects.requireNonNull(environment);
    }
}
```

This does not make every object reachable through a field immutable.

### Volatile

`volatile` provides visibility and ordering guarantees for reads and
writes of that variable. It does not make compound operations such as
`count++` atomic.

``` java
private volatile boolean running = true;
```

### Safe publication

Objects should be made visible to other threads through a safe
publication mechanism, such as: - Proper synchronization. - A volatile
reference. - A thread-safe collection. - Static initialization. -
Correctly constructed immutable objects published safely.

Encapsulation clarifies where state changes happen; the Java Memory
Model defines visibility and ordering.

------------------------------------------------------------------------

## 15. Encapsulation with Records

Records are designed for transparent data carriers. Their component
fields are private and final, and Java generates accessors and
value-oriented methods.

``` java
public record UserProfile(String name, String email) {

    public UserProfile {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

A record is shallowly immutable. Defend mutable components:

``` java
public record Team(List<String> members) {
    public Team {
        members = List.copyOf(members);
    }
}
```

Good record candidates: - DTOs. - Query results. - Value objects. -
Immutable request/response data.

A record is not automatically the best choice for a domain entity with a
complex lifecycle.

------------------------------------------------------------------------

## 16. Encapsulation in Domain-Driven Design

In Domain-Driven Design, an entity or aggregate protects its business
invariants.

``` java
public class Order {

    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public void addLine(Product product, int quantity) {
        ensureDraft();

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        lines.add(new OrderLine(product, quantity));
    }

    public void submit() {
        ensureDraft();

        if (lines.isEmpty()) {
            throw new IllegalStateException("Order must contain a line");
        }

        status = OrderStatus.SUBMITTED;
    }

    public List<OrderLine> getLines() {
        return List.copyOf(lines);
    }

    private void ensureDraft() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order is not editable");
        }
    }
}
```

The aggregate root (`Order`) controls changes to its internal
collection.

External code should not mutate an aggregate's internal entities or
collections in ways that bypass aggregate rules.

An **anemic domain model** often contains only fields and
getters/setters while business rules live in unrelated services. Not
every application needs rich domain objects, but when business
invariants matter, behavior should usually live near the state it
protects.

------------------------------------------------------------------------

## 17. Encapsulation in Spring Boot Applications

A common layered structure:

``` text
Controller
    |
    v
Application Service
    |
    v
Domain Model
    |
    v
Repository Interface
```

### DTO: boundary data

``` java
public record CreateAccountRequest(
    String accountNumber,
    BigDecimal initialBalance
) {}
```

### Domain object: business rules

``` java
public class Account {

    private final String accountNumber;
    private BigDecimal balance;

    public Account(String accountNumber, BigDecimal initialBalance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new IllegalArgumentException("Invalid initial balance");
        }

        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance = balance.add(amount);
    }
}
```

### Application service: coordinate a use case

``` java
@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = repository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.deposit(amount);
        repository.save(account);
    }
}
```

This is illustrative; actual repository and persistence mapping must
match the domain model.

Responsibilities: - **DTO:** transport data across boundaries. -
**Domain object:** protect business invariants. - **Application
service:** coordinate use cases and transactions. - **Repository:**
handle persistence.

DTOs may use setters when required by binding frameworks. Domain objects
should not become unsafe merely to make framework binding convenient.

Avoid exposing JPA entities directly as public API models without
considering lazy loading, serialization, data exposure, and invariant
bypass.

------------------------------------------------------------------------

## 18. Common Design Mistakes

### Mistake 1: Private fields with unrestricted setters

``` java
private BigDecimal balance;

public void setBalance(BigDecimal balance) {
    this.balance = balance;
}
```

Problem: callers can bypass business rules. Prefer operations such as
`deposit` and `withdraw`.

### Mistake 2: Returning mutable internal collections

``` java
public List<String> getRoles() {
    return roles;
}
```

Problem: callers can mutate internal state. Return a snapshot or
intentional unmodifiable view.

### Mistake 3: Assuming `final` means immutable

A final reference to an `ArrayList` still refers to a mutable list. Use
immutable values or defensive copies.

### Mistake 4: Using protected fields everywhere

Subclasses can bypass parent invariants. Keep fields private and expose
deliberate extension points.

### Mistake 5: Putting all rules in controllers

Other entry points may bypass the controller. Enforce core invariants in
the domain object as well as validating external input at boundaries.

### Mistake 6: Confusing read-only with immutable

An unmodifiable list may contain mutable objects. Consider the entire
object graph.

### Mistake 7: Over-encapsulation

Unnecessary wrappers and excessive private methods can make code harder
to use. Expose a small, meaningful API---not an unnecessarily
complicated one.

------------------------------------------------------------------------

## 19. Refactoring Weak Encapsulation

Use this process when improving an existing class.

1.  **Identify state:** List all fields and determine which can change.
2.  **Identify invariants:** Write down conditions that must always
    hold.
3.  **Find mutation paths:** Search for public setters, exposed fields,
    returned mutable collections, and mutable references.
4.  **Design meaningful methods:** Replace `setStatus(PAID)` with
    `markPaid()`, or direct balance assignment with `withdraw(amount)`.
5.  **Make state private:** Expose only what callers need.
6.  **Defend references:** Use immutable values, defensive copies, or
    controlled views.
7.  **Test invariants:** Test both valid and invalid transitions.
8.  **Review concurrency:** Define the synchronization strategy if
    multiple threads can access the object.

------------------------------------------------------------------------

## 20. Complete Example: Bank Account

This example combines validation, immutable identity, controlled
mutation, and a meaningful public API.

``` java
import java.math.BigDecimal;
import java.util.Objects;

public final class BankAccount {

    private final String accountNumber;
    private BigDecimal balance;

    public BankAccount(String accountNumber, BigDecimal initialBalance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }

        Objects.requireNonNull(initialBalance, "Initial balance is required");

        if (initialBalance.signum() < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public synchronized void deposit(BigDecimal amount) {
        validatePositive(amount);
        balance = balance.add(amount);
    }

    public synchronized void withdraw(BigDecimal amount) {
        validatePositive(amount);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        balance = balance.subtract(amount);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    private void validatePositive(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount is required");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

### Why this design is stronger

1.  `accountNumber` cannot change after construction.
2.  `balance` is private.
3.  Construction validates the initial state.
4.  Balance changes happen through business operations.
5.  Invalid deposits and withdrawals are rejected.
6.  The balance check and update happen under the same monitor.
7.  `BigDecimal` is immutable, so returning it does not expose mutable
    internal state.

### What this example does not solve

A production banking system also needs transaction boundaries,
persistence consistency, idempotency, audit history, authorization,
currency rules, and protection against concurrent updates across
multiple application instances.

Java `synchronized` coordinates threads using the same object instance
inside one JVM; it does not coordinate separate application instances or
servers.

------------------------------------------------------------------------

## 21. Advanced Questions and Code Analysis

Try to answer each question before reading the explanation.

### Q1. Does `private` guarantee immutability?

``` java
public class Example {
    private final List<String> values = new ArrayList<>();

    public List<String> getValues() {
        return values;
    }
}
```

**Answer:** No. The reference cannot be reassigned, but the list is
mutable and the getter exposes it.

### Q2. Is an unmodifiable view a snapshot?

``` java
List<String> view = Collections.unmodifiableList(values);
```

**Answer:** No. It is a read-only view of the underlying list. Changes
through another reference can still be observed.

### Q3. What is wrong here?

``` java
public class User {
    private Address address;

    public User(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
```

**Answer:** If `Address` is mutable, callers can mutate the user's
internal state through the constructor argument or getter result.

### Q4. Is this thread-safe?

``` java
public class Counter {
    private volatile int count;

    public void increment() {
        count++;
    }
}
```

**Answer:** No. `volatile` provides visibility guarantees, but `count++`
is not atomic.

### Q5. Why is this API dangerous?

``` java
public void setStatus(OrderStatus status) {
    this.status = status;
}
```

**Answer:** It allows arbitrary state transitions and may let callers
skip required business steps.

### Q6. Does `List.copyOf` create a deep copy?

**Answer:** No. It creates an unmodifiable collection structure, but
does not copy mutable elements.

### Q7. Must a DTO have strict domain encapsulation?

**Answer:** Not necessarily. DTOs primarily transfer data. Domain
objects have the stronger responsibility of protecting business
invariants.

### Q8. Why keep fields private in a base class?

**Answer:** It prevents subclasses from changing internal state without
following the base class's rules. Protected behavior can provide
controlled extension points.

### Q9. Can an immutable object contain a mutable object?

**Answer:** It can, but it is not deeply immutable unless it prevents
external mutation and does not expose the mutable object.

### Q10. Where should a business invariant be enforced?

**Answer:** As close as possible to the state it protects---usually
inside the domain object or aggregate responsible for that state.
Boundary validation is useful but should not be the only protection.

------------------------------------------------------------------------

## 22. Design Checklist

Before considering a class well-encapsulated, ask:

-   [ ] Are fields private unless a carefully justified alternative is
    needed?
-   [ ] Does the constructor establish a valid initial state?
-   [ ] Are important invariants documented and enforced?
-   [ ] Do public methods express meaningful operations?
-   [ ] Can callers bypass business rules through setters?
-   [ ] Are mutable constructor arguments defensively copied?
-   [ ] Are mutable internal objects protected from escaping?
-   [ ] Are returned collections snapshots or intentional read-only
    views?
-   [ ] Are nested objects immutable or safely controlled?
-   [ ] Are subclass extension points deliberate?
-   [ ] Is concurrent access handled where required?
-   [ ] Do tests cover invalid states and transitions?

------------------------------------------------------------------------

## 23. Final Mental Model

Think of an object as a small boundary of responsibility:

``` text
             External Code
                   |
                   | public operations
                   v
        +------------------------+
        |      Encapsulated      |
        |         Object         |
        |                        |
        |  Private State         |
        |  Business Invariants   |
        |  Validation            |
        |  Controlled Mutation   |
        +------------------------+
                   |
                   v
              Valid State
```

> **Do not let external code freely manipulate the state that your
> object is responsible for keeping valid. Let the object expose
> meaningful operations that preserve its invariants.**

Encapsulation is not about hiding every detail. It is about controlling
the boundary between an object's responsibilities and the rest of the
system.

## Recommended Practice

Build these classes and test their invariants:

1.  `BankAccount` --- deposit, withdraw, transfer, and balance rules.
2.  `ShoppingCart` --- add/remove items, quantity validation, and total
    calculation.
3.  `Order` --- valid lifecycle transitions.
4.  `Employee` --- salary changes with validation and role restrictions.
5.  `Library` --- manage books and borrowing limits without exposing
    internal collections.
6.  `Wallet` --- immutable identity, controlled balance changes, and
    concurrent access.

For each project, answer:

-   What state does this object own?
-   Which invariants must always hold?
-   Which operations are allowed to change its state?
-   Which references must never escape?
-   What happens when two threads call a mutating operation
    simultaneously?

If you can answer these questions clearly, you are applying
encapsulation as a software-design principle rather than merely using
access modifiers.
