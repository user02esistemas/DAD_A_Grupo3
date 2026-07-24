-- ============================================
-- MIGRACION v2.0 - Script para BD existente
-- Ejecutar en PgAdmin sobre GestionRestauranteDB
-- ============================================

-- Nuevas tablas de catalogo
CREATE TABLE IF NOT EXISTS EstadoPedido (
    IdEstadoPedido SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS EstadoComanda (
    IdEstadoComanda SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

-- Tabla Mozo
CREATE TABLE IF NOT EXISTS Mozo (
    IdMozo SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Telefono VARCHAR(20),
    Usuario VARCHAR(100) NOT NULL UNIQUE,
    Clave VARCHAR(100) NOT NULL,
    IdSalon INT,
    Activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (IdSalon) REFERENCES Salon(IdSalon)
);

-- Tabla MesaGrupo
CREATE TABLE IF NOT EXISTS MesaGrupo (
    IdMesaGrupo SERIAL PRIMARY KEY,
    Nombre VARCHAR(100),
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    Activo BOOLEAN DEFAULT TRUE
);

-- Modificar Mesa: agregar grupo
ALTER TABLE Mesa ADD COLUMN IF NOT EXISTS IdMesaGrupo INT;
ALTER TABLE Mesa ADD CONSTRAINT IF NOT EXISTS fk_mesa_grupo FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo);

-- Modificar DatosEmpresa: campos extra
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Direccion VARCHAR(200);
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Telefono VARCHAR(20);
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Email VARCHAR(100);

-- Modificar Cliente: campo email
ALTER TABLE Cliente ADD COLUMN IF NOT EXISTS Email VARCHAR(100);

-- Modificar Movimiento: agregar columnas nuevas
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS CodigoComanda VARCHAR(20) UNIQUE;
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdMozo INT;
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdEstadoComanda INT DEFAULT 1;
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdMesaGrupo INT;
ALTER TABLE Movimiento ADD CONSTRAINT IF NOT EXISTS fk_mov_mozo FOREIGN KEY (IdMozo) REFERENCES Mozo(IdMozo);
ALTER TABLE Movimiento ADD CONSTRAINT IF NOT EXISTS fk_mov_comanda FOREIGN KEY (IdEstadoComanda) REFERENCES EstadoComanda(IdEstadoComanda);
ALTER TABLE Movimiento ADD CONSTRAINT IF NOT EXISTS fk_mov_mesagrupo FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo);

-- Modificar MovimientoPedido: agregar estados y tiempos
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS PrecioUnitario DECIMAL(10,2) DEFAULT 0;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS IdEstadoPedido INT DEFAULT 1;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS FechaInicio TIMESTAMP;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS FechaFin TIMESTAMP;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS TiempoEstimado INT;
ALTER TABLE MovimientoPedido DROP CONSTRAINT IF EXISTS movimientopedido_pkey;
ALTER TABLE MovimientoPedido ADD PRIMARY KEY (IdMovimiento, IdPresentacion);
ALTER TABLE MovimientoPedido ADD CONSTRAINT IF NOT EXISTS fk_mp_estadopedido FOREIGN KEY (IdEstadoPedido) REFERENCES EstadoPedido(IdEstadoPedido);

-- Tabla Notificacion
CREATE TABLE IF NOT EXISTS Notificacion (
    IdNotificacion SERIAL PRIMARY KEY,
    IdMovimiento INT NOT NULL,
    Tipo VARCHAR(50) NOT NULL,
    Mensaje TEXT NOT NULL,
    IdDestinatario INT NOT NULL,
    TipoDestinatario VARCHAR(20) NOT NULL,
    Leida BOOLEAN DEFAULT FALSE,
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento)
);

-- Tabla DatosEnvio
CREATE TABLE IF NOT EXISTS DatosEnvio (
    IdDatosEnvio SERIAL PRIMARY KEY,
    IdMovimiento INT NOT NULL UNIQUE,
    TipoComprobante VARCHAR(20) NOT NULL DEFAULT 'BOLETA',
    RazonSocial VARCHAR(200),
    RUC VARCHAR(20),
    DireccionEnvio VARCHAR(200),
    EmailEnvio VARCHAR(100),
    TelefonoEnvio VARCHAR(20),
    Observaciones TEXT,
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento)
);

-- Tabla CodigoPago (idempotente)
CREATE TABLE IF NOT EXISTS CodigoPago (
    IdCodigoPago SERIAL PRIMARY KEY,
    Codigo VARCHAR(50) NOT NULL UNIQUE,
    IdMovimiento INT NOT NULL,
    MontoTotal DECIMAL(10,2) NOT NULL,
    Pagado BOOLEAN DEFAULT FALSE,
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    FechaPago TIMESTAMP,
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento)
);

-- Tabla IdempotenciaPago
CREATE TABLE IF NOT EXISTS IdempotenciaPago (
    IdemKey VARCHAR(50) PRIMARY KEY,
    IdMovimiento INT NOT NULL,
    Respuesta TEXT NOT NULL,
    FechaCreacion TIMESTAMP DEFAULT NOW()
);

-- Tabla ReservaBuffet
CREATE TABLE IF NOT EXISTS ReservaBuffet (
    IdReservaBuffet SERIAL PRIMARY KEY,
    IdCliente INT NOT NULL,
    FechaReserva DATE NOT NULL,
    HoraInicio TIME NOT NULL,
    HoraFin TIME NOT NULL,
    CantidadPersonas INT NOT NULL,
    MontoTotal DECIMAL(10,2) DEFAULT 0,
    Penalizacion DECIMAL(10,2) DEFAULT 0,
    Estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    Observaciones TEXT,
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente)
);

CREATE TABLE IF NOT EXISTS ReservaBuffetMesa (
    IdReservaBuffet INT NOT NULL,
    IdMesa INT NOT NULL,
    PRIMARY KEY (IdReservaBuffet, IdMesa),
    FOREIGN KEY (IdReservaBuffet) REFERENCES ReservaBuffet(IdReservaBuffet),
    FOREIGN KEY (IdMesa) REFERENCES Mesa(IdMesa)
);

CREATE TABLE IF NOT EXISTS ReservaBuffetDetalle (
    IdReservaBuffet INT NOT NULL,
    IdPresentacion INT NOT NULL,
    Cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (IdReservaBuffet, IdPresentacion),
    FOREIGN KEY (IdReservaBuffet) REFERENCES ReservaBuffet(IdReservaBuffet),
    FOREIGN KEY (IdPresentacion) REFERENCES Presentacion(IdPresentacion)
);

-- ============================================
-- SEED DATA para nuevas tablas
-- ============================================
INSERT INTO EstadoPedido (Nombre) VALUES ('Recibido'),('Preparando'),('Listo'),('Entregado');
INSERT INTO EstadoComanda (Nombre) VALUES ('Abierta'),('En Curso'),('Cerrada'),('Pagada'),('Cancelada');
INSERT INTO TipoMovimiento (Nombre) VALUES ('Buffet');

-- Datos empresa completos
UPDATE DatosEmpresa SET Direccion='Av. Principal 123', Telefono='01-2345678', Email='contacto@overos.com.pe' WHERE IdEmpresa=1;

-- Mozos
INSERT INTO Mozo (Nombre, Apellido, Telefono, Usuario, Clave, IdSalon) VALUES
('Carlos','Ramirez','999444555','carlos','carlos123',1),
('Ana','Torres','999666777','ana','ana123',2),
('Luis','Mendoza','999888999','luis','luis123',1)
ON CONFLICT (Usuario) DO NOTHING;

-- Mesas (solo si la tabla esta vacia)
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 1,'M01',4,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='M01');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 1,'M02',4,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='M02');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 1,'M03',6,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='M03');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 1,'M04',2,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='M04');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 2,'VIP01',8,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='VIP01');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 2,'VIP02',10,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='VIP02');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 3,'T01',4,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='T01');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 3,'T02',4,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='T02');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 3,'T03',2,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='T03');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 4,'B01',4,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='B01');
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) SELECT 4,'B02',6,1 WHERE NOT EXISTS (SELECT 1 FROM Mesa WHERE Numero='B02');

-- Productos (solo si vacio)
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Lomo Saltado','Lomo a la parrilla con papas fritas y arroz',3,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Lomo Saltado');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Aji de Gallina','Gallina deshilachada en salsa de aji amarillo',2,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Aji de Gallina');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Ceviche de Pescado','Pescado fresco marinado en limon',1,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Ceviche de Pescado');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 1,'Chicha Morada','Bebida tradicional de maiz morado',1,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Chicha Morada');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 1,'Cerveza Artesanal','Cerveza local 500ml',1,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Cerveza Artesanal');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 3,'Tres Leches','Bizcocho banado en tres leches',1,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Tres Leches');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 4,'Tequenos','Rollitos de queso fritos',1,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Tequenos');
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 5,'Pachamanca','Plato especial de la casa - carne y verduras',4,1 WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Pachamanca');

-- Clientes extra
INSERT INTO Cliente (IdTipoDocumento, Nombre, Documento, IdTipoCliente, Apellido, Telefono, Sexo, Email, Usuario, Clave)
SELECT 3,'Empresa XYZ','20555666778',2,'-','01-4445555','M','info@xyz.com','empresa','empresa123'
WHERE NOT EXISTS (SELECT 1 FROM Cliente WHERE Usuario='empresa');
