package Interface;
import DTO.TiempoPreparacion;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITiempoPreparacion extends Remote {
    List<TiempoPreparacion> listar() throws RemoteException;
}