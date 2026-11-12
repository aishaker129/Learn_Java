import Encapsulation.BankAccount;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main(String[] args) {
//    m('A');

//    BankAccount a1 = new BankAccount("ACC-101", new BigDecimal("5000"));
//    BankAccount a2 = new BankAccount("ACC-101", new BigDecimal("9000"));
//    BankAccount a3 = new BankAccount("ACC-102", new BigDecimal("5000"));
//    BankAccount a4 = new BankAccount("ACC-101", new BigDecimal("5000"));
//
//    System.out.println(a1.equals(a2)); // true
//    System.out.println(a1.equals(a3)); // false
//
//    Set<BankAccount> accounts = new HashSet<>();
//    accounts.add(a1);
//    accounts.add(a2);
//    accounts.add(a3);
//    accounts.add(a4);
//
//    System.out.println(accounts.size()); // 2
//
//
//    System.out.println(a1.hashCode() == a4.hashCode());

//    Map<String,BigDecimal> accounts = new HashMap<>();
//
//    accounts.put("ACC-101",new BigDecimal("5000"));
//    accounts.put("ACC-102", new BigDecimal("6000"));
//    accounts.put("ACC-101", new BigDecimal("5000"));
//
//    System.out.println(accounts.size());
//    System.out.println(accounts.get("ACC-101"));
    m('A');

//    String name = "jhon";
//    System.out.println(upper(name)); // JHON
//    System.out.println(name);  // jhon
//
//    Child c = new Child();
//    c.name = "bangladesh";
//
//    Child ch =  upper(c);
//    System.out.println(ch.name); // BANGLADESH
//    System.out.println(c.name); // BANGLADESH
}

public static Child upper(Child c){
     c.name = c.name.toUpperCase();
     return  c;
}

public static String upper(String a){
    return a.toUpperCase();
}

static void m(Character a){
    System.out.println("Character " + a);
}

static void m(Integer a){
    System.out.println("Integer "+a);
}
static void m(long a){
    System.out.println("Long "+a);
}

static void m(float a){
    System.out.println("Float "+a);
}

static void m(double a){
    System.out.println("Double "+a);
}

// char-int-long-float-double/wrapper