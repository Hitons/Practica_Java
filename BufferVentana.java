package producto_consumidor_buffer;

import javax.swing.*;
import java.awt.*;

public class BufferVentana extends JFrame {

    private JTextField txtProductor, txtBuffer, txtConsumidor;
    private JButton btnSuspender, btnReanudar, btnIniciar;
    private volatile boolean suspended = false;
    private final Object pauseLock = new Object();

    private Buffer buffer;

    public BufferVentana() {
        super("producto_consumidor.Buffer Ventana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitulo = new JLabel("- Productor/Consumidor con producto_consumidor.Buffer -", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(lblTitulo, gbc);

        btnIniciar = new JButton("Iniciar");
        btnSuspender = new JButton("Suspender");
        btnReanudar = new JButton("Reanudar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnIniciar);
        panelBotones.add(btnSuspender);
        panelBotones.add(btnReanudar);
        gbc.gridy = 1;
        add(panelBotones, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Productor:"), gbc);
        txtProductor = new JTextField("-"); txtProductor.setEditable(false); txtProductor.setColumns(30);
        gbc.gridx = 1; gbc.gridwidth = 2; add(txtProductor, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("producto_consumidor.Buffer:"), gbc);
        txtBuffer = new JTextField("0 0 0 0 0 0 0 0 0 0"); txtBuffer.setEditable(false);
        gbc.gridx = 1; gbc.gridwidth = 2; add(txtBuffer, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("Consumidor:"), gbc);
        txtConsumidor = new JTextField("-"); txtConsumidor.setColumns(30);txtConsumidor.setEditable(false);
        gbc.gridx = 1; gbc.gridwidth = 3; add(txtConsumidor, gbc);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // acciones botones
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnSuspender.addActionListener(e -> suspender());
        btnReanudar.addActionListener(e -> reanudar());
    }

    private void suspender() {
        suspended = true;
        SwingUtilities.invokeLater(() -> {
            txtProductor.setText("Producción suspendida...");
            txtConsumidor.setText("Consumo suspendido...");
        });
    }

    private void reanudar() {
        synchronized (pauseLock) {
            suspended = false;
            pauseLock.notifyAll();
        }
    }

    private void iniciarSimulacion() {
        btnIniciar.setEnabled(false);
        buffer = new Buffer();

        // Crea y arranca hilos (puedes crear varios si quieres)
        Thread p1 = new Thread(new ProducerRunnable(buffer, 1));
        Thread c1 = new Thread(new ConsumerRunnable(buffer, 1));
        p1.start();
        c1.start();
    }

    // Runnable productor que actualiza GUI
    private class ProducerRunnable implements Runnable {
        private Buffer buf;
        private int id;
        public ProducerRunnable(Buffer b, int id) { this.buf = b; this.id = id; }
        public void run() {
            for (int i = 1; i <= 25; i++) {
                // Pause handling
                synchronized (pauseLock) {
                    while (suspended) {
                        try { pauseLock.wait(); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return; }
                    }
                }

                buf.escribe(i);
                final int val = i;
                SwingUtilities.invokeLater(() -> {
                    txtProductor.setText("Productor " + id + " pone: " + val);
                    txtBuffer.setText(buf.snapshot());
                });

                try {
                    Thread.sleep((int)(Math.random() * 700));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        }
    }

    // Runnable consumidor que actualiza GUI
    private class ConsumerRunnable implements Runnable {
        private Buffer buf;
        private int id;
        public ConsumerRunnable(Buffer b, int id) { this.buf = b; this.id = id; }
        public void run() {
            for (int i = 1; i <= 25; i++) {
                synchronized (pauseLock) {
                    while (suspended) {
                        try { pauseLock.wait(); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return; }
                    }
                }

                int val = buf.lee();
                final int v = val;
                SwingUtilities.invokeLater(() -> {
                    txtConsumidor.setText("Consumidor " + id + " toma: " + v);
                    txtBuffer.setText(buf.snapshot());
                });

                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BufferVentana::new);
    }
}
