package Interface;
import DTO.Movimiento;
import DTO.MovimientoPedido;
import DTO.Cliente;
import DTO.DatosEmpresa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMovimiento extends Remote{
    int insertar(Movimiento obj) throws RemoteException;
    boolean actualizar(Movimiento obj) throws RemoteException;
    boolean eliminar(int id) throws RemoteException;
    List<Movimiento> listarActivos() throws RemoteException;
    List<Movimiento> listarVentasCerradas() throws RemoteException;
    List<MovimientoPedido> obtenerPedidosPorMesa(int idMesa) throws RemoteException;
    int obtenerIdMovimientoActivoPorMesa(int idMesa) throws RemoteException;
    Cliente obtenerClienteDelMovimiento(int idMovimiento) throws RemoteException;
    List<MovimientoPedido> obtenerPedidosPorMovimiento(int idMovimiento) throws RemoteException;
    DatosEmpresa obtenerDatosEmpresaPorMovimiento(int idMovimiento) throws RemoteException;
    Movimiento buscar(int id) throws RemoteException;
    Movimiento buscar(String codigoComanda) throws RemoteException;
    double totalVentasHoy() throws RemoteException;
    boolean cerrarVenta(int idMovimiento) throws RemoteException;
    List<Movimiento> listarPorEstadoComanda(int idEstadoComanda) throws RemoteException;
    List<Movimiento> listarPorMozo(int idMozo) throws RemoteException;
    List<Movimiento> listarComandasAbiertas() throws RemoteException;
    boolean abrirComanda(Movimiento obj) throws RemoteException;
}
