package Encapsulation;


import java.math.BigDecimal;

public class BankAccount {
    private final String accountNo;
    private BigDecimal balance;

    public BankAccount(String accountNo ,BigDecimal initialBalance){
        if(initialBalance ==null ||  initialBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Initial balance can't be negative");
        }

        if(accountNo.isBlank()  || accountNo == null){
            throw new RuntimeException("Account Number missing.");
        }

        this.balance = initialBalance;
        this.accountNo = accountNo;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public String getAccountNo(){ return accountNo;}

    public void widthrow(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0 ){
            throw new IllegalArgumentException("Amount should be greather than 0");
        }

        if(amount.compareTo(balance) > 0){
            throw new IllegalArgumentException("Insufficience balance.");
        }

        this.balance = this.balance.subtract(amount);

        System.out.println(amount + "Widthrowal successfully : amount = "+getBalance());
    }

    public void deposit(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0 ){
            throw new IllegalArgumentException("Amount should be greather than 0");
        }

        this.balance = this.balance.add(amount);
        System.out.println(amount + " taka Deposit successful: new Amount = "+getBalance());
    }

    @Override
    public  boolean equals(Object obj){
        // same reference

        if(this == obj){ return true;}
//      null or difference class
        if(obj == null || getClass() != obj.getClass()) {return false;}

        BankAccount bankAccount = (BankAccount) obj;
        return accountNo.equals(bankAccount.accountNo);
    }

//    @Override
//    public int hashCode(){
//        return accountNo.hashCode();
//    }
}
