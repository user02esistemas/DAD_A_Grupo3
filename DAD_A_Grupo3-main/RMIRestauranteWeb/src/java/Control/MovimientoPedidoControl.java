package Control;
import Interface.IMovimientoPedido;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MovimientoPedidoControl {
    public IMovimientoPedido DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MovimientoPedidoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMovimientoPedido) registry.lookup("movimientoPedido");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
