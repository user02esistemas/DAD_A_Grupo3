package Control;
import Interface.IReservaBuffet;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReservaBuffetControl {
    public IReservaBuffet DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public ReservaBuffetControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IReservaBuffet) registry.lookup("reservaBuffet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
