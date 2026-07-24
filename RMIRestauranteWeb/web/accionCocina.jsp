<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="DTO.Notificacion"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    int idMovimiento = Integer.parseInt(request.getParameter("idMovimiento"));
    int idPresentacion = Integer.parseInt(request.getParameter("idPresentacion"));
    String accion = request.getParameter("accion");

    MovimientoPedidoControl mpCtrl = new MovimientoPedidoControl();
    NotificacionControl notifCtrl = new NotificacionControl();
    boolean ok = false;

    MovimientoPedido actual = mpCtrl.DATOS.buscar(idMovimiento, idPresentacion);
    int estadoActual = actual != null ? actual.getIdEstadoPedido() : 0;

    if ("preparar".equals(accion)) {
        if (estadoActual != 1) {
            response.sendRedirect("cocina.jsp?err=Transicion+invalida");
            return;
        }
        ok = mpCtrl.DATOS.cambiarEstado(idMovimiento, idPresentacion, 2);
    } else if ("listo".equals(accion)) {
        if (estadoActual != 2) {
            response.sendRedirect("cocina.jsp?err=Transicion+invalida");
            return;
        }
        ok = mpCtrl.DATOS.cambiarEstado(idMovimiento, idPresentacion, 3);
        if (ok) {
            try {
                Notificacion n = new Notificacion();
                n.setIdMovimiento(idMovimiento);
                n.setMensaje("Pedido listo para entrega - Movimiento #" + idMovimiento);
                n.setTipo("PEDIDO_LISTO");
                n.setIdDestinatario(0);
                n.setTipoDestinatario("MOZO");
                n.setLeida(false);
                notifCtrl.DATOS.insertar(n);
            } catch (Exception e) { e.printStackTrace(); }
            try { Control.NotificacionWebSocket.enviarAMozo("{\"tipo\":\"PEDIDO_LISTO\",\"idMovimiento\":" + idMovimiento + "}"); } catch (Exception e) { e.printStackTrace(); }
        }
    } else if ("entregar".equals(accion)) {
        if (estadoActual != 3) {
            response.sendRedirect("cocina.jsp?err=Transicion+invalida");
            return;
        }
        ok = mpCtrl.DATOS.cambiarEstado(idMovimiento, idPresentacion, 4);
    }

    if (ok) {
        response.sendRedirect("cocina.jsp");
    } else {
        out.println("<script>alert('Error al procesar');history.back();</script>");
    }
%>