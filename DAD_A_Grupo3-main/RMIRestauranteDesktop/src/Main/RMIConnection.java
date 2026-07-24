package Main;

import Interface.*;
import java.rmi.Naming;

public class RMIConnection {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3239;
    private static final String BASE = "rmi://" + HOST + ":" + PORT + "/";

    private static IAdministrador administradorDAO;
    private static IMozo mozoDAO;
    private static ICliente clienteDAO;
    private static IProducto productoDAO;
    private static IPresentacion presentacionDAO;
    private static IMesa mesaDAO;
    private static ISalon salonDAO;
    private static IMovimiento movimientoDAO;
    private static IMovimientoPedido movimientoPedidoDAO;
    private static IMovimientoMesa movimientoMesaDAO;
    private static IRegistroCaja registroCajaDAO;
    private static IDatosEmpresa datosEmpresaDAO;
    private static IReservaBuffet reservaBuffetDAO;
    private static IEstadoComanda estadoComandaDAO;
    private static IFormaPago formaPagoDAO;
    private static IMovimientoPago movimientoPagoDAO;
    private static ITipoProducto tipoProductoDAO;
    private static ITiempoPreparacion tiempoPreparacionDAO;
    private static IEstadoProducto estadoProductoDAO;
    private static IEstadoMesa estadoMesaDAO;
    private static IEstadoPresentacion estadoPresentacionDAO;
    private static ITipoDocumento tipoDocumentoDAO;
    private static ITipoCliente tipoClienteDAO;
    private static INotificacion notificacionDAO;
    private static IMesaGrupo mesaGrupoDAO;

    private static void connect(String rmiName, Class<?> clazz) throws Exception {
        Object obj = Naming.lookup(BASE + rmiName);
        if (clazz.isInstance(obj)) {
            if (clazz == IAdministrador.class) administradorDAO = (IAdministrador) obj;
            else if (clazz == IMozo.class) mozoDAO = (IMozo) obj;
            else if (clazz == ICliente.class) clienteDAO = (ICliente) obj;
            else if (clazz == IProducto.class) productoDAO = (IProducto) obj;
            else if (clazz == IPresentacion.class) presentacionDAO = (IPresentacion) obj;
            else if (clazz == IMesa.class) mesaDAO = (IMesa) obj;
            else if (clazz == ISalon.class) salonDAO = (ISalon) obj;
            else if (clazz == IMovimiento.class) movimientoDAO = (IMovimiento) obj;
            else if (clazz == IMovimientoPedido.class) movimientoPedidoDAO = (IMovimientoPedido) obj;
            else if (clazz == IMovimientoMesa.class) movimientoMesaDAO = (IMovimientoMesa) obj;
            else if (clazz == IRegistroCaja.class) registroCajaDAO = (IRegistroCaja) obj;
            else if (clazz == IDatosEmpresa.class) datosEmpresaDAO = (IDatosEmpresa) obj;
            else if (clazz == IReservaBuffet.class) reservaBuffetDAO = (IReservaBuffet) obj;
            else if (clazz == IEstadoComanda.class) estadoComandaDAO = (IEstadoComanda) obj;
            else if (clazz == IFormaPago.class) formaPagoDAO = (IFormaPago) obj;
            else if (clazz == IMovimientoPago.class) movimientoPagoDAO = (IMovimientoPago) obj;
            else if (clazz == ITipoProducto.class) tipoProductoDAO = (ITipoProducto) obj;
            else if (clazz == ITiempoPreparacion.class) tiempoPreparacionDAO = (ITiempoPreparacion) obj;
            else if (clazz == IEstadoProducto.class) estadoProductoDAO = (IEstadoProducto) obj;
            else if (clazz == IEstadoMesa.class) estadoMesaDAO = (IEstadoMesa) obj;
            else if (clazz == IEstadoPresentacion.class) estadoPresentacionDAO = (IEstadoPresentacion) obj;
            else if (clazz == ITipoDocumento.class) tipoDocumentoDAO = (ITipoDocumento) obj;
            else if (clazz == ITipoCliente.class) tipoClienteDAO = (ITipoCliente) obj;
            else if (clazz == INotificacion.class) notificacionDAO = (INotificacion) obj;
            else if (clazz == IMesaGrupo.class) mesaGrupoDAO = (IMesaGrupo) obj;
        }
    }

    public static boolean inicializar() {
        try {
            connect("administrador", IAdministrador.class);
            connect("mozo", IMozo.class);
            connect("cliente", ICliente.class);
            connect("producto", IProducto.class);
            connect("presentacion", IPresentacion.class);
            connect("mesa", IMesa.class);
            connect("salon", ISalon.class);
            connect("movimiento", IMovimiento.class);
            connect("movimientoPedido", IMovimientoPedido.class);
            connect("movimientoMesa", IMovimientoMesa.class);
            connect("registroCaja", IRegistroCaja.class);
            connect("datosEmpresa", IDatosEmpresa.class);
            connect("reservaBuffet", IReservaBuffet.class);
            connect("estadoComanda", IEstadoComanda.class);
            connect("formaPago", IFormaPago.class);
            connect("movimientoPago", IMovimientoPago.class);
            connect("tipoProducto", ITipoProducto.class);
            connect("tiempoPreparacion", ITiempoPreparacion.class);
            connect("estadoProducto", IEstadoProducto.class);
            connect("estadoMesa", IEstadoMesa.class);
            connect("estadoPresentacion", IEstadoPresentacion.class);
            connect("tipoDocumento", ITipoDocumento.class);
            connect("tipoCliente", ITipoCliente.class);
            connect("notificacion", INotificacion.class);
            connect("mesaGrupo", IMesaGrupo.class);
            return true;
        } catch (Exception e) {
            System.err.println("Error conexión RMI: " + e.getMessage());
            return false;
        }
    }

    public static IAdministrador getAdministradorDAO() { return administradorDAO; }
    public static IMozo getMozoDAO() { return mozoDAO; }
    public static ICliente getClienteDAO() { return clienteDAO; }
    public static IProducto getProductoDAO() { return productoDAO; }
    public static IPresentacion getPresentacionDAO() { return presentacionDAO; }
    public static IMesa getMesaDAO() { return mesaDAO; }
    public static ISalon getSalonDAO() { return salonDAO; }
    public static IMovimiento getMovimientoDAO() { return movimientoDAO; }
    public static IMovimientoPedido getMovimientoPedidoDAO() { return movimientoPedidoDAO; }
    public static IMovimientoMesa getMovimientoMesaDAO() { return movimientoMesaDAO; }
    public static IRegistroCaja getRegistroCajaDAO() { return registroCajaDAO; }
    public static IDatosEmpresa getDatosEmpresaDAO() { return datosEmpresaDAO; }
    public static IReservaBuffet getReservaBuffetDAO() { return reservaBuffetDAO; }
    public static IEstadoComanda getEstadoComandaDAO() { return estadoComandaDAO; }
    public static IFormaPago getFormaPagoDAO() { return formaPagoDAO; }
    public static IMovimientoPago getMovimientoPagoDAO() { return movimientoPagoDAO; }
    public static ITipoProducto getTipoProductoDAO() { return tipoProductoDAO; }
    public static ITiempoPreparacion getTiempoPreparacionDAO() { return tiempoPreparacionDAO; }
    public static IEstadoProducto getEstadoProductoDAO() { return estadoProductoDAO; }
    public static IEstadoMesa getEstadoMesaDAO() { return estadoMesaDAO; }
    public static IEstadoPresentacion getEstadoPresentacionDAO() { return estadoPresentacionDAO; }
    public static ITipoDocumento getTipoDocumentoDAO() { return tipoDocumentoDAO; }
    public static ITipoCliente getTipoClienteDAO() { return tipoClienteDAO; }
    public static INotificacion getNotificacionDAO() { return notificacionDAO; }
    public static IMesaGrupo getMesaGrupoDAO() { return mesaGrupoDAO; }
}
