package Control;
import Interface.IMesa;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MesaControl {
    public IMesa DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MesaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMesa) registry.lookup("mesa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
