package Control;
import Interface.IMozo;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MozoControl {
    public IMozo DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MozoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMozo) registry.lookup("mozo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
