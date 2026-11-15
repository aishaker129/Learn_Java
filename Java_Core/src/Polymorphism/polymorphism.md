# Java OOP: Polymorphism --- Advanced Guide

**Level:** Intermediate to Advanced\
**Language:** Java\
**Goal:** Understand compile-time selection, runtime dispatch, type
systems, interfaces, generics, design principles, and practical
application.

## 1. What Is Polymorphism?

Polymorphism means **many forms**. It allows code to work through a
common type while the actual behavior varies according to the concrete
implementation.

``` java
interface PaymentMethod {
    void pay(BigDecimal amount);
}

class CardPayment implements PaymentMethod {
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paying by card: " + amount);
    }
}

class MobileWalletPayment implements PaymentMethod {
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paying by mobile wallet: " + amount);
    }
}

void checkout(PaymentMethod method, BigDecimal amount) {
    method.pay(amount);
}
```

The client depends on `PaymentMethod`, not on a specific payment class.

**Benefits** - Reduces coupling to concrete implementations. - Makes
implementations replaceable. - Supports extension without repeatedly
changing client logic. - Improves testability. - Helps apply dependency
inversion.

Polymorphism is not simply method overriding. Overriding is a major
mechanism for runtime polymorphism.

## 2. Main Forms in Java

At a practical Java level, distinguish:

1.  **Compile-time polymorphism:** commonly achieved through method
    overloading.
2.  **Runtime polymorphism:** achieved through overriding instance
    methods and dynamic method dispatch.

They use different selection rules.

## 3. Compile-Time Polymorphism: Overloading

Overloading declares methods with the same name but different parameter
lists.

``` java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

The compiler selects an applicable overload using argument types and
compile-time information.

### Rules

-   Method names must match.
-   Parameter lists must differ by number, types, or order.
-   Changing only the return type is not overloading.
-   Access modifiers or `throws` clauses alone do not create an
    overload.
-   Selection happens at compile time.

``` java
class Example {
    int calculate(int value) { return value; }

    // Invalid: return type alone cannot distinguish overloads.
    // double calculate(int value) { return value; }
}
```

### Primitive widening, boxing, and overload resolution

``` java
class Printer {
    void print(long value) {
        System.out.println("long");
    }

    void print(Integer value) {
        System.out.println("Integer");
    }
}

new Printer().print(10); // long
```

Java's overload resolution has phases. In simplified terms, it first
considers applicable methods without boxing (including primitive
widening and reference widening), then considers boxing/unboxing, and
finally variable-arity methods. Therefore, an `int` argument widens to
`long` before the compiler needs to consider boxing to `Integer`.

### Null and overloaded reference parameters

``` java
class Example {
    void show(String value) {}
    void show(Integer value) {}
}

// new Example().show(null); // Ambiguous
```

Neither `String` nor `Integer` is a subtype of the other, so neither
overload is more specific.

``` java
class Example {
    void show(Object value) {}
    void show(String value) {}
}

new Example().show(null); // String
```

`String` is more specific than `Object`.

## 4. Runtime Polymorphism: Overriding

A subclass overrides an inherited instance method with a compatible
implementation.

``` java
class Notification {
    public void send() {
        System.out.println("Generic notification");
    }
}

class EmailNotification extends Notification {
    @Override
    public void send() {
        System.out.println("Sending email");
    }
}

class SmsNotification extends Notification {
    @Override
    public void send() {
        System.out.println("Sending SMS");
    }
}
```

``` java
Notification notification = new EmailNotification();
notification.send(); // Sending email
```

-   Reference/declared type: `Notification`
-   Runtime/actual object type: `EmailNotification`

The reference type controls which members are available to the compiler.
The runtime type selects the overridden instance method implementation.

``` java
class Notification {
    public void send() {}
}

class EmailNotification extends Notification {
    @Override
    public void send() {}

    public void attachFile() {}
}

Notification notification = new EmailNotification();
notification.send();
// notification.attachFile(); // Compile-time error
```

The object is an `EmailNotification`, but the reference exposes members
available through `Notification`.

Pattern matching can safely expose a subtype-specific operation:

``` java
if (notification instanceof EmailNotification email) {
    email.attachFile();
}
```

Avoid unnecessary downcasts. Prefer putting genuinely shared behavior in
the common abstraction.

## 5. Dynamic Method Dispatch

Dynamic method dispatch selects an overridden instance method using the
actual object at runtime.

``` java
class Animal {
    public void makeSound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Bark");
    }
}

class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow");
    }
}

Animal animal = new Dog();
animal.makeSound(); // Bark

animal = new Cat();
animal.makeSound(); // Meow
```

Conceptually: 1. The compiler checks that `makeSound()` is available on
`Animal`. 2. At runtime, Java uses the actual object's class. 3. The
most specific applicable overridden implementation is invoked.

Dynamic dispatch applies to overridden instance methods, not fields,
static methods, or constructors.

## 6. Polymorphism with Abstract Classes

An abstract class can provide shared state and implementation while
requiring subclasses to implement specific behavior.

``` java
abstract class Report {
    private final String title;

    protected Report(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void generate();
}

class PdfReport extends Report {
    public PdfReport(String title) {
        super(title);
    }

    @Override
    public void generate() {
        System.out.println("Generating PDF: " + getTitle());
    }
}

class CsvReport extends Report {
    public CsvReport(String title) {
        super(title);
    }

    @Override
    public void generate() {
        System.out.println("Generating CSV: " + getTitle());
    }
}
```

``` java
List<Report> reports = List.of(
    new PdfReport("Monthly Sales"),
    new CsvReport("Monthly Customers")
);

for (Report report : reports) {
    report.generate();
}
```

Use an abstract class when related types share meaningful state or
implementation and need a common base type. Abstract classes can have
constructors, fields, concrete methods, and abstract methods. A class
can extend only one class.

## 7. Polymorphism with Interfaces

Interfaces define contracts or capabilities. Unrelated classes can
implement the same interface.

``` java
interface Exportable {
    void export(String destination);
}

class PdfExporter implements Exportable {
    @Override
    public void export(String destination) {
        System.out.println("Exporting PDF to " + destination);
    }
}

class JsonExporter implements Exportable {
    @Override
    public void export(String destination) {
        System.out.println("Exporting JSON to " + destination);
    }
}

void exportFile(Exportable exporter, String destination) {
    exporter.export(destination);
}
```

### Abstract class vs interface

  -----------------------------------------------------------------------
  Abstract class                      Interface
  ----------------------------------- -----------------------------------
  A class extends only one class      A class can implement multiple
                                      interfaces

  Can hold instance state             Fields are implicitly
                                      `public static final`

  Can define constructors             Cannot define constructors

  Can contain abstract and concrete   Can define abstract, default,
  methods                             static, and private methods

  Useful for shared base state and    Useful for contracts and
  implementation                      capabilities
  -----------------------------------------------------------------------

Choose the abstraction that best expresses the design.

## 8. Upcasting and Downcasting

### Upcasting

``` java
Dog dog = new Dog();
Animal animal = dog;
```

A `Dog` is an `Animal`, so upcasting is implicit.

### Downcasting

``` java
Animal animal = new Dog();
Dog dog = (Dog) animal;
```

A downcast is valid only if the actual object is compatible with the
target type.

``` java
Animal animal = new Cat();
// Dog dog = (Dog) animal; // ClassCastException
```

Use `instanceof` when the runtime type is not already guaranteed.
Frequent type checks and downcasts can indicate a missing abstraction.

## 9. Covariant Return Types

An overriding method may return a subtype of the original reference
return type.

``` java
class Animal {}
class Dog extends Animal {}

class AnimalFactory {
    public Animal create() {
        return new Animal();
    }
}

class DogFactory extends AnimalFactory {
    @Override
    public Dog create() {
        return new Dog();
    }
}
```

`Dog` is a subtype of `Animal`, so the return type is covariant. This
applies to reference types, not primitive return types.

## 10. Overriding Rules and Restrictions

1.  Method name and parameter types must match.
2.  Return type must be identical or covariant.
3.  Access cannot become more restrictive.
4.  A checked exception cannot be broadened beyond what the parent
    method permits.
5.  `final` methods cannot be overridden.
6.  `private` methods are not overridden because they are not inherited
    as accessible methods.
7.  Static methods are hidden, not overridden.
8.  Constructors cannot be overridden or inherited.

``` java
class Parent {
    protected Number calculate() {
        return 10;
    }
}

class Child extends Parent {
    @Override
    public Integer calculate() {
        return 20;
    }
}
```

This is valid: `Integer` extends `Number`, and `public` is less
restrictive than `protected`.

## 11. Field Hiding vs Method Overriding

``` java
class Parent {
    String value = "Parent";

    public void show() {
        System.out.println("Parent method");
    }
}

class Child extends Parent {
    String value = "Child";

    @Override
    public void show() {
        System.out.println("Child method");
    }
}

Parent ref = new Child();
System.out.println(ref.value); // Parent
ref.show();                    // Child method
```

Fields are selected using the compile-time reference type. Overridden
instance methods use dynamic dispatch. Avoid field hiding because it
makes code harder to reason about.

## 12. Static Method Hiding

Static methods belong to a class, not an individual object.

``` java
class Parent {
    static void show() {
        System.out.println("Parent static");
    }
}

class Child extends Parent {
    static void show() {
        System.out.println("Child static");
    }
}

Parent ref = new Child();
ref.show(); // Parent static
```

This is hiding, not overriding. Prefer `Parent.show()` or `Child.show()`
so the selection is clear.

## 13. Polymorphism with Collections and Generics

``` java
interface Shape {
    double area();
}

class Circle implements Shape {
    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle implements Shape {
    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }
}

List<Shape> shapes = List.of(
    new Circle(2),
    new Rectangle(3, 4)
);

double totalArea = 0;
for (Shape shape : shapes) {
    totalArea += shape.area();
}
```

Different concrete objects are stored through one common interface.

### Generics are not runtime polymorphism

Generics provide compile-time type safety. Generic type parameters are
generally erased at runtime. Overridden method dispatch is a separate
mechanism.

### Invariance

Even though `Dog extends Animal`, `List<Dog>` is not a subtype of
`List<Animal>`.

``` java
List<Dog> dogs = new ArrayList<>();
// List<Animal> animals = dogs; // Compile-time error
```

Otherwise, code could insert a `Cat` into a list intended to contain
only dogs.

### Producer: `? extends T`

``` java
double totalArea(List<? extends Shape> shapes) {
    double total = 0;
    for (Shape shape : shapes) {
        total += shape.area();
    }
    return total;
}
```

This accepts lists of `Circle`, `Rectangle`, or another `Shape` subtype.
It is safe to read elements as `Shape`, but you generally cannot add a
specific shape to this list.

### Consumer: `? super T`

``` java
void addDogs(List<? super Dog> destination) {
    destination.add(new Dog());
}
```

This accepts a list of `Dog`, `Animal`, or `Object`.

**PECS:** Producer Extends; Consumer Super. Use `extends` when reading
values as a type and `super` when adding values of that type.

## 14. Functional Interfaces and Polymorphic Behavior

Functional interfaces allow behavior to be passed as a value.

``` java
@FunctionalInterface
interface DiscountPolicy {
    BigDecimal apply(BigDecimal price);
}

DiscountPolicy noDiscount = price -> price;

DiscountPolicy tenPercent = price ->
    price.multiply(new BigDecimal("0.90"));

DiscountPolicy fixedDiscount = price ->
    price.subtract(new BigDecimal("100.00")).max(BigDecimal.ZERO);
```

``` java
class PricingService {
    public BigDecimal calculate(
            BigDecimal price,
            DiscountPolicy policy) {
        return policy.apply(price);
    }
}
```

Common functional interfaces: - `Predicate<T>` --- tests and returns
`boolean`. - `Function<T, R>` --- transforms a value. - `Consumer<T>`
--- accepts a value and returns nothing. - `Supplier<T>` --- supplies a
value. - `UnaryOperator<T>` --- transforms a value to the same type. -
`BinaryOperator<T>` --- combines two values of the same type.

This is behavior parameterization and complements interface-based
polymorphism.

## 15. Polymorphism, Dependency Inversion, and Spring Boot

The Dependency Inversion Principle says high-level business logic should
depend on abstractions rather than concrete low-level implementations.

Tightly coupled:

``` java
class OrderService {
    private final StripeGateway gateway = new StripeGateway();

    public void pay(BigDecimal amount) {
        gateway.charge(amount);
    }
}
```

Abstraction-based:

``` java
interface PaymentGateway {
    void charge(BigDecimal amount);
}

class StripeGateway implements PaymentGateway {
    @Override
    public void charge(BigDecimal amount) {
        System.out.println("Stripe charge: " + amount);
    }
}

class OrderService {
    private final PaymentGateway gateway;

    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void pay(BigDecimal amount) {
        gateway.charge(amount);
    }
}
```

The implementation can be replaced without changing `OrderService`.

### Spring Boot example

``` java
public interface NotificationService {
    void send(String message);
}

@Service
class EmailNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

@Service
class AccountService {
    private final NotificationService notificationService;

    public AccountService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void notifyAccountCreated() {
        notificationService.send("Account created");
    }
}
```

Spring's dependency injection container supplies a matching bean. If
multiple beans implement the same interface, use configuration such as
`@Qualifier` or `@Primary`.

Dependency injection and polymorphism are different concepts:
polymorphism permits multiple implementations through a common type;
dependency injection supplies an implementation to a dependent object.

## 16. Liskov Substitution Principle (LSP)

A subtype should be usable wherever its base type is expected without
breaking the correctness promised by the base type.

Consider a mutable rectangle:

``` java
class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int area() {
        return width * height;
    }
}
```

If `Square extends Rectangle` and overrides setters to keep dimensions
equal, code expecting to set width and height independently may no
longer behave correctly.

The issue is behavioral: the subtype violates assumptions made by
clients. Better options include separate immutable `Rectangle` and
`Square` implementations under a common `Shape` abstraction, or a base
contract that exposes only genuinely shared operations.

LSP concerns behavior and contracts, not merely class diagrams.

## 17. Composition vs Inheritance

Inheritance expresses **IS-A**:

``` java
class SavingsAccount extends BankAccount {
}
```

Composition expresses **HAS-A**:

``` java
class OrderService {
    private final PaymentGateway gateway;

    OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

Prefer composition when behavior should be replaceable or shared by
otherwise unrelated classes. Prefer inheritance when a genuine, stable
subtype relationship exists and the child can honor the parent's
contract.

## 18. Constructor Calls and Overridable Methods

Avoid calling overridable methods from constructors.

``` java
class Parent {
    Parent() {
        show();
    }

    void show() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    private String name = "Child";

    @Override
    void show() {
        System.out.println(name.length());
    }
}
```

During `new Child()`, the parent constructor calls `show()`. Dynamic
dispatch selects `Child.show()`, but the child's field initializer has
not run yet. `name` is still `null`, so `length()` throws
`NullPointerException`.

Constructors should establish state without invoking methods that
subclasses may override.

## 19. Object Methods and Polymorphism

Every class inherits methods from `Object`, commonly including: -
`toString()` - `equals(Object obj)` - `hashCode()`

``` java
class User {
    private final String username;

    User(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " | Username: " + username;
    }
}
```

`System.out.println(user)` uses `String.valueOf(user)`, which calls
`toString()` for a non-null reference.

When overriding `equals()`, also override `hashCode()` so equal objects
have equal hash codes. The `equals()` contract requires reflexivity,
symmetry, transitivity, consistency, and that a non-null object is not
equal to `null`.

Inheritance can make equality difficult because parent and child classes
may disagree about which fields define identity. Consider final classes,
records, or carefully designed equality policies for value objects.

## 20. Sealed Classes

Since Java 17, sealed classes and interfaces can restrict permitted
direct subtypes.

``` java
sealed interface Result permits Success, Failure {
}

record Success(String value) implements Result {
}

record Failure(String message) implements Result {
}
```

Permitted direct subclasses must be declared `final`, `sealed`, or
`non-sealed`.

-   `final`: cannot be extended.
-   `sealed`: restricts its own direct subtypes.
-   `non-sealed`: allows further unrestricted extension.

Sealed hierarchies are useful when a domain has a known, limited set of
variants.

## 21. Pattern Matching

Modern Java supports pattern matching with `instanceof`:

``` java
if (value instanceof String text) {
    System.out.println(text.length());
}
```

This avoids a separate cast after the type check. Newer Java versions
also support pattern matching in `switch`; exact syntax and availability
depend on the Java version used by the project.

Do not replace ordinary polymorphic method calls with type checks when
the behavior naturally belongs in the abstraction.

## 22. Common Misconceptions

1.  Polymorphism means only overriding. Overloading is commonly taught
    as compile-time polymorphism.
2.  Reference type decides which overridden method runs. It controls
    compile-time access; runtime type selects the override.
3.  Fields are dynamically dispatched. They are resolved by reference
    type.
4.  Static methods are overridden. They are hidden.
5.  Constructors are polymorphic. They are not inherited or overridden.
6.  A parent reference can call every child method. It cannot.
7.  Downcasting is always safe. It is safe only when the actual object
    is compatible.
8.  Generics and runtime polymorphism are identical. They solve
    different problems.
9.  More subclasses always mean better design. Composition may be safer.
10. Dependency injection is polymorphism. They are distinct concepts
    that often work together.

## 23. Practical Project: Extensible Payment Processing

Build a payment-processing design using interfaces and runtime
polymorphism.

### Requirements

Create:

``` java
public interface PaymentGateway {
    PaymentResult pay(BigDecimal amount);
}

public record PaymentResult(
        boolean successful,
        String transactionId,
        String message
) {
}
```

Implement: - `CardPaymentGateway` - `MobileWalletPaymentGateway` -
`BankTransferGateway`

Create a service depending only on the interface:

``` java
public class CheckoutService {
    private final PaymentGateway paymentGateway;

    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentResult checkout(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        return paymentGateway.pay(amount);
    }
}
```

### Design goals

-   `CheckoutService` must not depend on concrete gateway classes.
-   Each gateway implements its own behavior.
-   Use constructor injection.
-   Validate inputs.
-   Do not store sensitive payment credentials in plain text.
-   Write unit tests using a fake `PaymentGateway`.
-   Add a new gateway without changing the checkout service.

Advanced extension: add a gateway factory or strategy registry. Keep
gateway selection separate from checkout business logic.

## 24. Advanced Output-Prediction Questions

Predict each output before running the code. Explain the selection
rules.

### Problem 1: Overloading and overriding together

``` java
class Parent {
    void print(Object value) {
        System.out.println("Parent Object");
    }

    void print(String value) {
        System.out.println("Parent String");
    }
}

class Child extends Parent {
    @Override
    void print(Object value) {
        System.out.println("Child Object");
    }
}

Parent ref = new Child();
ref.print("Java");
```

Questions: - Which overload is selected at compile time? - Which
implementation runs at runtime?

### Problem 2: Field vs method

``` java
class A {
    String value = "A";
    void show() { System.out.println("A show"); }
}

class B extends A {
    String value = "B";

    @Override
    void show() { System.out.println("B show"); }
}

A ref = new B();
System.out.println(ref.value);
ref.show();
```

Predict both lines and explain the different selection rules.

### Problem 3: Static hiding

``` java
class A {
    static void test() { System.out.println("A"); }
}

class B extends A {
    static void test() { System.out.println("B"); }
}

A ref = new B();
ref.test();
```

Why does the runtime object not determine the result?

### Problem 4: Widening and boxing

``` java
class Demo {
    void show(long value) { System.out.println("long"); }
    void show(Integer value) { System.out.println("Integer"); }
}

new Demo().show(5);
```

Which overload is selected and why?

### Problem 5: Covariant return

``` java
class Animal {}
class Dog extends Animal {}

class Parent {
    Animal create() { return new Animal(); }
}

class Child extends Parent {
    @Override
    Dog create() { return new Dog(); }
}
```

Is this valid? Explain.

### Problem 6: Runtime type and accessible methods

``` java
class Animal {
    void eat() { System.out.println("eat"); }
}

class Dog extends Animal {
    void bark() { System.out.println("bark"); }
}

Animal animal = new Dog();
animal.eat();
// animal.bark();
```

Why does the second call fail at compile time?

### Problem 7: Constructor dispatch

Explain why calling an overridable method from a superclass constructor
can observe uninitialized subclass fields. Write a minimal example.

### Problem 8: Generic invariance

Why is `List<Dog>` not assignable to `List<Animal>`? Show how
`? extends Animal` and `? super Dog` solve different problems.

### Problem 9: LSP

Give an example of a subtype that violates its parent's behavioral
contract. Refactor it.

### Problem 10: Polymorphic service design

Design a notification service for email, SMS, and push notifications.
Explain the abstraction, implementations, constructor injection, test
fake, and how a new implementation can be added without modifying client
logic.

## 25. Suggested Study Plan

### Stage 1 --- Core mechanics

-   Overloading and overriding.
-   Reference type vs runtime type.
-   Dynamic dispatch.
-   Upcasting and downcasting.

Practice: Problems 1--6.

### Stage 2 --- Type design

-   Interfaces and abstract classes.
-   Covariant returns.
-   Sealed classes.
-   Field and static method hiding.

Practice: Build a notification or report-generation hierarchy.

### Stage 3 --- Advanced Java

-   Generics and wildcard bounds.
-   Functional interfaces.
-   Lambdas and method references.
-   `equals()` and `hashCode()` with inheritance.
-   Pattern matching.

Practice: Build a generic processing pipeline.

### Stage 4 --- Software design

-   SOLID principles.
-   Liskov Substitution Principle.
-   Dependency inversion.
-   Composition vs inheritance.
-   Test doubles and dependency injection.

Practice: Complete the payment-processing project and write unit tests.

## 26. Final Mental Model

-   **Overloading:** compiler selects among methods with different
    parameter lists.
-   **Overriding:** a subtype supplies a compatible implementation of an
    inherited instance method.
-   **Reference type:** controls compile-time member access.
-   **Runtime object type:** controls which overridden instance method
    executes.
-   **Fields:** are hidden, not dynamically overridden.
-   **Static methods:** are hidden, not dynamically dispatched.
-   **Interfaces:** define contracts and enable multiple inheritance of
    type.
-   **Generics:** provide compile-time type safety and variance through
    wildcards.
-   **Good polymorphic design:** depends on stable abstractions and
    substitutable implementations.

The goal is not to create as many subclasses as possible. The goal is to
write client code that depends on meaningful abstractions and works
correctly with multiple implementations.
