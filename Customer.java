package Bank;

/**
 * @(#) Customer.java
 *
 * @author Prof. Alvaro Pino N.
 * @version 1.00 2008/7/23
 */
public class Customer {

    private String firstName;
    private String lastName;
    private Account account;
    private int idCustomer;

    public Customer(String f, String l) {
        firstName = f;
        lastName = l;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String first) {
        firstName = first;
    }

    public void setLastName(String last) {
        lastName = last;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int id) {
        idCustomer = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account acct) {
        account = acct;
    }
}
