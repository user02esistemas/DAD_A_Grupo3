package Interface;
import DTO.DatosEmpresa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IDatosEmpresa extends Remote{
     List<DatosEmpresa> listar() throws RemoteException;
     boolean actualizar(DatosEmpresa obj) throws RemoteException;
}
