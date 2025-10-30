/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PC_FabricaDePapel;


import java.util.concurrent.*;

public class FabricaDePapelConSemaforos extends Thread
{
    // Bandera estática para permitir que la GUI detenga todos los hilos
    public static volatile boolean running = true;

    Caja cajita;
    Thread [] thread;
    Persona persona [];
    ThreadGroup g1;
    Supervisor supervisor;
    Thread consumer2;
    int cantPapel;
    int cantCajas;

    static Semaphore sem = new Semaphore(3,true);
    static Semaphore mutex = new Semaphore(1,true);

    // CONSTRUCTOR MODIFICADO: Recibe los valores de la GUI
    public FabricaDePapelConSemaforos(int cantCajas, int cantPapel)
    {
        // Reiniciar la bandera cada vez que se crea una nueva simulación
        running = true;
        this.cantCajas = cantCajas;
        this.cantPapel = cantPapel;

        cajita = new Caja(cantCajas,cantPapel);

        // La Caja usa variables estáticas, debemos reiniciarlas al crear una nueva simulación.
        // Asumiendo que Caja tiene setters públicos o que la lógica estática se maneja correctamente.
        // Si no, se necesita una función estática de limpieza en Caja.
        cajita.setCantCajaActual(0);
        cajita.setCantPapelActual(0);


        g1 = new ThreadGroup("t");
        thread = new Thread[3];
        persona  = new  Persona[3];
        supervisor = new Supervisor(mutex,cajita, 4);
        consumer2 = new Thread(g1,supervisor ,"t");
        consumer2.setDaemon(true);
        consumer2.start();
    }

    public void run()
    {
        try{
            for (int i= 0; i < 3; i++)
            {
                persona[i] = new Persona(sem,cajita,i + 1);
                thread[i] = new Thread (g1,persona[i],"t");
                thread[i].start();
                thread[i].join(5);
            }
        }catch(InterruptedException e){}
    }
}