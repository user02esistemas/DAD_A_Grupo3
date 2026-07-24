package Control;
import Interface.IMesaGrupo;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MesaGrupoControl {
    public IMesaGrupo DATOS;
    private final String SERVER = "127.0.0.1";
    private final int PORT = 3239;

    public MesaGrupoControl() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER, PORT);
            DATOS = (IMesaGrupo) registry.lookup("mesaGrupo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
