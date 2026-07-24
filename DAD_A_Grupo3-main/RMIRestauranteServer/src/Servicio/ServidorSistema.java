package Servicio;
import DAO.*;
import Interface.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorSistema {
    public static void main(String[] args) {
        int PUERTO = 3239;
        try {
            Registry registry = LocateRegistry.createRegistry(PUERTO);
            String dirIP = InetAddress.getLocalHost().toString();
            System.out.println("  SERVIDOR RMI - Restaurante");
            System.out.println("  IP: " + dirIP);
            System.out.println("  Puerto: " + PUERTO);

            bindService(registry, "administrador", new AdministradorDAO());
            bindService(registry, "cliente", new ClienteDAO());
            bindService(registry, "producto", new ProductoDAO());
            bindService(registry, "presentacion", new PresentacionDAO());
            bindService(registry, "mesa", new MesaDAO());
            bindService(registry, "movimiento", new MovimientoDAO());
            bindService(registry, "datosEmpresa", new DatosEmpresaDAO());
            bindService(registry, "registroCaja", new RegistroCajaDAO());
            bindService(registry, "movimientoPedido", new MovimientoPedidoDAO());
            bindService(registry, "movimientoPago", new MovimientoPagoDAO());
            bindService(registry, "movimientoMesa", new MovimientoMesaDAO());
            bindService(registry, "tipoProducto", new TipoProductoDAO());
            bindService(registry, "tipoMovimiento", new TipoMovimientoDAO());
            bindService(registry, "tipoDocumento", new TipoDocumentoDAO());
            bindService(registry, "tipoCliente", new TipoClienteDAO());
            bindService(registry, "tiempoPreparacion", new TiempoPreparacionDAO());
            bindService(registry, "salon", new SalonDAO());
            bindService(registry, "estadoProducto", new EstadoProductoDAO());
            bindService(registry, "estadoPresentacion", new EstadoPresentacionDAO());
            bindService(registry, "estadoMovimiento", new EstadoMovimientoDAO());
            bindService(registry, "estadoMesa", new EstadoMesaDAO());
            bindService(registry, "formaPago", new FormaPagoDAO());
            bindService(registry, "mozo", new MozoDAO());
            bindService(registry, "estadoPedido", new EstadoPedidoDAO());
            bindService(registry, "estadoComanda", new EstadoComandaDAO());
            bindService(registry, "mesaGrupo", new MesaGrupoDAO());
            bindService(registry, "notificacion", new NotificacionDAO());
            bindService(registry, "datosEnvio", new DatosEnvioDAO());
            bindService(registry, "codigoPago", new CodigoPagoDAO());
            bindService(registry, "reservaBuffet", new ReservaBuffetDAO());

            System.out.println("Todos los servicios registrados. Servidor listo.");
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void bindService(Registry registry, String name, Object obj) {
        try {
            java.rmi.server.UnicastRemoteObject uro = (java.rmi.server.UnicastRemoteObject) obj;
            registry.bind(name, uro);
            System.out.println("  [OK] " + name);
        } catch (AlreadyBoundException | RemoteException e) {
            System.out.println("  [ERROR] " + name + ": " + e.getMessage());
        }
    }
}
