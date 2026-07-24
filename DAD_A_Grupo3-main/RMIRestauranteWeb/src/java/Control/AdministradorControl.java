package Control;
import Interface.IAdministrador;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AdministradorControl {
    public IAdministrador DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public AdministradorControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IAdministrador) registry.lookup("administrador");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
