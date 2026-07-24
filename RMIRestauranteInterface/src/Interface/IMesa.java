package Interface;
import DTO.Mesa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMesa extends Remote{
    List<Mesa> listar(String texto) throws RemoteException;
    boolean insertar(Mesa obj) throws RemoteException;
    boolean actualizar(Mesa obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    boolean cambiarEstado(int idMesa, int nuevoEstado) throws RemoteException;
}
