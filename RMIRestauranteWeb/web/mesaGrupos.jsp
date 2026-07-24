<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Administrador admin = (Administrador) session.getAttribute("administradorSesion");
    if (admin == null) { response.sendRedirect("administrador.jsp"); return; }

    MesaGrupoControl grupoCtrl = new MesaGrupoControl();
    MesaControl mesaCtrl = new MesaControl();
    List<MesaGrupo> grupos = grupoCtrl.DATOS.listar();
    List<Mesa> todasMesas = mesaCtrl.DATOS.listar("");
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grupos de Mesas</title>
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
        <a href="mesaGrupos.jsp" class="active"><i class="fa-solid fa-object-group"></i> Grupos de Mesas</a>
        <a href="reservasBuffet.jsp"><i class="fa-solid fa-calendar-check"></i> Reservas Buffet</a>
    </div>
    <div class="main-content">
        <h1 class="page-title fade-in">Grupos de Mesas</h1>

        <% if ("ok".equals(msg)) { %>
        <div class="card" style="background:rgba(39,174,96,0.1); border:1px solid var(--success); margin-bottom:16px; padding:12px; color:var(--success);">
            <i class="fa-solid fa-check-circle"></i> Operacion realizada correctamente
        </div>
        <% } %>

        <div class="grid grid-2 fade-in">
            <div class="card">
                <div class="card-header"><i class="fa-solid fa-plus"></i> Crear Grupo</div>
                <div class="card-body">
                    <form action="accionMesaGrupo.jsp" method="post">
                        <input type="hidden" name="accion" value="crear">
                        <div class="form-group">
                            <label>Nombre del Grupo</label>
                            <input type="text" name="nombre" class="form-control" placeholder="Ej: Fiesta X" required>
                        </div>
                        <button type="submit" class="btn btn-primary" style="width:100%;justify-content:center;">
                            <i class="fa-solid fa-plus"></i> Crear Grupo
                        </button>
                    </form>
                </div>
            </div>

            <div class="card">
                <div class="card-header"><i class="fa-solid fa-link"></i> Agregar Mesa a Grupo</div>
                <div class="card-body">
                    <form action="accionMesaGrupo.jsp" method="post">
                        <input type="hidden" name="accion" value="agregarMesa">
                        <div class="form-group">
                            <label>Mesa</label>
                            <select name="idMesa" class="form-control">
                                <% for (Mesa m : todasMesas) { %>
                                <option value="<%= m.getIdMesa() %>">Mesa <%= m.getNumero() %> - <%= m.getNombreSalon() %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Grupo</label>
                            <select name="idGrupo" class="form-control">
                                <% for (MesaGrupo g : grupos) { %>
                                <option value="<%= g.getIdMesaGrupo() %>"><%= g.getNombre() %></option>
                                <% } %>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-accent" style="width:100%;justify-content:center;">
                            <i class="fa-solid fa-link"></i> Agregar Mesa
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <div class="card mt-3 fade-in">
            <div class="card-header"><i class="fa-solid fa-object-group"></i> Grupos Activos (<%= grupos.size() %>)</div>
            <div class="card-body" style="padding:0;">
                <% for (MesaGrupo g : grupos) { %>
                <div style="display:flex;justify-content:space-between;align-items:center;padding:12px 16px;border-bottom:1px solid var(--border);">
                    <div>
                        <div style="font-weight:600;"><i class="fa-solid fa-object-group" style="color:var(--primary);"></i> <%= g.getNombre() %></div>
                        <div style="font-size:12px;color:var(--text-light);">Mesas: <%= g.getMesas() != null ? g.getMesas() : "Ninguna" %> | Personas: <%= g.getNumPersonas() %></div>
                    </div>
                    <form action="accionMesaGrupo.jsp" method="post" style="margin:0;">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="idGrupo" value="<%= g.getIdMesaGrupo() %>">
                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Eliminar grupo?')">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </form>
                </div>
                <% } %>
                <% if (grupos.isEmpty()) { %>
                <div style="text-align:center;padding:30px;color:var(--text-light);">No hay grupos creados</div>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>
