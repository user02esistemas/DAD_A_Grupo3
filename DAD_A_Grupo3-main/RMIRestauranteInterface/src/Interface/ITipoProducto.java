package Interface;
import DTO.TipoProducto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITipoProducto extends Remote {
    List<TipoProducto> listar() throws RemoteException;
}
