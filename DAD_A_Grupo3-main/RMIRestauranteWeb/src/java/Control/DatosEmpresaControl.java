package Control;
import Interface.IDatosEmpresa;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DatosEmpresaControl {
    public IDatosEmpresa DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public DatosEmpresaControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IDatosEmpresa) registry.lookup("datosEmpresa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
