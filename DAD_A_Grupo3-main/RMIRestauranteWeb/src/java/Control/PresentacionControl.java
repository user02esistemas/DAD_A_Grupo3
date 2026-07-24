package Control;
import Interface.IPresentacion;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PresentacionControl {
    public IPresentacion DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public PresentacionControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IPresentacion) registry.lookup("presentacion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
