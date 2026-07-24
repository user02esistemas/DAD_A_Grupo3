package Interface;
import DTO.CodigoPago;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICodigoPago extends Remote {
    CodigoPago generarCodigo(int idMovimiento) throws RemoteException;
    CodigoPago validarCodigo(String codigo) throws RemoteException;
    boolean marcarPagado(int idCodigoPago) throws RemoteException;
    CodigoPago buscar(int id) throws RemoteException;
    List<CodigoPago> listar() throws RemoteException;
}
