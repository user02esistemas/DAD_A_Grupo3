package Control;
import Interface.IEstadoComanda;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EstadoComandaControl {
    public IEstadoComanda DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public EstadoComandaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IEstadoComanda) registry.lookup("estadoComanda");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
