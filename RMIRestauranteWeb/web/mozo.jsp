<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Mozo mozo = (Mozo) session.getAttribute("mozoSesion");
    if (mozo == null) { response.sendRedirect("mozoLogin.jsp"); return; }

    MesaControl mesaCtrl = new MesaControl();
    SalonControl salonCtrl = new SalonControl();
    MovimientoControl movCtrl = new MovimientoControl();
    MovimientoPedidoControl mpCtrl = new MovimientoPedidoControl();
    PresentacionControl presCtrl = new PresentacionControl();
    MesaGrupoControl grupoCtrl = new MesaGrupoControl();
    MovimientoMesaControl mmCtrl = new MovimientoMesaControl();
    ProductoControl prodCtrl = new ProductoControl();

    List<Salon> salones = salonCtrl.DATOS.listar();
    String idMesaStr = request.getParameter("idMesa") != null ? request.getParameter("idMesa") : "0";
    int idMesaInt = Integer.parseInt(idMesaStr);
    String paso = request.getParameter("paso") != null ? request.getParameter("paso") : "dashboard";
    String idMovParam = request.getParameter("idMov");
    int idMovSel = idMovParam != null ? Integer.parseInt(idMovParam) : 0;
    String idMovQS = idMovSel > 0 ? ("&idMov=" + idMovSel) : "";
    List<Presentacion> presentaciones = presCtrl.DATOS.listar("");
    int idMovimientoActivo = idMovSel > 0 ? idMovSel : (idMesaInt > 0 ? movCtrl.DATOS.obtenerIdMovimientoActivoPorMesa(idMesaInt) : 0);

    // Build category map from Productos
    Map<Integer, String> mapaCategoria = new HashMap<>();
    List<String> categoriasOrden = new ArrayList<>();
    List<Producto> todosProductos = prodCtrl.DATOS.listar("");
    for (Producto prod : todosProductos) {
        mapaCategoria.put(prod.getIdProducto(), prod.getNombreTipoProducto());
        String cat = prod.getNombreTipoProducto();
        if (!categoriasOrden.contains(cat)) categoriasOrden.add(cat);
    }
    Map<String, List<Presentacion>> presPorCategoria = new LinkedHashMap<>();
    for (String cat : categoriasOrden) presPorCategoria.put(cat, new ArrayList<>());
    for (Presentacion p : presentaciones) {
        String cat = mapaCategoria.getOrDefault(p.getIdProducto(), "Otros");
        if (!presPorCategoria.containsKey(cat)) {
            presPorCategoria.put(cat, new ArrayList<>());
            categoriasOrden.add(cat);
        }
        presPorCategoria.get(cat).add(p);
    }

    // Build comandas list with mesa IDs
    List<Movimiento> comandasIds = movCtrl.DATOS.listarActivos();
    List<Movimiento> comandasAbiertas = new ArrayList<>();
    for (Movimiento tmp : comandasIds) {
        Movimiento full = movCtrl.DATOS.buscar(tmp.getIdMovimiento());
        if (full != null) {
            List<MovimientoMesa> mmList = mmCtrl.DATOS.listarPorMovimiento(full.getIdMovimiento());
            int idPrimeraMesa = 0;
            if (!mmList.isEmpty()) idPrimeraMesa = mmList.get(0).getIdMesa();
            full.setNumeroMesa(tmp.getNroDocumento() + "|" + idPrimeraMesa);
            comandasAbiertas.add(full);
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mozo - Overo's</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="CSS/estilos.css">
    <style>
        body { font-size:15px; }
        .mesa-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(110px,1fr)); gap:12px; }
        .mesa-btn {
            aspect-ratio:1; display:flex; flex-direction:column; align-items:center; justify-content:center;
            border:2px solid var(--border); border-radius:var(--radius); background:var(--bg-card);
            cursor:pointer; transition:var(--transition); font-weight:600; font-size:14px;
        }
        .mesa-btn:hover { border-color:var(--primary); transform:scale(1.05); }
        .mesa-btn.libre { border-color:var(--success); }
        .mesa-btn.ocupada { border-color:var(--warning); background:rgba(243,156,18,0.08); }
        .mesa-btn i { font-size:28px; margin-bottom:6px; }
        .step-bar { display:flex; justify-content:center; gap:20px; margin-bottom:24px; flex-wrap:wrap; }
        .step { display:flex; align-items:center; gap:6px; color:var(--text-light); font-size:13px; }
        .step.active { color:var(--primary); font-weight:700; }
        .step.done { color:var(--success); }
        .step-n {
            width:28px; height:28px; border-radius:50%; display:flex; align-items:center; justify-content:center;
            background:var(--border); color:var(--text-light); font-size:12px; font-weight:700;
        }
        .step.active .step-n { background:var(--primary); color:white; }
        .step.done .step-n { background:var(--success); color:white; }
        .salon-title { font-size:16px; font-weight:700; color:var(--secondary); margin:16px 0 8px; }

        .comanda-card {
            background:var(--bg-card); border-radius:var(--radius); padding:16px;
            box-shadow:var(--shadow); border-left:4px solid var(--accent);
            display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;
        }
        .comanda-card .info { flex:1; }
        .comanda-card .mesa-label { font-weight:700; font-size:16px; color:var(--primary); }
        .comanda-card .cliente-label { color:var(--text-light); font-size:13px; }
        .comanda-card .codigo { font-size:11px; color:var(--accent); text-transform:uppercase; letter-spacing:0.5px; }
        .comanda-badge {
            padding:3px 10px; border-radius:20px; font-size:11px; font-weight:600;
        }
        .badge-pendiente { background:rgba(243,156,18,0.15); color:#E67E22; }
        .badge-procesando { background:rgba(39,174,96,0.15); color:#27AE60; }

        .empty-state { text-align:center; padding:40px 20px; color:var(--text-light); }
        .empty-state i { font-size:48px; opacity:0.3; display:block; margin-bottom:12px; }

        .tab-bar { display:flex; gap:0; margin-bottom:20px; border-bottom:2px solid var(--border); }
        .tab-btn {
            flex:1; padding:12px 16px; border:none; background:none; font-size:14px; font-weight:600;
            color:var(--text-light); cursor:pointer; transition:var(--transition);
            border-bottom:3px solid transparent; margin-bottom:-2px;
        }
        .tab-btn.active { color:var(--primary); border-bottom-color:var(--primary); }
        .tab-btn:hover { color:var(--primary); }
        .tab-content { display:none; }
        .tab-content.active { display:block; }

        /* Category tabs for pedido */
        .cat-tabs { display:flex; overflow-x:auto; gap:6px; padding:8px 0 12px; scrollbar-width:none; }
        .cat-tabs::-webkit-scrollbar { display:none; }
        .cat-tab {
            flex-shrink:0; padding:6px 16px; border:none; border-radius:20px; font-size:12px; font-weight:600;
            cursor:pointer; background:var(--border); color:var(--text-light); transition:all 0.2s; white-space:nowrap;
        }
        .cat-tab.active { background:var(--primary); color:white; }
        .cat-section { padding:4px 0 12px; }
        .cat-section.hidden { display:none; }
        .prod-card {
            display:flex; justify-content:space-between; align-items:center;
            background:var(--bg-card); border:1px solid var(--border); border-radius:var(--radius);
            padding:10px 14px; margin-bottom:8px; border-left:3px solid var(--accent);
        }
        .prod-nombre { font-weight:600; font-size:13px; color:var(--text); }
        .prod-pres { font-size:12px; color:var(--text-light); margin-top:1px; }
        .prod-precio { font-weight:700; color:var(--primary); font-size:15px; margin-left:10px; white-space:nowrap; }
        .prod-action { display:flex; align-items:center; gap:4px; margin-left:8px; }
        .prod-action input[type=number] { width:44px; text-align:center; font-size:12px; padding:4px; border:1px solid var(--border); border-radius:6px; }
    </style>
</head>
<body>
    <div class="topbar">
        <div class="brand"><i class="fa-solid fa-leaf"></i> Overo's - Mozo</div>
        <div class="user-area">
            <span><i class="fa-solid fa-user"></i> <%= mozo.getNombre() %></span>
            <a href="mozoLogout.jsp"><i class="fa-solid fa-right-from-bracket"></i></a>
        </div>
    </div>
    <div class="main-content" style="padding:20px; max-width:900px; margin:auto;">

        <% if ("dashboard".equals(paso)) { %>
        <div class="card fade-in">
            <div class="card-header"><i class="fa-solid fa-gauge-high"></i> Panel Principal</div>
            <div class="card-body">
                <div class="tab-bar">
                    <button class="tab-btn active" onclick="showTab('comandas')">
                        <i class="fa-solid fa-receipt"></i> Comandas Abiertas
                        <% if (!comandasAbiertas.isEmpty()) { %>
                        <span style="background:var(--danger);color:white;border-radius:50%;padding:2px 7px;font-size:11px;margin-left:4px;"><%= comandasAbiertas.size() %></span>
                        <% } %>
                    </button>
                    <button class="tab-btn" onclick="showTab('mesas')">
                        <i class="fa-solid fa-chair"></i> Control Mesas
                    </button>
                </div>

                <div class="tab-content active" id="tab-comandas">
                    <% if (comandasAbiertas.isEmpty()) { %>
                    <div class="empty-state">
                        <i class="fa-solid fa-inbox"></i>
                        <p>No hay comandas pendientes</p>
                        <p style="font-size:12px;">Las comandas de clientes aparecerán aquí</p>
                    </div>
                    <% } else {
                        for (Movimiento mov : comandasAbiertas) {
                            String numMesaRaw = mov.getNumeroMesa() != null ? mov.getNumeroMesa() : "?|0";
                            String[] parts = numMesaRaw.split("\\|");
                            String numMesa = parts.length > 0 ? parts[0] : "?";
                            int idMesaMov = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                            String cliente = mov.getNombreCliente() != null ? mov.getNombreCliente() : "Sin cliente";
                            String codigo = mov.getCodigoComanda() != null ? mov.getCodigoComanda() : "";
                            boolean sinMozo = mov.getIdMozo() == 0;
                    %>
                    <div class="comanda-card">
                        <div class="info">
                            <div class="mesa-label"><i class="fa-solid fa-chair"></i> <%= numMesa %></div>
                            <div class="cliente-label"><i class="fa-solid fa-user"></i> <%= cliente %></div>
                            <div class="codigo"><%= codigo %></div>
                        </div>
                        <div style="display:flex;gap:8px;align-items:center;">
                            <% if (sinMozo) { %>
                            <span class="comanda-badge badge-pendiente"><i class="fa-solid fa-clock"></i> Pendiente</span>
                            <form action="accionMozo.jsp" method="post" style="margin:0;">
                                <input type="hidden" name="accion" value="aceptarComanda">
                                <input type="hidden" name="idMovimiento" value="<%= mov.getIdMovimiento() %>">
                                <button type="submit" class="btn btn-sm btn-primary">
                                    <i class="fa-solid fa-check"></i> Aceptar
                                </button>
                            </form>
                            <% } else { %>
                            <span class="comanda-badge badge-procesando"><i class="fa-solid fa-fire"></i> En proceso</span>
                            <a href="mozo.jsp?paso=pedido&idMesa=<%= idMesaMov %>&idMov=<%= mov.getIdMovimiento() %>" class="btn btn-sm btn-accent">
                                <i class="fa-solid fa-cart-shopping"></i> Ver
                            </a>
                            <% } %>
                        </div>
                    </div>
                    <% } } %>
                </div>

                <div class="tab-content" id="tab-mesas">
                    <p style="margin-bottom:16px;color:var(--text-light);font-size:14px;">Gestiona el estado de las mesas manualmente</p>
                    <% for (Salon s : salones) {
                        List<Mesa> mesasSalon = mesaCtrl.DATOS.listar(String.valueOf(s.getIdSalon()));
                    %>
                    <div class="salon-title"><i class="fa-solid fa-door-open"></i> <%= s.getNombre() %></div>
                    <div class="mesa-grid" style="margin-bottom:16px;">
                        <% for (Mesa m : mesasSalon) {
                            boolean libre = m.getNombreEstadoMesa() != null && m.getNombreEstadoMesa().equalsIgnoreCase("Libre");
                            int idEstadoActual = libre ? 1 : 2;
                        %>
                        <div class="mesa-btn <%= libre ? "libre" : "ocupada" %>" data-numero="<%= m.getNumero() %>">
                            <i class="fa-solid fa-chair"></i>
                            M<%= m.getNumero() %>
                            <span style="font-size:11px; font-weight:400; color:var(--text-light);"><%= m.getCapacidad() %>p</span>
                            <form action="accionMozo.jsp" method="post" style="margin-top:4px;">
                                <input type="hidden" name="accion" value="cambiarEstadoMesa">
                                <input type="hidden" name="idMesa" value="<%= m.getIdMesa() %>">
                                <input type="hidden" name="nuevoEstado" value="<%= libre ? 2 : 1 %>">
                                <button type="submit" style="border:none;background:none;color:<%= libre ? "var(--warning)" : "var(--success)" %>;font-size:10px;cursor:pointer;font-weight:600;">
                                    <i class="fa-solid fa-<%= libre ? "lock" : "unlock" %>"></i>
                                    <%= libre ? "Ocupar" : "Liberar" %>
                                </button>
                            </form>
                        </div>
                        <% } %>
                    </div>
                    <% } %>
                </div>

                <div style="margin-top:20px;">
                    <a href="mozo.jsp?paso=seleccionar" class="btn btn-primary" style="width:100%;justify-content:center;">
                        <i class="fa-solid fa-plus"></i> Nueva Comanda (Sin dispositivo)
                    </a>
                </div>
            </div>
        </div>

        <% } else if ("seleccionar".equals(paso)) { %>
        <div class="card fade-in">
            <div class="card-header"><i class="fa-solid fa-table"></i> Seleccionar Mesa</div>
            <div class="card-body">
                <input type="text" id="buscarMesa" class="form-control" placeholder="Buscar mesa..." oninput="filtrarMesas()" style="margin-bottom:16px;">
                <% for (Salon s : salones) {
                    List<Mesa> mesasSalon = mesaCtrl.DATOS.listar(String.valueOf(s.getIdSalon()));
                %>
                <div class="salon-title"><i class="fa-solid fa-door-open"></i> <%= s.getNombre() %></div>
                <div class="mesa-grid" style="margin-bottom:12px;">
                    <% for (Mesa m : mesasSalon) {
                        boolean libre = m.getNombreEstadoMesa() != null && m.getNombreEstadoMesa().equalsIgnoreCase("Libre");
                        String clase = libre ? "libre" : "ocupada";
                    %>
                    <a href="mozo.jsp?paso=cliente&idMesa=<%= m.getIdMesa() %>" class="mesa-btn <%= clase %>" data-numero="<%= m.getNumero() %>">
                        <i class="fa-solid fa-chair"></i>
                        M<%= m.getNumero() %>
                        <span style="font-size:11px; font-weight:400; color:var(--text-light);"><%= m.getCapacidad() %>p</span>
                    </a>
                    <% } %>
                </div>
                <% } %>
                <a href="mozo.jsp?paso=dashboard" class="btn btn-outline" style="width:100%;justify-content:center;margin-top:12px;">
                    <i class="fa-solid fa-arrow-left"></i> Volver al Panel
                </a>
            </div>
        </div>

        <% } else if ("cliente".equals(paso)) { %>
        <div class="card fade-in">
            <div class="card-header"><i class="fa-solid fa-user-plus"></i> Datos del Cliente</div>
            <div class="card-body">
                <form action="mozo.jsp" method="get">
                    <input type="hidden" name="paso" value="pedido">
                    <input type="hidden" name="idMesa" value="<%= idMesaStr %>">
                    <div class="form-group">
                        <label>Nombre del Cliente</label>
                        <input type="text" name="clienteNombre" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Tipo Documento</label>
                        <select name="idTipoDocumento" class="form-control">
                            <option value="1">DNI</option>
                            <option value="2">RUC</option>
                            <option value="3">Carnet</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Numero Documento</label>
                        <input type="text" name="clienteDoc" class="form-control">
                    </div>
                    <div class="form-group">
                        <label>Telefono</label>
                        <input type="text" name="clienteTel" class="form-control">
                    </div>
                    <div style="display:flex; gap:8px; margin-top:16px;">
                        <a href="mozo.jsp?paso=seleccionar" class="btn btn-outline" style="flex:1;justify-content:center;">
                            <i class="fa-solid fa-arrow-left"></i> Volver
                        </a>
                        <button type="submit" class="btn btn-primary" style="flex:2;justify-content:center;">
                            <i class="fa-solid fa-arrow-right"></i> Siguiente
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <% } else if ("pedido".equals(paso)) { %>
        <div class="card fade-in">
            <div class="card-header" style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:6px;">
                <span><i class="fa-solid fa-cart-shopping"></i> Tomar Pedido</span>
                <div style="display:flex;gap:6px;">
                    <button class="btn btn-sm btn-outline" onclick="mostrarUnirMesas()" title="Unir otra mesa">
                        <i class="fa-solid fa-link"></i> Unir Mesa
                    </button>
                </div>
            </div>
            <div class="card-body">
                <input type="text" id="buscarProd" class="form-control" placeholder="Buscar producto..." oninput="filtrarProd()" style="margin-bottom:12px;">

                <nav class="cat-tabs" id="catTabs">
                    <% int catIdx = 0; boolean hasItems = false;
                    for (String cat : categoriasOrden) {
                        List<Presentacion> items = presPorCategoria.get(cat);
                        if (items == null || items.isEmpty()) continue;
                        hasItems = true;
                    %>
                    <button class="cat-tab <%= catIdx == 0 ? "active" : "" %>" data-cat="<%= catIdx %>"><%= cat %></button>
                    <% catIdx++; } %>
                </nav>

                <% if (!hasItems) { %>
                <div class="empty-state">
                    <i class="fa-solid fa-plate-wheat"></i>
                    <p>No hay productos disponibles</p>
                </div>
                <% } else {
                    catIdx = 0;
                    for (String cat : categoriasOrden) {
                        List<Presentacion> items = presPorCategoria.get(cat);
                        if (items == null || items.isEmpty()) { catIdx++; continue; }
                %>
                <div class="cat-section <%= catIdx == 0 ? "" : "hidden" %>" data-cat="<%= catIdx %>">
                    <% for (Presentacion p : items) {
                        String nombrePres = p.getNombre() != null ? p.getNombre() : "";
                        String nomProd = p.getNombreProducto() != null ? p.getNombreProducto() : "";
                        double precio = p.getPrecio();
                    %>
                    <div class="prod-card" data-nombre="<%= (nomProd + " " + nombrePres).toLowerCase() %>">
                        <div class="prod-img" style="width:44px;height:44px;border-radius:10px;background:var(--accent);display:flex;align-items:center;justify-content:center;font-weight:700;font-size:14px;color:var(--secondary);flex-shrink:0;margin-right:10px;">
                            <%= nomProd.length() > 0 ? nomProd.substring(0,1).toUpperCase() : "?" %>
                        </div>
                        <div class="prod-body" style="flex:1;min-width:0;">
                            <div class="prod-nombre"><%= nomProd %></div>
                            <div class="prod-pres"><%= nombrePres %></div>
                        </div>
                        <div class="prod-precio">S/ <%= String.format("%.2f", precio) %></div>
                        <div class="prod-action">
                            <a href="agregarPedido.jsp?idMesa=<%= idMesaStr %><%= idMovQS %>&idPres=<%= p.getIdPresentacion() %>" class="btn btn-sm btn-primary" style="padding:6px 10px;font-size:13px;">
                                <i class="fa-solid fa-plus"></i>
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% catIdx++; } } %>

                <div style="display:flex; gap:8px; margin-top:12px;">
                    <a href="mozo.jsp?paso=dashboard" class="btn btn-outline" style="flex:1;justify-content:center;">
                        <i class="fa-solid fa-arrow-left"></i> Volver
                    </a>
                    <% if (idMovimientoActivo > 0) { %>
                    <a href="mozo.jsp?paso=confirmar&idMesa=<%= idMesaStr %><%= idMovQS %>" class="btn btn-primary" style="flex:2;justify-content:center;">
                        <i class="fa-solid fa-clipboard-check"></i> Revisar Pedido
                    </a>
                    <% } %>
                </div>
            </div>
        </div>

        <% } else if ("confirmar".equals(paso)) {
            String estadoComanda = "";
            String nombreMozo = "";
            String nombreCliente = "";
            String codigoComanda = "";
            Movimiento movFull = null;
            List<MovimientoMesa> mesasDelMov = new ArrayList<>();
            List<MovimientoPedido> pedidos = new ArrayList<>();
            double total = 0;
            if (idMovimientoActivo > 0) {
                movFull = movCtrl.DATOS.buscar(idMovimientoActivo);
                if (movFull != null) {
                    estadoComanda = movFull.getNombreEstadoComanda() != null ? movFull.getNombreEstadoComanda() : "—";
                    nombreMozo = movFull.getNombreMozo() != null ? movFull.getNombreMozo() : mozo.getNombre();
                    nombreCliente = movFull.getNombreCliente() != null ? movFull.getNombreCliente() : "—";
                    codigoComanda = movFull.getCodigoComanda() != null ? movFull.getCodigoComanda() : "";
                }
                mesasDelMov = mmCtrl.DATOS.listarPorMovimiento(idMovimientoActivo);
                pedidos = mpCtrl.DATOS.listarPorMovimiento(idMovimientoActivo);
                for (MovimientoPedido mp : pedidos) total += mp.getTotal();
            }
        %>
        <div class="card fade-in">
            <div class="card-header" style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:6px;">
                <span><i class="fa-solid fa-clipboard-check"></i> Revisar Pedido</span>
                <span style="font-size:12px;background:var(--accent);color:var(--secondary);padding:3px 12px;border-radius:20px;font-weight:600;"><%= estadoComanda %></span>
            </div>
            <div class="card-body">
                <% if (movFull == null) { %>
                <div class="empty-state">
                    <i class="fa-solid fa-inbox"></i>
                    <p>No hay pedidos registrados.</p>
                </div>
                <% } else { %>

                <!-- Info cabecera -->
                <div style="background:var(--bg);border-radius:var(--radius);padding:14px;margin-bottom:16px;">
                    <div style="display:flex;justify-content:space-between;margin-bottom:10px;flex-wrap:wrap;gap:8px;">
                        <div>
                            <div style="font-size:11px;color:var(--text-light);text-transform:uppercase;letter-spacing:0.5px;">Comanda</div>
                            <div style="font-weight:700;font-size:15px;color:var(--primary);"><%= codigoComanda %></div>
                            <div style="font-size:11px;color:var(--text-light);">#<%= idMovimientoActivo %></div>
                        </div>
                        <div style="text-align:right;">
                            <div style="font-size:11px;color:var(--text-light);text-transform:uppercase;letter-spacing:0.5px;">Mozo</div>
                            <div style="font-weight:600;font-size:14px;"><i class="fa-solid fa-user-tie"></i> <%= nombreMozo %></div>
                        </div>
                    </div>
                    <div style="display:flex;gap:12px;flex-wrap:wrap;">
                        <div>
                            <div style="font-size:11px;color:var(--text-light);text-transform:uppercase;letter-spacing:0.5px;">Cliente</div>
                            <div style="font-weight:500;font-size:13px;"><%= nombreCliente %></div>
                        </div>
                        <div>
                            <div style="font-size:11px;color:var(--text-light);text-transform:uppercase;letter-spacing:0.5px;">Mesas</div>
                            <div style="font-weight:500;font-size:13px;display:flex;gap:4px;flex-wrap:wrap;">
                                <% if (mesasDelMov.isEmpty()) { %>
                                <span><i class="fa-solid fa-chair"></i> <%= idMesaStr %></span>
                                <% } else {
                                    List<Mesa> todasMlocal = mesaCtrl.DATOS.listar("");
                                    for (MovimientoMesa mmRel : mesasDelMov) {
                                        String numM = "";
                                        for (Mesa mx : todasMlocal) { if (mx.getIdMesa() == mmRel.getIdMesa()) { numM = mx.getNumero(); break; } }
                                %>
                                <span style="background:var(--border);padding:2px 10px;border-radius:12px;font-size:12px;"><i class="fa-solid fa-chair"></i> Mesa <%= numM %></span>
                                <% } } %>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Items del pedido agrupados por categoria -->
                <div style="font-size:13px;font-weight:700;color:var(--secondary);margin-bottom:10px;display:flex;align-items:center;gap:6px;">
                    <i class="fa-solid fa-utensils"></i> Detalle del Pedido
                    <span style="font-weight:400;font-size:12px;color:var(--text-light);">(<%= pedidos.size() %> items)</span>
                </div>

                <% if (pedidos.isEmpty()) { %>
                <div style="text-align:center;padding:20px;color:var(--text-light);font-size:13px;">
                    <i class="fa-solid fa-cart-empty" style="font-size:32px;opacity:0.3;display:block;margin-bottom:8px;"></i>
                    Sin items aún
                </div>
                <% } else {
                    // Group pedidos by category
                    List<String> catsPedido = new ArrayList<>(categoriasOrden);
                    Map<String, List<MovimientoPedido>> pedidosCat = new LinkedHashMap<>();
                    for (String cat : catsPedido) pedidosCat.put(cat, new ArrayList<>());
                    for (MovimientoPedido mp : pedidos) {
                        String catItem = "Otros";
                        int idPresItem = mp.getIdPresentacion();
                        for (Presentacion pp : presentaciones) {
                            if (pp.getIdPresentacion() == idPresItem) {
                                catItem = mapaCategoria.getOrDefault(pp.getIdProducto(), "Otros");
                                break;
                            }
                        }
                        if (!pedidosCat.containsKey(catItem)) { pedidosCat.put(catItem, new ArrayList<>()); catsPedido.add(catItem); }
                        pedidosCat.get(catItem).add(mp);
                    }
                    for (String cat : catsPedido) {
                        List<MovimientoPedido> itemsCat = pedidosCat.get(cat);
                        if (itemsCat == null || itemsCat.isEmpty()) continue;
                %>
                <div style="margin-bottom:12px;">
                    <div style="font-size:11px;font-weight:700;color:var(--text-light);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:4px;"><%= cat %></div>
                    <% for (MovimientoPedido mp : itemsCat) { %>
                    <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid var(--border);">
                        <div style="flex:1;min-width:0;">
                            <div style="font-weight:500;font-size:13px;"><%= mp.getNombrePresentacion() != null ? mp.getNombrePresentacion() : "—" %></div>
                        </div>
                        <div style="display:flex;align-items:center;gap:8px;">
                            <span style="font-size:12px;color:var(--text-light);">x<%= mp.getCantidad() %></span>
                            <span style="font-weight:600;font-size:13px;color:var(--primary);">S/ <%= String.format("%.2f", mp.getTotal()) %></span>
                            <a href="accionMozo.jsp?accion=eliminarItem&idMovimiento=<%= idMovimientoActivo %>&idPresentacion=<%= mp.getIdPresentacion() %>&idMesa=<%= idMesaStr %>" style="color:var(--danger);font-size:14px;text-decoration:none;padding:4px;" onclick="return confirm('¿Eliminar este item?')">
                                <i class="fa-solid fa-trash-can"></i>
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } } %>

                <!-- Total -->
                <div style="display:flex;justify-content:space-between;align-items:center;padding:14px 0;margin-top:4px;border-top:2px solid var(--primary);">
                    <span style="font-weight:700;font-size:18px;">Total</span>
                    <span style="font-weight:700;font-size:22px;color:var(--primary);">S/ <%= String.format("%.2f", total) %></span>
                </div>

                <% } %>
                <div style="display:flex; gap:8px; margin-top:16px;">
                    <a href="mozo.jsp?paso=pedido&idMesa=<%= idMesaStr %><%= idMovQS %>" class="btn btn-outline" style="flex:1;justify-content:center;">
                        <i class="fa-solid fa-arrow-left"></i> Agregar Más
                    </a>
                    <% if (idMovimientoActivo > 0) { %>
                    <form action="accionMozo.jsp" method="post" style="flex:2;">
                        <input type="hidden" name="accion" value="enviarCocina">
                        <input type="hidden" name="idMesa" value="<%= idMesaStr %>">
                        <button type="submit" class="btn btn-primary" style="width:100%;justify-content:center;">
                            <i class="fa-solid fa-fire-burner"></i> Enviar a Cocina
                        </button>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <script>
        var idMesaActual = <%= idMesaInt %>;
        var idMovActual = <%= idMovimientoActivo %>;
        var mesasData = [
            <% 
            List<Mesa> todasM = mesaCtrl.DATOS.listar("");
            List<Salon> todosSalones = salonCtrl.DATOS.listar();
            Map<Integer, String> salonMap = new HashMap<>();
            for (Salon s : todosSalones) salonMap.put(s.getIdSalon(), s.getNombre());
            for (Mesa mm : todasM) {
                String salonName = mm.getNombreSalon() != null ? mm.getNombreSalon() : salonMap.getOrDefault(mm.getIdSalon(), "Sin Salón");
                boolean libreMM = mm.getNombreEstadoMesa() != null && mm.getNombreEstadoMesa().equalsIgnoreCase("Libre");
            %>
            {id:<%= mm.getIdMesa() %>,num:"<%= mm.getNumero() %>",cap:<%= mm.getCapacidad() %>,salon:"<%= salonName %>",libre:<%= libreMM ? "true" : "false" %>},
            <% } %>
        ];

        function mostrarUnirMesas() {
            var html = '<div class="overlay" id="overlayUnir" style="position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(27,67,50,0.85);z-index:200;display:flex;align-items:center;justify-content:center;backdrop-filter:blur(8px);">';
            html += '<div style="background:#fff;border-radius:16px;padding:24px;width:90%;max-width:450px;max-height:85vh;overflow-y:auto;">';
            if (idMovActual === 0) {
                html += '<h3 style="margin-bottom:12px;"><i class="fa-solid fa-link"></i> Unir Mesa</h3>';
                html += '<p style="color:#E74C3C;text-align:center;padding:20px;">No hay una comanda activa. Agregue items primero.</p>';
            } else {
                html += '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">';
                html += '<h3 style="margin:0;"><i class="fa-solid fa-link"></i> Unir Mesa</h3>';
                html += '<span style="font-size:12px;color:var(--text-light);">Comanda #' + idMovActual + '</span>';
                html += '</div>';

                var agrupadas = {};
                mesasData.forEach(function(m) {
                    if (!agrupadas[m.salon]) agrupadas[m.salon] = { libres: [], ocupadas: [] };
                    if (m.libre && m.id !== idMesaActual) agrupadas[m.salon].libres.push(m);
                    else if (!m.libre) agrupadas[m.salon].ocupadas.push(m);
                });

                html += '<form id="formUnir" action="accionMozo.jsp" method="post">';
                html += '<input type="hidden" name="accion" value="unirMesas">';
                html += '<input type="hidden" name="idMovimiento" value="' + idMovActual + '">';
                html += '<input type="hidden" name="idMesaActual" value="' + idMesaActual + '">';

                var hayLibres = false;
                Object.keys(agrupadas).forEach(function(salon) {
                    var g = agrupadas[salon];
                    html += '<div style="margin-bottom:12px;">';
                    html += '<div style="font-size:12px;font-weight:700;color:var(--secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:4px;"><i class="fa-solid fa-door-open"></i> ' + salon + '</div>';
                    g.libres.forEach(function(m) {
                        hayLibres = true;
                        var checked = (m.id === idMesaActual) ? 'disabled' : '';
                        html += '<label style="display:flex;align-items:center;gap:10px;padding:8px 12px;border:2px solid var(--success);border-radius:10px;margin-bottom:6px;cursor:pointer;background:rgba(39,174,96,0.04);">';
                        html += '<input type="checkbox" name="idMesaUnir" value="' + m.id + '" ' + checked + ' style="width:18px;height:18px;accent-color:var(--success);">';
                        html += '<span style="flex:1;"><strong>Mesa ' + m.num + '</strong> <span style="color:var(--text-light);font-size:12px;">' + m.cap + ' personas</span></span>';
                        html += '<span style="font-size:11px;color:var(--success);font-weight:600;"><i class="fa-solid fa-circle"></i> Libre</span>';
                        html += '</label>';
                    });
                    g.ocupadas.forEach(function(m) {
                        html += '<div style="display:flex;align-items:center;gap:10px;padding:8px 12px;border:2px solid var(--border);border-radius:10px;margin-bottom:6px;opacity:0.5;">';
                        html += '<span style="flex:1;"><strong>Mesa ' + m.num + '</strong> <span style="color:var(--text-light);font-size:12px;">' + m.cap + ' personas</span></span>';
                        html += '<span style="font-size:11px;color:var(--text-light);font-weight:600;"><i class="fa-solid fa-circle"></i> Ocupada</span>';
                        html += '</div>';
                    });
                    html += '</div>';
                });

                if (!hayLibres) {
                    html += '<p style="color:var(--text-light);text-align:center;padding:20px;">No hay mesas libres disponibles en ningún salón.</p>';
                } else {
                    html += '<button type="submit" class="btn btn-primary" style="width:100%;justify-content:center;">';
                    html += '<i class="fa-solid fa-link"></i> Unir Seleccionadas</button>';
                }
                html += '</form>';
            }
            html += '<button onclick="this.closest(\'.overlay\').remove()" style="width:100%;padding:10px;border:2px solid #E8E0D5;border-radius:12px;background:transparent;font-weight:600;cursor:pointer;margin-top:8px;">Cancelar</button>';
            html += '</div></div>';
            var div = document.createElement('div');
            div.innerHTML = html;
            document.body.appendChild(div);
        }

        function showTab(tab) {
            document.querySelectorAll('.tab-btn').forEach(function(b) { b.classList.remove('active'); });
            document.querySelectorAll('.tab-content').forEach(function(c) { c.classList.remove('active'); });
            event.target.closest('.tab-btn').classList.add('active');
            document.getElementById('tab-' + tab).classList.add('active');
        }
        function filtrarMesas() {
            var f = document.getElementById('buscarMesa').value.toLowerCase();
            document.querySelectorAll('.mesa-btn').forEach(function(b) {
                b.style.display = (b.dataset.numero || '').includes(f) ? '' : 'none';
            });
        }
        function filtrarProd() {
            var f = document.getElementById('buscarProd').value.toLowerCase();
            document.querySelectorAll('.prod-card').forEach(function(c) {
                c.style.display = (c.dataset.nombre || '').includes(f) ? '' : 'none';
            });
        }

        document.addEventListener('click', function(e) {
            var tab = e.target.closest('.cat-tab');
            if (!tab) return;
            document.querySelectorAll('.cat-tab').forEach(function(t){ t.classList.remove('active'); });
            tab.classList.add('active');
            var idx = tab.getAttribute('data-cat');
            document.querySelectorAll('.cat-section').forEach(function(s){
                s.classList.toggle('hidden', s.getAttribute('data-cat') !== idx);
            });
        });

        function onWSMessage(msg) {
            var toast = document.createElement('div');
            var texto = '';
            var color = '#1B4332';
            if (msg.tipo === 'LLAMAR_MOZO') {
                texto = 'Llaman desde la Mesa ' + (msg.mesa || '?');
                color = '#D4A574';
            } else if (msg.tipo === 'PEDIDO_LISTO') {
                texto = 'Pedido listo (Mov #' + (msg.idMovimiento || '?') + ')';
                color = '#27AE60';
            } else if (msg.tipo === 'COMANDA_ACEPTADA') {
                texto = 'Comanda aceptada por ' + (msg.mozo || 'otro mozo');
            } else {
                texto = msg.tipo || 'Notificacion';
            }
            toast.textContent = texto;
            toast.style.cssText = 'position:fixed;top:80px;right:24px;background:' + color + ';color:#fff;padding:14px 22px;border-radius:12px;z-index:9999;font-weight:600;box-shadow:0 6px 20px rgba(0,0,0,0.3);';
            document.body.appendChild(toast);
            setTimeout(function(){ toast.remove(); }, 4000);
            if (msg.tipo === 'PEDIDO_LISTO' || msg.tipo === 'COMANDA_ACEPTADA') {
                setTimeout(function(){ location.reload(); }, 1200);
            }
        }
    </script>
    <% String wsRol = "mozo"; %>
    <%@ include file="wsCliente.jspf" %>
    <script>WS.conectar('mozo');</script>
</body>
</html>