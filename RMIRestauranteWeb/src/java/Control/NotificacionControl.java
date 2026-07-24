package Control;
import Interface.INotificacion;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NotificacionControl {
    public INotificacion DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public NotificacionControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (INotificacion) registry.lookup("notificacion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
