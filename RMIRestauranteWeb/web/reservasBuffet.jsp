<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Administrador admin = (Administrador) session.getAttribute("administradorSesion");
    if (admin == null) { response.sendRedirect("administrador.jsp"); return; }

    ReservaBuffetControl resCtrl = new ReservaBuffetControl();
    List<ReservaBuffet> reservas = resCtrl.DATOS.listar();
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservas Buffet</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="CSS/estilos.css">
</head>
<body>
    <div class="topbar">
        <div class="brand"><i class="fa-solid fa-leaf"></i> Overo's Restaurant</div>
        <div class="user-area"><a href="controlAdmin.jsp"><i class="fa-solid fa-arrow-left"></i></a></div>
    </div>
    <div class="sidebar">
        <a href="controlAdmin.jsp"><i class="fa-solid fa-gauge"></i> Dashboard</a>
        <a href="puntoVenta.jsp"><i class="fa-solid fa-store"></i> Punto de Venta</a>
        <a href="monitorVentas.jsp"><i class="fa-solid fa-chart-line"></i> Monitor Ventas</a>
        <a href="caja.jsp"><i class="fa-solid fa-cash-register"></i> Caja</a>
        <a href="dashboardCaja.jsp"><i class="fa-solid fa-chart-pie"></i> Dashboard Caja</a>
        <a href="listaClientes.jsp"><i class="fa-solid fa-users"></i> Clientes</a>
        <a href="inventario.jsp"><i class="fa-solid fa-boxes-stacked"></i> Inventario</a>
        <a href="mesaGrupos.jsp"><i class="fa-solid fa-object-group"></i> Grupos de Mesas</a>
        <a href="reservasBuffet.jsp" class="active"><i class="fa-solid fa-calendar-check"></i> Reservas Buffet</a>
    </div>
    <div class="main-content">
        <h1 class="page-title fade-in">Reservas Buffet</h1>

        <% if ("ok".equals(msg)) { %>
        <div class="card" style="background:rgba(39,174,96,0.1); border:1px solid var(--success); margin-bottom:16px; padding:12px; color:var(--success);">
            <i class="fa-solid fa-check-circle"></i> Operacion realizada correctamente
        </div>
        <% } %>

        <div class="card fade-in">
            <div class="card-header" style="display:flex;justify-content:space-between;align-items:center;">
                <span><i class="fa-solid fa-calendar-check"></i> Reservas (<%= reservas.size() %>)</span>
            </div>
            <div class="card-body" style="padding:0;">
                <table style="width:100%;border-collapse:collapse;">
                    <thead><tr style="border-bottom:2px solid var(--border);text-align:left;">
                        <th style="padding:10px 16px;">ID</th>
                        <th style="padding:10px;">Cliente</th>
                        <th style="padding:10px;">Fecha/Hora</th>
                        <th style="padding:10px;text-align:center;">Personas</th>
                        <th style="padding:10px;text-align:right;">Total</th>
                        <th style="padding:10px;">Estado</th>
                        <th style="padding:10px;text-align:center;">Acciones</th>
                    </tr></thead>
                    <tbody>
                    <% for (ReservaBuffet r : reservas) { %>
                    <tr style="border-bottom:1px solid var(--border);">
                        <td style="padding:10px 16px;font-weight:600;">#<%= r.getIdReserva() %></td>
                        <td style="padding:10px;"><%= r.getNombreCliente() != null ? r.getNombreCliente() : "N/A" %></td>
                        <td style="padding:10px;font-size:13px;"><%= r.getFechaHora() != null ? r.getFechaHora().toString().replace("T"," ") : "" %></td>
                        <td style="padding:10px;text-align:center;"><%= r.getPersonas() %></td>
                        <td style="padding:10px;text-align:right;font-weight:600;">S/ <%= String.format("%.2f", r.getTotal()) %></td>
                        <td style="padding:10px;"><span class="badge <%= r.getIdEstado() == 5 ? "badge-success" : r.getIdEstado() == 6 ? "badge-danger" : "badge-warning" %>"><%= r.getNombreEstado() != null ? r.getNombreEstado() : "" %></span></td>
                        <td style="padding:10px;text-align:center;">
                            <% if (r.getIdEstado() != 5 && r.getIdEstado() != 6) { %>
                            <form action="accionReservaBuffet.jsp" method="post" style="display:inline;">
                                <input type="hidden" name="idReserva" value="<%= r.getIdReserva() %>">
                                <input type="hidden" name="accion" value="confirmar">
                                <button class="btn btn-sm btn-primary"><i class="fa-solid fa-check"></i></button>
                            </form>
                            <form action="accionReservaBuffet.jsp" method="post" style="display:inline;">
                                <input type="hidden" name="idReserva" value="<%= r.getIdReserva() %>">
                                <input type="hidden" name="accion" value="cancelar">
                                <button class="btn btn-sm btn-danger"><i class="fa-solid fa-xmark"></i></button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    <% if (reservas.isEmpty()) { %>
                    <tr><td colspan="7" style="text-align:center;padding:30px;color:var(--text-light);">No hay reservas</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
