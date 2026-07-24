package Interface;
import DTO.FormaPago;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IFormaPago extends Remote {
    List<FormaPago> listar() throws RemoteException;
}