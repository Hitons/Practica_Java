package PC_FabricaDePapel;



import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class FabricaGUI extends JFrame {

    // Componentes de la GUI
    private JTextField txtCantCajas;
    private JTextField txtCantPapel;
    private JButton btnIniciar;
    private JButton btnDetener;
    private JTextArea areaProceso;

    // Lógica de la Fábrica
    private FabricaDePapelConSemaforos fabrica;

    public FabricaGUI() {
        super("Fábrica de Papel con Semáforos");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Usamos BorderLayout para las secciones NORTH (Inputs) y CENTER (Botones y Output)
        this.setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
        colocarComponentes();
        redirigirSalida(); // Inicializa la redirección de System.out al JTextArea
        configurarEventos();

        this.pack();
        this.setLocationRelativeTo(null);
    }

    // --- Lógica de la Redirección de System.out ---
    private void redirigirSalida() {
        // Creamos nuestro flujo de salida especial, apuntando al areaProceso
        OutputStream outputStream = new TextAreaOutputStream(areaProceso);
        System.setOut(new PrintStream(outputStream));
        System.out.println("[SISTEMA] Bienvenido. Ingrese las cantidades.");
    }

    private void inicializarComponentes() {
        txtCantCajas = new JTextField(5);
        txtCantPapel = new JTextField(5);
        btnIniciar = new JButton("Iniciar Simulación");
        btnDetener = new JButton("Detener Simulación");
        btnDetener.setEnabled(false);
        areaProceso = new JTextArea(15, 60);
        areaProceso.setEditable(false);
    }

    private void colocarComponentes() {
        // --- SECCIÓN NORTE (Inputs) ---
        JPanel panelNorth = new JPanel(new GridLayout(2, 2, 5, 5));
        panelNorth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelNorth.add(new JLabel("Cantidad de Cajas a fabricar:"));
        panelNorth.add(txtCantCajas);
        panelNorth.add(new JLabel("Cantidad de Papel Por Caja:"));
        panelNorth.add(txtCantPapel);
        this.add(panelNorth, BorderLayout.NORTH);

        // --- SECCIÓN CENTRAL (Botones y Área de Texto) ---
        // Usamos un JPanel con BoxLayout para apilar botones y JTextArea
        JPanel panelCenterContainer = new JPanel();
        panelCenterContainer.setLayout(new BoxLayout(panelCenterContainer, BoxLayout.Y_AXIS));

        // 1. Botones
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelButtons.add(btnIniciar);
        panelButtons.add(btnDetener);

        // 2. Área de Proceso
        JScrollPane scrollPane = new JScrollPane(areaProceso);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registro de Proceso"));

        // Apilar botones y JTextArea
        panelCenterContainer.add(panelButtons);
        panelCenterContainer.add(scrollPane);

        this.add(panelCenterContainer, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarSimulacion());

        btnDetener.addActionListener(e -> {
            // Detener la simulación de forma inmediata
            FabricaDePapelConSemaforos.running = false;
            System.out.println("\n[SISTEMA] --- SIMULACIÓN DETENIDA POR EL USUARIO ---");
            // Reiniciar botones
            resetControls();
        });
    }

    private void resetControls() {
        // Habilitar la entrada de datos en el Hilo de Eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            txtCantCajas.setEnabled(true);
            txtCantPapel.setEnabled(true);
            btnIniciar.setEnabled(true);
            btnDetener.setEnabled(false);
        });
    }

    private void iniciarSimulacion() {
        int cantCajas = 0;
        int cantPapel = 0;

        try {
            // CAPTURA Y VALIDACIÓN
            cantCajas = Integer.parseInt(txtCantCajas.getText());
            cantPapel = Integer.parseInt(txtCantPapel.getText());

            if (cantCajas <= 0 || cantPapel <= 0) {
                throw new Exception("Error: La cantidad debe ser un número entero mayor que cero.");
            }

            // Si todo es válido:
            System.out.println("\n--- SIMULACIÓN INICIADA ---");
            System.out.println("[SISTEMA] Cajas a fabricar: " + cantCajas + ", Papel/Caja: " + cantPapel);

            // INICIAMOS LA FÁBRICA
            fabrica = new FabricaDePapelConSemaforos(cantCajas, cantPapel);
            fabrica.start();

            // INICIAMOS EL MONITOR DE ESTADO
            startStatusMonitor();

            // Deshabilitar/Habilitar Controles
            txtCantCajas.setEnabled(false);
            txtCantPapel.setEnabled(false);
            btnIniciar.setEnabled(false);
            btnDetener.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: Ingrese números enteros válidos.",
                    "Error de Entrada", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startStatusMonitor() {
        // Hilo monitor que espera a que la simulación termine
        new Thread(() -> {
            try {
                // Esperar hasta que el grupo de hilos termine O la bandera 'running' sea false
                // Se usa g1.activeGroupCount() para las Personas
                while (fabrica.g1.activeGroupCount() > 0 && FabricaDePapelConSemaforos.running) {
                    Thread.sleep(250);
                }

                // Esperar a que el supervisor (consumer2) termine si aún está activo y no fue detenido por el usuario
                if (fabrica.consumer2.isAlive() && FabricaDePapelConSemaforos.running) {
                    fabrica.consumer2.join(2000); // Esperar un poco
                }

                // Pequeña pausa final para asegurar la impresión de mensajes
                Thread.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Si se detuvo por el usuario, salimos.
            if (!FabricaDePapelConSemaforos.running) {
                return;
            }

            // Si llegó hasta aquí, terminó naturalmente: mostramos resultados.
            displayFinalStatistics();

        }, "FactoryStatusMonitor").start();
    }

    private void displayFinalStatistics() {
        // Aseguramos que la impresión se haga en el hilo de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            if (fabrica != null && fabrica.cajita != null) {
                System.out.println("\n--- ESTADÍSTICAS FINALES ---");
                System.out.println("Caja Actual tiene:  " + fabrica.cajita.getCantPapelActual());
                System.out.println("Cantidad de cajas llenas:  " + fabrica.cajita.getCantCajaActual());
                System.out.println("Cantidad Maxima de cajas:  " + fabrica.cajita.getMaxCantCajas());
                System.out.println("Cantidad Total de Papel:  " + fabrica.cajita.getCantTotalPapel());
                System.out.println("Cantidad MAXIMA de Papel POR CAJA:  " + fabrica.cajita.getCantMaxPapel());
                System.out.println("-----------------------------\n");
            }
            // Habilita los botones de nuevo
            resetControls();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FabricaGUI().setVisible(true);
        });
    }

    /**
     * Clase auxiliar para redirigir System.out a un JTextArea.
     */
    private static class TextAreaOutputStream extends OutputStream {
        private final JTextArea textArea;

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            write(new byte[]{(byte) b}, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            final String text = new String(b, off, len);

            // Modificamos el JTextArea en el hilo de Swing (EDT)
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                // Auto-scroll
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}