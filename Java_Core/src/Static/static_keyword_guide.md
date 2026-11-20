# Java `static` Keyword --- Guide Basic to Advance 

**Level:** Intermediate to Advanced \| **Language:** Java

## 1. What Does `static` Mean?

`static` marks a member as belonging to a class rather than to each
object instance. A static field is shared by instances of the same class
definition (within the same defining class loader).

``` java
class Employee {
    String name;                       // One per object
    static String company = "ABC";     // Class-level shared field
}
```

``` java
Employee e1 = new Employee();
Employee e2 = new Employee();
e1.name = "Asha";
e2.name = "Rafi";

System.out.println(e1.name);        // Asha
System.out.println(e2.name);        // Rafi
System.out.println(Employee.company); // ABC
```

Mental model: - Instance member: belongs to a particular object. -
Static member: belongs to the class definition.

`static` does not mean constant. Use `final` to prevent reassignment.

## 2. Static Fields

``` java
class User {
    private static int userCount;

    public User() {
        userCount++;
    }

    public static int getUserCount() {
        return userCount;
    }
}
```

``` java
new User();
new User();
System.out.println(User.getUserCount()); // 2
```

Useful for class-level statistics, constants, configuration, or
carefully managed shared registries.

Do not use static fields for per-object state:

``` java
class User {
    static String name; // All User objects share this field
}
```

Prefer an instance field when each user has an independent name.

## 3. Static Methods

A static method can be called without creating an object.

``` java
class MathUtil {
    public static int square(int number) {
        return number * number;
    }
}

int result = MathUtil.square(5); // 25
```

A static method has no implicit `this` reference, so it cannot directly
access instance members:

``` java
class Example {
    private int value = 10;
    private static int count;

    static void show() {
        count++;
        // System.out.println(value); // Compile-time error
        // System.out.println(this.value); // Compile-time error
    }

    void printValue() {
        System.out.println(value); // Valid
        System.out.println(count); // Valid
    }

    static void printValue(Example object) {
        System.out.println(object.value); // Explicit object supplied
    }
}
```

Use static methods for operations that logically belong to the class:
utility operations, factory methods, and class-level behavior. Do not
make every method static just to avoid creating objects.

## 4. Static vs Instance Members

  -----------------------------------------------------------------------
  Feature                 Static                  Instance
  ----------------------- ----------------------- -----------------------
  Belongs to              Class                   Object

  Field storage           One per class           One per object
                          definition / class      
                          loader                  

  Needs object to call    No                      Usually yes

  Has implicit `this`     No                      Yes

  Can directly access     No                      Yes
  instance fields                                 

  Method dispatch         Compile-time hiding     Runtime overriding
                                                  possible
  -----------------------------------------------------------------------

## 5. `static final`: Constants

``` java
class AppConfig {
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final String APPLICATION_NAME = "CampusKart";
}
```

-   `static`: class-level.
-   `final`: cannot be reassigned after assignment.

A final reference does not make its object immutable:

``` java
static final List<String> ROLES = new ArrayList<>();
ROLES.add("ADMIN"); // Allowed
// ROLES = new ArrayList<>(); // Not allowed
```

For an immutable collection:

``` java
static final List<String> ROLES = List.of("ADMIN", "USER");
```

## 6. Static Initialization Blocks

A static initialization block runs when the JVM initializes the class.

``` java
class DatabaseConfig {
    static String url;

    static {
        System.out.println("Static block executed");
        url = "jdbc:postgresql://localhost:5432/app";
    }
}
```

Multiple static field initializers and static blocks execute in textual
order:

``` java
class Example {
    static int first = print("first field");

    static {
        System.out.println("first block");
    }

    static int second = print("second field");

    static {
        System.out.println("second block");
    }

    static int print(String message) {
        System.out.println(message);
        return 1;
    }
}
```

Avoid slow I/O, network calls, or complex startup logic in static
blocks. Initialization failures can surface as
`ExceptionInInitializerError`. Prefer explicit lifecycle management when
configuration or dependencies are involved.

## 7. Class Loading, Linking, and Initialization

The JVM distinguishes three broad stages:

1.  **Loading:** finds the class definition and creates its runtime
    representation.
2.  **Linking:**
    -   Verification checks class-file correctness.
    -   Preparation creates static fields and gives them default values.
    -   Resolution converts symbolic references into direct references
        as needed; resolution may be lazy.
3.  **Initialization:** executes static field initializers and static
    blocks in textual order.

A class is initialized before certain active uses, including: - Creating
an instance with `new`. - Invoking a static method declared by the
class. - Assigning a static field declared by the class. - Reading a
non-constant static field declared by the class. - Certain reflective
operations.

Merely using a class literal or reading a compile-time constant does not
necessarily initialize the class.

``` java
class Demo {
    static {
        System.out.println("Demo initialized");
    }

    static void show() {
        System.out.println("show()");
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Main started");
        Demo.show();
    }
}
```

Output:

``` text
Main started
Demo initialized
show()
```

### Compile-time constant variables

``` java
class Constants {
    static {
        System.out.println("Constants initialized");
    }

    static final int MAX = 100;
}
```

A `final` primitive or `String` initialized with a constant expression
is a constant variable. Its value may be embedded in client bytecode, so
reading it does not necessarily initialize the declaring class.

A `static final` reference to a mutable object is not a constant
variable:

``` java
static final List<String> NAMES = new ArrayList<>();
```

## 8. Static Members and Inheritance

Static members can be accessed through a subclass name when inherited:

``` java
class Parent {
    static int count = 10;

    static void show() {
        System.out.println("Parent static method");
    }
}

class Child extends Parent {
}

System.out.println(Child.count);
Child.show();
```

This is access to inherited static members, not overriding. Prefer
naming the class that actually declares the member (`Parent.show()`) to
make ownership clear.

## 9. Static Method Hiding vs Instance Method Overriding

Static methods are hidden, not overridden.

``` java
class Parent {
    static void display() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    static void display() {
        System.out.println("Child");
    }
}

Parent ref = new Child();
ref.display();   // Parent
Child.display(); // Child
```

Static method selection is based on compile-time type, not runtime
object.

Compare instance overriding:

``` java
class Parent {
    void print() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    @Override
    void print() {
        System.out.println("Child");
    }
}

Parent ref = new Child();
ref.print(); // Child
```

  -----------------------------------------------------------------------
  Static hiding                       Instance overriding
  ----------------------------------- -----------------------------------
  Static methods                      Instance methods

  Compile-time selection              Runtime dispatch

  Runtime object does not select      Runtime object selects override
  method                              

  `@Override` is not applicable       `@Override` is recommended
  -----------------------------------------------------------------------

A static method cannot hide an instance method with the same signature
in an inheritance relationship, or vice versa.

## 10. Static Field Hiding

``` java
class Parent {
    static String type = "Parent";
}

class Child extends Parent {
    static String type = "Child";
}

System.out.println(Parent.type); // Parent
System.out.println(Child.type);  // Child

Parent ref = new Child();
System.out.println(ref.type);    // Parent; legal, but avoid this style
```

Field access is not dynamically dispatched. Use class names for static
fields.

## 11. Static Nested Classes

A member class can be declared static:

``` java
class Outer {
    private static String company = "ABC";
    private String owner = "Akhlakul";

    static class Nested {
        void show() {
            System.out.println(company); // Valid
            // System.out.println(owner); // Error: no enclosing Outer object
        }

        void showOwner(Outer outer) {
            System.out.println(outer.owner); // Explicit object
        }
    }
}
```

Create it without an `Outer` instance:

``` java
Outer.Nested nested = new Outer.Nested();
```

A static nested class has no implicit reference to an enclosing object.
It can directly access enclosing static members. It still can access
private members when given an explicit enclosing instance.

## 12. Inner Class vs Static Nested Class

``` java
class Outer {
    private String message = "Hello";

    class Inner {
        void show() {
            System.out.println(message);
        }
    }

    static class Nested {
        void show() {
            // No implicit Outer instance
        }
    }
}

Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
Outer.Nested nested = new Outer.Nested();
```

  -----------------------------------------------------------------------
  Inner class                         Static nested class
  ----------------------------------- -----------------------------------
  Non-static member class             Static member class

  Requires enclosing instance         Does not require enclosing instance

  Has implicit enclosing-instance     No implicit enclosing-instance
  reference                           reference

  Can directly access outer instance  Direct access to outer static
  members                             members
  -----------------------------------------------------------------------

Java does not allow a top-level class to be declared `static`.

## 13. Static Import

Static import lets code use static members without qualifying the
declaring class.

``` java
import static java.lang.Math.sqrt;

double result = sqrt(25);
```

Importing all static members is also possible:

``` java
import static java.lang.Math.*;

double result = sqrt(pow(3, 2) + pow(4, 2));
```

Use static imports sparingly; too many can make the origin of methods
and constants unclear.

## 14. Static Methods and Generics

A static member belongs to the class, not to a particular parameterized
instance. It cannot directly use the enclosing generic class's type
parameter.

``` java
class Box<T> {
    // static T value; // Invalid
    // static T getValue() { return value; } // Invalid

    public static <U> U identity(U value) {
        return value;
    }
}

String text = Box.<String>identity("Java");
Integer number = Box.identity(100); // U inferred
```

The static method declares its own type parameter `U`, independent of
the class's `T`.

## 15. Static Factory Methods

A static factory method creates and returns an object.

``` java
class User {
    private final String username;

    private User(String username) {
        this.username = username;
    }

    public static User create(String username) {
        return new User(username);
    }

    public String getUsername() {
        return username;
    }
}

User user = User.create("akhlakul");
```

Static factories can: - Have meaningful names. - Hide constructor
details. - Validate or normalize input. - Return cached instances when
appropriate. - Return a subtype behind an abstraction.

Interfaces can also define static methods:

``` java
interface Shape {
    double area();

    static Shape unitCircle() {
        return new Circle(1.0);
    }
}
```

Call it through the declaring interface: `Shape.unitCircle()`. Static
interface methods are not inherited as instance methods by implementing
classes.

## 16. Static State and Thread Safety

A static field is shared by threads accessing the same class definition.
That does not make updates thread-safe.

``` java
class Counter {
    static int count = 0;

    static void increment() {
        count++; // Not atomic
    }
}
```

`count++` is a read-modify-write operation. Concurrent calls can lose
updates.

### Option A: synchronized

``` java
class Counter {
    private static int count;

    public static synchronized void increment() {
        count++;
    }

    public static synchronized int getCount() {
        return count;
    }
}
```

A static synchronized method locks the `Class` object for that class,
not an individual instance.

### Option B: AtomicInteger

``` java
import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private static final AtomicInteger COUNT = new AtomicInteger();

    public static void increment() {
        COUNT.incrementAndGet();
    }

    public static int getCount() {
        return COUNT.get();
    }
}
```

### Option C: LongAdder

For highly contended statistics, `LongAdder` can provide better
throughput than one `AtomicLong`. Its `sum()` is not necessarily a
single atomic snapshot while updates are occurring.

``` java
import java.util.concurrent.atomic.LongAdder;

class RequestMetrics {
    private static final LongAdder REQUESTS = new LongAdder();

    public static void recordRequest() {
        REQUESTS.increment();
    }

    public static long totalRequests() {
        return REQUESTS.sum();
    }
}
```

Choose according to required semantics.

## 17. Static Initialization and Thread Safety

JVM class initialization is synchronized: one thread performs
initialization and other threads that need the class wait for it to
complete. This enables the initialization-on-demand holder idiom:

``` java
public class AppConfig {
    private AppConfig() {}

    private static class Holder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }
}
```

`Holder` is initialized only when `getInstance()` first accesses it.
Class initialization provides safe publication of the instance.

A singleton is not always the best design. Global state can complicate
testing and lifecycle management. In Spring applications, prefer
container-managed beans and dependency injection for services.

## 18. Static Variables and Class Loaders

A static field is not one universal variable for the entire JVM process.
A class is identified by its binary name and defining class loader.

If two class loaders independently load the same binary class name, they
create distinct class definitions and distinct static state.

``` text
ClassLoader A -> com.example.Cache -> static cache A
ClassLoader B -> com.example.Cache -> static cache B
```

This matters in application servers, plugin architectures, and
hot-reload systems. Static fields can also contribute to class-loader
leaks if they retain references to classes or objects that should be
unloaded.

## 19. Static Fields and Memory Management

Objects reachable through static fields remain reachable while those
references remain reachable.

``` java
class Cache {
    static final List<Object> ITEMS = new ArrayList<>();
}
```

If items are added indefinitely and never removed, the list can retain
them and cause memory growth.

Use bounded caches, eviction, cleanup, and clear lifecycle ownership.
`static` does not mean "stored forever," but long-lived references can
prevent garbage collection.

## 20. `static` vs `final` vs `const`

Java does not use `const` for ordinary variable declarations; it is a
reserved word.

  -----------------------------------------------------------------------
  Keyword                             Meaning
  ----------------------------------- -----------------------------------
  `static`                            Class-level member

  `final`                             Variable assigned once; method
                                      cannot be overridden; class cannot
                                      be extended

  `static final`                      Class-level variable that cannot be
                                      reassigned
  -----------------------------------------------------------------------

``` java
static int count = 0;              // Shared and mutable
final int id = 10;                 // Per-instance final field
static final int MAX_USERS = 100; // Class-level constant-style field
```

A final object reference does not make the object immutable.

## 21. Common Mistakes

1.  Accessing instance fields directly from static methods.
2.  Making every utility-like method static without considering object
    ownership.
3.  Using static fields for per-object state.
4.  Assuming static methods are overridden.
5.  Calling static members through object references.
6.  Assuming `static final` makes a referenced object immutable.
7.  Assuming `count++` is thread-safe.
8.  Performing complex I/O in static initialization blocks.
9.  Using static collections as unlimited global storage.
10. Trying to use a generic class's type parameter in a static field or
    method.

## 22. Practical Project: Thread-Safe Application Metrics

Build a utility that tracks application activity.

``` java
import java.util.concurrent.atomic.LongAdder;

public final class Metrics {
    private static final LongAdder REQUEST_COUNT = new LongAdder();

    private Metrics() {
        // Prevent ordinary instantiation.
    }

    public static void recordRequest() {
        REQUEST_COUNT.increment();
    }

    public static long getRequestCount() {
        return REQUEST_COUNT.sum();
    }
}
```

Test it by submitting many tasks to an `ExecutorService`, waiting for
all tasks to finish, and verifying the count.

Investigate: 1. Why is the constructor private? 2. Why is the field both
`static` and `final`? 3. Why is `LongAdder` useful for concurrent
metrics? 4. Would `AtomicInteger` also work? 5. What changes if the
application runs in multiple JVM processes? 6. Should production metrics
use a static in-memory counter or an external metrics system?

## 23. Advanced Output-Prediction Questions

Predict each output before running the code. Explain why.

### Problem 1: Static and instance fields

``` java
class Example {
    static int count = 10;
    int value = 20;
}

Example e1 = new Example();
Example e2 = new Example();

e1.count++;
e1.value++;

System.out.println(e2.count);
System.out.println(e2.value);
```

Why do the values behave differently?

### Problem 2: Static method hiding

``` java
class Parent {
    static void show() { System.out.println("Parent"); }
}

class Child extends Parent {
    static void show() { System.out.println("Child"); }
}

Parent ref = new Child();
ref.show();
Child.show();
```

Predict both lines and explain compile-time selection.

### Problem 3: Static initialization order

``` java
class Demo {
    static int a = print("A");

    static {
        System.out.println("Static block");
    }

    static int b = print("B");

    static int print(String value) {
        System.out.println(value);
        return 1;
    }
}

System.out.println(Demo.b);
```

What is the output order?

### Problem 4: Compile-time constant

``` java
class Constants {
    static {
        System.out.println("Initialized");
    }

    static final int VALUE = 50;
}

System.out.println(Constants.VALUE);
```

Does this necessarily initialize `Constants`? Explain constant
variables.

### Problem 5: Static nested class

``` java
class Outer {
    static int a = 10;
    int b = 20;

    static class Nested {
        void show() {
            System.out.println(a);
            // System.out.println(b);
        }
    }
}
```

Why can `Nested` access `a` directly but not `b`?

### Problem 6: Static generic method

``` java
class Box<T> {
    static <U> U identity(U value) {
        return value;
    }
}

String result = Box.identity("Java");
```

Why is this legal although a static method cannot use class type
parameter `T`?

### Problem 7: Shared state and threads

``` java
class Counter {
    static int count;

    static void increment() {
        count++;
    }
}
```

Why can concurrent calls lose updates? Suggest two fixes.

### Problem 8: Static field hiding

``` java
class A {
    static String name = "A";
}

class B extends A {
    static String name = "B";
}

A ref = new B();
System.out.println(ref.name);
System.out.println(B.name);
```

Explain the output and why accessing static fields through references is
discouraged.

### Problem 9: Class initialization

Explain loading, linking, and initialization. Give examples of active
uses that trigger initialization.

### Problem 10: Class loaders

Can two class loaders load the same fully qualified class name and have
separate static fields? Explain.

## 24. Suggested Study Sequence

### Stage 1 --- Fundamentals

-   Static fields and methods.
-   Static vs instance members.
-   `static final`.
-   Static initialization blocks.

### Stage 2 --- OOP and class behavior

-   Static members and inheritance.
-   Method hiding vs overriding.
-   Field hiding.
-   Static nested classes vs inner classes.
-   Static imports.

### Stage 3 --- Advanced Java

-   Class loading, linking, and initialization.
-   Compile-time constants.
-   Static methods and generics.
-   Static factory methods.
-   Static interface methods.

### Stage 4 --- JVM and concurrency

-   Thread safety of shared static state.
-   `synchronized`, `AtomicInteger`, and `LongAdder`.
-   Initialization-on-demand holder.
-   Class loaders and separate static state.
-   Static references and memory retention.

### Stage 5 --- Design practice

-   Identify inappropriate global state.
-   Refactor static dependencies where injection is better.
-   Build and test the metrics utility.
-   Solve the output-prediction questions.

## 25. Final Mental Model

-   `static` means class-level, not object-level.
-   Static fields are shared by instances of the same class definition
    and class loader.
-   Static methods have no implicit `this` reference.
-   Static methods are hidden, not overridden.
-   Static fields are hidden, not dynamically dispatched.
-   `static final` prevents reassignment; it does not automatically make
    referenced objects immutable.
-   Static initialization runs as part of class initialization.
-   JVM class initialization is synchronized, but ordinary static field
    updates are not automatically thread-safe.
-   Static nested classes do not require an enclosing instance.
-   Static members cannot directly use the type parameter of their
    enclosing generic class.
-   Static state can create global coupling, concurrency problems, and
    memory-retention issues.

**Key design question:** Does this data or behavior truly belong to the
class as a whole, or should it belong to an individual object or an
injected service?

Use `static` when class-level ownership is intentional---not merely to
avoid creating an object.
