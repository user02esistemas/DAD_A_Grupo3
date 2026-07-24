-- ============================================
-- MIGRACION v2.0 CORREGIDA - Ejecutar en PG Admin
-- ============================================

-- 1. EstadoPedido
CREATE TABLE IF NOT EXISTS EstadoPedido (
    IdEstadoPedido SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

-- 2. EstadoComanda
CREATE TABLE IF NOT EXISTS EstadoComanda (
    IdEstadoComanda SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL
);

-- 3. Mozo
CREATE TABLE IF NOT EXISTS Mozo (
    IdMozo SERIAL PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Usuario VARCHAR(100) NOT NULL UNIQUE,
    Clave VARCHAR(100) NOT NULL
);

-- 4. MesaGrupo
CREATE TABLE IF NOT EXISTS MesaGrupo (
    IdMesaGrupo SERIAL PRIMARY KEY,
    Nombre VARCHAR(100),
    NumPersonas INT DEFAULT 0
);

-- 5. Mesa: FK a MesaGrupo
ALTER TABLE Mesa ADD COLUMN IF NOT EXISTS IdMesaGrupo INT;
DO $$ BEGIN
    ALTER TABLE Mesa ADD CONSTRAINT fk_mesa_grupo FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- 6. DatosEmpresa: campos extra
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Direccion VARCHAR(200);
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Telefono VARCHAR(20);
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS Email VARCHAR(100);

-- 7. Cliente: email
ALTER TABLE Cliente ADD COLUMN IF NOT EXISTS Email VARCHAR(100);

-- 8. Movimiento: columnas v2.0
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS CodigoComanda VARCHAR(20);
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdMozo INT;
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdEstadoComanda INT DEFAULT 1;
ALTER TABLE Movimiento ADD COLUMN IF NOT EXISTS IdMesaGrupo INT;
DO $$ BEGIN
    ALTER TABLE Movimiento ADD CONSTRAINT fk_mov_mozo FOREIGN KEY (IdMozo) REFERENCES Mozo(IdMozo);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
DO $$ BEGIN
    ALTER TABLE Movimiento ADD CONSTRAINT fk_mov_comanda FOREIGN KEY (IdEstadoComanda) REFERENCES EstadoComanda(IdEstadoComanda);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
DO $$ BEGIN
    ALTER TABLE Movimiento ADD CONSTRAINT fk_mov_mesagrupo FOREIGN KEY (IdMesaGrupo) REFERENCES MesaGrupo(IdMesaGrupo);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
ALTER TABLE Movimiento ADD CONSTRAINT uq_codigo_comanda UNIQUE (CodigoComanda);

-- 9. MovimientoPedido: columnas v2.0
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS PrecioUnitario DECIMAL(10,2) DEFAULT 0;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS IdEstadoPedido INT DEFAULT 1;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS FechaInicio TIMESTAMP;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS FechaFin TIMESTAMP;
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS TiempoEstimado INT;
ALTER TABLE MovimientoPedido DROP CONSTRAINT IF EXISTS movimientopedido_pkey;
ALTER TABLE MovimientoPedido ADD PRIMARY KEY (IdMovimiento, IdPresentacion);
DO $$ BEGIN
    ALTER TABLE MovimientoPedido ADD CONSTRAINT fk_mp_estadopedido FOREIGN KEY (IdEstadoPedido) REFERENCES EstadoPedido(IdEstadoPedido);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- 10. Notificacion
CREATE TABLE IF NOT EXISTS Notificacion (
    IdNotificacion SERIAL PRIMARY KEY,
    Mensaje TEXT NOT NULL,
    Tipo VARCHAR(50) NOT NULL,
    Leida BOOLEAN DEFAULT FALSE,
    RolOrigen INT DEFAULT 0,
    IdRolDestino INT DEFAULT 0,
    Fecha TIMESTAMP DEFAULT NOW(),
    IdReferencia INT DEFAULT 0
);

-- 11. DatosEnvio
CREATE TABLE IF NOT EXISTS DatosEnvio (
    IdDatosEnvio SERIAL PRIMARY KEY,
    Destinatario VARCHAR(200),
    Telefono VARCHAR(20),
    Email VARCHAR(100),
    Direccion VARCHAR(200),
    IdMovimiento INT
);

-- 12. CodigoPago
CREATE TABLE IF NOT EXISTS CodigoPago (
    IdCodigoPago SERIAL PRIMARY KEY,
    Codigo VARCHAR(50) NOT NULL UNIQUE,
    Estado VARCHAR(20) DEFAULT 'PENDIENTE'
);

-- 13. IdempotenciaPago
CREATE TABLE IF NOT EXISTS IdempotenciaPago (
    IdCodigoPago SERIAL PRIMARY KEY,
    IdMovimiento INT NOT NULL,
    IdCodigoPagoRef INT,
    Estado VARCHAR(20) DEFAULT 'ACTIVO',
    FechaCreacion TIMESTAMP DEFAULT NOW()
);

-- 14. ReservaBuffet
CREATE TABLE IF NOT EXISTS ReservaBuffet (
    IdReserva SERIAL PRIMARY KEY,
    FechaHora TIMESTAMP,
    Personas INT DEFAULT 0,
    Bebidas TEXT,
    PlatosFrio TEXT,
    PlatosCaliente TEXT,
    Total DECIMAL(10,2) DEFAULT 0,
    IdCliente INT,
    IdEstado INT DEFAULT 1,
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente)
);

-- ============================================
-- SEED DATA
-- ============================================
INSERT INTO EstadoPedido (Nombre) VALUES ('Recibido'),('Preparando'),('Listo'),('Entregado');
INSERT INTO EstadoComanda (Nombre) VALUES ('Abierta'),('En Curso'),('Cerrada'),('Pagada'),('Cancelada');

DO $$ BEGIN
    INSERT INTO TipoMovimiento (Nombre) VALUES ('Buffet');
EXCEPTION WHEN unique_violation THEN NULL;
END $$;

UPDATE DatosEmpresa SET Direccion='Av. Principal 123', Telefono='01-2345678', Email='contacto@overos.com.pe' WHERE IdEmpresa=1;

INSERT INTO Mozo (Nombre, Apellido, Usuario, Clave) VALUES
('Carlos','Ramirez','carlos','carlos123'),
('Ana','Torres','ana','ana123'),
('Luis','Mendoza','luis','luis123')
ON CONFLICT (Usuario) DO NOTHING;

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

INSERT INTO Cliente (IdTipoDocumento, Nombre, Documento, IdTipoCliente, Apellido, Telefono, Sexo, Email, Usuario, Clave)
SELECT 3,'Empresa XYZ','20555666778',2,'-','01-4445555','M','info@xyz.com','empresa','empresa123'
WHERE NOT EXISTS (SELECT 1 FROM Cliente WHERE Usuario='empresa');
