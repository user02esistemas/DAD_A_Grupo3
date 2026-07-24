
-- ============================================
-- TABLAS DE CATALOGO (sin FK)
-- ============================================

CREATE TABLE TipoProducto (
    IdTipoProducto SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE TipoMovimiento (
    IdTipoMovimiento SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE TipoDocumento (
    IdTipoDocumento SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE TipoCliente (
    IdTipoCliente SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE TiempoPreparacion (
    IdTiempoPreparacion SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Tiempo VARCHAR(50) NOT NULL
);

CREATE TABLE Salon (
    IdSalon SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoProducto (
    IdEstadoProducto SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoPresentacion (
    IdEstadoPresentacion SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoMovimiento (
    IdEstadoMovimiento SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoMesa (
    IdEstadoMesa SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE FormaPago (
    IdFormaPago SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoPedido (
    IdEstadoPedido SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE EstadoComanda (
    IdEstadoComanda SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

CREATE TABLE DatosEmpresa (
    IdEmpresa SERIAL PRIMARY KEY,
    RUC VARCHAR(20) NOT NULL,
    RazonSocial VARCHAR(200) NOT NULL,
    Direccion VARCHAR(200),
    Telefono VARCHAR(20),
    Email VARCHAR(100)
);

CREATE TABLE Administrador (
    IdAdministrador SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Usuario VARCHAR(100) NOT NULL UNIQUE,
    Clave VARCHAR(100) NOT NULL
);

-- ============================================
-- TABLAS DE PERSONAS
-- ============================================

CREATE TABLE Cliente (
    IdCliente SERIAL PRIMARY KEY,
    IdTipoDocumento INT NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Documento VARCHAR(20) NOT NULL,
    IdTipoCliente INT NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Telefono VARCHAR(20),
    Sexo CHAR(1) DEFAULT 'M',
    Direccion VARCHAR(200),
    Email VARCHAR(100),
    FOREIGN KEY (IdTipoDocumento) REFERENCES TipoDocumento(IdTipoDocumento),
    FOREIGN KEY (IdTipoCliente) REFERENCES TipoCliente(IdTipoCliente)
);

CREATE TABLE Mozo (
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

-- ============================================
-- PRODUCTOS Y MESAS
-- ============================================

CREATE TABLE Producto (
    IdProducto SERIAL PRIMARY KEY,
    IdTipoProducto INT NOT NULL,
    Nombre VARCHAR(150) NOT NULL,
    Descripcion VARCHAR(300),
    IdTiempoPreparacion INT NOT NULL,
    IdEstadoProducto INT NOT NULL,
    RutaImg VARCHAR(500),
    FOREIGN KEY (IdTipoProducto) REFERENCES TipoProducto(IdTipoProducto),
    FOREIGN KEY (IdTiempoPreparacion) REFERENCES TiempoPreparacion(IdTiempoPreparacion),
    FOREIGN KEY (IdEstadoProducto) REFERENCES EstadoProducto(IdEstadoProducto)
);

CREATE TABLE Presentacion (
    IdPresentacion SERIAL PRIMARY KEY,
    IdProducto INT NOT NULL,
    Nombre VARCHAR(150) NOT NULL,
    Precio DECIMAL(10,2) NOT NULL,
    Stock INT NOT NULL DEFAULT 0,
    IdEstadoPresentacion INT NOT NULL,
    ImagenUrl VARCHAR(300),
    FOREIGN KEY (IdProducto) REFERENCES Producto(IdProducto),
    FOREIGN KEY (IdEstadoPresentacion) REFERENCES EstadoPresentacion(IdEstadoPresentacion)
);

CREATE TABLE MesaGrupo (
    IdMesaGrupo SERIAL PRIMARY KEY,
    Nombre VARCHAR(100),
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    Activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE Mesa (
    IdMesa SERIAL PRIMARY KEY,
    IdSalon INT NOT NULL,
    Numero VARCHAR(10) NOT NULL,
    Capacidad INT NOT NULL,
    IdEstadoMesa INT NOT NULL,
    IdMesaGrupo INT,
    FOREIGN KEY (IdSalon) REFERENCES Salon(IdSalon),
    FOREIGN KEY (IdEstadoMesa) REFERENCES EstadoMesa(IdEstadoMesa),
    FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo)
);

-- ============================================
-- CAJA
-- ============================================

CREATE TABLE RegistroCaja (
    IdRegistroCaja SERIAL PRIMARY KEY,
    MontoApertura DECIMAL(10,2) NOT NULL,
    FechaApertura TIMESTAMP DEFAULT NOW(),
    FechaCierre TIMESTAMP,
    MontoCierre DECIMAL(10,2),
    Estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA'
);

-- ============================================
-- COMANDA (MOVIMIENTO)
-- ============================================

CREATE TABLE Movimiento (
    IdMovimiento SERIAL PRIMARY KEY,
    Fecha TIMESTAMP NOT NULL DEFAULT NOW(),
    NroDocumento VARCHAR(50),
    CodigoComanda VARCHAR(20) UNIQUE,
    IdCliente INT NOT NULL,
    IdMozo INT,
    IdTipoMovimiento INT NOT NULL,
    TotalPagar DECIMAL(10,2) NOT NULL DEFAULT 0,
    IdDatosEmpresa INT NOT NULL,
    IdEstadoComanda INT NOT NULL DEFAULT 1,
    NumPersonas INT NOT NULL DEFAULT 1,
    IdMesaGrupo INT,
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente),
    FOREIGN KEY (IdMozo) REFERENCES Mozo(IdMozo),
    FOREIGN KEY (IdTipoMovimiento) REFERENCES TipoMovimiento(IdTipoMovimiento),
    FOREIGN KEY (IdDatosEmpresa) REFERENCES DatosEmpresa(IdEmpresa),
    FOREIGN KEY (IdEstadoComanda) REFERENCES EstadoComanda(IdEstadoComanda),
    FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo)
);

CREATE TABLE MovimientoMesa (
    IdMovimiento INT NOT NULL,
    IdMesa INT NOT NULL,
    PRIMARY KEY (IdMovimiento, IdMesa),
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento),
    FOREIGN KEY (IdMesa) REFERENCES Mesa(IdMesa)
);

CREATE TABLE MovimientoPedido (
    IdMovimiento INT NOT NULL,
    IdPresentacion INT NOT NULL,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    Total DECIMAL(10,2) NOT NULL DEFAULT 0,
    IdEstadoPedido INT NOT NULL DEFAULT 1,
    FechaInicio TIMESTAMP,
    FechaFin TIMESTAMP,
    TiempoEstimado INT,
    Pagado BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento),
    FOREIGN KEY (IdPresentacion) REFERENCES Presentacion(IdPresentacion),
    FOREIGN KEY (IdEstadoPedido) REFERENCES EstadoPedido(IdEstadoPedido)
);

CREATE TABLE MovimientoPago (
    IdMovimiento INT NOT NULL,
    IdFormaPago INT NOT NULL,
    Monto DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (IdMovimiento, IdFormaPago),
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento),
    FOREIGN KEY (IdFormaPago) REFERENCES FormaPago(IdFormaPago)
);

-- ============================================
-- NOTIFICACIONES
-- ============================================

CREATE TABLE Notificacion (
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

-- ============================================
-- DATOS DE ENVIO / COMPROBANTE
-- ============================================

CREATE TABLE DatosEnvio (
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

-- ============================================
-- PAGO IDEMPOTENTE
-- ============================================

CREATE TABLE CodigoPago (
    IdCodigoPago SERIAL PRIMARY KEY,
    Codigo VARCHAR(50) NOT NULL UNIQUE,
    IdMovimiento INT NOT NULL,
    MontoTotal DECIMAL(10,2) NOT NULL,
    Pagado BOOLEAN DEFAULT FALSE,
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    FechaPago TIMESTAMP,
    FOREIGN KEY (IdMovimiento) REFERENCES Movimiento(IdMovimiento)
);

CREATE TABLE IdempotenciaPago (
    IdemKey VARCHAR(50) PRIMARY KEY,
    IdMovimiento INT NOT NULL,
    Respuesta TEXT NOT NULL,
    FechaCreacion TIMESTAMP DEFAULT NOW()
);

-- ============================================
-- RESERVA BUFFET
-- ============================================

CREATE TABLE ReservaBuffet (
    IdReservaBuffet SERIAL PRIMARY KEY,
    IdCliente INT NOT NULL,
    FechaReserva DATE NOT NULL,
    HoraInicio TIME NOT NULL,
    HoraFin TIME NOT NULL,
    CantidadPersonas INT NOT NULL,
    MontoTotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    Penalizacion DECIMAL(10,2) DEFAULT 0,
    Estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    Observaciones TEXT,
    FechaCreacion TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente)
);

CREATE TABLE ReservaBuffetMesa (
    IdReservaBuffet INT NOT NULL,
    IdMesa INT NOT NULL,
    PRIMARY KEY (IdReservaBuffet, IdMesa),
    FOREIGN KEY (IdReservaBuffet) REFERENCES ReservaBuffet(IdReservaBuffet),
    FOREIGN KEY (IdMesa) REFERENCES Mesa(IdMesa)
);

CREATE TABLE ReservaBuffetDetalle (
    IdReservaBuffet INT NOT NULL,
    IdPresentacion INT NOT NULL,
    Cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (IdReservaBuffet, IdPresentacion),
    FOREIGN KEY (IdReservaBuffet) REFERENCES ReservaBuffet(IdReservaBuffet),
    FOREIGN KEY (IdPresentacion) REFERENCES Presentacion(IdPresentacion)
);

-- ============================================
-- SEED DATA
-- ============================================

-- Catalogos
INSERT INTO TipoProducto (Nombre) VALUES ('Bebidas'),('Platos'),('Postres'),('Entradas'),('Especiales');
INSERT INTO TipoMovimiento (Nombre) VALUES ('Pedido'),('Delivery'),('Para Llevar'),('Buffet');
INSERT INTO TipoDocumento (Nombre) VALUES ('DNI'),('Carnet Extranjeria'),('RUC'),('Pasaporte');
INSERT INTO TipoCliente (Nombre) VALUES ('Regular'),('VIP'),('Frecuente');
INSERT INTO TiempoPreparacion (Nombre, Tiempo) VALUES ('Rapido','5-10 min'),('Medio','15-20 min'),('Lento','25-35 min'),('Muy Lento','40+ min');
INSERT INTO Salon (Nombre) VALUES ('Salon Principal'),('Salon VIP'),('Terraza'),('Bar');
INSERT INTO EstadoProducto (Nombre) VALUES ('Activo'),('Inactivo'),('Agotado');
INSERT INTO EstadoPresentacion (Nombre) VALUES ('Disponible'),('No Disponible'),('En Promocion');
INSERT INTO EstadoMovimiento (Nombre) VALUES ('Pendiente'),('Pagado'),('Cancelado');
INSERT INTO EstadoMesa (Nombre) VALUES ('Libre'),('Ocupada'),('Reservada'),('Mantenimiento');
INSERT INTO FormaPago (Nombre) VALUES ('Efectivo'),('Tarjeta Credito'),('Tarjeta Debito'),('Yape'),('Plin');
INSERT INTO EstadoPedido (Nombre) VALUES ('Recibido'),('Preparando'),('Listo'),('Entregado');
INSERT INTO EstadoComanda (Nombre) VALUES ('Abierta'),('En Curso'),('Cerrada'),('Pagada'),('Cancelada');
INSERT INTO DatosEmpresa (RUC, RazonSocial, Direccion, Telefono, Email) VALUES ('20512345678','Overos Restaurante Campestre S.A.C.','Av. Principal 123','01-2345678','contacto@overos.com.pe');

-- Administrador
INSERT INTO Administrador (Nombre, Usuario, Clave) VALUES ('Administrador','admin','admin123');

-- Clientes (sin autenticacion - alineado a Migracion v3)
INSERT INTO Cliente (IdTipoDocumento, Nombre, Documento, IdTipoCliente, Apellido, Telefono, Sexo, Email) VALUES
(1,'Juan','12345678',1,'Perez','999111222','M','juan@mail.com'),
(1,'Maria','87654321',3,'Lopez','999333444','F','maria@mail.com'),
(3,'Empresa XYZ','20555666778',2,'-','01-4445555','M','info@xyz.com');

-- Mozos
INSERT INTO Mozo (Nombre, Apellido, Telefono, Usuario, Clave, IdSalon) VALUES
('Carlos','Ramirez','999444555','carlos','carlos123',1),
('Ana','Torres','999666777','ana','ana123',2),
('Luis','Mendoza','999888999','luis','luis123',1);

-- Mesas
INSERT INTO Mesa (IdSalon, Numero, Capacidad, IdEstadoMesa) VALUES
(1,'M01',4,1),(1,'M02',4,1),(1,'M03',6,1),(1,'M04',2,1),
(2,'VIP01',8,1),(2,'VIP02',10,1),
(3,'T01',4,1),(3,'T02',4,1),(3,'T03',2,1),
(4,'B01',4,1),(4,'B02',6,1);

-- Productos de ejemplo
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto) VALUES
(2,'Lomo Saltado','Lomo a la parrilla con papas fritas y arroz',3,1),
(2,'Ají de Gallina','Gallina deshilachada en salsa de ají amarillo',2,1),
(2,'Ceviche de Pescado','Pescado fresco marinado en limón',1,1),
(1,'Chicha Morada','Bebida tradicional de maíz morado',1,1),
(1,'Cerveza Artesanal','Cerveza local 500ml',1,1),
(3,'Tres Leches','Bizcocho bañado en tres leches',1,1),
(4,'Tequeños','Rollitos de queso fritos',1,1),
(5,'Pachamanca','Plato especial de la casa - carne y verduras',4,1);

-- Presentaciones
INSERT INTO Presentacion (IdProducto, Nombre, Precio, Stock, IdEstadoPresentacion) VALUES
(1,'Lomo Saltado Personal',35.00,50,1),(1,'Lomo Saltado para 2',60.00,50,1),
(2,'Ají de Gallina Personal',28.00,40,1),
(3,'Ceviche Personal',32.00,30,1),(3,'Ceviche Familiar',55.00,30,1),
(4,'Chicha Morada 500ml',8.00,100,1),(4,'Chicha Morada 1L',14.00,100,1),
(5,'Cerveza Artesanal 500ml',15.00,80,1),
(6,'Tres Leches Individual',18.00,25,1),
(7,'Tequeños 6 unidades',12.00,40,1),
(8,'Pachamanca Personal',55.00,20,1),(8,'Pachamanca Familiar',95.00,20,1);
