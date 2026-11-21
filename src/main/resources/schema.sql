-- =============================================================================
-- PROYECTO POS SUPERMERCADO - SCRIPT DDL SIMPLIFICADO (PostgreSQL)
-- =============================================================================

-- El esquema se puede ejecutar al arrancar sin borrar datos existentes.

-- -----------------------------------------------------------------------------
-- 1. TABLA: admin (Solo para autenticación del panel administrativo)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. TABLA: categoria
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- -----------------------------------------------------------------------------
-- 3. TABLA: cupon
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cupon (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descuento_porcentaje NUMERIC(5, 2) NOT NULL CHECK (descuento_porcentaje > 0 AND descuento_porcentaje <= 100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_expiracion TIMESTAMP WITH TIME ZONE
);

-- -----------------------------------------------------------------------------
-- 4. TABLA: producto
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id BIGSERIAL PRIMARY KEY,
    codigo_barras VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    categoria_id BIGINT NOT NULL,
    precio NUMERIC(12, 2) NOT NULL CHECK (precio >= 0),
    iva_porcentaje NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (iva_porcentaje >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo INT NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) 
        REFERENCES categoria(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- -----------------------------------------------------------------------------
-- 5. TABLA: venta (Aquí se registran las compras asociadas a la CC del comprador)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS venta (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    documento_cliente VARCHAR(20) NOT NULL, -- La CC que digita el cajero en el POS (texto plano)
    cupon_id BIGINT,
    fecha_hora TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (subtotal >= 0),
    total_iva NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (total_iva >= 0),
    descuento NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (descuento >= 0),
    total NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (total >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGADA' CHECK (estado IN ('PENDIENTE', 'PAGADA', 'ANULADA')),
    CONSTRAINT fk_venta_cupon FOREIGN KEY (cupon_id) 
        REFERENCES cupon(id) ON UPDATE CASCADE ON DELETE SET NULL
);

-- -----------------------------------------------------------------------------
-- 6. TABLA: detalle_venta (Ítems de la compra con copia del precio histórico)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_venta (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT,
    nombre VARCHAR(150) NOT NULL,
    precio_unitario NUMERIC(12, 2) NOT NULL CHECK (precio_unitario >= 0),
    iva_porcentaje NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (iva_porcentaje >= 0),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    total_linea NUMERIC(12, 2) NOT NULL CHECK (total_linea >= 0),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) 
        REFERENCES venta(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) 
        REFERENCES producto(id) ON DELETE SET NULL
);

-- -----------------------------------------------------------------------------
-- 7. TABLA: pago (Simulación de pago)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    metodo VARCHAR(30) NOT NULL CHECK (metodo IN ('EFECTIVO', 'TARJETA', 'QR')),
    monto NUMERIC(12, 2) NOT NULL CHECK (monto >= 0),
    recibido NUMERIC(12, 2) CHECK (recibido >= 0),
    cambio NUMERIC(12, 2) DEFAULT 0.00 CHECK (cambio >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'APROBADO' CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'EXPIRADO')),
    referencia VARCHAR(100),
    ultimos4 VARCHAR(4),
    codigo_autorizacion VARCHAR(50),
    fecha TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_pago_venta FOREIGN KEY (venta_id) 
        REFERENCES venta(id) ON DELETE CASCADE
);

-- Índices de rendimiento
CREATE INDEX IF NOT EXISTS idx_producto_categoria ON producto(categoria_id);
CREATE INDEX IF NOT EXISTS idx_producto_codigo_barras ON producto(codigo_barras);
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_venta_cliente ON venta(documento_cliente);
CREATE INDEX IF NOT EXISTS idx_detalle_venta_venta ON detalle_venta(venta_id);
CREATE INDEX IF NOT EXISTS idx_pago_venta ON pago(venta_id);
