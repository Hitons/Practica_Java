package producto_consumidor_buffer;

public class Buffer {

    private int capacidad = 10;
    private int pila[] = new int[capacidad];
    private int puntero = -1;
    private boolean estaLleno = false;
    private boolean estaVacio = true;

    public synchronized int lee() {
        // System.out.println("LEE - ptro: "+puntero+" - vacio: "+estaVacio );
        while (estaVacio) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupción del hilo en lee...");
                return -1;
            }
        }
        int num = pila[puntero];
        pila[puntero] = 0;
        puntero--;
        if (puntero < 0) estaVacio = true;
        estaLleno = false;
        // notifyAll en lugar de notify
        notifyAll();
        return num;
    }

    public synchronized void escribe(int num) {
        // System.out.println("ESCRIBE - ptro: "+puntero+" - lleno: "+estaLleno );
        while (estaLleno) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupción del hilo en escribe...");
                return;
            }
        }
        puntero++;
        pila[puntero] = num;
        if (puntero == capacidad - 1) estaLleno = true;
        estaVacio = false;
        notifyAll();
    }

    // Método para obtener una representación del buffer (snapshot) para la GUI
    public synchronized String snapshot() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacidad; i++) {
            sb.append(pila[i]);
            if (i < capacidad - 1) sb.append(" ");
        }
        return sb.toString();
    }
}

