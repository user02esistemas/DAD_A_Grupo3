-- ============================================
-- SEED DATA - Overo's Restaurant Campestre
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

-- Clientes
INSERT INTO Cliente (IdTipoDocumento, Nombre, Documento, IdTipoCliente, Apellido, Telefono, Sexo, Email, Usuario, Clave) VALUES
(1,'Juan','12345678',1,'Perez','999111222','M','juan@mail.com','juan','juan123'),
(1,'Maria','87654321',3,'Lopez','999333444','F','maria@mail.com','maria','maria123'),
(3,'Empresa XYZ','20555666778',2,'-','01-4445555','M','info@xyz.com','empresa','empresa123');

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

-- Productos
INSERT INTO Producto (IdTipoProducto, Nombre, Descripcion, IdTiempoPreparacion, IdEstadoProducto) VALUES
(2,'Lomo Saltado','Lomo a la parrilla con papas fritas y arroz',3,1),
(2,'Aji de Gallina','Gallina deshilachada en salsa de aji amarillo',2,1),
(2,'Ceviche de Pescado','Pescado fresco marinado en limon',1,1),
(1,'Chicha Morada','Bebida tradicional de maiz morado',1,1),
(1,'Cerveza Artesanal','Cerveza local 500ml',1,1),
(3,'Tres Leches','Bizcocho banado en tres leches',1,1),
(4,'Tequenos','Rollitos de queso fritos',1,1),
(5,'Pachamanca','Plato especial de la casa - carne y verduras',4,1);

-- Presentaciones
INSERT INTO Presentacion (IdProducto, Nombre, Precio, Stock, IdEstadoPresentacion) VALUES
(1,'Lomo Saltado Personal',35.00,50,1),(1,'Lomo Saltado para 2',60.00,50,1),
(2,'Aji de Gallina Personal',28.00,40,1),
(3,'Ceviche Personal',32.00,30,1),(3,'Ceviche Familiar',55.00,30,1),
(4,'Chicha Morada 500ml',8.00,100,1),(4,'Chicha Morada 1L',14.00,100,1),
(5,'Cerveza Artesanal 500ml',15.00,80,1),
(6,'Tres Leches Individual',18.00,25,1),
(7,'Tequenos 6 unidades',12.00,40,1),
(8,'Pachamanca Personal',55.00,20,1),(8,'Pachamanca Familiar',95.00,20,1);
