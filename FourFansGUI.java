package abanico;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FourFansGUI extends JFrame {

    private List<FanPanel> fans = new ArrayList<>();

    public FourFansGUI(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel para los 4 ventiladores en una cuadrícula de 2x2
        JPanel fansPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 10px de espacio entre ellos
        fansPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Inicializar los 4 ventiladores
        for (int i = 0; i < 4; i++) {
            FanPanel fan = new FanPanel();
            fans.add(fan);
            fansPanel.add(fan);
        }

        // Panel de Controles Globales (en la parte inferior)
        JPanel globalControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton resumeAllButton = new JButton("Resume All");
        JButton suspendAllButton = new JButton("Suspend All");

        globalControlPanel.add(resumeAllButton);
        globalControlPanel.add(suspendAllButton);
        globalControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Configuración de Listeners Globales
        resumeAllButton.addActionListener(e -> {
            for (FanPanel fan : fans) {
                fan.startFan();
            }
        });

        suspendAllButton.addActionListener(e -> {
            for (FanPanel fan : fans) {
                fan.stopFan();
            }
        });

        // Añadir paneles al frame principal
        add(fansPanel, BorderLayout.CENTER);
        add(globalControlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Centrar en la pantalla
        setVisible(true);
    }

    public static void main(String[] args) {
        // Ejecutar en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            new FourFansGUI("FOUR FANS");
        });
    }
}