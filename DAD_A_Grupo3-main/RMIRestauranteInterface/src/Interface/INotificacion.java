package Interface;
import DTO.Notificacion;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface INotificacion extends Remote {
    List<Notificacion> listar() throws RemoteException;
    List<Notificacion> listarNoLeidas(String tipoDestinatario, int idDestinatario) throws RemoteException;
    boolean marcarLeida(int idNotificacion) throws RemoteException;
    boolean insertar(Notificacion obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
}
