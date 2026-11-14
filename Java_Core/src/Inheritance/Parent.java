package Inheritance;

public class Parent extends GrandParent{

    public Parent(String name, int age){
        super(name, age);
        System.out.println("Parent Constructor called.");
    }

    @Override
    public void methodCalled(){
        System.out.println("Parent Method called.");
    }

    @Override
    public String toString(){
        return Parent.class.getSimpleName() + " Name: "+ getName() + " age: "+getAge();
    }
}
