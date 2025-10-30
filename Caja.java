
package PC_FabricaDePapel;


public class Caja
{
    boolean noTengo=false;
    private int cantTotalPapel; // Variable de instancia

    // Variables estáticas (compartidas por todas las instancias)
    private static int cantPapelActual;
    private static int cantCajaActual;
    private static int cantMaxCaja;
    private static int cantMaxPapel;

    public Caja(int cantMaxCaja, int cantMaxPapel)
    {
        // Se establecen las capacidades
        Caja.cantMaxCaja = cantMaxCaja;
        Caja.cantMaxPapel = cantMaxPapel;
        // La cuenta total de papel es por esta instancia/simulación
        this.cantTotalPapel = 0;
    }

    // MÉTODO AGREGADO: Resetea los contadores estáticos
    public static void resetStaticValues() {
        cantPapelActual = 0;
        cantCajaActual = 0;
        // cantTotalPapel es de instancia, no se resetea aquí.
    }

    public synchronized boolean isNoTengo()
    {
        return noTengo;
    }

    public synchronized void setNoTengo(boolean b)
    {
        noTengo = b;
    }

    public synchronized void agregarPapel()
    {
        cantPapelActual++;
        cantTotalPapel++;
    }

    public synchronized int getCantPapelActual()
    {
        return(cantPapelActual);
    }

    public synchronized void setCantPapelActual(int cant)
    {
        cantPapelActual=cant;
    }

    public synchronized int getCantMaxPapel()
    {
        return(cantMaxPapel);
    }

    public synchronized void setCantMaxPapel(int cant)
    {
        cantMaxPapel=cant;
    }

    public synchronized int getCantCajaActual()
    {
        return(cantCajaActual);
    }

    public synchronized void setCantCajaActual(int cant)
    {
        cantCajaActual=cant;
    }

    public synchronized int getMaxCantCajas()
    {
        return(cantMaxCaja);
    }

    public synchronized void setMaxCantCajas(int cant)
    {
        cantMaxCaja=cant;
    }

    public synchronized int getCantTotalPapel()
    {
        return(cantTotalPapel);
    }

}