package Interface;
import DTO.EstadoMesa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoMesa extends Remote {
    List<EstadoMesa> listar() throws RemoteException;
}