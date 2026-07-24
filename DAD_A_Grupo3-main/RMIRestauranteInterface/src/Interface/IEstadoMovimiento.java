package Interface;
import DTO.EstadoMovimiento;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IEstadoMovimiento extends Remote {
    List<EstadoMovimiento> listar() throws RemoteException;
}