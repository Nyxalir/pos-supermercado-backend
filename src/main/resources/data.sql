-- =============================================================================
-- PROYECTO POS SUPERMERCADO - DATOS SEMILLA INICIALES (PostgreSQL)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. ADMINISTRADORES DEL PANEL
-- Contraseña en texto plano para ambos: "admin123"
-- Generada con BCrypt (coste 10).
-- -----------------------------------------------------------------------------
INSERT INTO admin (username, password_hash)
VALUES 
    ('admin', '$2a$10$OE8amf1C2.X5F82AIp.La.ZmOTEqKpL8Iu8RkFBFsxuIGd.jSoJVu'),
    ('supervisor', '$2a$10$OE8amf1C2.X5F82AIp.La.ZmOTEqKpL8Iu8RkFBFsxuIGd.jSoJVu')
ON CONFLICT (username) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 2. CATEGORÍAS BASE
-- -----------------------------------------------------------------------------
INSERT INTO categoria (nombre)
VALUES 
    ('Lácteos y Refrigerados'),      -- id: 1
    ('Abarrotes y Despensa'),        -- id: 2
    ('Bebidas y Licores'),           -- id: 3
    ('Frutas y Verduras'),           -- id: 4
    ('Aseo y Limpieza'),             -- id: 5
    ('Snacks y Dulces')              -- id: 6
ON CONFLICT (nombre) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 3. CUPONES DE DESCUENTO
-- -----------------------------------------------------------------------------
INSERT INTO cupon (codigo, descuento_porcentaje, activo, fecha_expiracion)
VALUES 
    ('BIENVENIDO10', 10.00, true, NOW() + INTERVAL '30 days'),
    ('SUPER5', 5.00, true, NOW() + INTERVAL '60 days'),
    ('PROMO15', 15.00, true, NOW() + INTERVAL '15 days'),
    ('DESCUENTO20', 20.00, true, NOW() + INTERVAL '90 days')
ON CONFLICT (codigo) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 4. PRODUCTOS DEL SUPERMERCADO
-- (Precios en COP, IVA según tarifas colombianas: 0%, 5% y 19%)
-- -----------------------------------------------------------------------------
INSERT INTO producto (codigo_barras, nombre, categoria_id, precio, iva_porcentaje, stock, stock_minimo, activo)
VALUES 
    -- Lácteos y Refrigerados (cat 1)
    ('7701001001', 'Leche Entera 1 Litro', 1, 4600.00, 0.00, 80, 15, true),
    ('7701001002', 'Queso Campesino 500g', 1, 9800.00, 5.00, 35, 8, true),
    ('7701001003', 'Yogurt Fresa 1 Litro', 1, 6500.00, 19.00, 40, 10, true),
    ('7701001004', 'Mantequilla con Sal 250g', 1, 7200.00, 19.00, 25, 5, true),
    ('7701001005', 'Huevos AA x30 Unidades', 1, 18500.00, 0.00, 50, 10, true),

    -- Abarrotes y Despensa (cat 2)
    ('7702002001', 'Arroz Blanco Extra 1kg', 2, 4200.00, 0.00, 120, 25, true),
    ('7702002002', 'Aceite Vegetal 900ml', 2, 11900.00, 0.00, 60, 12, true),
    ('7702002003', 'Café Tostado y Molido 500g', 2, 16500.00, 5.00, 45, 10, true),
    ('7702002004', 'Azúcar Blanca 1kg', 2, 3800.00, 5.00, 70, 15, true),
    ('7702002005', 'Pasta Spaghetti 500g', 2, 3400.00, 5.00, 90, 20, true),
    ('7702002006', 'Atún en Lomitos en Aceite 170g', 2, 6900.00, 0.00, 65, 15, true),

    -- Bebidas y Licores (cat 3)
    ('7703003001', 'Agua Mineral sin Gas 600ml', 3, 2000.00, 0.00, 100, 20, true),
    ('7703003002', 'Gaseosa Cola 1.5 Litros', 3, 5800.00, 19.00, 55, 12, true),
    ('7703003003', 'Jugo Néctar Durazno 1L', 3, 4900.00, 19.00, 40, 10, true),
    ('7703003004', 'Cerveza Nacional Lata 330ml', 3, 3200.00, 19.00, 120, 24, true),

    -- Frutas y Verduras (cat 4)
    ('7704004001', 'Plátano Maduro 1kg', 4, 3200.00, 0.00, 50, 10, true),
    ('7704004002', 'Tomate Chonto 1kg', 4, 4500.00, 0.00, 40, 10, true),
    ('7704004003', 'Cebolla Cabezona Roja 1kg', 4, 3900.00, 0.00, 45, 10, true),
    ('7704004004', 'Manzana Roja Unidad', 4, 2200.00, 0.00, 80, 15, true),

    -- Aseo y Limpieza (cat 5)
    ('7705005001', 'Detergente en Polvo 1kg', 5, 9200.00, 19.00, 45, 10, true),
    ('7705005002', 'Lavaplatos Líquido 500ml', 5, 5400.00, 19.00, 35, 8, true),
    ('7705005003', 'Papel Higiénico x4 Rollos', 5, 8600.00, 19.00, 60, 15, true),
    ('7705005004', 'Jabón de Baño en Barra 3x120g', 5, 7500.00, 19.00, 50, 10, true),

    -- Snacks y Dulces (cat 6)
    ('7706006001', 'Papas Fritas Natural 115g', 6, 4200.00, 19.00, 70, 15, true),
    ('7706006002', 'Galletas Dulces Chocolate x6', 6, 2800.00, 19.00, 80, 15, true),
    ('7706006003', 'Chocolatina Tradicional 50g', 6, 2500.00, 19.00, 90, 20, true)
ON CONFLICT (codigo_barras) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 5. VENTA DEMOSTRATIVA INICIAL (Para validar reportes e historial en el POS)
-- Factura POS-000001 asociada a un cliente con CC digitada directamente
-- -----------------------------------------------------------------------------
INSERT INTO venta (numero_factura, documento_cliente, cupon_id, fecha_hora, subtotal, total_iva, descuento, total, estado)
VALUES 
    ('POS-000001', '1098765432', NULL, NOW() - INTERVAL '1 hour', 8800.00, 0.00, 0.00, 8800.00, 'PAGADA')
ON CONFLICT (numero_factura) DO NOTHING;

-- Detalle de la venta POS-000001 (2 Leches Enteras)
INSERT INTO detalle_venta (venta_id, producto_id, nombre, precio_unitario, iva_porcentaje, cantidad, total_linea)
SELECT v.id, p.id, p.nombre, p.precio, p.iva_porcentaje, 2, 8800.00
FROM venta v
JOIN producto p ON p.codigo_barras = '7701001001'
WHERE v.numero_factura = 'POS-000001'
  AND NOT EXISTS (
      SELECT 1 FROM detalle_venta d
      WHERE d.venta_id = v.id AND d.producto_id = p.id
  )
ON CONFLICT DO NOTHING;

-- Pago de la venta POS-000001 (Efectivo con cambio)
INSERT INTO pago (venta_id, metodo, monto, recibido, cambio, estado, fecha)
SELECT v.id, 'EFECTIVO', 8800.00, 10000.00, 1200.00, 'APROBADO', NOW() - INTERVAL '1 hour'
FROM venta v
WHERE v.numero_factura = 'POS-000001'
  AND NOT EXISTS (
      SELECT 1 FROM pago p WHERE p.venta_id = v.id AND p.metodo = 'EFECTIVO'
  )
ON CONFLICT DO NOTHING;
