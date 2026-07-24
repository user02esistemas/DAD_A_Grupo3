-- ============================================
-- MIGRACION v3.0
-- Ejecutar en PostgreSQL sobre GestionRestauranteDB
-- ============================================

-- 1. Eliminar autenticacion de cliente
ALTER TABLE Cliente DROP COLUMN IF EXISTS Usuario;
ALTER TABLE Cliente DROP COLUMN IF EXISTS Clave;

-- 2. Cobro por item
ALTER TABLE MovimientoPedido ADD COLUMN IF NOT EXISTS Pagado BOOLEAN DEFAULT FALSE;
