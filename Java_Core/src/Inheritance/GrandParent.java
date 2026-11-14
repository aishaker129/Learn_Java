package Inheritance;

public class GrandParent {
    private String name;
    private int age;
    private boolean isSupperPower;

    public GrandParent(String name, int age){
        this.name = name;
        this.age = age;
        this.isSupperPower = false;
        System.out.println("GrandParent Constructor called.");
    }

    public boolean hasIsSupperPower() {
        return isSupperPower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public void methodCalled(){
        System.out.println("GrandParent Method called.");
    }

    @Override
    public String toString(){
        return GrandParent.class.getSimpleName() + " Name: "+ getName() + " age: "+getAge();
    }
}
