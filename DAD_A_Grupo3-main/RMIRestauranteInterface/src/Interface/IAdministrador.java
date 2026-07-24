package Interface;
import DTO.Administrador;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAdministrador extends Remote{
    Administrador login(String usuario, String clave) throws RemoteException;

}