package Interface;
import DTO.Producto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProducto extends Remote {
    List<Producto> listar(String texto) throws RemoteException;
    boolean insertar(Producto obj) throws RemoteException;
    boolean actualizar(Producto obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    Producto buscar(int id) throws RemoteException;
}
