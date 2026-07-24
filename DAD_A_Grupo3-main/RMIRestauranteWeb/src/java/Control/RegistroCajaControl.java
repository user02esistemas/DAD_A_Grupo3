package Control;
import Interface.IRegistroCaja;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistroCajaControl {
    public IRegistroCaja DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public RegistroCajaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IRegistroCaja) registry.lookup("registroCaja");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
