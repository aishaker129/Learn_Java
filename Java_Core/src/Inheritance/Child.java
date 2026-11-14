package Inheritance;

public class Child extends Parent{
    public Child(String name, int age){
        super(name, age);
        System.out.println("Child Constructor called.");
    }

    @Override
    public void methodCalled(){
        System.out.println("Child Method called.");
    }

    @Override
    public String toString(){
        return Child.class.getSimpleName() + " Name: "+ getName() + " age: "+getAge();
    }
}
