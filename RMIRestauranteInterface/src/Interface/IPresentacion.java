package Interface;
import DTO.Presentacion;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPresentacion extends Remote{
    List<Presentacion> listar(String texto) throws RemoteException;
    List<Presentacion> listarPorProducto(int idProducto) throws RemoteException;
    boolean insertar(Presentacion obj) throws RemoteException;
    boolean actualizar(Presentacion obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
}
