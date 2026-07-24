package Interface;
import DTO.Cliente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICliente extends Remote {
    List<Cliente> listar(String texto) throws RemoteException;
    boolean insertar(Cliente obj) throws RemoteException;
    boolean actualizar(Cliente obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    Cliente buscarPorId(int id) throws RemoteException;
    Cliente buscarPorDocumento(String doc) throws RemoteException;
    List<Cliente> listarAtendidos() throws RemoteException;
}
