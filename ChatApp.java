package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatApp extends JFrame implements ActionListener, KeyListener {

    // --- Componentes de la GUI ---
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String userName;

    // --- Configuración de Sockets ---
    private final int PORT = 5000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerSocket serverSocket; // Necesario si esta instancia actúa como servidor

    /**
     * Constructor de la aplicación de chat.
     * @param name El nombre de usuario para esta instancia.
     */
    public ChatApp(String name) {
        // 1. Configuración inicial de la ventana
        super("Chat Simulador - " + name);
        this.userName = name;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLayout(new BorderLayout());

        // 2. Creación de componentes
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField(30);
        sendButton = new JButton("Enviar");

        // 3. Panel inferior (Entrada de texto y Botón)
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 4. Añadir componentes a la ventana principal
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // 5. Asignar Listeners (Manejadores de Eventos)
        sendButton.addActionListener(this);
        inputField.addKeyListener(this);

        // 6. Mostrar la ventana y establecer la conexión de red
        setVisible(true);
        // Intentar conectar como Cliente o iniciar como Servidor
        tryConnect();
    }

    // ----------------------------------------------------------------------
    // --- MÉTODOS AUXILIARES ---
    // ----------------------------------------------------------------------

    /**
     * Método auxiliar para agregar mensajes al JTextArea, usado para mensajes propios y del sistema.
     */
    private void appendMessage(String sender, String message) {
        chatArea.append("[" + sender + "]: " + message + "\n");
    }

    /**
     * Procesa el texto de entrada, lo añade al chat y lo envía por el socket.
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // 1. Mostrar en mi propio historial
            appendMessage(userName, message);

            // 2. Enviar a la otra instancia (a través del socket)
            if (out != null) {
                // Enviamos el nombre junto con el mensaje
                out.println(userName + ": " + message);
            } else {
                appendMessage("Sistema", "Error: No hay conexión para enviar.");
            }

            // 3. Limpiar el campo de entrada
            inputField.setText("");
            inputField.requestFocusInWindow(); // Mantiene el foco para seguir escribiendo
        }
    }

    // ----------------------------------------------------------------------
    // --- LÓGICA DE CONEXIÓN DE RED (SOCKETS) ---
    // ----------------------------------------------------------------------

    /**
     * Intenta conectar como Cliente o inicia como Servidor.
     */
    private void tryConnect() {
        // Intentar ser Cliente y conectarse al puerto 5000
        try {
            socket = new Socket("localhost", PORT);
            setupStreams(socket);
            new Thread(new IncomingMessageHandler()).start();
            appendMessage("Sistema", "Conectado como Cliente.");
            return; // Conexión exitosa como cliente
        } catch (IOException e) {
            // Si falla, intentar ser Servidor
            try {
                serverSocket = new ServerSocket(PORT);
                appendMessage("Sistema", "Esperando conexión como Servidor en puerto " + PORT + "...");

                // Esperar la conexión en un nuevo hilo para no bloquear la GUI
                new Thread(() -> {
                    try {
                        socket = serverSocket.accept(); // Espera y acepta la conexión
                        setupStreams(socket);
                        SwingUtilities.invokeLater(() -> appendMessage("Sistema", "Cliente conectado."));
                        new Thread(new IncomingMessageHandler()).start();
                    } catch (IOException ex) {
                        SwingUtilities.invokeLater(() -> appendMessage("Sistema", "Error de servidor al aceptar: " + ex.getMessage()));
                    }
                }).start();
            } catch (IOException ex) {
                appendMessage("Sistema", "ERROR: No se pudo conectar ni iniciar el Servidor. Puerto " + PORT + " ocupado.");
            }
        }
    }

    // Configura los flujos de lectura y escritura del Socket
    private void setupStreams(Socket s) throws IOException {
        out = new PrintWriter(s.getOutputStream(), true); // 'true' para auto-flush
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    /**
     * Clase interna (Inner Class) para escuchar mensajes entrantes. 
     * **Esta definición resuelve el error 'Cannot resolve symbol 'IncomingMessageHandler'**
     */
    private class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            String line;
            try {
                // Lee constantemente mensajes del socket
                while ((line = in.readLine()) != null) {
                    final String receivedMessage = line;
                    // Actualiza la GUI en el hilo de eventos (EDT)
                    SwingUtilities.invokeLater(() -> appendMessageFromOther(receivedMessage));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> appendMessage("Sistema", "Conexión perdida con el otro usuario."));
            }
        }

        // Método que separa el nombre del mensaje recibido y lo añade al chatArea
        private void appendMessageFromOther(String fullMessage) {
            // El formato es "Nombre: Mensaje"
            String[] parts = fullMessage.split(": ", 2);
            String sender = parts.length > 0 ? parts[0] : "Desconocido";
            String message = parts.length > 1 ? parts[1] : fullMessage;

            chatArea.append("[" + sender + "]: " + message + "\n");
        }
    }

    // ----------------------------------------------------------------------
    // --- IMPLEMENTACIÓN DE LISTENERS (Manejo de GUI) ---
    // ----------------------------------------------------------------------

    // ActionListener: Para el botón "Enviar"
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            sendMessage();
        }
    }

    // KeyListener: Para la tecla Enter en el campo de texto
    @Override
    public void keyPressed(KeyEvent e) {
        // Si el foco está en inputField y se presiona ENTER
        if (e.getSource() == inputField && e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    // Métodos de KeyListener que no usamos, pero debemos implementar
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    // ----------------------------------------------------------------------
    // --- MÉTODO PRINCIPAL ---
    // ----------------------------------------------------------------------

    public static void main(String[] args) {
        // Si no se proporciona un nombre, usamos "Usuario Genérico"
        String defaultName = (args.length > 0) ? args[0] : "Usuario Genérico";

        // Asegura que la GUI se ejecute en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new ChatApp(defaultName));
    }
}