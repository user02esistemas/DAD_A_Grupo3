package Control;
import Interface.IProducto;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ProductoControl {
    public IProducto DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public ProductoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IProducto) registry.lookup("producto");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
