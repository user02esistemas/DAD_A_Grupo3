# Sistema de Gestión Restaurante — Overo's

## Arquitectura

Aplicación distribuida con 3 capas:

```
Cliente Web  ──HTTP──┐
                      ├── Tomcat 9 (JSP/Servlets) ──RMI──▶ Servidor RMI (JDK 21) ──JDBC──▶ PostgreSQL 16
Mozo Web     ──HTTP──┘
Admin Escritorio      ────────────RMI─────────────────────▶
```

- **Tomcat 9** (puerto 8081): Sirve las JSPs del cliente y del mozo.
- **Servidor RMI** (puerto 3239): 30 servicios registrados (Mesa, Producto, Movimiento, etc.).
- **PostgreSQL 16**: Base de datos `GestionRestauranteDB`, user `postgres`.
- **WebSockets**: Endpoint `/ws/notificaciones` para notificaciones en tiempo real (cliente ↔ mozo ↔ cocina).

## Flujo Principal

### 1. Cliente escanea QR
- URL: `clienteCatalogo.jsp?mesa=N&h=HASH`
- Se valida el hash (SHA-256 de `mesaN + secret`).
- Si ya hay comanda activa → salta directo al catálogo.
- Si no → overlay de confirmación de mesa → overlay de datos (opcional) → se abre comanda.

### 2. Cliente llama al mozo
- Botón flotante → WebSocket + insert en tabla Notificacion.
- Mozo recibe notificación en tiempo real.

### 3. Mozo acepta comanda
- Dashboard → lista de comandas abiertas → "Aceptar".
- WebSocket: cliente recibe `COMANDA_ACEPTADA`.

### 4. Mozo toma pedido
- Vista con categorías tipo tabs (Platos, Bebidas, Postres, Entradas, Especiales).
- Cada producto con inicial coloreada (no hay imágenes en DB).
- `+` → `agregarPedido.jsp` → selecciona cantidad → agrega item.

### 5. Mozo revisa y envía
- "Revisar Pedido": muestra resumen con mesas, mozo, cliente, items por categoría, total.
- Botón 🗑️ para eliminar items.
- "Enviar a Cocina": cambia estado comanda a 2 → notifica a cocina por WS.

### 6. Unión de mesas
- Botón "Unir Mesa" en vista de pedido.
- Modal con checkboxes agrupados por salón (libres vs ocupadas).
- Selección múltiple → crea/actualiza `MesaGrupo` y `MovimientoMesa`.

## Estructura del Proyecto

```
DAD_A_Grupo3-main/
├── Lib/                          # Librerías compartidas (JARs)
│   └── RMIRestauranteInterface.jar   # Interfaces RMI + DTOs
├── RMIRestauranteServer/         # Servidor RMI
│   └── src/
│       ├── DAO/                  # Acceso a datos (PostgreSQL)
│       ├── DTO/                  # Modelos (Mesa, Producto, Movimiento...)
│       ├── Servicio/             # ServidorSistema.java (main)
│       └── Implement/            # Implementaciones RMI
├── RMIRestauranteWeb/            # Aplicación web
│   └── web/
│       ├── *.jsp                 # Vistas (mozo, cliente, login, etc.)
│       ├── CSS/estilos.css       # Estilos globales
│       ├── WEB-INF/web.xml       # Config web
│       └── src/java/Control/     # Controladores (RPC a RMI)
└── Errores.md                    # Log de errores reportados
```

## Tablas Clave en DB

| Tabla | Propósito |
|-------|-----------|
| `Mesa` | Mesas del restaurante con capacidad y estado |
| `MesaGrupo` | Grupos de mesas unidas |
| `Movimiento` | Comandas/ventas |
| `MovimientoMesa` | Relación comanda ↔ mesa (soporta multi-mesa) |
| `MovimientoPedido` | Items dentro de una comanda |
| `Producto` | Catálogo de productos |
| `Presentacion` | Variantes/precios de un producto |
| `Notificacion` | Bandeja de notificaciones |

## Endpoints RMI (30 servicios)

Principales: `mesa`, `producto`, `presentacion`, `movimiento`, `movimientoPedido`, `movimientoMesa`, `mesaGrupo`, `cliente`, `mozo`, `salon`, `notificacion`, `datosEmpresa`, `codigoPago`, `reservaBuffet`, etc.

## URLs Útiles

- `http://localhost:8081/RMIRestauranteWeb/clienteCatalogo.jsp?mesa=1&h=HASH` — Cliente
- `http://localhost:8081/RMIRestauranteWeb/mozoLogin.jsp` — Login mozo
- `http://localhost:8081/RMIRestauranteWeb/` — Índice

## Credenciales

- **Mozos**: carlos/carlos123, ana/ana123, luis/luis123
- **Admin escritorio**: admin/admin123
- **QR Secret**: overos2026
- **QrBaseUrl**: http://192.168.100.131:8081/RMIRestauranteWeb

## Comandos de Mantenimiento

```powershell
# Iniciar servidor RMI
java -cp "RMIRestauranteServer\build\classes;Lib\RMIRestauranteInterface.jar;Lib\postgresql-42.5.4.jar" Servicio.ServidorSistema

# Compilar web
javac -cp "Lib\RMIRestauranteInterface.jar;Lib\servlet-api.jar;..." -d web\WEB-INF\classes -sourcepath src\java src\java\Control\*.java

# Compilar servidor
javac -cp "Lib\RMIRestauranteInterface.jar;Lib\postgresql-42.5.4.jar" -d build\classes -sourcepath src src\DAO\*.java src\Implement\*.java src\Servicio\*.java
```
