package Interface;
import DTO.MovimientoMesa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMovimientoMesa extends Remote {
    boolean insertar(MovimientoMesa obj) throws RemoteException;
    boolean eliminar(MovimientoMesa obj) throws RemoteException;
    List<MovimientoMesa> listarPorMovimiento(int idMovimiento) throws RemoteException;
}
