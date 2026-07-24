package Interface;
import DTO.MovimientoPago;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMovimientoPago extends Remote {
    boolean insertar(MovimientoPago obj) throws RemoteException;
    List<MovimientoPago> listarPorMovimiento(int idMovimiento) throws RemoteException;
}
