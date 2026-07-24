package Interface;
import DTO.EstadoPedido;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoPedido extends Remote {
    List<EstadoPedido> listar() throws RemoteException;
    EstadoPedido buscar(int id) throws RemoteException;
}
