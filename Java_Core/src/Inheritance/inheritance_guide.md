# Java Inheritance

## From Fundamentals to Advanced Concepts

> Language: Java\
> Level: Beginner to Advanced\
> Goal: Understand not only *how* inheritance works, but also *why* it
> exists, how Java executes inherited code, and when inheritance is the
> right design choice.

------------------------------------------------------------------------

## 1. What is Inheritance?

**Inheritance** is an OOP mechanism through which one class (the
**subclass/child class**) acquires accessible fields and methods from
another class (the **superclass/parent class**).

It allows us to model an **is-a** relationship and reuse or specialize
behavior.

### A simple real-world example

-   A `Vehicle` has a brand and can start.
-   A `Car` is a `Vehicle`.
-   A `Bike` is also a `Vehicle`.

Instead of implementing the common `brand` field and `start()` method
separately in both `Car` and `Bike`, we can place the shared behavior in
`Vehicle`.

``` java
class Vehicle {
    protected String brand;

    public Vehicle(String brand) {
        this.brand = brand;
    }

    public void start() {
        System.out.println(brand + " is starting");
    }
}

class Car extends Vehicle {
    public Car(String brand) {
        super(brand);
    }

    public void openTrunk() {
        System.out.println("Trunk opened");
    }
}

class Bike extends Vehicle {
    public Bike(String brand) {
        super(brand);
    }

    public void ringBell() {
        System.out.println("Bell ringing");
    }
}
```

Usage:

``` java
Car car = new Car("Toyota");
car.start();       // Inherited method
car.openTrunk();   // Car-specific method

Bike bike = new Bike("Yamaha");
bike.start();      // Inherited method
bike.ringBell();   // Bike-specific method
```

`Car` and `Bike` inherit accessible behavior from `Vehicle`, while each
class can add its own behavior.

### The key relationship: IS-A

Ask this question before using inheritance:

> Is every object of the child type genuinely a kind of the parent type?

Examples:

-   `Car` is a `Vehicle` --- yes.
-   `Dog` is an `Animal` --- yes.
-   `SavingsAccount` is a `BankAccount` --- yes.
-   `Engine` is a `Car` --- no. A car *has an* engine.

The last example is a **has-a** relationship and is usually modeled with
composition, not inheritance.

------------------------------------------------------------------------

## 2. Basic Syntax

``` java
class Parent {
    public void showParent() {
        System.out.println("Parent method");
    }
}

class Child extends Parent {
    public void showChild() {
        System.out.println("Child method");
    }
}
```

-   `extends` establishes class inheritance.
-   `Parent` is the superclass.
-   `Child` is the subclass.
-   A `Child` object can use accessible inherited members and its own
    members.

``` java
Child child = new Child();

child.showParent();
child.showChild();
```

### Important: private members are not directly accessible

A subclass does not get direct access to a superclass's `private` fields
or methods.

``` java
class Parent {
    private String name = "Alex";

    public String getName() {
        return name;
    }
}

class Child extends Parent {
    public void printName() {
        // System.out.println(name); // Compilation error
        System.out.println(getName()); // Correct
    }
}
```

The private field is part of the parent object state, but the subclass
cannot access it directly. It can use a public or protected method
provided by the parent.

------------------------------------------------------------------------

## 3. Types of Inheritance in Java

### 3.1 Single inheritance

One child extends one parent.

``` java
class Animal {
    public void eat() {
        System.out.println("Eating");
    }
}

class Dog extends Animal {
    public void bark() {
        System.out.println("Barking");
    }
}
```

### 3.2 Multilevel inheritance

A class extends a class that itself extends another class.

``` java
class Grandparent {
    public void familyRule() {
        System.out.println("Grandparent rule");
    }
}

class Parent extends Grandparent {
    public void guide() {
        System.out.println("Parent guidance");
    }
}

class Child extends Parent {
    public void learn() {
        System.out.println("Child learning");
    }
}
```

A `Child` object can use accessible methods from both `Parent` and
`Grandparent`.

### 3.3 Hierarchical inheritance

Multiple child classes extend the same parent.

``` java
class Employee {
    public void work() {
        System.out.println("Working");
    }
}

class Developer extends Employee {
    public void writeCode() {
        System.out.println("Writing code");
    }
}

class Designer extends Employee {
    public void designUI() {
        System.out.println("Designing UI");
    }
}
```

### 3.4 Multiple inheritance

Java does **not** allow a class to extend multiple classes:

``` java
// Not valid Java:
// class C extends A, B { }
```

The main concern is ambiguity when parent classes provide conflicting
implementations or state. Java supports multiple inheritance of type
through interfaces, and a class can implement multiple interfaces:

``` java
interface Printable {
    void print();
}

interface Scannable {
    void scan();
}

class MultiFunctionPrinter implements Printable, Scannable {
    @Override
    public void print() {
        System.out.println("Printing");
    }

    @Override
    public void scan() {
        System.out.println("Scanning");
    }
}
```

A class can extend one class and implement multiple interfaces:

``` java
class SmartPrinter extends Device implements Printable, Scannable {
    // Implement required methods
}
```

------------------------------------------------------------------------

## 4. Understanding `super`

The keyword `super` refers to the superclass part of the current object.
It is commonly used in three ways.

### 4.1 Calling a superclass constructor

``` java
class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
        System.out.println("Person constructor");
    }
}

class Student extends Person {
    private final int studentId;

    public Student(String name, int studentId) {
        super(name);
        this.studentId = studentId;
        System.out.println("Student constructor");
    }
}
```

`super(name)` invokes the matching constructor in `Person`.

**Constructor rule:** A superclass constructor must complete before the
subclass constructor body can initialize the subclass portion of the
object.

In Java, an explicit `super(...)` or `this(...)` constructor invocation
must be the first statement in a constructor. If neither is written, the
compiler inserts `super()` only when an accessible no-argument
superclass constructor exists. Modern Java also permits certain
restricted constructor prologue statements before an explicit
constructor invocation; the invocation still occurs before the
superclass constructor is run.

### 4.2 Calling a superclass method

``` java
class Parent {
    public void show() {
        System.out.println("Parent show()");
    }
}

class Child extends Parent {
    @Override
    public void show() {
        super.show();
        System.out.println("Child show()");
    }
}
```

Output:

``` text
Parent show()
Child show()
```

Use `super.methodName()` when the child wants to extend the parent's
implementation rather than completely replace it.

### 4.3 Accessing a superclass field

``` java
class Parent {
    protected String label = "Parent";
}

class Child extends Parent {
    protected String label = "Child";

    public void printLabels() {
        System.out.println(label);       // Child
        System.out.println(super.label); // Parent
    }
}
```

This is **field hiding**, not field overriding. Fields are selected
using the reference's compile-time type; instance methods use dynamic
dispatch.

------------------------------------------------------------------------

## 5. Constructor Chaining and Initialization Order

Consider this hierarchy:

``` java
class GrandParent {
    public GrandParent() {
        System.out.println("GrandParent constructor");
    }
}

class Parent extends GrandParent {
    public Parent() {
        System.out.println("Parent constructor");
    }
}

class Child extends Parent {
    public Child() {
        System.out.println("Child constructor");
    }
}
```

``` java
new Child();
```

Output:

``` text
GrandParent constructor
Parent constructor
Child constructor
```

Why?

1.  `Child` construction begins.
2.  `Child` invokes `Parent`'s constructor through `super()` (implicitly
    here).
3.  `Parent` invokes `GrandParent`'s constructor.
4.  `GrandParent` finishes first.
5.  Control returns to `Parent`, which finishes.
6.  Control returns to `Child`, which finishes.

### More complete initialization order

For a newly created object, the important conceptual order is:

1.  Memory for the object is allocated; instance fields initially have
    default values (`0`, `false`, `null`, etc.).
2.  The superclass constructor chain runs from the highest superclass
    downward.
3.  For each class, its instance field initializers and instance
    initializer blocks run before that class's constructor body.
4.  The subclass's initialization then runs, followed by its constructor
    body.

Static initialization is separate: static fields and static initializer
blocks run when a class is initialized, generally before its first
active use. Parent classes are initialized before child classes.

### A common bug: parameters are not automatically assigned to fields

Incorrect:

``` java
class GrandParent {
    private String name;
    private int age;

    public GrandParent(String name, int age) {
        System.out.println("GrandParent constructor called");
    }
}
```

The constructor parameters are local variables. They do not
automatically update the instance fields. Therefore, `name` remains
`null` and `age` remains `0`.

Correct:

``` java
class GrandParent {
    private String name;
    private int age;

    public GrandParent(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

`this.name` means the instance field; `name` on the right means the
constructor parameter.

### Example: GrandParent → Parent → Child

``` java
class GrandParent {
    private final String name;
    private final int age;

    public GrandParent(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("GrandParent constructor called");
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void methodCalled() {
        System.out.println("GrandParent method");
    }
}

class Parent extends GrandParent {
    public Parent(String name, int age) {
        super(name, age);
        System.out.println("Parent constructor called");
    }

    @Override
    public void methodCalled() {
        System.out.println("Parent method");
    }
}

class Child extends Parent {
    public Child(String name, int age) {
        super(name, age);
        System.out.println("Child constructor called");
    }

    @Override
    public void methodCalled() {
        System.out.println("Child method");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " | Name: " + getName()
                + " | Age: " + getAge();
    }
}

class Test {
    public static void main(String[] args) {
        Child child = new Child("Akhlakul Islam", 24);
        child.methodCalled();
        System.out.println(child);
    }
}
```

Output:

``` text
GrandParent constructor called
Parent constructor called
Child constructor called
Child method
Child | Name: Akhlakul Islam | Age: 24
```

`getClass().getSimpleName()` returns the runtime class's simple name. In
this example it returns `Child`. By contrast,
`Child.class.getSimpleName()` is fixed to the class literal `Child`.

------------------------------------------------------------------------

## 6. Method Overriding

**Method overriding** happens when a subclass provides a compatible
implementation of an inherited instance method.

``` java
class Animal {
    public void makeSound() {
        System.out.println("Some animal sound");
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Bark");
    }
}
```

``` java
Animal animal = new Dog();
animal.makeSound();
```

Output:

``` text
Bark
```

### Why use `@Override`?

`@Override` tells the compiler that you intend to override an inherited
method. It catches mistakes such as misspelling the method name or using
an incompatible signature.

### Overriding rules

-   The method name and parameter types must match the inherited method
    signature.
-   The return type must be the same or covariant (a subtype of the
    original return type for reference types).
-   The access level cannot be more restrictive.
-   A checked exception declaration cannot be broader than the
    overridden method's declaration.
-   `final` methods cannot be overridden.
-   `private` methods are not overridden because they are not inherited
    as accessible methods.
-   Static methods are hidden, not overridden.
-   Constructors cannot be overridden.

### Overloading vs overriding

  -----------------------------------------------------------------------
  Overloading                         Overriding
  ----------------------------------- -----------------------------------
  Usually same class, can also occur  Requires inheritance
  across inheritance                  

  Same method name, different         Same signature, compatible return
  parameter list                      type

  Resolved at compile time            Instance method dispatch resolved
                                      at runtime

  Example: `sum(int, int)` and        Child redefines parent's
  `sum(double, double)`               `makeSound()`
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 7. Runtime Polymorphism and Dynamic Method Dispatch

A superclass reference can refer to a subclass object:

``` java
Animal animal = new Dog();
```

This is an **upcast**. It is safe and often implicit.

``` java
animal.makeSound(); // Dog's implementation
```

The compiler checks whether `makeSound()` is available on the reference
type `Animal`. At runtime, Java selects the overridden implementation
belonging to the actual object (`Dog`).

### Reference type vs object type

``` java
Animal animal = new Dog();
```

-   Reference/declared type: `Animal`
-   Runtime/actual object type: `Dog`

The reference type controls which members can be accessed at compile
time. The runtime object type controls which overridden instance method
implementation runs.

``` java
class Animal {
    public void eat() {}
}

class Dog extends Animal {
    @Override
    public void eat() {}

    public void bark() {}
}

Animal animal = new Dog();
animal.eat();  // Allowed
// animal.bark(); // Compile-time error
```

To access a Dog-specific member, a checked downcast may be used:

``` java
if (animal instanceof Dog dog) {
    dog.bark();
}
```

Avoid unnecessary downcasting. Prefer designing methods on the shared
abstraction when the behavior genuinely belongs there.

### Polymorphism with an array

``` java
Animal[] animals = {
    new Dog(),
    new Cat(),
    new Dog()
};

for (Animal animal : animals) {
    animal.makeSound();
}
```

Each object responds according to its runtime type.

------------------------------------------------------------------------

## 8. Upcasting and Downcasting

### Upcasting

Converting a subclass reference to a superclass reference:

``` java
Dog dog = new Dog();
Animal animal = dog;
```

-   Usually implicit.
-   Safe because every `Dog` is an `Animal`.
-   The superclass reference exposes only members declared in the
    superclass (and its ancestors), subject to access rules.

### Downcasting

Converting a superclass reference to a subclass reference:

``` java
Animal animal = new Dog();
Dog dog = (Dog) animal;
dog.bark();
```

A downcast is not automatically safe. If the object is not actually a
`Dog`, a `ClassCastException` can occur.

``` java
Animal animal = new Cat();
// Dog dog = (Dog) animal; // ClassCastException at runtime
```

Use `instanceof` before a downcast when the runtime type is not already
guaranteed.

------------------------------------------------------------------------

## 9. What Is Inherited? Access Modifiers

  -----------------------------------------------------------------------------
  Modifier              Same class   Same package    Subclass in      Unrelated
                                                         another       class in
                                                         package        another
                                                                        package
  ----------------- -------------- -------------- -------------- --------------
  `private`                    Yes             No             No             No

  package-private              Yes            Yes             No             No
  (no modifier)                                                  

  `protected`                  Yes            Yes      Yes, with             No
                                                        subclass 
                                                    access rules 

  `public`                     Yes            Yes            Yes            Yes
  -----------------------------------------------------------------------------

Important details:

-   `private` members are not directly accessible from a subclass.
-   Package-private members are accessible only within the same package.
-   `protected` is not simply "public to every subclass everywhere."
    Across packages, access is subject to the protected-subclass
    reference rules.
-   Public members are accessible wherever the declaring type itself is
    accessible.

Prefer `private` fields and controlled methods over exposing mutable
state.

------------------------------------------------------------------------

## 10. Fields, Static Methods, and Instance Methods

Do not assume all inherited members behave the same way.

### Instance methods: overridden and dynamically dispatched

``` java
class Parent {
    public void show() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    @Override
    public void show() {
        System.out.println("Child");
    }
}

Parent ref = new Child();
ref.show(); // Child
```

### Fields: hidden, selected by reference type

``` java
class Parent {
    String value = "Parent";
}

class Child extends Parent {
    String value = "Child";
}

Parent ref = new Child();
System.out.println(ref.value); // Parent
```

Fields are not polymorphic in the same way as overridden instance
methods.

### Static methods: hidden, selected by reference/class type

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

Call static methods using the class name (`Parent.show()` or
`Child.show()`) to make the selection clear.

------------------------------------------------------------------------

## 11. The `final` Keyword and Inheritance

### Final class

A final class cannot be extended.

``` java
final class SecurityConfig {
}

// class CustomConfig extends SecurityConfig { } // Compilation error
```

### Final method

A final method cannot be overridden.

``` java
class Parent {
    public final void validate() {
        System.out.println("Common validation");
    }
}
```

### Final field

A final field can be assigned once (subject to Java's
definite-assignment rules).

``` java
class User {
    private final String username;

    public User(String username) {
        this.username = username;
    }
}
```

`final` is useful when a class or behavior must not be specialized
through inheritance.

------------------------------------------------------------------------

## 12. Abstract Classes and Inheritance

An abstract class cannot be instantiated directly. It can define shared
state and implemented methods while requiring subclasses to provide
certain behavior.

``` java
abstract class Shape {
    public abstract double area();

    public void printDescription() {
        System.out.println("I am a shape");
    }
}

class Circle extends Shape {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }
}
```

``` java
Shape shape = new Circle(5);
System.out.println(shape.area());
```

Use an abstract class when related classes share meaningful state or
implementation and also need a common base type.

An abstract class can have constructors, fields, concrete methods, and
abstract methods.

------------------------------------------------------------------------

## 13. Interfaces and Multiple Inheritance of Type

An interface defines a contract that classes can implement. A class can
implement multiple interfaces.

``` java
interface Flyable {
    void fly();
}

interface Swimmable {
    void swim();
}

class Duck implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("Duck is flying");
    }

    @Override
    public void swim() {
        System.out.println("Duck is swimming");
    }
}
```

Interfaces can also contain `default`, `static`, and (since Java 9)
`private` methods, subject to their rules.

### Default method conflict

If two unrelated interfaces provide the same default method signature,
the implementing class must resolve the conflict explicitly.

``` java
interface A {
    default void show() {
        System.out.println("A");
    }
}

interface B {
    default void show() {
        System.out.println("B");
    }
}

class C implements A, B {
    @Override
    public void show() {
        A.super.show();
        B.super.show();
        System.out.println("C");
    }
}
```

This is one reason interface multiple inheritance is more controlled
than multiple class inheritance.

------------------------------------------------------------------------

## 14. Inheritance vs Composition

Inheritance is not always the best way to reuse code.

### Inheritance: IS-A

``` java
class Dog extends Animal {
}
```

A dog is an animal.

### Composition: HAS-A

``` java
class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

class Car {
    private final Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public void start() {
        engine.start();
    }
}
```

A car has an engine. It is not an engine.

### When should you choose which?

Choose inheritance when:

-   There is a genuine substitutable IS-A relationship.
-   The subclass can honor the promises/behavior of the superclass.
-   The shared abstraction is stable and meaningful.
-   Subclasses need to specialize behavior through overriding.

Choose composition when:

-   You want to reuse a component without claiming an IS-A relationship.
-   Behavior should be replaceable at runtime.
-   You want to avoid tight coupling to a superclass.
-   The parent class has many implementation details that children
    should not depend on.

**Design principle:** Prefer composition over inheritance when both
would solve the problem equally well. This is a guideline, not an
absolute rule.

------------------------------------------------------------------------

## 15. The Liskov Substitution Principle (LSP)

LSP is one of the SOLID principles.

> Objects of a subtype should be usable wherever objects of the base
> type are expected, without breaking the correctness expected by the
> client.

Consider:

``` java
class Bird {
    public void eat() {
        System.out.println("Eating");
    }

    public void fly() {
        System.out.println("Flying");
    }
}

class Sparrow extends Bird {
}

class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins cannot fly");
    }
}
```

This design is problematic if code expects every `Bird` to be able to
fly:

``` java
void makeBirdFly(Bird bird) {
    bird.fly();
}
```

Passing a `Penguin` breaks that expectation.

A better design separates capabilities:

``` java
abstract class Bird {
    public void eat() {
        System.out.println("Eating");
    }
}

interface Flyable {
    void fly();
}

class Sparrow extends Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("Sparrow flying");
    }
}

class Penguin extends Bird {
    public void swim() {
        System.out.println("Penguin swimming");
    }
}
```

Now the type system expresses which birds can fly.

LSP is not merely about avoiding exceptions. A subtype should preserve
the base type's behavioral contract, including valid inputs, guarantees,
and invariants.

------------------------------------------------------------------------

## 16. Constructor and Method Dispatch: An Advanced Warning

Avoid calling overridable methods from constructors.

``` java
class Parent {
    public Parent() {
        show();
    }

    public void show() {
        System.out.println("Parent show");
    }
}

class Child extends Parent {
    private String name = "Child";

    @Override
    public void show() {
        System.out.println(name.length());
    }
}
```

When `new Child()` is created, the `Parent` constructor calls `show()`.
Dynamic dispatch selects `Child.show()`, even though the child's field
initializer has not run yet. At that moment, `name` still has its
default value `null`, so this code can throw `NullPointerException`.

**Rule:** Constructors should establish the object's own state. Avoid
invoking overridable methods from constructors because subclass state
may not be initialized.

------------------------------------------------------------------------

## 17. Common Mistakes and Misconceptions

1.  **Thinking constructor parameters automatically initialize
    fields.**\
    They do not. Use `this.field = parameter`.

2.  **Thinking constructors are inherited.**\
    They are not. A subclass constructor explicitly or implicitly
    invokes a superclass constructor.

3.  **Thinking `private` fields disappear in child objects.**\
    They remain part of the superclass portion of the object, but cannot
    be accessed directly by the subclass.

4.  **Thinking fields are overridden.**\
    Fields are hidden. Overridden instance methods use dynamic dispatch.

5.  **Thinking static methods are overridden.**\
    Static methods are hidden, not dynamically dispatched.

6.  **Thinking a superclass reference changes the object's runtime
    type.**\
    `Animal a = new Dog()` still refers to a `Dog` object.

7.  **Thinking every code-reuse relationship should use inheritance.**\
    Use composition for HAS-A relationships.

8.  **Thinking `super()` can be placed anywhere in a constructor.**\
    Explicit constructor invocation has strict placement rules.

9.  **Thinking a subclass can reduce method visibility when
    overriding.**\
    It cannot make the inherited method less accessible.

10. **Thinking `toString()` is automatically called only when written
    explicitly.**\
    `System.out.println(object)` calls `String.valueOf(object)`, which
    calls `toString()` for a non-null object.

------------------------------------------------------------------------

## 18. Practical Exercise: Build a Banking Inheritance Model

Create these classes:

### `BankAccount`

Fields:

-   `accountNumber`
-   `ownerName`
-   `balance`

Methods:

-   `deposit(amount)`
-   `withdraw(amount)`
-   `getBalance()`
-   `toString()`

Requirements:

-   Keep fields private.
-   Validate that deposit and withdrawal amounts are positive.
-   Do not allow a withdrawal that violates the account's balance rules.
-   Use `BigDecimal` for money rather than `double`.
-   Make the account number immutable after construction.

### `SavingsAccount extends BankAccount`

Add:

-   `interestRate`
-   A method to calculate interest.
-   Override `toString()` if useful, while reusing the parent's
    representation with `super.toString()`.

### `CurrentAccount extends BankAccount`

Add:

-   An overdraft limit.
-   Override withdrawal behavior only if the rules genuinely differ.
-   Preserve the base account's contract.

### Test polymorphism

``` java
BankAccount[] accounts = {
    new SavingsAccount(/* your values */),
    new CurrentAccount(/* your values */)
};

for (BankAccount account : accounts) {
    System.out.println(account);
}
```

Think carefully about whether withdrawal rules belong in the base class,
and how each subclass can preserve the base class contract.

------------------------------------------------------------------------

## 19. Advanced Interview Questions

Try answering these without running the code first.

### Question 1: Constructor order

``` java
class A {
    A() {
        System.out.println("A");
    }
}

class B extends A {
    B() {
        System.out.println("B");
    }
}

class C extends B {
    C() {
        System.out.println("C");
    }
}
```

What is printed by `new C()` and why?

### Question 2: Runtime polymorphism

``` java
class A {
    void show() {
        System.out.println("A");
    }
}

class B extends A {
    @Override
    void show() {
        System.out.println("B");
    }
}

A ref = new B();
ref.show();
```

Which implementation executes? Explain reference type vs runtime type.

### Question 3: Field hiding

``` java
class A {
    String name = "A";
}

class B extends A {
    String name = "B";
}

A ref = new B();
System.out.println(ref.name);
```

What is printed? Why is this different from method overriding?

### Question 4: Static method hiding

``` java
class A {
    static void print() {
        System.out.println("A");
    }
}

class B extends A {
    static void print() {
        System.out.println("B");
    }
}

A ref = new B();
ref.print();
```

What is printed? Why should static methods normally be called through
the class name?

### Question 5: Private members

Can a child class directly access a private field declared in its
parent? How can the child read or update that state safely?

### Question 6: Constructor dispatch

Why can calling an overridable method from a parent constructor cause a
bug in a child class?

### Question 7: LSP

Why might `Square extends Rectangle` be a poor design if `Rectangle`
exposes independent `setWidth()` and `setHeight()` methods?

### Question 8: Composition

A `Car` has an `Engine`. Why is `Car extends Engine` usually incorrect?
Show a composition-based design.

### Question 9: Abstract class vs interface

When would you choose an abstract class? When would you choose an
interface? Can one class extend an abstract class and implement multiple
interfaces?

### Question 10: Design review

A base `Employee` class has `calculateSalary()`. Two subclasses
implement it differently. What makes this a good use of polymorphism,
and what assumptions must the base contract define?

------------------------------------------------------------------------

## 20. Suggested Learning Sequence

Study the topics in this order:

1.  Inheritance meaning, IS-A relationship, and `extends`.
2.  Parent and child class members; access modifiers.
3.  Constructor chaining, `super()`, and initialization order.
4.  Method overriding and `@Override`.
5.  Runtime polymorphism and dynamic method dispatch.
6.  Upcasting, downcasting, and `instanceof`.
7.  Field hiding vs method overriding; static method hiding.
8.  `final` classes and methods.
9.  Abstract classes and interfaces.
10. Composition vs inheritance.
11. Liskov Substitution Principle and inheritance design.
12. Constructor dispatch hazards and practical design exercises.

### Final mental model

Keep these five ideas clear:

-   **Inheritance** defines a subtype relationship and reuses accessible
    behavior.
-   **Constructors** initialize the superclass portion before the
    subclass portion.
-   **Overriding** allows a subtype to specialize an inherited instance
    method.
-   **Runtime polymorphism** selects the overridden method based on the
    actual object.
-   **Good inheritance design** preserves the superclass contract;
    otherwise, composition may be safer.

Inheritance is not simply a way to avoid duplicate code. It is a way to
model meaningful type relationships while preserving predictable
behavior.
