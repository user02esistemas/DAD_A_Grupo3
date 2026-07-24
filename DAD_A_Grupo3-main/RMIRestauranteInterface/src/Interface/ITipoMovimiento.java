package Interface;
import DTO.TipoMovimiento;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITipoMovimiento extends Remote {
    List<TipoMovimiento> listar() throws RemoteException;
}