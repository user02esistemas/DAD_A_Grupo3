package Control;
import Interface.IEstadoPresentacion;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EstadoPresentacionControl {
    public IEstadoPresentacion DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public EstadoPresentacionControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IEstadoPresentacion) registry.lookup("estadoPresentacion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
