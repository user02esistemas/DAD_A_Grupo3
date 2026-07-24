package Control;
import Interface.ITipoCliente;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TipoClienteControl {
    public ITipoCliente DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public TipoClienteControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ITipoCliente) registry.lookup("tipoCliente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
