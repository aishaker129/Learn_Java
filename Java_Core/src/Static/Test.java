package Static;

public class Test {
    static void main() {
        System.out.println(Student.count);
        System.out.println(Utils.sum(1,2,6,30));
        System.out.println(Utils.isPrime(27));
        System.out.println(Utils.toUpper("aishaker"));

        Test t = new Test();
        System.out.println(t.add(10,13));
    }

    public int add(int a,int b){ // since add is non static variable that's why can't
                                // call the add method inside the static method without creating object.
        return a + b;
    }
}
