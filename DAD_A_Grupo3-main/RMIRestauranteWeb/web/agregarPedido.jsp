<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Mozo mozo = (Mozo) session.getAttribute("mozoSesion");
    if (mozo == null) { response.sendRedirect("mozoLogin.jsp"); return; }

    String idMesa = request.getParameter("idMesa") != null ? request.getParameter("idMesa") : "0";
    String idPresParam = request.getParameter("idPres") != null ? request.getParameter("idPres") : "0";
    int idPresentacion = Integer.parseInt(idPresParam);

    PresentacionControl presCtrl = new PresentacionControl();
    Presentacion pres = null;
    for (Presentacion p : presCtrl.DATOS.listar("")) { if (p.getIdPresentacion() == idPresentacion) { pres = p; break; } }
    String nombre = pres != null ? pres.getNombre() : "";
    double precio = pres != null ? pres.getPrecio() : 0;
    String producto = pres != null ? pres.getNombreProducto() : "";
    String inicialProducto = producto != null && !producto.isEmpty() ? producto.substring(0,1).toUpperCase() : "?";
    String imgUrlAgr = pres != null && pres.getImagenUrl() != null ? pres.getImagenUrl() : "";
    String[] coloresImg = {"#D4A574","#27AE60","#E74C3C","#3498DB","#9B59B6","#F39C12","#1ABC9C","#E67E22"};
    int colorIdx = Math.abs(idPresentacion) % coloresImg.length;
    String colorFondo = coloresImg[colorIdx];
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar al Pedido - Mozo</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="CSS/estilos.css">
</head>
<body>
    <div class="topbar">
        <div class="brand"><i class="fa-solid fa-leaf"></i> Overo's - Mozo</div>
        <div class="user-area"><a href="mozo.jsp?paso=pedido&idMesa=<%= idMesa %>"><i class="fa-solid fa-arrow-left"></i></a></div>
    </div>
    <div class="main-content" style="padding:20px; max-width:500px; margin:auto;">
        <div class="card fade-in">
            <div class="card-header"><i class="fa-solid fa-cart-plus"></i> Agregar al Pedido</div>
            <div class="card-body">
                <div style="text-align:center; margin-bottom:20px;">
                    <% if (!imgUrlAgr.isEmpty()) { %>
                    <img id="imgAgr" src="<%= imgUrlAgr %>" style="width:90px;height:90px;border-radius:16px;object-fit:cover;margin:0 auto 12px;box-shadow:0 4px 15px rgba(0,0,0,0.1);display:block;border:1px solid rgba(0,0,0,0.04);" onerror="document.getElementById('imgAgr').style.display='none';document.getElementById('fbAgr').style.display='flex'">
                    <% } %>
                    <div id="fbAgr" style="width:80px;height:80px;border-radius:50%;background:<%= colorFondo %>;display:<%= imgUrlAgr.isEmpty() ? "flex" : "none" %>;align-items:center;justify-content:center;margin:0 auto 12px;font-weight:700;font-size:28px;color:white;box-shadow:0 4px 15px rgba(0,0,0,0.1);">
                        <%= inicialProducto %>
                    </div>
                    <p style="color:var(--text-light); font-size:13px; text-transform:uppercase; letter-spacing:0.5px;margin-bottom:4px;"><%= producto %></p>
                    <h2 style="color:var(--primary); margin:4px 0;"><%= nombre %></h2>
                    <p style="color:var(--primary); font-weight:700; font-size:20px;">S/ <%= String.format("%.2f", precio) %></p>
                </div>
                <form action="accionMozo.jsp" method="post">
                    <input type="hidden" name="accion" value="agregarItem">
                    <input type="hidden" name="idMesa" value="<%= idMesa %>">
                    <input type="hidden" name="idPresentacion" value="<%= idPresentacion %>">
                    <div class="form-group">
                        <label>Cantidad</label>
                        <input type="number" name="cantidad" class="form-control" value="1" min="1" id="cantidad" onchange="actualizarTotal()">
                    </div>
                    <div style="background:var(--bg); padding:16px; border-radius:var(--radius); margin:16px 0; text-align:center;">
                        <p style="color:var(--text-light); margin-bottom:4px;">Subtotal</p>
                        <h2 id="totalCalc" style="color:var(--primary);">S/ <%= String.format("%.2f", precio) %></h2>
                    </div>
                    <div style="display:flex; gap:8px;">
                        <a href="mozo.jsp?paso=pedido&idMesa=<%= idMesa %>" class="btn btn-outline" style="flex:1; justify-content:center;">
                            <i class="fa-solid fa-arrow-left"></i> Volver
                        </a>
                        <button type="submit" class="btn btn-primary" style="flex:2; justify-content:center;">
                            <i class="fa-solid fa-cart-plus"></i> Agregar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        var precioUnit = <%= precio %>;
        function actualizarTotal() {
            var cant = parseInt(document.getElementById('cantidad').value);
            var total = precioUnit * cant;
            document.getElementById('totalCalc').textContent = 'S/ ' + total.toFixed(2);
        }
    </script>
</body>
</html>
