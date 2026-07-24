package Control;
import Interface.IEstadoMesa;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EstadoMesaControl {
    public IEstadoMesa DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public EstadoMesaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IEstadoMesa) registry.lookup("estadoMesa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
