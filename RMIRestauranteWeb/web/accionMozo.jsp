<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Mozo mozo = (Mozo) session.getAttribute("mozoSesion");
    if (mozo == null) { response.sendRedirect("mozoLogin.jsp"); return; }

    String accion = request.getParameter("accion");

    MovimientoControl movCtrl = new MovimientoControl();
    MovimientoPedidoControl mpCtrl = new MovimientoPedidoControl();
    PresentacionControl presCtrl = new PresentacionControl();
    MesaControl mesaCtrl = new MesaControl();

    if ("enviarCocina".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
        if (idMov > 0) {
            Movimiento mov = movCtrl.DATOS.buscar(idMov);
            if (mov != null) {
                mov.setIdEstadoComanda(2);
                movCtrl.DATOS.actualizar(mov);
                try {
                    NotificacionWebSocket.enviarACocina("{\"tipo\":\"NUEVO_PEDIDO\",\"idMovimiento\":" + idMov + "}");
                } catch (Exception e) {}
            }
        }
        response.sendRedirect("mozo.jsp?paso=dashboard");
        return;
    }

    if ("unirMesas".equals(accion)) {
        int idMovimiento = Integer.parseInt(request.getParameter("idMovimiento"));
        int idMesaActual = Integer.parseInt(request.getParameter("idMesaActual"));
        String[] idMesasUnir = request.getParameterValues("idMesaUnir");

        if (idMesasUnir == null || idMesasUnir.length == 0) {
            response.sendRedirect("mozo.jsp?paso=pedido&idMesa=" + idMesaActual + "&idMov=" + idMovimiento + "&err=Seleccione+al+menos+una+mesa");
            return;
        }

        MesaGrupoControl grupoCtrl = new MesaGrupoControl();
        MesaControl mesaCtrlLocal = new MesaControl();
        MovimientoMesaControl mmCtrl = new MovimientoMesaControl();

        int idGrupo = 0;
        if (grupoCtrl.DATOS.tieneGrupo(idMesaActual)) {
            List<MesaGrupo> todosGrupos = grupoCtrl.DATOS.listar();
            for (MesaGrupo g : todosGrupos) {
                List<Integer> mesasG = grupoCtrl.DATOS.obtenerMesasDelGrupo(g.getIdMesaGrupo());
                if (mesasG.contains(idMesaActual)) {
                    idGrupo = g.getIdMesaGrupo();
                    break;
                }
            }
        }
        if (idGrupo == 0) {
            MesaGrupo nuevoGrupo = new MesaGrupo();
            nuevoGrupo.setNombre("Grupo " + java.time.LocalDate.now());
            nuevoGrupo.setNumPersonas(0);
            grupoCtrl.DATOS.insertar(nuevoGrupo);
            idGrupo = nuevoGrupo.getIdMesaGrupo();
            grupoCtrl.DATOS.agregarMesaAGrupo(idMesaActual, idGrupo);
        }

        for (String idMesaStr : idMesasUnir) {
            int idMesaUnir = Integer.parseInt(idMesaStr);
            grupoCtrl.DATOS.agregarMesaAGrupo(idMesaUnir, idGrupo);

            MovimientoMesa mm = new MovimientoMesa();
            mm.setIdMovimiento(idMovimiento);
            mm.setIdMesa(idMesaUnir);
            mmCtrl.DATOS.insertar(mm);

            mesaCtrlLocal.DATOS.cambiarEstado(idMesaUnir, 2);
        }

        Movimiento movUpdate = movCtrl.DATOS.buscar(idMovimiento);
        if (movUpdate != null) {
            movUpdate.setIdMesaGrupo(idGrupo);
            movCtrl.DATOS.actualizar(movUpdate);
        }

        response.sendRedirect("mozo.jsp?paso=pedido&idMesa=" + idMesaActual + "&idMov=" + idMovimiento);
        return;
    }

    if ("aceptarComanda".equals(accion)) {
        int idMovimiento = Integer.parseInt(request.getParameter("idMovimiento"));
        Movimiento mov = movCtrl.DATOS.buscar(idMovimiento);
        if (mov != null) {
            mov.setIdMozo(mozo.getIdMozo());
            mov.setIdEstadoComanda(2);
            movCtrl.DATOS.actualizar(mov);
            try {
                NotificacionWebSocket.enviarACliente(mov.getIdMovimiento(),
                    "{\"tipo\":\"COMANDA_ACEPTADA\",\"idMovimiento\":" + idMovimiento + ",\"mozo\":\"" + mozo.getNombre() + "\"}");
            } catch (Exception e) {}
        }
        response.sendRedirect("mozo.jsp?paso=dashboard");
        return;
    }

    if ("agregarItem".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int idPresentacion = Integer.parseInt(request.getParameter("idPresentacion"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));

        int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
        if (idMov <= 0) {
            response.sendRedirect("mozo.jsp?paso=dashboard&err=Comanda+no+activa");
            return;
        }
        Movimiento movCheck = movCtrl.DATOS.buscar(idMov);
        if (movCheck == null || (movCheck.getIdEstadoComanda() != 1 && movCheck.getIdEstadoComanda() != 2)) {
            response.sendRedirect("mozo.jsp?paso=dashboard&err=Comanda+cerrada+o+enviada");
            return;
        }

        Presentacion pres = null;
        for (Presentacion p : presCtrl.DATOS.listar("")) { if (p.getIdPresentacion() == idPresentacion) { pres = p; break; } }
        double precio = pres != null ? pres.getPrecio() : 0;
        double totalItem = precio * cantidad;

        MovimientoPedido mp = new MovimientoPedido();
        mp.setIdMovimiento(idMov);
        mp.setIdPresentacion(idPresentacion);
        mp.setCantidad(cantidad);
        mp.setPrecioUnitario(precio);
        mp.setTotal(totalItem);
        mp.setPagado(false);
        mpCtrl.DATOS.insertar(mp);

        Movimiento mov = movCtrl.DATOS.buscar(idMov);
        if (mov != null) {
            mov.setTotalPagar(mov.getTotalPagar() + totalItem);
            movCtrl.DATOS.actualizar(mov);
        }
        response.sendRedirect("mozo.jsp?paso=pedido&idMesa=" + idMesa);
        return;
    }

    if ("eliminarItem".equals(accion)) {
        int idMov = Integer.parseInt(request.getParameter("idMovimiento"));
        int idPres = Integer.parseInt(request.getParameter("idPresentacion"));
        String redirMesa = request.getParameter("idMesa");

        MovimientoPedido mpElim = mpCtrl.DATOS.buscar(idMov, idPres);
        if (mpElim != null) {
            double resta = mpElim.getTotal();
            mpCtrl.DATOS.eliminar(mpElim);
            Movimiento movUpd = movCtrl.DATOS.buscar(idMov);
            if (movUpd != null) {
                movUpd.setTotalPagar(Math.max(0, movUpd.getTotalPagar() - resta));
                movCtrl.DATOS.actualizar(movUpd);
            }
        }
        response.sendRedirect("mozo.jsp?paso=confirmar&idMesa=" + redirMesa + "&idMov=" + idMov);
        return;
    }

    if ("cambiarEstadoMesa".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int nuevoEstado = Integer.parseInt(request.getParameter("nuevoEstado"));
        mesaCtrl.DATOS.cambiarEstado(idMesa, nuevoEstado);
        response.sendRedirect("mozo.jsp?paso=dashboard");
        return;
    }

    response.sendRedirect("mozo.jsp?paso=dashboard");
%>
