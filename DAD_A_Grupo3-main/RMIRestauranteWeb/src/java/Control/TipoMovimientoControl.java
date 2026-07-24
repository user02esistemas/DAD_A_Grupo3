package Control;
import Interface.ITipoMovimiento;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TipoMovimientoControl {
    public ITipoMovimiento DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public TipoMovimientoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ITipoMovimiento) registry.lookup("tipoMovimiento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
