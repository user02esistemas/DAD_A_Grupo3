package Interface;
import DTO.TipoDocumento;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITipoDocumento extends Remote {
    List<TipoDocumento> listar() throws RemoteException;
}