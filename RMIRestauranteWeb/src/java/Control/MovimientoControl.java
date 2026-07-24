package Control;
import Interface.IMovimiento;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MovimientoControl {
    public IMovimiento DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MovimientoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMovimiento) registry.lookup("movimiento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
