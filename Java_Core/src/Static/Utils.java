package Static;

public class Utils {
    public static int sum(int... a){
        int sum = 0;
        for(int i:a){sum +=i;}
        return sum;
    }

    public static String isPrime(int num){
        int cnt = 0;
        for(int i=1; i<num/2; i++){
            if(num % i==0){
                cnt ++;
            }
        }
        return cnt==1? "Prime Number ":"Not Prime";
    }

    public static String toUpper(String s){
        if(s !=null){
            return s.trim().toUpperCase();
        }
        else {
            return "";
        }
    }
}
