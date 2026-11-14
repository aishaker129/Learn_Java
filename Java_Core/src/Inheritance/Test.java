package Inheritance;

public class Test {
    void main(String... args){
        Child ch = new Child("Akhlakul Islam", 24);
        ch.methodCalled();
        System.out.println(ch.toString());

        GrandParent p = new Parent("A",50);
        System.out.println(p.toString());
    }
}
