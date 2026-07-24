package Control;
import Interface.IDatosEnvio;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DatosEnvioControl {
    public IDatosEnvio DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public DatosEnvioControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IDatosEnvio) registry.lookup("datosEnvio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
