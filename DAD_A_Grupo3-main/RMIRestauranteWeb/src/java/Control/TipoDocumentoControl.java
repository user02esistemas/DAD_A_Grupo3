package Control;
import Interface.ITipoDocumento;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TipoDocumentoControl {
    public ITipoDocumento DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public TipoDocumentoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (ITipoDocumento) registry.lookup("tipoDocumento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
