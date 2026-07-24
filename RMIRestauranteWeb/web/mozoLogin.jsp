<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String error = "";
    String rol = request.getParameter("rol") != null ? request.getParameter("rol") : "mozo";
    String usuario = request.getParameter("usuario");
    String clave = request.getParameter("clave");
    if (usuario != null && clave != null) {
        try {
            MozoControl ctrl = new MozoControl();
            Mozo mozo = ctrl.DATOS.login(usuario, clave);
            if (mozo != null) {
                session.setAttribute("mozoSesion", mozo);
                String destino = "cocina".equals(rol) ? "cocina.jsp" : "mozo.jsp";
                response.sendRedirect(destino);
                return;
            } else {
                error = "Credenciales incorrectas";
            }
        } catch (Exception e) {
            error = "Error de conexion: " + e.getMessage();
        }
    }
    String titulo = "cocina".equals(rol) ? "Cocina" : "Mozo";
    String icono = "cocina".equals(rol) ? "fa-fire-burner" : "fa-clipboard-list";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= titulo %> Login - Overo's</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="CSS/estilos.css">
</head>
<body>
    <div class="login-wrapper">
        <div class="login-card fade-in">
            <div class="logo">
                <i class="fa-solid <%= icono %>"></i>
            </div>
            <h2><%= titulo %></h2>
            <p class="subtitle">Inicia sesion para comenzar</p>
            <% if (!error.isEmpty()) { %>
            <div class="toast toast-error" style="margin-bottom:16px;">
                <i class="fa-solid fa-circle-exclamation"></i> <%= error %>
            </div>
            <% } %>
            <form action="mozoLogin.jsp?rol=<%= rol %>" method="post">
                <div class="form-group">
                    <label>Usuario</label>
                    <input type="text" name="usuario" class="form-control" placeholder="Ej: juan" required autofocus>
                </div>
                <div class="form-group">
                    <label>Clave</label>
                    <input type="password" name="clave" class="form-control" placeholder="Tu clave" required>
                </div>
                <button type="submit" class="btn btn-primary btn-lg" style="width:100%;justify-content:center;margin-top:8px;">
                    <i class="fa-solid fa-right-to-bracket"></i> Entrar
                </button>
            </form>
            <div style="text-align:center;margin-top:20px;">
                <a href="index.html" style="color:var(--text-light);font-size:13px;text-decoration:none;">
                    <i class="fa-solid fa-arrow-left"></i> Volver al inicio
                </a>
            </div>
        </div>
    </div>
</body>
</html>
