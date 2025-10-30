package Bank;

/**
 * @(#)Account.java
 *
 * @author Prof. Alvaro Pino N.
 * @version 1.00 2008/7/23
 */
public class Account {

    protected double balance;

    public Account(double bal) {
        balance = bal;
    }

    public double getBalance() {
        return balance;
    }

    public double deposit(double amount) {
        balance = balance + amount;
        return amount;
    }

    public void withdraw(double amount) throws OverdraftException {
        if (balance < amount) {
            throw new OverdraftException("Insufficient funds ", amount - balance);
        } else {
            balance = balance - amount;
        }
    }
}