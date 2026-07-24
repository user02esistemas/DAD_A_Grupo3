package Control;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/notificaciones")
public class NotificacionWebSocket {
    private static final Set<Session> sesiones = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<Session> cocina = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<Session> caja = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<Session> mozo = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final java.util.Map<Integer, Session> clientes = new java.util.concurrent.ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session sesion) {
        sesiones.add(sesion);
        String rol = sesion.getQueryString();
        if (rol != null) {
            if (rol.startsWith("cliente:")) {
                try {
                    int idMov = Integer.parseInt(rol.substring(8));
                    clientes.put(idMov, sesion);
                } catch (NumberFormatException e) {}
            } else {
                switch (rol) {
                    case "cocina" -> cocina.add(sesion);
                    case "caja" -> caja.add(sesion);
                    case "mozo" -> mozo.add(sesion);
                }
            }
        }
        System.out.println("WS conectado: " + sesion.getId() + " rol=" + rol);
    }

    @OnMessage
    public void onMessage(String mensaje, Session sesion) {
        System.out.println("WS mensaje: " + mensaje);
    }

    @OnClose
    public void onClose(Session sesion) {
        sesiones.remove(sesion);
        String rol = sesion.getQueryString();
        if (rol != null) {
            if (rol.startsWith("cliente:")) {
                try {
                    int idMov = Integer.parseInt(rol.substring(8));
                    clientes.remove(idMov);
                } catch (NumberFormatException e) {}
            } else {
                switch (rol) {
                    case "cocina" -> cocina.remove(sesion);
                    case "caja" -> caja.remove(sesion);
                    case "mozo" -> mozo.remove(sesion);
                }
            }
        }
        System.out.println("WS cerrado: " + sesion.getId());
    }

    @OnError
    public void onError(Session sesion, Throwable error) {
        sesiones.remove(sesion);
        System.out.println("WS error: " + error.getMessage());
    }

    public static void enviarACocina(String mensaje) {
        enviarAConjunto(cocina, mensaje);
    }

    public static void enviarACaja(String mensaje) {
        enviarAConjunto(caja, mensaje);
    }

    public static void enviarAMozo(String mensaje) {
        enviarAConjunto(mozo, mensaje);
    }

    public static void enviarACliente(int idMovimiento, String mensaje) {
        Session s = clientes.get(idMovimiento);
        if (s != null && s.isOpen()) {
            try {
                s.getBasicRemote().sendText(mensaje);
            } catch (IOException e) {
                System.out.println("Error WS envio cliente: " + e.getMessage());
            }
        }
    }

    public static void enviarATodos(String mensaje) {
        enviarAConjunto(sesiones, mensaje);
    }

    private static void enviarAConjunto(Set<Session> conjunto, String mensaje) {
        for (Session s : conjunto) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(mensaje);
                } catch (IOException e) {
                    System.out.println("Error WS envio: " + e.getMessage());
                }
            }
        }
    }
}
