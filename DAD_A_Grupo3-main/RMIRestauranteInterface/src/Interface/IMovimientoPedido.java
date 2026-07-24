package Interface;
import DTO.MovimientoPedido;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMovimientoPedido extends Remote {
    boolean insertar(MovimientoPedido obj) throws RemoteException;
    boolean actualizar(MovimientoPedido obj) throws RemoteException;
    boolean eliminar(MovimientoPedido obj) throws RemoteException;
    List<MovimientoPedido> listarPorMovimiento(int idMovimiento) throws RemoteException;
    List<MovimientoPedido> listarPorCliente(int idCliente) throws RemoteException;
    List<MovimientoPedido> listarPorEstado(int idEstadoPedido) throws RemoteException;
    List<MovimientoPedido> listarParaCocina() throws RemoteException;
    boolean cambiarEstado(int idMovimiento, int idPresentacion, int nuevoEstado) throws RemoteException;
    MovimientoPedido buscar(int idMovimiento, int idPresentacion) throws RemoteException;
    List<MovimientoPedido> listarNoPagados(int idMovimiento) throws RemoteException;
    boolean marcarPagados(int idMovimiento, String idsPresentaciones) throws RemoteException;
}
