package Polymorphism;

public class Calculator {
    public int sum(int... a){
        int sum = 0;
        for(int i: a){
            sum += i;
        }
        return  sum;
    }

    public float sum(float... a){
        float sum = 0;
        for(float i: a){
            sum +=i;
        }
        return  sum;
    }

    public double sum(double... a){
        double sum = 0;
        for(double i:a){
            sum += i;
        }
        return sum;
    }
}
