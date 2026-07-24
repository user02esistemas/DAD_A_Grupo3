package Control;
import Interface.IEstadoMovimiento;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EstadoMovimientoControl {
    public IEstadoMovimiento DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public EstadoMovimientoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IEstadoMovimiento) registry.lookup("estadoMovimiento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
