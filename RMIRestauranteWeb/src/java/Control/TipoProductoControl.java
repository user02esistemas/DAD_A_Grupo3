package Control;
import Interface.ITipoProducto;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TipoProductoControl {
    public ITipoProducto DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public TipoProductoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ITipoProducto) registry.lookup("tipoProducto");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
