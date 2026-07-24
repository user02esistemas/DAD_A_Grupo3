<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String accion = request.getParameter("accion");
    MesaGrupoControl grupoCtrl = new MesaGrupoControl();

    if ("crear".equals(accion)) {
        String nombre = request.getParameter("nombre");
        MesaGrupo g = new MesaGrupo();
        g.setNombre(nombre);
        g.setNumPersonas(0);
        grupoCtrl.DATOS.insertar(g);
    } else if ("agregarMesa".equals(accion)) {
        int idMesa = Integer.parseInt(request.getParameter("idMesa"));
        int idGrupo = Integer.parseInt(request.getParameter("idGrupo"));
        grupoCtrl.DATOS.agregarMesaAGrupo(idMesa, idGrupo);
    } else if ("eliminar".equals(accion)) {
        int idGrupo = Integer.parseInt(request.getParameter("idGrupo"));
        grupoCtrl.DATOS.eliminar(idGrupo);
    }
    response.sendRedirect("mesaGrupos.jsp?msg=ok");
%>
