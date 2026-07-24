package Interface;
import DTO.RegistroCaja;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRegistroCaja extends Remote {
    boolean estaAbierta() throws RemoteException;
    void abrir(double monto) throws RemoteException;
    void cerrar(double monto) throws RemoteException;
    List<RegistroCaja> listarHistorial() throws RemoteException;
}
