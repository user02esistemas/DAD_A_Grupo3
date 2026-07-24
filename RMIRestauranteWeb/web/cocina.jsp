<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Mozo mozo = (Mozo) session.getAttribute("mozoSesion");
    if (mozo == null) { response.sendRedirect("mozoLogin.jsp"); return; }

    MovimientoPedidoControl mpCtrl = new MovimientoPedidoControl();
    List<MovimientoPedido> pedidosCocina = mpCtrl.DATOS.listarParaCocina();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cocina - Overo's</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="CSS/estilos.css">
    <style>
        .kitchen-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:16px; }
        .order-card {
            background:var(--bg-card); border-radius:var(--radius); padding:16px;
            border-left:4px solid var(--warning); box-shadow:var(--shadow); transition:var(--transition);
        }
        .order-card.recibido { border-left-color:var(--warning); }
        .order-card.preparando { border-left-color:#3498db; }
        .order-card.listo { border-left-color:var(--success); }
        .order-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; }
        .order-header h3 { color:var(--primary); font-size:16px; }
        .order-timer {
            font-size:13px; font-weight:600; padding:4px 10px; border-radius:20px;
            background:rgba(243,156,18,0.1); color:var(--warning);
        }
        .order-timer.vencido { background:rgba(231,76,60,0.1); color:var(--danger); animation:pulse 1s infinite; }
        .order-items { list-style:none; margin-bottom:12px; }
        .order-items li { padding:4px 0; font-size:13px; color:var(--text); display:flex; justify-content:space-between; }
        .order-items li .qty { font-weight:600; color:var(--primary); margin-right:8px; }
        .order-footer { display:flex; gap:8px; }
        .order-footer .btn { flex:1; justify-content:center; font-size:12px; padding:8px; }
        .badge-estado {
            display:inline-block; padding:3px 10px; border-radius:20px;
            font-size:11px; font-weight:600; text-transform:uppercase;
        }
        .badge-recibido { background:rgba(243,156,18,0.15); color:var(--warning); }
        .badge-preparando { background:rgba(52,152,219,0.15); color:#3498db; }
        .badge-listo { background:rgba(39,174,96,0.15); color:var(--success); }
    </style>
</head>
<body>
    <div class="topbar">
        <div class="brand"><i class="fa-solid fa-fire-burner"></i> Cocina - Overo's</div>
        <div class="user-area">
            <span><i class="fa-solid fa-hat-chef"></i> <%= mozo.getNombre() %></span>
            <button onclick="location.reload()" class="btn btn-sm btn-outline" style="color:white;border-color:rgba(255,255,255,0.4);">
                <i class="fa-solid fa-rotate-right"></i>
            </button>
            <a href="index.html" title="Salir"><i class="fa-solid fa-right-from-bracket" style="color:white;"></i></a>
        </div>
    </div>

    <div class="main-content" style="padding:24px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
            <h1 class="page-title fade-in" style="margin:0;">Pedidos en Cocina</h1>
            <div style="display:flex; gap:8px;">
                <span class="badge-estado badge-recibido">Recibido</span>
                <span class="badge-estado badge-preparando">Preparando</span>
                <span class="badge-estado badge-listo">Listo</span>
            </div>
        </div>

        <div class="kitchen-grid fade-in">
            <% if (pedidosCocina.isEmpty()) { %>
            <div class="card" style="grid-column:1/-1; text-align:center; padding:60px;">
                <i class="fa-solid fa-plate-wheat" style="font-size:48px; color:var(--primary); opacity:0.2;"></i>
                <h3 style="color:var(--text-light); margin-top:16px;">No hay pedidos pendientes</h3>
            </div>
            <% } %>
            <% for (MovimientoPedido mp : pedidosCocina) {
                String estado = mp.getNombreEstadoPedido();
                String claseEstado = "recibido";
                if ("Preparando".equals(estado)) claseEstado = "preparando";
                else if ("Listo".equals(estado)) claseEstado = "listo";
            %>
            <div class="order-card <%= claseEstado %>" id="pedido-<%= mp.getIdMovimiento() %>-<%= mp.getIdPresentacion() %>">
                <div class="order-header">
                    <h3>Mesa <%= mp.getNumeroMesa() != null ? mp.getNumeroMesa() : "S/N" %></h3>
                    <span class="badge-estado badge-<%= claseEstado %>"><%= estado %></span>
                </div>
                <div style="font-size:12px; color:var(--text-light); margin-bottom:8px;">
                    <i class="fa-solid fa-utensils"></i> <%= mp.getNombreProducto() != null ? mp.getNombreProducto() : "" %>
                    (<%= mp.getNombrePresentacion() %>)
                </div>
                <ul class="order-items">
                    <li>
                        <span><span class="qty">x<%= mp.getCantidad() %></span> <%= mp.getNombrePresentacion() %></span>
                        <span>S/ <%= String.format("%.2f", mp.getTotal()) %></span>
                    </li>
                </ul>
                <% if (mp.getNombreMozo() != null) { %>
                <div style="font-size:11px; color:var(--text-light); margin-bottom:8px;">
                    <i class="fa-solid fa-user"></i> Mozo: <%= mp.getNombreMozo() %>
                </div>
                <% } %>
                <div class="order-timer" id="timer-<%= mp.getIdMovimiento() %>-<%= mp.getIdPresentacion() %>"
                     data-inicio="<%= mp.getFechaInicio() != null ? mp.getFechaInicio() : "" %>"
                     data-estimado="<%= mp.getTiempoEstimado() %>">
                    <i class="fa-solid fa-clock"></i> --:--
                </div>
                <div class="order-footer" style="margin-top:12px;">
                    <% if ("Recibido".equals(estado)) { %>
                    <form action="accionCocina.jsp" method="post" style="flex:1;">
                        <input type="hidden" name="idMovimiento" value="<%= mp.getIdMovimiento() %>">
                        <input type="hidden" name="idPresentacion" value="<%= mp.getIdPresentacion() %>">
                        <input type="hidden" name="accion" value="preparar">
                        <button type="submit" class="btn btn-sm btn-primary" style="width:100%;justify-content:center;">
                            <i class="fa-solid fa-fire"></i> Preparar
                        </button>
                    </form>
                    <% } else if ("Preparando".equals(estado)) { %>
                    <form action="accionCocina.jsp" method="post" style="flex:1;">
                        <input type="hidden" name="idMovimiento" value="<%= mp.getIdMovimiento() %>">
                        <input type="hidden" name="idPresentacion" value="<%= mp.getIdPresentacion() %>">
                        <input type="hidden" name="accion" value="listo">
                        <button type="submit" class="btn btn-sm btn-accent" style="width:100%;justify-content:center;">
                            <i class="fa-solid fa-check"></i> Listo
                        </button>
                    </form>
                    <% } else if ("Listo".equals(estado)) { %>
                    <form action="accionCocina.jsp" method="post" style="flex:1;">
                        <input type="hidden" name="idMovimiento" value="<%= mp.getIdMovimiento() %>">
                        <input type="hidden" name="idPresentacion" value="<%= mp.getIdPresentacion() %>">
                        <input type="hidden" name="accion" value="entregar">
                        <button type="submit" class="btn btn-sm btn-primary" style="width:100%;justify-content:center;background:var(--success);">
                            <i class="fa-solid fa-truck"></i> Entregado
                        </button>
                    </form>
                    <% } %>
                </div>
            </div>
            <% } %>
        </div>
    </div>

    <% String wsRol = "cocina"; %>
    <%@ include file="wsCliente.jspf" %>
    <script>
        function actualizarTimers() {
            document.querySelectorAll('.order-timer').forEach(el => {
                var inicio = el.dataset.inicio;
                var estimado = parseInt(el.dataset.estimado) || 15;
                if (!inicio) return;
                var fechaInicio = new Date(inicio);
                var ahora = new Date();
                var transcurrido = Math.floor((ahora - fechaInicio) / 1000);
                var restante = (estimado * 60) - transcurrido;
                var min = Math.floor(Math.abs(restante) / 60);
                var seg = Math.abs(restante) % 60;
                var texto = (restante >= 0 ? '' : '-') + String(min).padStart(2,'0') + ':' + String(seg).padStart(2,'0');
                el.innerHTML = '<i class="fa-solid fa-clock"></i> ' + texto;
                if (restante < 0) el.classList.add('vencido');
                else el.classList.remove('vencido');
            });
        }
        actualizarTimers();
        setInterval(actualizarTimers, 1000);

        function onWSMessage(msg) {
            if (msg.tipo === 'NUEVO_PEDIDO' || msg.tipo === 'PEDIDO_PREPARANDO' || msg.tipo === 'PEDIDO_LISTO' || msg.tipo === 'PEDIDO_ENTREGADO') {
                mostrarToast(msg.tipo);
                setTimeout(function(){ location.reload(); }, 800);
            }
        }

        function mostrarToast(texto) {
            var t = document.createElement('div');
            t.textContent = texto.replace(/_/g, ' ');
            t.style.cssText = 'position:fixed;top:80px;right:24px;background:var(--primary);color:#fff;padding:12px 20px;border-radius:12px;z-index:9999;font-weight:600;box-shadow:0 6px 20px rgba(0,0,0,0.3);';
            document.body.appendChild(t);
            setTimeout(function(){ t.remove(); }, 2500);
        }

        WS.conectar('cocina');
    </script>
</body>
</html>
