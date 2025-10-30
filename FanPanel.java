package abanico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

// Enum para la dirección de giro
enum Direction {
    CLOCKWISE(1),
    COUNTER_CLOCKWISE(-1);

    final int multiplier;
    Direction(int multiplier) {
        this.multiplier = multiplier;
    }
}

public class FanPanel extends JPanel implements Runnable {

    private final int FAN_BLADES = 4;
    private int startAngle = 0;
    private int delay = 100; // Velocidad inicial (Mid)
    private volatile boolean running = false;
    private Direction direction = Direction.CLOCKWISE;

    private JRadioButton lowButton;
    private JRadioButton midButton;
    private JRadioButton highButton;
    private JButton stopButton;
    private JButton reverseButton;

    private Thread fanThread;

    public FanPanel() {
        this.setPreferredSize(new Dimension(200, 250));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Panel de Controles
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        lowButton = new JRadioButton("Low");
        midButton = new JRadioButton("Mid", true);
        highButton = new JRadioButton("High");
        stopButton = new JButton("Stop");
        reverseButton = new JButton("Reverse");

        ButtonGroup speedGroup = new ButtonGroup();
        speedGroup.add(lowButton);
        speedGroup.add(midButton);
        speedGroup.add(highButton);

        controlPanel.add(lowButton);
        controlPanel.add(midButton);
        controlPanel.add(highButton);
        controlPanel.add(stopButton);
        controlPanel.add(reverseButton);

        this.add(controlPanel, BorderLayout.NORTH);

        // Configuración de Listeners
        lowButton.addActionListener(e -> setSpeed(125));
        midButton.addActionListener(e -> setSpeed(50));
        highButton.addActionListener(e -> setSpeed(25));

        // *** Lógica para alternar STOP y RESUME ***
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopButton.getText().equals("Stop")) {
                    stopFan();
                } else {
                    startFan(); // Si es "Resume", llama a startFan() para reanudar.
                }
            }
        });

        reverseButton.addActionListener(e -> reverseDirection());

        startFan(); // Iniciar el hilo del ventilador por defecto
    }

    // --- Métodos de Control ---

    // Al seleccionar una velocidad, si está detenido, lo reanuda.
    private void setSpeed(int newDelay) {
        this.delay = newDelay;
        if (!running) {
            startFan();
        }
    }

    private void reverseDirection() {
        direction = (direction == Direction.CLOCKWISE) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;
    }

    // Mueve la lógica de reanudar y habilitar controles aquí
    public synchronized void startFan() {
        if (!running) {
            running = true;
            if (fanThread == null || !fanThread.isAlive()) {
                fanThread = new Thread(this);
                fanThread.start();
            }

            // Re-habilita todos los controles al reanudar
            lowButton.setEnabled(true);
            midButton.setEnabled(true);
            highButton.setEnabled(true);
            reverseButton.setEnabled(true);
            stopButton.setText("Stop");
        }
    }

    // Mueve la lógica de detener y deshabilitar controles aquí
    public synchronized void stopFan() {
        running = false;

        // Deshabilita los botones de velocidad/Reverse
        lowButton.setEnabled(false);
        midButton.setEnabled(false);
        highButton.setEnabled(false);
        reverseButton.setEnabled(false);

        stopButton.setText("Resume"); // Cambia el texto para que pueda ser reanudado
    }

    // --- Lógica del Hilo (Runnable) ---

    @Override
    public void run() {
        while (true) {
            try {
                if (running) {
                    startAngle = (startAngle + 5 * direction.multiplier) % 360;
                    repaint();
                    Thread.sleep(delay);
                } else {
                    // Espera segura cuando está detenido
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    // --- Lógica de Dibujo (Graphics) ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();
        int center_x = w / 2;
        int center_y = h / 2 + 20;
        int radius = (Math.min(w, h) / 2) - 30;

        // 1. Dibuja la cuadrícula roja y gris
        g2d.setColor(Color.RED);
        g2d.drawRect(center_x - radius, center_y - radius, 2 * radius, 2 * radius);

        g2d.setColor(Color.LIGHT_GRAY);
        int gridSize = 2 * radius / 6;
        for (int i = 1; i < 6; i++) {
            g2d.drawLine(center_x - radius + i * gridSize, center_y - radius,
                    center_x - radius + i * gridSize, center_y + radius);
            g2d.drawLine(center_x - radius, center_y - radius + i * gridSize,
                    center_x + radius, center_y - radius + i * gridSize);
        }

        // 2. Dibuja las aspas azules
        g2d.setColor(Color.BLUE);
        for (int i = 0; i < FAN_BLADES; i++) {
            int angle = startAngle + i * (360 / FAN_BLADES);

            g2d.fill(new Arc2D.Double(
                    center_x - radius, center_y - radius,
                    2 * radius, 2 * radius,
                    angle, 60, Arc2D.PIE
            ));
        }

        // 3. Dibuja el eje central
        g2d.setColor(Color.BLACK);
        g2d.fillOval(center_x - 5, center_y - 5, 10, 10);
    }
}