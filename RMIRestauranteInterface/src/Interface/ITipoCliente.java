package Interface;
import DTO.TipoCliente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITipoCliente extends Remote {
    List<TipoCliente> listar() throws RemoteException;
}