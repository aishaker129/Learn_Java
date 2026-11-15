package Polymorphism;

public class Test {
    static void main() {
        Calculator cal = new Calculator();
        System.out.println(cal.sum(10,20,30));
        System.out.println(cal.sum(10.0,20,30));

        Animal dog = new Dog(); // upcasting
        dog.sayHello();
        //dog.eat(); // only reference property access

        Dog d = (Dog)dog; // downcasting
        d.eat();

        Animal cat = new Cat();
        cat.sayHello();
    }
}
