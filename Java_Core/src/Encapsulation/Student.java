package Encapsulation;

public class Student {
    private String name;
    private int rollNo;
    private int age;

    public Student(){
        this.name = "AI Shaker";
        this.age = 10;
        this.rollNo = 1;
    }

    public Student(String name){
        if(!name.isEmpty()){
            this.name = name;
        }
        else {
            System.out.println("You must be added the Student name");
        }
    }

    public Student(int age){
        if(age < 0){
            System.out.println("age can't be negative");
        }
        this.age = age;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!name.isEmpty()){
            this.name = name;
        }
        else {
            System.out.println("You must be added the Student name");
        }

    }

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        if(rollNo < 0 ){
            System.out.println("Roll no must be greater than 0");
        }
        this.rollNo = rollNo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if(age < 0){
            System.out.println("age can't be negative");
        }
        this.age = age;
    }
}
