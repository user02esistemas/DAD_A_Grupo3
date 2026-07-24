package Control;
import Interface.ISalon;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SalonControl {
    public ISalon DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public SalonControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ISalon) registry.lookup("salon");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
