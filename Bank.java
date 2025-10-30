package Bank;

/**
 * @(#)Bank.java
 *
 * @author Prof. Alvaro Pino N.
 * @version 1.00 2008/7/23
 */
public class Bank {

    private Customer[] customers;
    private int numberOfCustomers;
    private static final int MAX_CUSTOMERS = 5;

    public Bank() {
        customers = new Customer[MAX_CUSTOMERS];
        numberOfCustomers = 0;
    }

    public void addCustomer(Customer cust) {
        int i = numberOfCustomers++;
        customers[i] = cust;
    }

    public Customer getCustomer(int customer_index) {
        return customers[customer_index];
    }

    public int getNumOfCustomers() {
        return numberOfCustomers;
    }
}