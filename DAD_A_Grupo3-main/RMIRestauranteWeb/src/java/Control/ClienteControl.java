package Control;
import Interface.ICliente;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClienteControl {
    public ICliente DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public ClienteControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ICliente) registry.lookup("cliente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
