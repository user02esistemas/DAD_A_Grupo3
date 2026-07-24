package Control;
import Interface.IMovimientoMesa;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MovimientoMesaControl {
    public IMovimientoMesa DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MovimientoMesaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMovimientoMesa) registry.lookup("movimientoMesa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
