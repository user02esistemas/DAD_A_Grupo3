<%@page import="Control.*"%>
<%@page import="DTO.*"%>
<%@page import="java.util.List"%>
<%@page import="java.security.MessageDigest"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String mesaParam = request.getParameter("mesa");
    String hashParam = request.getParameter("h");
    int idMesa = 0;
    String numeroMesa = "";
    String salonMesa = "";
    boolean hashValido = false;

    if (mesaParam != null && hashParam != null) {
        idMesa = Integer.parseInt(mesaParam);
        DatosEmpresaControl empCtrl = new DatosEmpresaControl();
        List<DatosEmpresa> empresas = empCtrl.DATOS.listar();
        String secret = "overos2026";
        String baseUrl = "http://localhost:8080/RMIRestauranteWeb";
        if (!empresas.isEmpty()) {
            DatosEmpresa emp = empresas.get(0);
            if (emp.getQrSecret() != null) secret = emp.getQrSecret();
            if (emp.getQrBaseUrl() != null) baseUrl = emp.getQrBaseUrl();
        }
        String data = "mesa" + idMesa + secret;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) sb.append(String.format("%02x", b));
        String hashCalculado = sb.toString().substring(0, 16);
        hashValido = hashCalculado.equals(hashParam);

        if (hashValido) {
            MesaControl mesaCtrl = new MesaControl();
            List<Mesa> todasMesas = mesaCtrl.DATOS.listar("");
            Mesa mesa = null;
            for (Mesa m : todasMesas) {
                if (m.getIdMesa() == idMesa) { mesa = m; break; }
            }
            if (mesa != null) {
                numeroMesa = mesa.getNumero();
                SalonControl salonCtrl = new SalonControl();
                List<Salon> todosSalones = salonCtrl.DATOS.listar();
                for (Salon s : todosSalones) {
                    if (s.getIdSalon() == mesa.getIdSalon()) { salonMesa = s.getNombre(); break; }
                }
            }
        }
    }

    MovimientoControl movCtrlCliente = new MovimientoControl();
    int idMovExistente = (idMesa > 0 && hashValido) ? movCtrlCliente.DATOS.obtenerIdMovimientoActivoPorMesa(idMesa) : 0;

    PresentacionControl presCtrl = new PresentacionControl();
    List<Presentacion> presentaciones = hashValido ? presCtrl.DATOS.listar("") : new java.util.ArrayList<>();
    
    java.util.Map<Integer, String> mapCategoria = new java.util.HashMap<>();
    List<String> categoriasOrden = new java.util.ArrayList<>();
    if (hashValido) {
        ProductoControl prodCtrl = new ProductoControl();
        List<Producto> productos = prodCtrl.DATOS.listar("");
        for (Producto prod : productos) {
            if (!mapCategoria.containsKey(prod.getIdProducto())) {
                mapCategoria.put(prod.getIdProducto(), prod.getNombreTipoProducto());
            }
        }
        for (Producto prod : productos) {
            String cat = prod.getNombreTipoProducto();
            if (!categoriasOrden.contains(cat)) categoriasOrden.add(cat);
        }
    }
    java.util.Map<String, List<Presentacion>> presPorCategoria = new java.util.LinkedHashMap<>();
    for (String cat : categoriasOrden) {
        presPorCategoria.put(cat, new java.util.ArrayList<>());
    }
    for (Presentacion p : presentaciones) {
        int idProd = p.getIdProducto();
        String cat = mapCategoria.getOrDefault(idProd, "Otros");
        if (!presPorCategoria.containsKey(cat)) {
            presPorCategoria.put(cat, new java.util.ArrayList<>());
            categoriasOrden.add(cat);
        }
        presPorCategoria.get(cat).add(p);
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Overo's - Menú</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:wght@600;700&display=swap');
        :root {
            --primary:#1B4332;--primary-light:#2D6A4F;--secondary:#5C4033;
            --accent:#D4A574;--bg:#FAF6F0;--bg-card:#FFF;--text:#2D3436;
            --text-light:#636E72;--border:#E8E0D5;--success:#27AE60;
            --danger:#E74C3C;--radius:12px;--shadow:0 2px 12px rgba(27,67,50,0.08);
        }
        *{margin:0;padding:0;box-sizing:border-box;}
        body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);max-width:480px;margin:auto;min-height:100vh;position:relative;}

        .topbar{background:var(--primary);color:white;padding:14px 20px;display:flex;justify-content:space-between;align-items:center;position:sticky;top:0;z-index:100;}
        .topbar .brand{font-family:'Playfair Display',serif;font-size:18px;font-weight:700;}
        .topbar .mesa-info{background:var(--accent);color:var(--secondary);padding:4px 12px;border-radius:20px;font-weight:600;font-size:13px;}

        .overlay{position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(27,67,50,0.85);z-index:200;display:flex;align-items:center;justify-content:center;backdrop-filter:blur(8px);}
        .overlay.hidden{display:none;}
        .overlay-card{background:var(--bg-card);border-radius:16px;padding:32px 24px;width:90%;max-width:380px;box-shadow:0 20px 50px rgba(0,0,0,0.3);text-align:center;}
        .overlay-card h2{font-family:'Playfair Display',serif;color:var(--primary);font-size:22px;margin-bottom:8px;}
        .overlay-card p{color:var(--text-light);font-size:14px;margin-bottom:20px;}
        .overlay-card .mesa-num{font-size:48px;font-weight:700;color:var(--accent);margin:12px 0;}
        .overlay-card .mesa-salon{color:var(--text-light);font-size:13px;margin-bottom:20px;}
        .overlay-card .icon-big{font-size:48px;color:var(--primary);margin-bottom:16px;}

        .form-group{margin-bottom:14px;text-align:left;}
        .form-group label{display:block;font-size:12px;font-weight:600;color:var(--text-light);margin-bottom:4px;text-transform:uppercase;letter-spacing:0.5px;}
        .form-group input,.form-group select{width:100%;padding:10px 14px;border:2px solid var(--border);border-radius:var(--radius);font-size:14px;transition:all 0.3s;}
        .form-group input:focus{border-color:var(--primary);outline:none;}

        .btn{display:inline-flex;align-items:center;justify-content:center;gap:8px;padding:12px 24px;border:none;border-radius:var(--radius);font-size:15px;font-weight:600;cursor:pointer;transition:all 0.3s;width:100%;}
        .btn-primary{background:var(--primary);color:white;}
        .btn-primary:hover{background:var(--primary-light);}
        .btn-accent{background:var(--accent);color:var(--secondary);}
        .btn-outline{background:transparent;border:2px solid var(--border);color:var(--text-light);}
        .btn-outline:hover{border-color:var(--primary);color:var(--primary);}

        /* Catalogo */
        .catalogo-contenido{padding-bottom:100px;}
        .cat-tabs{display:flex;overflow-x:auto;gap:8px;padding:12px 16px 8px;scrollbar-width:none;position:sticky;top:56px;background:var(--bg);z-index:50;}
        .cat-tabs::-webkit-scrollbar{display:none;}
        .cat-tab{flex-shrink:0;padding:8px 18px;border:none;border-radius:20px;font-size:13px;font-weight:600;cursor:pointer;background:var(--border);color:var(--text-light);transition:all 0.3s;white-space:nowrap;}
        .cat-tab.active{background:var(--primary);color:white;}
        .cat-section{padding:8px 16px 16px;}
        .cat-section.hidden{display:none;}
        .prod-card{display:flex;justify-content:space-between;align-items:center;background:var(--bg-card);border-radius:var(--radius);padding:14px 16px;margin-bottom:10px;box-shadow:var(--shadow);border-left:4px solid var(--accent);transition:transform 0.2s;}
        .prod-card:active{transform:scale(0.98);}
        .prod-card.promo{border-left-color:var(--accent);background:linear-gradient(135deg,#FFF8F0,#FFF);}
        .prod-body{flex:1;min-width:0;}
        .prod-nombre{font-weight:600;font-size:14px;color:var(--text);}
        .prod-pres{font-size:13px;color:var(--text-light);margin-top:2px;}
        .badge-promo{display:inline-block;background:var(--accent);color:var(--secondary);font-size:10px;font-weight:700;padding:2px 8px;border-radius:10px;margin-top:4px;letter-spacing:0.3px;}
        .prod-precio{font-weight:700;color:var(--primary);font-size:17px;margin-left:12px;white-space:nowrap;}

        .btn-mozo{position:fixed;bottom:24px;right:24px;width:60px;height:60px;border-radius:50%;background:var(--accent);color:var(--secondary);border:none;font-size:24px;box-shadow:0 4px 20px rgba(212,165,116,0.5);cursor:pointer;z-index:50;transition:all 0.3s;display:flex;align-items:center;justify-content:center;}
        .btn-mozo:hover{transform:scale(1.1);box-shadow:0 6px 30px rgba(212,165,116,0.7);}
        .btn-mozo.enviando{background:var(--success);color:white;animation:pulse 1s infinite;}

        @keyframes pulse{0%,100%{transform:scale(1)}50%{transform:scale(1.1)}}

        .error-page{display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:100vh;padding:32px;text-align:center;}
        .error-page i{font-size:64px;color:var(--danger);opacity:0.5;margin-bottom:16px;}
        .error-page h2{color:var(--secondary);margin-bottom:8px;}

        .toast{position:fixed;top:80px;left:50%;transform:translateX(-50%);background:var(--success);color:white;padding:12px 24px;border-radius:var(--radius);font-weight:600;z-index:300;display:none;box-shadow:0 4px 20px rgba(0,0,0,0.2);}
        .toast.show{display:block;animation:fadeIn 0.3s ease;}
    </style>
</head>
<body>
<% if (!hashValido || idMesa == 0) { %>
    <div class="error-page">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <h2>QR no válido</h2>
        <p style="color:var(--text-light);">El código QR no es válido o ha expirado. Solicite un nuevo código al personal.</p>
    </div>
<% } else { %>

    <div class="topbar">
        <div class="brand"><i class="fa-solid fa-leaf"></i> Overo's</div>
        <div class="mesa-info"><i class="fa-solid fa-chair"></i> Mesa <%= numeroMesa %></div>
    </div>

    <!-- Overlay 1: Confirmar mesa -->
    <div class="overlay" id="overlayMesa">
        <div class="overlay-card">
            <div class="icon-big"><i class="fa-solid fa-chair"></i></div>
            <h2>Tu Mesa</h2>
            <div class="mesa-num"><%= numeroMesa %></div>
            <div class="mesa-salon"><%= salonMesa %></div>
            <p>¿Es esta tu mesa?</p>
            <div style="display:flex;gap:10px;">
                <button class="btn btn-outline" onclick="location.href='index.html'" style="flex:1;">
                    <i class="fa-solid fa-xmark"></i> No
                </button>
                <button class="btn btn-primary" onclick="document.getElementById('overlayMesa').classList.add('hidden');document.getElementById('overlayDatos').classList.remove('hidden');" style="flex:2;">
                    <i class="fa-solid fa-check"></i> Sí, es mi mesa
                </button>
            </div>
        </div>
    </div>

    <!-- Overlay 2: Datos comprobante (opcional) -->
    <div class="overlay hidden" id="overlayDatos">
        <div class="overlay-card">
            <div class="icon-big"><i class="fa-solid fa-file-invoice"></i></div>
            <h2>Datos para Comprobante</h2>
            <p>Opcional. Puede omitirlo.</p>
            <form id="formDatos">
                <div class="form-group">
                    <label>Nombre</label>
                    <input type="text" id="dNombre" placeholder="Su nombre">
                </div>
                <div style="display:flex;gap:8px;">
                    <div class="form-group" style="flex:1;">
                        <label>Tipo Doc.</label>
                        <select id="dTipoDoc" onchange="ajustarDocField()">
                            <option value="1" data-len="8" data-place="00000000">DNI</option>
                            <option value="2" data-len="11" data-place="00000000000">RUC</option>
                            <option value="3" data-len="12" data-place="000000000000">Carnet</option>
                        </select>
                    </div>
                    <div class="form-group" style="flex:2;">
                        <label>N° Documento</label>
                        <input type="text" id="dDoc" placeholder="00000000" maxlength="8" inputmode="numeric">
                    </div>
                </div>
                <div class="form-group">
                    <label>Teléfono</label>
                    <input type="tel" id="dTel" placeholder="999999999" maxlength="9" inputmode="numeric">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" id="dEmail" placeholder="correo@ejemplo.com">
                </div>
            </form>
            <div style="display:flex;gap:10px;margin-top:8px;">
                <button class="btn btn-outline" onclick="abrirComanda(true)" style="flex:1;">
                    <i class="fa-solid fa-forward"></i> Sin datos
                </button>
                <button class="btn btn-primary" onclick="abrirComanda(false)" style="flex:2;">
                    <i class="fa-solid fa-check"></i> Continuar
                </button>
            </div>
        </div>
    </div>

    <div class="toast" id="toast"><i class="fa-solid fa-bell"></i> <span id="toastMsg"></span></div>

    <div class="catalogo-contenido" id="catalogo" style="display:none;">
        <nav class="cat-tabs" id="catTabs">
            <% int catIdx = 0; for (String cat : categoriasOrden) {
                List<Presentacion> items = presPorCategoria.get(cat);
                if (items == null || items.isEmpty()) continue;
            %>
            <button class="cat-tab <%= catIdx == 0 ? "active" : "" %>" data-cat="<%= catIdx %>">
                <%= cat %>
            </button>
            <% catIdx++; } %>
        </nav>
        <% if (presentaciones.isEmpty()) { %>
        <div style="text-align:center;padding:60px 20px;color:var(--text-light);">
            <i class="fa-solid fa-plate-wheat" style="font-size:64px;opacity:0.2;margin-bottom:16px;"></i>
            <p style="font-size:16px;">No hay productos disponibles</p>
        </div>
        <% } else { %>
            <% catIdx = 0; for (String cat : categoriasOrden) {
                List<Presentacion> items = presPorCategoria.get(cat);
                if (items == null || items.isEmpty()) { catIdx++; continue; }
            %>
            <div class="cat-section <%= catIdx == 0 ? "" : "hidden" %>" data-cat="<%= catIdx %>">
                <% for (Presentacion p : items) {
                    String nombrePres = p.getNombre() != null ? p.getNombre() : "";
                    double precio = p.getPrecio();
                    String nomProd = p.getNombreProducto() != null ? p.getNombreProducto() : "";
                    String nomEst = p.getNombreEstadoPresentacion() != null ? p.getNombreEstadoPresentacion() : "";
                    boolean enPromo = "En Promocion".equalsIgnoreCase(nomEst);
                %>
                <div class="prod-card <%= enPromo ? "promo" : "" %>">
                    <div style="width:48px;height:48px;border-radius:12px;background:<%= enPromo ? "#D4A574" : "#1B4332" %>;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:18px;color:white;flex-shrink:0;margin-right:12px;">
                        <%= nomProd.length() > 0 ? nomProd.substring(0,1).toUpperCase() : "?" %>
                    </div>
                    <div class="prod-body">
                        <div class="prod-nombre"><%= nomProd %></div>
                        <div class="prod-pres"><%= nombrePres %></div>
                        <% if (enPromo) { %><span class="badge-promo"><i class="fa-solid fa-tag"></i> Promo</span><% } %>
                    </div>
                    <div class="prod-precio">S/ <%= String.format("%.2f", precio) %></div>
                </div>
                <% } %>
            </div>
            <% catIdx++; } %>
        <% } %>
    </div>

    <button class="btn-mozo" id="btnMozo" style="display:none;" onclick="llamarMozo()" title="Llamar Mozo">
        <i class="fa-solid fa-bell-concierge"></i>
    </button>

    <script>
        var idMesa = <%= idMesa %>;
        var idMovimiento = <%= idMovExistente %>;

        (function init() {
            if (idMovimiento > 0) {
                document.getElementById('overlayMesa').classList.add('hidden');
                document.getElementById('overlayDatos').classList.add('hidden');
                document.getElementById('catalogo').style.display = 'grid';
                document.getElementById('btnMozo').style.display = 'flex';
                if (typeof WS !== 'undefined') WS.conectar('cliente:' + idMovimiento);
            }
        })();

        function ajustarDocField() {
            var sel = document.getElementById('dTipoDoc');
            var opt = sel.options[sel.selectedIndex];
            var len = opt.getAttribute('data-len') || '8';
            var place = opt.getAttribute('data-place') || '00000000';
            var input = document.getElementById('dDoc');
            input.maxLength = parseInt(len);
            input.placeholder = place;
            input.value = '';
        }

        function validarForm() {
            var tipoDoc = document.getElementById('dTipoDoc').value;
            var doc = document.getElementById('dDoc').value.trim();
            var tel = document.getElementById('dTel').value.trim();
            var sel = document.getElementById('dTipoDoc');
            var opt = sel.options[sel.selectedIndex];
            var len = parseInt(opt.getAttribute('data-len') || '8');
            if (doc && doc.length !== len) {
                alert('El documento debe tener ' + len + ' dígitos.');
                return false;
            }
            if (tel && tel.length !== 9) {
                alert('El teléfono debe tener 9 dígitos.');
                return false;
            }
            return true;
        }

        function abrirComanda(sinDatos) {
            if (!sinDatos && !validarForm()) return;
            var nombre = sinDatos ? 'CLIENTE GENERAL' : document.getElementById('dNombre').value || 'CLIENTE GENERAL';
            var tipoDoc = document.getElementById('dTipoDoc').value;
            var doc = sinDatos ? '00000000' : document.getElementById('dDoc').value || '00000000';
            var tel = sinDatos ? '999999999' : document.getElementById('dTel').value || '999999999';
            var email = sinDatos ? '' : document.getElementById('dEmail').value || '';

            var xhr = new XMLHttpRequest();
            xhr.open('POST', 'accionClienteCatalogo.jsp', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var resp = xhr.responseText.trim();
                    if (resp.startsWith('OK:')) {
                        idMovimiento = parseInt(resp.split(':')[1]);
                        document.getElementById('overlayDatos').classList.add('hidden');
                        document.getElementById('catalogo').style.display = 'grid';
                        document.getElementById('btnMozo').style.display = 'flex';
                        showToast('Comanda abierta. Bienvenido!');
                        if (typeof WS !== 'undefined') WS.conectar('cliente:' + idMovimiento);
                    } else {
                        alert(resp);
                    }
                }
            };
            xhr.send('accion=abrirComanda&idMesa=' + idMesa +
                '&nombre=' + encodeURIComponent(nombre) +
                '&tipoDoc=' + tipoDoc +
                '&doc=' + encodeURIComponent(doc) +
                '&tel=' + encodeURIComponent(tel) +
                '&email=' + encodeURIComponent(email));
        }

        function onWSMessage(msg) {
            var texto = '';
            if (msg.tipo === 'COMANDA_ACEPTADA') {
                texto = 'Tu mozo ' + (msg.mozo || '') + ' está en camino';
                showToast(texto);
            } else if (msg.tipo === 'PEDIDO_LISTO') {
                texto = 'Tu pedido está listo';
                showToast(texto);
            } else {
                texto = msg.mensaje || msg.tipo || '';
                if (texto) showToast(texto);
            }
        }

        function llamarMozo() {
            var btn = document.getElementById('btnMozo');
            btn.classList.add('enviando');
            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';

            var xhr = new XMLHttpRequest();
            xhr.open('POST', 'accionClienteCatalogo.jsp', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    setTimeout(function() {
                        btn.classList.remove('enviando');
                        btn.innerHTML = '<i class="fa-solid fa-bell-concierge"></i>';
                        showToast('Mozo notificado. Espere un momento.');
                    }, 1500);
                }
            };
            xhr.send('accion=llamarMozo&idMesa=' + idMesa + '&idMovimiento=' + idMovimiento);
        }

        function showToast(msg) {
            var t = document.getElementById('toast');
            document.getElementById('toastMsg').textContent = msg;
            t.classList.add('show');
            setTimeout(function() { t.classList.remove('show'); }, 3000);
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
    </script>
    <% String wsRol = ""; %>
    <%@ include file="wsCliente.jspf" %>
<% } %>
</body>
</html>
