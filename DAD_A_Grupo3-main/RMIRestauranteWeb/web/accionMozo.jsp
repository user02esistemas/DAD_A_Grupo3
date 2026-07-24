<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.UUID"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Mozo mozo = (Mozo) session.getAttribute("mozoSesion");
    if (mozo == null || "cocina".equals(mozo.getTipo())) { response.sendRedirect("mozoLogin.jsp"); return; }

    String accion = request.getParameter("accion");

    MovimientoControl movCtrl = new MovimientoControl();
    MovimientoPedidoControl mpCtrl = new MovimientoPedidoControl();
    PresentacionControl presCtrl = new PresentacionControl();
    MesaControl mesaCtrl = new MesaControl();
    ClienteControl cliCtrl = new ClienteControl();

    if ("crearComanda".equals(accion)) {
        String[] idMesasArr = request.getParameterValues("idMesa");
        if (idMesasArr == null || idMesasArr.length == 0) {
            response.sendRedirect("mozo.jsp?paso=seleccionar&err=Seleccione+al+menos+una+mesa");
            return;
        }
        // Validate all mesas belong to the same salon
        Set<Integer> salones = new HashSet<>();
        for (String s : idMesasArr) {
            for (Mesa mx : mesaCtrl.DATOS.listar("")) {
                if (mx.getIdMesa() == Integer.parseInt(s)) {
                    salones.add(mx.getIdSalon());
                    break;
                }
            }
        }
        if (salones.size() > 1) {
            response.sendRedirect("mozo.jsp?paso=seleccionar&err=No+puedes+unir+mesas+de+diferentes+salones");
            return;
        }
        int idMesaPrimera = Integer.parseInt(idMesasArr[0]);
        String clienteNombre = request.getParameter("clienteNombre");
        boolean sinDatos = "1".equals(request.getParameter("sinDatos"));

        int idCliente = 0;
        if (sinDatos) {
            Cliente gen = cliCtrl.DATOS.buscarPorDocumento("00000000");
            if (gen == null) {
                gen = new Cliente();
                gen.setNombre("Cliente General");
                gen.setIdTipoDocumento(1);
                gen.setDocumento("00000000");
                gen.setTelefono("999999999");
                boolean creado = cliCtrl.DATOS.insertar(gen);
                if (creado) idCliente = gen.getIdCliente();
            } else {
                idCliente = gen.getIdCliente();
            }
        } else if (clienteNombre != null && !clienteNombre.trim().isEmpty()) {
            Cliente c = new Cliente();
            c.setNombre(clienteNombre.trim());
            c.setIdTipoDocumento(request.getParameter("idTipoDocumento") != null ? Integer.parseInt(request.getParameter("idTipoDocumento")) : 1);
            c.setDocumento(request.getParameter("clienteDoc") != null ? request.getParameter("clienteDoc") : "S/N");
            c.setTelefono(request.getParameter("clienteTel") != null ? request.getParameter("clienteTel") : "");
            boolean creado = cliCtrl.DATOS.insertar(c);
            if (creado) idCliente = c.getIdCliente();
        }

        List<Integer> mesasList = new ArrayList<>();
        for (String s : idMesasArr) mesasList.add(Integer.parseInt(s));

        Movimiento mov = new Movimiento();
        mov.setNroDocumento("");
        mov.setIdCliente(idCliente);
        mov.setIdTipoMovimiento(1);
        mov.setTotalPagar(0);
        mov.setIdDatosEmpresa(1);
        mov.setIdEstadoMovimiento(1);
        mov.setNumPersonas(1);
        mov.setCodigoComanda("CM-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        mov.setIdMozo(mozo.getIdMozo());
        mov.setIdEstadoComanda(1);
        mov.setListaMesas(mesasList);

        boolean ok = movCtrl.DATOS.abrirComanda(mov);
        if (ok) {
            response.sendRedirect("mozo.jsp?paso=pedido&idMesa=" + idMesaPrimera + "&idMov=" + mov.getIdMovimiento());
        } else {
            response.sendRedirect("mozo.jsp?paso=dashboard&err=Error+al+crear+comanda");
        }
        return;
    }

    if ("enviarCocina".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
        if (idMov > 0) {
            Movimiento mov = movCtrl.DATOS.buscar(idMov);
            if (mov != null && (mov.getIdEstadoComanda() == 1 || mov.getIdEstadoComanda() == 2)) {
                if (mov.getIdMozo() == 0) mov.setIdMozo(mozo.getIdMozo());
                mov.setIdEstadoComanda(3);
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

        MovimientoMesaControl mmCtrl = new MovimientoMesaControl();
        MesaControl mesaCtrlLocal = new MesaControl();

        int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
        if (idMov <= 0) {
            Movimiento nueva = new Movimiento();
            nueva.setIdCliente(0);
            nueva.setIdTipoMovimiento(1);
            nueva.setTotalPagar(0);
            nueva.setIdDatosEmpresa(1);
            nueva.setIdEstadoMovimiento(1);
            nueva.setNumPersonas(1);
            nueva.setCodigoComanda("CM-" + java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase());
            nueva.setIdMozo(mozo.getIdMozo());
            nueva.setIdEstadoComanda(1);
            List<Integer> mesasList = new ArrayList<>();
            mesasList.add(idMesa);
            nueva.setListaMesas(mesasList);
            boolean creada = movCtrl.DATOS.abrirComandaExtra(nueva);
            if (creada) {
                idMov = nueva.getIdMovimiento();
            } else {
                response.sendRedirect("mozo.jsp?paso=dashboard&err=No+se+pudo+crear+comanda");
                return;
            }
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

    if ("entregarItem".equals(accion)) {
        int idMov = Integer.parseInt(request.getParameter("idMovimiento"));
        int idPres = Integer.parseInt(request.getParameter("idPresentacion"));
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        mpCtrl.DATOS.cambiarEstado(idMov, idPres, 4);
        response.sendRedirect("mozo.jsp?paso=confirmar&idMesa=" + idMesa + "&idMov=" + idMov);
        return;
    }

    if ("entregarPedido".equals(accion)) {
        int idMov = Integer.parseInt(request.getParameter("idMovimiento"));
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        List<MovimientoPedido> items = mpCtrl.DATOS.listarPorMovimiento(idMov);
        if (items != null) {
            for (MovimientoPedido item : items) {
                mpCtrl.DATOS.cambiarEstado(idMov, item.getIdPresentacion(), 4);
            }
        }
        response.sendRedirect("mozo.jsp?paso=dashboard");
        return;
    }

    if ("cancelarComanda".equals(accion)) {
        int idMov = Integer.parseInt(request.getParameter("idMovimiento"));
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        boolean ok = movCtrl.DATOS.cancelarComanda(idMov);
        if (ok) {
            try {
                NotificacionWebSocket.enviarACliente(idMov,
                    "{\"tipo\":\"COMANDA_CANCELADA\",\"idMovimiento\":" + idMov + "}");
            } catch (Exception e) {}
        }
        response.sendRedirect("mozo.jsp?paso=dashboard" + (ok ? "" : "&err=No+se+pudo+cancelar+la+comanda"));
        return;
    }

    if ("cambiarEstadoMesa".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int nuevoEstado = Integer.parseInt(request.getParameter("nuevoEstado"));

        if (nuevoEstado == 1) {
            int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
            if (idMov > 0) {
                Movimiento mov = movCtrl.DATOS.buscar(idMov);
                if (mov != null) {
                    int ec = mov.getIdEstadoComanda();
                    if (ec == 1 || ec == 2) {
                        movCtrl.DATOS.cancelarComanda(idMov);
                    } else {
                        response.sendRedirect("mozo.jsp?paso=dashboard&err=La+mesa+tiene+comanda+en+cocina,+no+se+puede+liberar");
                        return;
                    }
                }
            }
        }
        mesaCtrl.DATOS.cambiarEstado(idMesa, nuevoEstado);
        response.sendRedirect("mozo.jsp?paso=dashboard");
        return;
    }

    response.sendRedirect("mozo.jsp?paso=dashboard");
%>
