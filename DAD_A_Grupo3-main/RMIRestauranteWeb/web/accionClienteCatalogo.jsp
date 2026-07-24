<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.UUID"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String accion = request.getParameter("accion");

    if ("abrirComanda".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        String nombre = request.getParameter("nombre") != null ? request.getParameter("nombre") : "Cliente General";
        int tipoDoc = Integer.parseInt(request.getParameter("tipoDoc") != null ? request.getParameter("tipoDoc") : "1");
        String doc = request.getParameter("doc") != null ? request.getParameter("doc") : "00000000";
        String tel = request.getParameter("tel") != null ? request.getParameter("tel") : "";
        String email = request.getParameter("email") != null ? request.getParameter("email") : "";

        MovimientoControl movCtrl = new MovimientoControl();
        int movActivo = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
        if (movActivo > 0) {
            out.print("ERROR: Ya existe una comanda abierta para esta mesa");
            return;
        }

        ClienteControl clienteCtrl = new ClienteControl();
        Cliente cliente = clienteCtrl.DATOS.buscarPorDocumento(doc);
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setIdTipoDocumento(tipoDoc);
            cliente.setNombre(nombre);
            cliente.setDocumento(doc);
            cliente.setIdTipoCliente(1);
            cliente.setApellido("-");
            cliente.setTelefono(tel);
            cliente.setEmail(email);
            cliente.setSexo("M");
            clienteCtrl.DATOS.insertar(cliente);
            cliente = clienteCtrl.DATOS.buscarPorDocumento(doc);
        }

        MesaControl mesaCtrl = new MesaControl();
        List<Mesa> todasM = mesaCtrl.DATOS.listar("");
        Mesa mesa = null;
        for (Mesa mm : todasM) { if (mm.getIdMesa() == idMesa) { mesa = mm; break; } }
        int capacidad = mesa != null ? mesa.getCapacidad() : 1;

        String codigo = "CM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        DatosEmpresaControl empCtrl = new DatosEmpresaControl();
        List<DatosEmpresa> empresas = empCtrl.DATOS.listar();
        int idEmpresa = 1;
        if (!empresas.isEmpty()) idEmpresa = empresas.get(0).getIdEmpresa();

        Movimiento mov = new Movimiento();
        mov.setNroDocumento(doc);
        mov.setIdCliente(cliente.getIdCliente());
        mov.setIdTipoMovimiento(1);
        mov.setTotalPagar(0);
        mov.setIdDatosEmpresa(idEmpresa);
        mov.setIdEstadoMovimiento(1);
        mov.setNumPersonas(capacidad);
        mov.setCodigoComanda(codigo);
        mov.setIdMozo(0);
        mov.setIdEstadoComanda(1);
        mov.setIdMesaGrupo(0);
        java.util.List<Integer> mesas = new java.util.ArrayList<>();
        mesas.add(idMesa);
        mov.setListaMesas(mesas);

        boolean ok = movCtrl.DATOS.abrirComanda(mov);

        if (ok) {
            int idMov = movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa);
            MesaControl mc = new MesaControl();
            List<Mesa> todasM2 = mc.DATOS.listar("");
            Mesa m = null;
            for (Mesa mm : todasM2) { if (mm.getIdMesa() == idMesa) { m = mm; break; } }
            if (m != null) {
                m.setIdEstadoMesa(2);
                mc.DATOS.actualizar(m);
            }
            out.print("OK:" + idMov);
        } else {
            out.print("Error al abrir comanda");
        }
        return;
    }

    if ("llamarMozo".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int idMovimiento = Integer.parseInt(request.getParameter("idMovimiento"));
        MesaControl mesaCtrl = new MesaControl();
        List<Mesa> todasM3 = mesaCtrl.DATOS.listar("");
        Mesa mesa = null;
        for (Mesa mm : todasM3) { if (mm.getIdMesa() == idMesa) { mesa = mm; break; } }
        String numMesa = mesa != null ? mesa.getNumero() : String.valueOf(idMesa);
        String mensajeJson = "{\"tipo\":\"LLAMAR_MOZO\",\"idMesa\":" + idMesa + ",\"mesa\":\"" + numMesa + "\",\"idMovimiento\":" + idMovimiento + "}";

        try {
            NotificacionControl notifCtrl = new NotificacionControl();
            Notificacion n = new Notificacion();
            n.setIdMovimiento(idMovimiento);
            n.setTipo("LLAMAR_MOZO");
            n.setMensaje("Cliente llama al mozo desde mesa " + numMesa);
            n.setIdDestinatario(0);
            n.setTipoDestinatario("MOZO");
            n.setLeida(false);
            notifCtrl.DATOS.insertar(n);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            Control.NotificacionWebSocket.enviarAMozo(mensajeJson);
        } catch (Exception e) { e.printStackTrace(); }
        out.print("OK");
        return;
    }

    out.print("Accion no valida");
%>
