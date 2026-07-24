package Interface;
import DTO.ReservaBuffet;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IReservaBuffet extends Remote {
    List<ReservaBuffet> listar() throws RemoteException;
    List<ReservaBuffet> listarPorEstado(int idEstado) throws RemoteException;
    ReservaBuffet buscar(int id) throws RemoteException;
    boolean insertar(ReservaBuffet obj) throws RemoteException;
    boolean actualizar(ReservaBuffet obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    boolean confirmar(int id) throws RemoteException;
    boolean cancelar(int id) throws RemoteException;
    boolean verificarDisponibilidad(String fecha, int personas) throws RemoteException;
}
