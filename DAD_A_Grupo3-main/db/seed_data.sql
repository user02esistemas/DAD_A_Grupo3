-- Seed Data - Overo's Restaurant Campestre

-- 1. Columnas faltantes
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS QrBaseUrl VARCHAR(500) DEFAULT 'http://localhost:8080/RMIRestauranteWeb';
ALTER TABLE DatosEmpresa ADD COLUMN IF NOT EXISTS QrSecret VARCHAR(100) DEFAULT 'overos2026';
ALTER TABLE Mozo ADD COLUMN IF NOT EXISTS Telefono VARCHAR(20) DEFAULT '';
ALTER TABLE Mozo ADD COLUMN IF NOT EXISTS IdSalon INT DEFAULT 1;
ALTER TABLE Mozo ADD COLUMN IF NOT EXISTS Activo BOOLEAN DEFAULT TRUE;

-- 2. Actualizar DatosEmpresa
UPDATE DatosEmpresa SET QrBaseUrl='http://localhost:8080/RMIRestauranteWeb', QrSecret='overos2026' WHERE IdEmpresa=1;

-- 3. Limpiar mesas de prueba
DELETE FROM Mesa WHERE Numero IN ('1','2') AND IdSalon=1 AND IdMesa>13;

-- 4. Mozos
UPDATE Mozo SET Telefono='999000111', IdSalon=1, Activo=TRUE WHERE Usuario='juan';
UPDATE Mozo SET Telefono='999000222', IdSalon=2, Activo=TRUE WHERE Usuario='carlos';

INSERT INTO Mozo (Nombre, Apellido, Usuario, Clave, Telefono, IdSalon, Activo)
SELECT 'Maria','Lopez','maria','maria123','999000333',3,TRUE
WHERE NOT EXISTS (SELECT 1 FROM Mozo WHERE Usuario='maria');

INSERT INTO Mozo (Nombre, Apellido, Usuario, Clave, Telefono, IdSalon, Activo)
SELECT 'Pedro','Ramirez','pedro','pedro123','999000444',4,TRUE
WHERE NOT EXISTS (SELECT 1 FROM Mozo WHERE Usuario='pedro');

-- 5. Productos
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 1,'Coca Cola 500ml','Gaseosa Coca Cola personal',1,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Coca Cola 500ml');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 1,'Inka Kola 500ml','Gaseosa Inka Kola personal',1,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Inka Kola 500ml');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 1,'Agua San Luis 500ml','Agua mineral sin gas',1,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Agua San Luis 500ml');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Lomo Saltado','Lomo de res salteado con verduras y papas fritas',2,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Lomo Saltado');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Aji de Gallina','Pollo desmenuzado en crema de aji amarillo',2,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Aji de Gallina');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Ceviche Clasico','Pescado fresco marinado en limon con cebolla',2,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Ceviche Clasico');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 2,'Parrilla Campestre','Parrilla mixta con papas al horno',3,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Parrilla Campestre');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 3,'Suspiro Limeno','Postre tradicional peruano',1,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Suspiro Limeno');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 3,'Picarones','Rosquitas de zapallo con miel',2,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Picarones');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 4,'Papas Fritas','Papas fritas crocantes',1,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Papas Fritas');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 4,'Tequenos','Rollitos de queso con salsa huancaina',2,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Tequenos');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 5,'Pachamanca a la Olla','Plato tipico campestre con carnes y vegetales',4,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Pachamanca a la Olla');

INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto)
SELECT 5,'Cuy Chactado','Cuy frito crocante con papas y ensalada',3,1
WHERE NOT EXISTS (SELECT 1 FROM Producto WHERE Nombre='Cuy Chactado');

-- 6. Presentaciones (una por producto, precio desde 3 hasta 55)
DO $$
DECLARE
    prod RECORD;
BEGIN
    FOR prod IN SELECT IdProducto, Nombre FROM Producto ORDER BY IdProducto LOOP
        IF NOT EXISTS (SELECT 1 FROM Presentacion WHERE IdProducto=prod.IdProducto AND Nombre='Unidad') THEN
            INSERT INTO Presentacion (IdProducto, Nombre, Precio, Stock, IdEstadoPresentacion)
            VALUES (prod.IdProducto, 'Unidad',
                CASE
                    WHEN prod.Nombre LIKE '%Coca Cola%' OR prod.Nombre LIKE '%Inka Kola%' THEN 5.00
                    WHEN prod.Nombre LIKE '%Agua%' THEN 3.00
                    WHEN prod.Nombre LIKE '%Lomo%' THEN 28.00
                    WHEN prod.Nombre LIKE '%Aji%' OR prod.Nombre LIKE '%Gallina%' THEN 24.00
                    WHEN prod.Nombre LIKE '%Ceviche%' THEN 26.00
                    WHEN prod.Nombre LIKE '%Parrilla%' THEN 45.00
                    WHEN prod.Nombre LIKE '%Suspiro%' THEN 12.00
                    WHEN prod.Nombre LIKE '%Picarones%' THEN 10.00
                    WHEN prod.Nombre LIKE '%Papas%' THEN 8.00
                    WHEN prod.Nombre LIKE '%Tequeno%' THEN 14.00
                    WHEN prod.Nombre LIKE '%Pachamanca%' THEN 55.00
                    WHEN prod.Nombre LIKE '%Cuy%' THEN 38.00
                    ELSE 15.00
                END,
                100, 1);
        END IF;
    END LOOP;
END $$;
