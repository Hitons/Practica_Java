/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PC_FabricaDePapel;


import java.util.concurrent.*;

public class Persona extends Thread
{
    private Caja caja;
    private int id;
    private final Semaphore s;

    public Persona(Semaphore s,Caja caja, int id)
    {
        this.s=s;
        this.caja = caja;
        this.id = id;
    }

    public void run()
    {
        int tiempo = 0;

        // CAMBIO CLAVE: Usamos la bandera estática para detenernos si la GUI lo pide
        while(FabricaDePapelConSemaforos.running)
        {
            if (caja.getCantCajaActual() >= caja.getMaxCantCajas()) {
                break; // Condición original de detención por caja llena
            }

            try
            {
                s.acquire();
                while(!caja.isNoTengo())
                {
                    try
                    {
                        caja.wait(50);
                    }catch(InterruptedException e){}
                    break;
                }
            }catch(Exception ex){}
            finally{
                s.release();
            }

            // Produce the paper
            if (caja.getCantCajaActual() != caja.getMaxCantCajas())
            {
                try
                {
                    tiempo = (int) (Math.random()* 100 + 1);
                    Thread.sleep(tiempo);
                }catch(InterruptedException e){}

                if(caja.getCantPapelActual() < caja.getCantMaxPapel() && caja.getCantCajaActual() != caja.getMaxCantCajas())
                {
                    try
                    {
                        s.acquire();
                        caja.setNoTengo(false);
                        addPapel();
                        System.out.println("Id: "+ id + " Puso papel: "+
                                caja.getCantPapelActual());
                    }catch(Exception ex){}
                    finally {
                        s.release();
                    }
                }
            } else{	break;
            }
        } //fin del while
    }

    public synchronized void addPapel()
    {
        caja.agregarPapel();
    }
}