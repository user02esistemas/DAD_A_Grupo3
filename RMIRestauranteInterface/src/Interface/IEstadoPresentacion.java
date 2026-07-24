package Interface;
import DTO.EstadoPresentacion;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoPresentacion extends Remote {
    List<EstadoPresentacion> listar() throws RemoteException;
}