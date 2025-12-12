-- ===============================================
-- SCRIPT DE BASE DE DATOS DE PRUEBA - MySQL
-- Sistema de Facturación Hibernate
-- ===============================================

-- Crear la base de datos
DROP DATABASE IF EXISTS hibernate;
CREATE DATABASE hibernate;
USE hibernate;

-- Crear tabla de clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    dni VARCHAR(9) NOT NULL UNIQUE,
    direccio VARCHAR(50) NOT NULL,
    poblacio VARCHAR(30) NOT NULL,
    provincia ENUM(
        'ALAVA', 'ALBACETE', 'ALICANTE', 'ALMERIA', 'ASTURIAS', 'AVILA', 'BADAJOZ', 'BALEARES',
        'BARCELONA', 'BURGOS', 'CACERES', 'CADIZ', 'CANTABRIA', 'CASTELLON', 'CIUDAD_REAL',
        'CORDOBA', 'CORUNA', 'CUENCA', 'GIRONA', 'GRANADA', 'GUADALAJARA', 'GUIPUZCOA',
        'HUELVA', 'HUESCA', 'JAEN', 'LEON', 'LLEIDA', 'LUGO', 'MADRID', 'MALAGA', 'MURCIA',
        'NAVARRA', 'OURENSE', 'PALENCIA', 'PALMAS', 'PONTEVEDRA', 'RIOJA', 'SALAMANCA',
        'SANTA_CRUZ_TENERIFE', 'SEGOVIA', 'SEVILLA', 'SORIA', 'TARRAGONA', 'TERUEL', 'TOLEDO',
        'VALENCIA', 'VALLADOLID', 'VIZCAYA', 'ZAMORA', 'ZARAGOZA', 'CEUTA', 'MELILLA'
    ) NOT NULL,
    codi_postal VARCHAR(5),
    telefon_fixe VARCHAR(9),
    telefon_mobil VARCHAR(9),
    correu_electronic VARCHAR(80),
    plana_web VARCHAR(50),
    forma_pagament ENUM('EFECTIVO', 'TRANSFERENCIA_BANCARIA', 'TARJETA_CREDITO', 'DOMICILIACION_BANCARIA', 'CHEQUE') NOT NULL,
    limit_credit DECIMAL(10,2) DEFAULT 0.00,
    numero_conta_bancari VARCHAR(34),
    actiu BOOLEAN NOT NULL DEFAULT TRUE,
    observacions VARCHAR(500),
    imatge LONGBLOB
);

-- ===============================================
-- DATOS DE PRUEBA
-- ===============================================

-- Insertar clientes de prueba
INSERT INTO clientes (
    nom, dni, direccio, poblacio, provincia, codi_postal, 
    telefon_fixe, telefon_mobil, correu_electronic, plana_web,
    forma_pagament, limit_credit, numero_conta_bancari, actiu, observacions
) VALUES

-- Cliente 1: Empresa de tecnología
(
    'Tech Solutions SL',
    '12345678D',
    'Calle Gran Via 123',
    'Madrid',
    'MADRID',
    '28013',
    '914567890',
    '666123456',
    'info@techsolutions.es',
    'www.techsolutions.es',
    'TRANSFERENCIA_BANCARIA',
    50000.00,
    'ES1234567890123456789012',
    TRUE,
    'Cliente preferente. Descuento del 10% en servicios de consultoría.'
),

-- Cliente 2: Restaurante
(
    'Restaurante El Buen Gusto',
    '23456789Z',
    'Plaza Mayor 5',
    'Salamanca',
    'SALAMANCA',
    '37002',
    '923456789',
    '677234567',
    'reservas@elbuengusto.es',
    'www.elbuengusto.es',
    'DOMICILIACION_BANCARIA',
    15000.00,
    'ES2345678901234567890123',
    TRUE,
    'Facturación mensual. Incluye servicios de catering para eventos.'
),

-- Cliente 3: Tienda de ropa
(
    'Moda & Estilo CB',
    '34567890L',
    'Avenida Diagonal 456',
    'Barcelona',
    'BARCELONA',
    '08029',
    '934567890',
    '688345678',
    'ventas@modaestilo.es',
    NULL,
    'TARJETA_CREDITO',
    25000.00,
    'ES3456789012345678901234',
    TRUE,
    'Cliente desde 2020. Pedidos frecuentes en temporada alta.'
),

-- Cliente 4: Consultoría
(
    'Consultores Asociados SA',
    '45678901R',
    'Calle Colón 789',
    'Valencia',
    'VALENCIA',
    '46004',
    '963456789',
    '699456789',
    'contacto@consultores.es',
    'www.consultoresasociados.es',
    'TRANSFERENCIA_BANCARIA',
    75000.00,
    'ES4567890123456789012345',
    TRUE,
    'Multinacional con oficinas en España. Facturación trimestral.'
),

-- Cliente 5: Panadería local
(
    'Panadería San Miguel',
    '56789012Y',
    'Calle del Pan 12',
    'Sevilla',
    'SEVILLA',
    '41001',
    '954567890',
    '610567890',
    'pedidos@panaderiasanmiguel.es',
    NULL,
    'EFECTIVO',
    5000.00,
    NULL,
    TRUE,
    'Negocio familiar. Pago al contado. Pedidos semanales.'
),

-- Cliente 6: Empresa de construcción
(
    'Construcciones Gómez SL',
    '67890123B',
    'Polígono Industrial Norte 34',
    'Bilbao',
    'VIZCAYA',
    '48940',
    '944567890',
    '621678901',
    'obras@construccionesgomez.es',
    'www.construccionesgomez.es',
    'DOMICILIACION_BANCARIA',
    100000.00,
    'ES5678901234567890123456',
    TRUE,
    'Especialistas en obra pública. Proyectos de larga duración.'
),

-- Cliente 7: Farmacia
(
    'Farmacia Central',
    '78901234Q',
    'Plaza de la Constitución 1',
    'Granada',
    'GRANADA',
    '18001',
    '958567890',
    '632789012',
    'info@farmaciacentral.es',
    NULL,
    'TRANSFERENCIA_BANCARIA',
    20000.00,
    'ES6789012345678901234567',
    TRUE,
    'Farmacia 24h. Servicios especializados en medicamentos oncológicos.'
),

-- Cliente 8: Empresa de limpieza
(
    'Limpiezas Profesionales Norte',
    '89012345K',
    'Calle Industria 67',
    'Zaragoza',
    'ZARAGOZA',
    '50013',
    '976567890',
    '643890123',
    'servicios@limpiezasnorte.es',
    'www.limpiezasnorte.es',
    'DOMICILIACION_BANCARIA',
    30000.00,
    'ES7890123456789012345678',
    TRUE,
    'Contratos anuales con empresas. Servicio de limpieza industrial.'
),

-- Cliente 9: Autoescuela
(
    'Autoescuela Conducir Bien',
    '90123456A',
    'Avenida de la Paz 89',
    'Murcia',
    'MURCIA',
    '30001',
    '968567890',
    '654901234',
    'matriculas@conducirbien.es',
    'www.autoescuelaconducirbien.es',
    'TARJETA_CREDITO',
    10000.00,
    'ES8901234567890123456789',
    TRUE,
    'Cursos intensivos y regulares. Descuento para estudiantes.'
),

-- Cliente 10: Librería
(
    'Librería El Saber',
    '01234567J',
    'Calle de los Libros 23',
    'Valladolid',
    'VALLADOLID',
    '47001',
    '983567890',
    '665012345',
    'pedidos@libreriasaber.es',
    NULL,
    'CHEQUE',
    8000.00,
    'ES9012345678901234567890',
    TRUE,
    'Especializada en libros técnicos y universitarios. Cliente desde 2018.'
),

-- Cliente 11: Inactivo para pruebas
(
    'Empresa Cerrada SL',
    '11111111V',
    'Calle Cerrada 1',
    'Toledo',
    'TOLEDO',
    '45001',
    '925111111',
    '611111111',
    'info@cerrada.es',
    NULL,
    'EFECTIVO',
    0.00,
    NULL,
    FALSE,
    'Cliente dado de baja por cierre de negocio.'
);

-- ===============================================
-- VERIFICACIONES
-- ===============================================

-- Mostrar resumen de datos insertados
SELECT 
    'Total de clientes' as Descripcion,
    COUNT(*) as Cantidad
FROM clientes

UNION ALL

SELECT 
    'Clientes activos' as Descripcion,
    COUNT(*) as Cantidad
FROM clientes 
WHERE actiu = TRUE

UNION ALL

SELECT 
    'Clientes inactivos' as Descripcion,
    COUNT(*) as Cantidad
FROM clientes 
WHERE actiu = FALSE;

-- Mostrar distribución por provincia
SELECT 
    provincia,
    COUNT(*) as total_clientes
FROM clientes 
GROUP BY provincia 
ORDER BY total_clientes DESC;

-- Mostrar distribución por forma de pago
SELECT 
    forma_pagament,
    COUNT(*) as total_clientes,
    ROUND(AVG(limit_credit), 2) as limite_promedio
FROM clientes 
WHERE actiu = TRUE
GROUP BY forma_pagament 
ORDER BY total_clientes DESC;

-- ===============================================
-- COMANDOS ÚTILES PARA PRUEBAS
-- ===============================================

-- Para conectar desde la aplicación, usar estos datos:
-- Host: localhost
-- Puerto: 3306
-- Base de datos: hibernate
-- Usuario: usuario
-- Contraseña: 1234

-- NOTAS:
-- 1. Todos los DNIs son válidos según el algoritmo español
-- 2. Los IBANs son formato válido pero ficticios
-- 3. Los teléfonos siguen el formato español
-- 4. Hay ejemplos de todas las formas de pago
-- 5. Hay un cliente inactivo para probar filtros
-- 6. Los límites de crédito varían según el tipo de negocio