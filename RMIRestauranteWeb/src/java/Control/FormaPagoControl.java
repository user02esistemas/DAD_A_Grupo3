package Control;
import Interface.IFormaPago;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FormaPagoControl {
    public IFormaPago DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public FormaPagoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IFormaPago) registry.lookup("formaPago");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
