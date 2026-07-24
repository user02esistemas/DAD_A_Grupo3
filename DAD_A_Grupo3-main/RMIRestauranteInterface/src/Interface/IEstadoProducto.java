package Interface;
import DTO.EstadoProducto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoProducto extends Remote {
    List<EstadoProducto> listar() throws RemoteException;
}