/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PC_FabricaDePapel;

import java.util.concurrent.*;


public class Supervisor extends Thread
{
    private Caja caja;
    private int id;
    private int i=0;
    private final Semaphore s;

    public Supervisor(Semaphore s,Caja caja, int id)
    {
        this.s=s;
        this.caja = caja;
        this.id=id;
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
                while(caja.isNoTengo())
                {
                    if (caja.getCantCajaActual() != caja.getMaxCantCajas())
                    {
                        try
                        {
                            caja.wait(50);
                        }catch(InterruptedException e){}
                        finally{
                            s.release();
                        }
                    }
                    break;
                }
            }catch(Exception ex){}
            finally{
                s.release();
            }

            //Consumer try to take the box
            try
            {
                tiempo = (int) (Math.random()* 1 + 1);
                Thread.sleep(tiempo);
            }catch(InterruptedException e){}

            if( caja.getCantPapelActual() == caja.getCantMaxPapel())
            {
                caja.setNoTengo(false);

                if (caja.getCantCajaActual() != caja.getMaxCantCajas())
                {
                    try {
                        s.acquire();
                        quitarCaja();
                        System.out.println("Supervisor: " + id + " Quita la Cajeta:" + caja.getCantCajaActual());

                    }catch(Exception ex){
                        System.out.println("[ERROR] Supervisor no pudo adquirir semáforo: " + ex.getMessage());
                    }
                    finally {
                        s.release();
                        caja.setNoTengo(true);
                    }
                }else{
                    break;
                }
            }
        } // fin del while
    } // fin del run()

    public synchronized void quitarCaja()
    {
        caja.setCantPapelActual(0);
        caja.setCantCajaActual(++i);
    }
}