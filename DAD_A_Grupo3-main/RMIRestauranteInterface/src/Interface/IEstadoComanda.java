package Interface;
import DTO.EstadoComanda;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoComanda extends Remote {
    List<EstadoComanda> listar() throws RemoteException;
    EstadoComanda buscar(int id) throws RemoteException;
}
