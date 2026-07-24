package Interface;
import DTO.Mozo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMozo extends Remote {
    Mozo login(String usuario, String clave) throws RemoteException;
    List<Mozo> listar() throws RemoteException;
    boolean insertar(Mozo obj) throws RemoteException;
    boolean actualizar(Mozo obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    Mozo buscar(int id) throws RemoteException;
}
