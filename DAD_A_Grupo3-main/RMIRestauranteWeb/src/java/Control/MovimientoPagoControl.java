package Control;
import Interface.IMovimientoPago;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MovimientoPagoControl {
    public IMovimientoPago DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MovimientoPagoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMovimientoPago) registry.lookup("movimientoPago");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
