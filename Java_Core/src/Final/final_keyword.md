# Java `final` Keyword --- Basic to Advanced

**Level:** Beginner to Advanced \| **Java:** 17+

## Contents

1.  Meaning of `final`
2.  Final local variables
3.  Final parameters
4.  Final fields
5.  Final references vs immutable objects
6.  Blank final fields
7.  `static final`
8.  Compile-time constants and inlining
9.  Initialization order
10. Final and constructors
11. Final methods
12. Final classes
13. Inheritance and overloading
14. Immutability
15. Defensive copying
16. Java Memory Model
17. `final` vs `volatile` vs `synchronized`
18. Lambdas and effectively-final variables
19. Interfaces and records
20. Common misconceptions
21. Examples
22. Output questions
23. Interview questions
24. Best practices
25. Mental model

------------------------------------------------------------------------

## 1. Meaning of `final`

`final` restricts certain changes, depending on where it is used.

  -----------------------------------------------------------------------
  Use                                 Meaning
  ----------------------------------- -----------------------------------
  Variable                            Cannot be assigned again after
                                      initialization

  Reference                           Cannot point to another object; the
                                      object may still be mutable

  Field                               Must be assigned exactly once
                                      according to Java initialization
                                      rules

  Method                              Cannot be overridden

  Class                               Cannot be subclassed

  Parameter                           Cannot be reassigned within the
                                      method
  -----------------------------------------------------------------------

`final` does **not** automatically mean immutable, thread-safe, or
compile-time constant.

## 2. Final local variables

``` java
final int age = 25;
// age = 26; // Compilation error
```

A blank final local variable may be assigned later, but only once and
before use:

``` java
final int result;

if (condition) {
    result = 10;
} else {
    result = 20;
}

System.out.println(result);
```

The compiler checks **definite assignment**: every valid path must
assign the variable before it is read.

## 3. Final parameters

``` java
void print(final String name) {
    System.out.println(name);
    // name = "Other"; // Error
}
```

For object parameters, final prevents reference reassignment---not
object mutation:

``` java
void update(final List<String> names) {
    names.add("Java");              // Allowed
    // names = new ArrayList<>();   // Not allowed
}
```

## 4. Final fields

``` java
public class Employee {
    private final int id;
    private final String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

Once initialized, these fields cannot be reassigned. Final fields help
express stable identity/configuration, reduce accidental changes, and
support initialization-safety guarantees.

## 5. Final reference vs immutable object

``` java
final List<String> names = new ArrayList<>();
names.add("Java"); // Allowed
// names = new ArrayList<>(); // Error
```

The reference cannot be changed, but the list is mutable.

``` text
final reference ───────> mutable object
       X reassignment       ✓ object mutation
```

Distinguish: - **Final reference:** cannot be reassigned. - **Immutable
object:** its observable state cannot change. - **Deeply immutable:**
its reachable state cannot be mutated through any exposed reference.

## 6. Blank final fields

A blank final field is declared without an initializer and assigned in a
constructor.

``` java
class Config {
    private final int port;

    Config() {
        port = 8080;
    }

    Config(int port) {
        this.port = port;
    }
}
```

Each valid constructor path must initialize it exactly once. Reassigning
it is a compile-time error.

## 7. `static final`

``` java
public class AppConfig {
    public static final int MAX_RETRY = 3;
}
```

-   `static`: belongs to the class, not each instance.
-   `final`: cannot be reassigned after initialization.

A static final field may be initialized at declaration or in a static
initializer:

``` java
class Config {
    static final String APP_NAME;

    static {
        APP_NAME = loadName();
    }

    private static String loadName() {
        return "MyApp";
    }
}
```

Constants commonly use uppercase names with underscores.

## 8. Compile-time constants

A Java **constant variable** is a `final` primitive or `String` variable
initialized with a constant expression.

``` java
static final int MAX = 100;              // Constant variable
static final String NAME = "Java";       // Constant variable
static final Integer BOXED = 100;        // Not a constant variable
static final String VALUE = getValue();  // Not a constant variable
```

### Constant inlining

The compiler may inline compile-time constant values into client
bytecode.

If a library declares:

``` java
public static final int VERSION = 1;
```

and a client compiles against it, the client may embed `1`. If the
library later changes it to `2` without recompiling the client, the
client may still use `1`.

Public compile-time constants can therefore have binary compatibility
implications. For values expected to change independently, consider a
method or configuration mechanism.

## 9. Initialization order

Simplified initialization order:

1.  Static field initializers and static blocks, in textual order, once
    per class initialization.
2.  Instance field initializers and instance initializer blocks, in
    textual order, for each object.
3.  Constructor body, after superclass construction.

``` java
class Demo {
    static {
        System.out.println("Static");
    }

    {
        System.out.println("Instance");
    }

    Demo() {
        System.out.println("Constructor");
    }
}
```

Creating an instance prints:

``` text
Static
Instance
Constructor
```

Textual order matters within each initialization phase.

## 10. Final and constructors

Final fields must be initialized before construction completes.

``` java
public class User {
    private final String email;

    public User(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        this.email = email.trim().toLowerCase(Locale.ROOT);
    }
}
```

Constructor delegation:

``` java
class Point {
    private final int x;
    private final int y;

    Point() {
        this(0, 0);
    }

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
```

Avoid letting `this` escape during construction---such as registering it
externally or starting a thread before initialization completes. Another
thread could observe a partially constructed object.

## 11. Final methods

A final method cannot be overridden.

``` java
class Parent {
    public final void validate() {
        System.out.println("Critical validation");
    }
}

class Child extends Parent {
    // Cannot override validate()
}
```

Reasons to use final methods: - Protect critical behavior. - Preserve
invariants. - Prevent unsafe customization.

A final method can still be inherited and called. Final methods can also
be overloaded.

Private methods are not overridden in the normal sense because they are
not inherited as accessible methods.

## 12. Final classes

``` java
public final class Money {
}

// class SpecialMoney extends Money {} // Error
```

A final class cannot be subclassed. This can protect behavior and help
immutable designs.

However, final class does not imply immutability:

``` java
final class Team {
    private final List<String> members = new ArrayList<>();

    void add(String member) {
        members.add(member); // Still mutable
    }
}
```

## 13. Inheritance and overloading

``` java
class Parent {
    public void show() {}
    public final void secureOperation() {}
}

class Child extends Parent {
    @Override
    public void show() {}

    // Cannot override secureOperation()
}
```

Final prevents overriding, not calling.

Overloading is different:

``` java
class Calculator {
    final int add(int a, int b) { return a + b; }
    final double add(double a, double b) { return a + b; }
}
```

Same name with different parameter lists is overloading.

## 14. Final and immutability

A well-designed immutable class typically uses: - A final class (or
otherwise controlled inheritance). - Private final fields. - Constructor
initialization. - No mutators. - Immutable field types or defensive
copies. - No leaked mutable references.

``` java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Negative amount");
        }
    }

    public BigDecimal amount() { return amount; }
    public Currency currency() { return currency; }
}
```

`BigDecimal` and `Currency` are immutable types.

## 15. Defensive copying

A final field can reference mutable data. Copy inputs and protect
outputs.

``` java
public final class Course {
    private final List<String> students;

    public Course(List<String> students) {
        this.students = List.copyOf(students);
    }

    public List<String> students() {
        return students;
    }
}
```

`List.copyOf` creates an unmodifiable collection structure, but it does
not deep-copy mutable elements.

If a list contains mutable `Employee` objects, those objects may still
be modified through other references.

## 16. Final fields and the Java Memory Model

The Java Memory Model gives special initialization-safety guarantees to
final fields when: 1. The constructor initializes them. 2. The object
does not escape during construction. 3. Construction completes
correctly.

``` java
final class Settings {
    private final int timeout;
    private final String mode;

    Settings(int timeout, String mode) {
        this.timeout = timeout;
        this.mode = mode;
    }
}
```

This does not make the whole object thread-safe if it contains mutable
reachable state.

## 17. `final` vs `volatile` vs `synchronized`

  -----------------------------------------------------------------------
  Feature                             Main purpose
  ----------------------------------- -----------------------------------
  `final`                             Restrict reassignment, overriding,
                                      or subclassing

  `volatile`                          Visibility/order for reads and
                                      writes of a variable

  `synchronized`                      Mutual exclusion and visibility
                                      through monitor locking

  Atomic classes                      Atomic operations for supported
                                      state changes
  -----------------------------------------------------------------------

``` java
private volatile int count;

void increment() {
    count++; // Not atomic, even though volatile
}
```

`count++` is read-modify-write. Use synchronization or an atomic type
for atomic increments.

## 18. Lambdas and effectively-final variables

Captured local variables must be final or effectively final.

``` java
int limit = 10;
Runnable task = () -> System.out.println(limit);
```

`limit` is effectively final because it is not reassigned.

This is invalid:

``` java
int limit = 10;
Runnable task = () -> System.out.println(limit);
limit = 20; // Compile error: no longer effectively final
```

An object reference can be effectively final while the object is
mutable:

``` java
List<String> names = new ArrayList<>();
Runnable task = () -> names.add("Java"); // Allowed
```

## 19. Interfaces and records

Interface fields are implicitly `public static final`:

``` java
interface Constants {
    int MAX_USERS = 100;
}
```

Records have final component fields and are implicitly final as record
classes:

``` java
public record User(String name, int age) {
    public User {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name required");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Invalid age");
        }
    }
}
```

Record immutability is shallow. Protect mutable components:

``` java
public record Team(List<String> members) {
    public Team {
        members = List.copyOf(members);
    }
}
```

## 20. Common misconceptions

1.  **`final` makes an object immutable.** False; a final reference can
    point to a mutable object.
2.  **Every `static final` is a compile-time constant.** False; object
    references and method-initialized values are not constant variables.
3.  **A final method cannot be called by subclasses.** False; it can be
    inherited and called, just not overridden.
4.  **A final class has no mutable state.** False.
5.  **`final` means `static`.** False; one controls
    reassignment/extension, the other class membership.
6.  **`final` guarantees thread safety.** False; mutable reachable state
    still needs safe design.
7.  **`List.copyOf` is a deep copy.** False; elements are not copied.

## 21. Practical examples

### Final local

``` java
final int x = 10;
System.out.println(x);
```

Output: `10`

### Final reference, mutable object

``` java
final StringBuilder sb = new StringBuilder("Java");
sb.append(" 21");
System.out.println(sb);
// sb = new StringBuilder(); // Error
```

Output: `Java 21`

### Final field

``` java
class Employee {
    private final int id;

    Employee(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
```

### Final constant

``` java
class MathConfig {
    public static final double PI = 3.14159;
}
```

### Final method / class

``` java
class Parent {
    final void display() {}
}

final class SecurityToken {}
```

## 22. Output-prediction questions

Try first; answers follow.

### Q1

``` java
final int x = 10;
System.out.println(x);
```

**Answer:** `10`

### Q2

``` java
final StringBuilder sb = new StringBuilder("A");
sb.append("B");
System.out.println(sb);
```

**Answer:** `AB`. The reference is final; the object is mutable.

### Q3

``` java
final int x;
x = 5;
System.out.println(x);
```

**Answer:** `5`. A blank final local can be assigned once before use.

### Q4

``` java
class Parent {
    final void show() {}
}
class Child extends Parent {
    // void show() {}
}
```

**Answer:** Compilation error: final method cannot be overridden.

### Q5

``` java
final class Parent {}
// class Child extends Parent {}
```

**Answer:** Compilation error: final class cannot be extended.

### Q6

``` java
final List<String> list = new ArrayList<>();
list.add("A");
// list = new ArrayList<>();
System.out.println(list);
```

**Answer:** `[A]`

### Q7

``` java
int x = 10;
Runnable r = () -> System.out.println(x);
r.run();
```

**Answer:** `10`; `x` is effectively final.

### Q8

``` java
int x = 10;
Runnable r = () -> System.out.println(x);
x = 20;
```

**Answer:** Compilation error; `x` is no longer effectively final.

### Q9

``` java
static final Integer VALUE = 10;
```

Compile-time constant? **Answer:** No. `Integer` is a reference type.

### Q10

``` java
static final int VALUE = 10;
```

Compile-time constant? **Answer:** Yes, because it is a primitive
initialized with a constant expression.

## 23. Interview questions

### Beginner

1.  What does `final` mean in Java?
2.  Can a final local variable be initialized later?
3.  Can a final parameter be reassigned?
4.  What is a blank final variable?
5.  Can a final class be extended?

### Intermediate

6.  Final reference vs immutable object?
7.  Can a final method be overloaded?
8.  Can a final method be inherited?
9.  What is effectively final?
10. Are all static final fields compile-time constants?

### Advanced

11. Explain constant inlining and binary compatibility.
12. What final-field guarantees does the Java Memory Model provide?
13. Why must `this` not escape during construction?
14. Does finality make a class thread-safe?
15. How can a final field refer to a mutable object?
16. Why use final classes for immutable value types?
17. Compare `final`, `volatile`, and `synchronized`.
18. What extensibility trade-offs come with final classes/methods?
19. Why is `List.copyOf` not deep copying?
20. When should a public constant be replaced with a
    method/configuration?

## 24. Best practices

-   Use `final` for references that should not be reassigned.
-   Prefer `private final` for stable identity and configuration.
-   Initialize final fields during construction.
-   Validate before assigning.
-   Do not expose mutable objects just because the reference is final.
-   Defensively copy mutable inputs.
-   Prefer immutable value types.
-   Consider final classes for immutable value objects.
-   Use final methods only when preventing overrides is intentional.
-   Do not mark everything final without considering extensibility.
-   Distinguish compile-time constants from runtime-initialized static
    final fields.
-   Never assume finality alone guarantees thread safety.

## 25. Final mental model

``` text
final variable
  -> cannot be reassigned

final reference
  -> cannot point to another object
  -> object may still be mutable

final field
  -> assigned once under initialization rules

static final
  -> class-level, non-reassignable field
  -> may or may not be a compile-time constant

final method
  -> cannot be overridden

final class
  -> cannot be subclassed

final + immutable design
  -> stable state after construction
  -> easier reasoning and safer sharing
```

> **Core idea:** `final` restricts reassignment or extension. It does
> not automatically freeze an object, validate data, or guarantee thread
> safety. Use it deliberately to express what must remain stable and
> make your design intention clear to the compiler and other developers.
