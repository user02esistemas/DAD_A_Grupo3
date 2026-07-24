# PAF_DAW_RMI - Sistema de Gestion de Restaurante

Sistema de gestion para restaurante implementado con **Java RMI** en arquitectura de 3 capas, desarrollado para el curso de Desarrollo de Aplicaciones Distribuidas.

## Arquitectura

```
┌─────────────────────┐  RMI   ┌──────────────────────┐  JDBC  ┌────────────┐
│   RMIRestauranteWeb │◄──────►│  RMIRestauranteServer│◄──────►│ PostgreSQL │
│   (Cliente Web)     │ Puerto │  (Servidor RMI)      │        │   (BD)     │
│   JSP + Servlets    │  3239  │  DAOs + ServidorSis  │        │            │
└─────────────────────┘        └──────────────────────┘        └────────────┘
         ▲                              ▲
         │ Usa                          │ Usa
┌───────────────────────────────────────────────────┐
│              RMIRestauranteInterface               │
│           (JAR compartido - DTOs + Interfaces)      │
└───────────────────────────────────────────────────┘
         ▲
         │
┌──────────────────────────────────────┐
│   RMIRestauranteDesktop              │
│   (Cliente JavaFX - CRUDs + Caja)   │
└──────────────────────────────────────┘
```

## Componentes

| Proyecto | Tipo | Descripcion |
|----------|------|-------------|
| **RMIRestauranteInterface** | Java Library (JAR) | 22 DTOs serializables + 22 Interfaces Remote |
| **RMIRestauranteServer** | Java Application | Conexion PostgreSQL + 22 DAOs + ServidorRMI |
| **RMIRestauranteDesktop** | JavaFX Application | Cliente RMI desktop — Login, CRUDs Admin, Dashboard, Caja, QR |
| **RMIRestauranteWeb** | Web Application | Cliente RMI web — Mozo tablet, Cocina, Cliente catalogo |

## Modulos del Sistema

### RMIRestauranteDesktop (JavaFX)
- **Login**: Autenticacion de administradores
- **Dashboard**: KPIs del dia (ventas, comandas, mesas ocupadas, reservas)
- **Productos**: CRUD con combo de TipoProducto, TiempoPreparacion, EstadoProducto
- **Presentaciones**: CRUD con Stock, ImagenUrl, EstadoPresentacion
- **Mesas**: CRUD con EstadoMesa (Libre/Ocupada/Reservada/Mantenimiento)
- **Salones**: CRUD completo de salones
- **Mozos**: CRUD con Usuario, Clave, Activo, Salon asignado
- **Reserva Buffet**: Gestion de reservas con seleccion de cliente
- **QR**: Generacion de codigos QR por mesa
- **Caja**: Gestion de caja (apertura/cierre, movimientos)
- **Config**: Configuracion de empresa (RUC, Razon Social, QR base URL)

### RMIRestauranteWeb (JSP)
- **Mozo Tablet**: Seleccion de mesa, catalogo de productos, carrito de pedidos
- **Cocina**: Visualizacion de pedidos entrantes por comanda
- **Cliente**: Catalogo de platos, historial de pedidos via WebSocket
- **Administracion**: Panel de monitoreo de ventas y pedidos activos

## Base de Datos

- **Motor**: PostgreSQL 18
- **Nombre**: GestionRestauranteDB
- **Tablas**: 22 tablas (11 de catalogo, 11 principales)
- Scripts SQL en `/db/`:
  - `SciptCrateTablePgAdmin.sql` — esquema completo
  - `Migracion_v3.sql` — migraciones incrementales
  - `seed_data.sql` — datos de prueba (productos, presentaciones, mozos)

## Tecnologias

- Java 21 / Java RMI
- JavaFX 21.0.2 (Desktop)
- PostgreSQL 18
- Jakarta Servlet API + Tomcat (Web)
- Bootstrap 5 + jQuery (Web frontend)
- WebSocket (notificaciones en tiempo real)
- NetBeans + Ant (IDE/Build)
- VS Code + Java Language Server (alternativo)

## Como Ejecutar

### 1. Base de datos
Ejecutar los scripts en orden en pgAdmin4:
1. `db/SciptCrateTablePgAdmin.sql`
2. `db/seed_data.sql`

### 2. Servidor RMI
```bash
compileServer.bat
java -cp "Lib/RMIRestauranteInterface.jar;RMIRestauranteServer/build/classes;<postgresql-jdbc.jar>" Servicio.ServidorSistema
```
Debera mostrar: **"Servidor listo en el puerto 3239"**

### 3. Desktop (JavaFX)
```bash
cd RMIRestauranteDesktop
compile.bat
run.bat
```

### 4. Web (Tomcat)
1. Colocar `RMIRestauranteInterface.jar` en `RMIRestauranteWeb/web/WEB-INF/lib/`
2. Desplegar `RMIRestauranteWeb` en Tomcat
3. Abrir `http://localhost:8080/RMIRestauranteWeb/`

## Credenciales

| Usuario | Contrasena | Rol |
|---------|-----------|-----|
| admin | admin123 | Administrador (Desktop + Web) |
| juan | juan123 | Mozo |
| carlos | carlos123 | Mozo |
| maria | maria123 | Mozo |
| pedro | pedro123 | Mozo |

## Autores

- Grupo 3 - Ingenieria de Sistemas
- Universidad Continental - Ciclo IX
