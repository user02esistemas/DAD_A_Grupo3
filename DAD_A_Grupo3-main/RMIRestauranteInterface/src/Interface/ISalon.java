package Interface;
import DTO.Salon;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ISalon extends Remote {
    List<Salon> listar() throws RemoteException;
    boolean insertar(Salon obj) throws RemoteException;
    boolean actualizar(Salon obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
}