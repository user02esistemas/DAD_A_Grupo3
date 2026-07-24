<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String accion = request.getParameter("accion");
    int idReserva = Integer.parseInt(request.getParameter("idReserva"));
    ReservaBuffetControl resCtrl = new ReservaBuffetControl();

    if ("confirmar".equals(accion)) {
        resCtrl.DATOS.confirmar(idReserva);
    } else if ("cancelar".equals(accion)) {
        resCtrl.DATOS.cancelar(idReserva);
    }
    response.sendRedirect("reservasBuffet.jsp?msg=ok");
%>
