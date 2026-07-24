package Interface;
import DTO.MesaGrupo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMesaGrupo extends Remote {
    List<MesaGrupo> listar() throws RemoteException;
    boolean insertar(MesaGrupo obj) throws RemoteException;
    boolean actualizar(MesaGrupo obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    MesaGrupo buscar(int id) throws RemoteException;
    boolean agregarMesaAGrupo(int idMesa, int idGrupo) throws RemoteException;
    boolean sacarMesaDeGrupo(int idMesa) throws RemoteException;
    List<Integer> obtenerMesasDelGrupo(int idGrupo) throws RemoteException;
    boolean tieneGrupo(int idMesa) throws RemoteException;
}
