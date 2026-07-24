package Interface;
import DTO.DatosEnvio;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IDatosEnvio extends Remote {
    List<DatosEnvio> listar() throws RemoteException;
    boolean insertar(DatosEnvio obj) throws RemoteException;
    boolean actualizar(DatosEnvio obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    DatosEnvio buscar(int id) throws RemoteException;
}
