package Bank;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @(#) BankAtmSwing.java
 *
 * @author Prof. Alvaro Pino N.
 *
 * @version 1.00 2008/7/23
 */
public class BankAtmSwing extends JFrame implements ActionListener {

    private JTextArea jtA;
    private JTextField jtfDataE;
    private JTextField jtfMsn;
    private JButton jb1, jb2, jb3, jb4, jb5, jb6, jb7;
    private JButton jb8, jb9, jb0, jbEnter, jbClear;
    private JButton jbDisplay;
    private JButton jbDeposit;
    private JButton jbWithdraw;

    private boolean first;
    Bank banco = new Bank();
    boolean isValido = false;

    // Constructor
    public BankAtmSwing(String title) {
        super(title);

        MenuBar menuBar = new MenuBar();
        Menu menuClientes = new Menu();
        MenuItem menuCrear = new MenuItem();
        MenuItem menuSalir = new MenuItem();

        menuClientes.setLabel("Clientes");
        menuCrear.setLabel("Crear Clientes");
        menuSalir.setLabel("Salir");

        menuClientes.add(menuCrear);
        menuClientes.add(menuSalir);
        menuBar.add(menuClientes);
        setMenuBar(menuBar);

        jtA = new JTextArea(10, 75);
        jtA.setBackground(new java.awt.Color(160, 160, 160));
        jtA.setFont(new java.awt.Font("Georgia", 1, 14));
        //jtA.setCaretColor(new java.awt.Color(9,9,9));
        jtA.setForeground(new java.awt.Color(12, 15, 147));
        jtA.setText("Enter your customer ID into the key pad and press the\nENTER button");

        jtfDataE = new JTextField(10);
        jtfDataE.setBackground(new java.awt.Color(224, 224, 224));
        jtfDataE.setHorizontalAlignment(JTextField.RIGHT);
        jtfDataE.setFont(new java.awt.Font("Sylfaen", 1, 14));
        jtfDataE.setForeground(new java.awt.Color(9, 9, 9));

        // Asumiendo que jtfMsn se inicializa aquí o como campo de clase
        jtfMsn = new JTextField(10);
        jtfMsn.setBackground(new java.awt.Color(224, 224, 224));
        jtfMsn.setHorizontalAlignment(JTextField.LEFT);
        jtfMsn.setFont(new java.awt.Font("Georgia", 1, 14 ));
        jtfMsn.setForeground(new java.awt.Color(9, 9, 9));

        jbDisplay = new JButton("Display account balance");
        jbDeposit = new JButton("Make a deposit");
        jbWithdraw = new JButton("Make a withdrawal");

        jbDisplay.setEnabled(false);
        jbDeposit.setEnabled(false);
        jbWithdraw.setEnabled(false);

        jb0 = new JButton("0");
        jb1 = new JButton("1");
        jb2 = new JButton("2");
        jb3 = new JButton("3");
        jb4 = new JButton("4");
        jb5 = new JButton("5");
        jb6 = new JButton("6");
        jb7 = new JButton("7");
        jb8 = new JButton("8");
        jb9 = new JButton("9");
        jbEnter = new JButton("Enter");
        jbClear = new JButton("Clear");
        first = true;

        // Add action listener for the menu button
        menuSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BankAtmSwing.this.windowClosed();
            }
        });
    }

    protected void windowClosed() {
        System.exit(0);
    }

    public void launchFrame() {
        // Keypad Panel
        JPanel pn = new JPanel(new GridLayout(4, 3));
        pn.add(jb1);
        pn.add(jb2);
        pn.add(jb3);
        pn.add(jb4);
        pn.add(jb5);
        pn.add(jb6);
        pn.add(jb7);
        pn.add(jb8);
        pn.add(jb9);
        pn.add(jb0);
        pn.add(jbClear);
        pn.add(jbEnter);

        // Keypad and Data Entry Panel (WEST - Bottom)
        JPanel psw = new JPanel(new BorderLayout());
        psw.add(jtfDataE, BorderLayout.NORTH);
        psw.add(pn, BorderLayout.CENTER);

        // Buttons Panel (WEST - Top)
        JPanel pbot = new JPanel(new GridLayout(3, 1));
        pbot.add(jbDisplay);
        pbot.add(jbDeposit);
        pbot.add(jbWithdraw);

        // WEST Panel (Buttons and Keypad)
        JPanel pwest = new JPanel(new GridLayout(2, 1));
        pwest.add(pbot);
        pwest.add(psw);

        // EAST Panel (Display Area)
        JPanel peast = new JPanel(new BorderLayout());
        jtA.setEnabled(false);
        jtfMsn.setEnabled(false);
        peast.add(jtA, BorderLayout.CENTER);
        peast.add(jtfMsn, BorderLayout.SOUTH);

        // Main Frame setup
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pwest, BorderLayout.WEST);
        getContentPane().add(peast, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);

        // Register listeners
        jb0.addActionListener(this);
        jb1.addActionListener(this);
        jb2.addActionListener(this);
        jb3.addActionListener(this);
        jb4.addActionListener(this);
        jb5.addActionListener(this);
        jb6.addActionListener(this);
        jb7.addActionListener(this);
        jb8.addActionListener(this);
        jb9.addActionListener(this);
        jbEnter.addActionListener(this);
        jbClear.addActionListener(this);
        jbDisplay.addActionListener(this);
        jbDeposit.addActionListener(this);
        jbWithdraw.addActionListener(this);
    }

    /** Handle ActionEvent from buttons */
    public void actionPerformed(ActionEvent e) {
        String buttonStr = e.getActionCommand();
        int clave;
        double amount;

        // Handle button events
        if (e.getSource() instanceof JButton) {

            if ("Display account balance".equals(buttonStr)) {
                jtA.setText(jtA.getText() + "\nDisplay account balance: "
                        + banco.getCustomer(0).getAccount().getBalance());
            } else if ("Make a deposit".equals(buttonStr)) {
                try {
                    amount = Double.parseDouble(jtfDataE.getText());
                } catch (RuntimeException el) {
                    jtfMsn.setText("Please type numeric caracter into the Data Entry and Press deposit Button");
                    return;
                }

                jtA.setText(jtA.getText() + "\nMake a deposit: " + " "
                        + banco.getCustomer(0).getAccount().deposit(amount));
                jtfMsn.setText("deposit Sussesful!");

            } else if ("Make a withdrawal".equals(buttonStr)) {
                try {
                    amount = Double.parseDouble(jtfDataE.getText());
                } catch (RuntimeException el) {
                    jtfMsn.setText("Please type numeric caracter into the Data Entry and Press Withdraw Button");
                    return;
                }

                try {
                    banco.getCustomer(0).getAccount().withdraw(amount);
                } catch (OverdraftException e3) {
                    jtfMsn.setText("Deficit: " + e3.getDeficit() + " " + e3.getMessage()
                            + "Actual balance: " + banco.getCustomer(0).getAccount().getBalance());
                    return;
                }

                jtA.setText(jtA.getText() + "\nMake a withdrawal " + amount);
                jtfMsn.setText("withdraw Sussesful!");

            } else if (buttonStr.equals("0") || buttonStr.equals("1") || buttonStr.equals("2")
                    || buttonStr.equals("3") || buttonStr.equals("4") || buttonStr.equals("5")
                    || buttonStr.equals("6") || buttonStr.equals("7") || buttonStr.equals("8")
                    || buttonStr.equals("9")) {

                jtfDataE.setText(jtfDataE.getText() + buttonStr);
                first = false;

            } else if (buttonStr.equals("Enter")) {
                if (isValido == false) {
                    try {
                        clave = Integer.parseInt(jtfDataE.getText());
                        // Incluir algoritmo de busqueda que debe encontrar al iesimo cliente con el Id dado

                        if (clave == banco.getCustomer(0).getIdCustomer()) {
                            jtA.setText(jtA.getText() + "\nWelcome " + banco.getCustomer(0).getFirstName() + " "
                                    + banco.getCustomer(0).getLastName());
                            jbDisplay.setEnabled(true);
                            jbDeposit.setEnabled(true);
                            jbWithdraw.setEnabled(true);
                            jtfMsn.setText("Su clave esta correcta");
                            jtfDataE.setText("");
                            isValido = true;
                        } else {
                            jtA.setText(jtA.getText() + "\nCustomer Id was not found");
                            jbDisplay.setEnabled(false);
                            jbDeposit.setEnabled(false);
                            jbWithdraw.setEnabled(false);
                            jtfMsn.setText("Su clave esta Incorrecta"); // Corregido el mensaje redundante
                        }
                    } catch (RuntimeException el) {
                        jtA.setText("Enter your customer ID into the key pad and press the ENTER button");
                        jtfMsn.setText("Ingrese su clave");
                        return;
                    }
                } else {
                    if (!jtfDataE.getText().equals("")) { // Usar .equals() para comparar String
                        jtfMsn.setText("Please Select Deposit or Withdraw Button");
                    }
                }

            } else if (buttonStr.equals("Clear")) {
                if (isValido == false) {
                    jtA.setText("Enter your customer ID into the key pad and press the ENTER button");
                }

                jbDisplay.setEnabled(false);
                jbDeposit.setEnabled(false);
                jbWithdraw.setEnabled(false);
                jtfDataE.setText("");
                jtfMsn.setText("");
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        BankAtmSwing batm = new BankAtmSwing("First Java Bank ATM");
        batm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        batm.launchFrame();

        // Modificar para crear los clientes desde el menuItem
        Customer cust = new Customer("Jaime", "Cedeño");
        Account acct = new Account(500.0);
        cust.setAccount(acct);
        cust.setIdCustomer(0220);
        batm.banco.addCustomer(cust);
    }
}