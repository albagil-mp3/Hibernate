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

-- Cliente 1
 (
     'Juan García López',
     '12345678Z',
     'Calle Gran Vía 123',
     'Madrid',
     'MADRID',
     '28013',
     '914567890',
     '666123456',
     'juan.garcia@example.com',
     NULL,
     'TRANSFERENCIA_BANCARIA',
     5000.00,
     'ES1234567890123456789012',
     TRUE,
     'Cliente particular, contacto por correo.'
 ),

-- Cliente 2
 (
     'María Fernández Ruiz',
     '23456789D',
     'Plaza Mayor 5',
     'Salamanca',
     'SALAMANCA',
     '37002',
     '923456789',
     '677234567',
     'maria.fernandez@example.com',
     NULL,
     'DOMICILIACION_BANCARIA',
     1500.00,
     'ES2345678901234567890123',
     TRUE,
     'Pago mensual mediante domiciliación.'
 ),

-- Cliente 3
 (
     'Carlos Martínez Pérez',
     '34567890V',
     'Avenida Diagonal 456',
     'Barcelona',
     'BARCELONA',
     '08029',
     '934567890',
     '688345678',
     'carlos.martinez@example.com',
     NULL,
     'TARJETA_CREDITO',
     2500.00,
     'ES3456789012345678901234',
     TRUE,
     'Cliente con pagos con tarjeta.'
 ),

-- Cliente 4
 (
     'Lucía Gómez Sánchez',
     '45678901G',
     'Calle Colón 789',
     'Valencia',
     'VALENCIA',
     '46004',
     '963456789',
     '699456789',
     'lucia.gomez@example.com',
     NULL,
     'TRANSFERENCIA_BANCARIA',
     7500.00,
     'ES4567890123456789012345',
     TRUE,
     'Cliente habitual, facturación trimestral.'
 ),

-- Cliente 5
 (
     'Ana Ruiz Torres',
     '56789012B',
     'Calle del Pan 12',
     'Sevilla',
     'SEVILLA',
     '41001',
     '954567890',
     '610567890',
     'ana.ruiz@example.com',
     NULL,
     'EFECTIVO',
     500.00,
     NULL,
     TRUE,
     'Cliente particular, pagos al contado.'
 ),

-- Cliente 6
 (
     'Miguel Ángel Gómez',
     '67890123B',
     'Polígono Industrial Norte 34',
     'Bilbao',
     'VIZCAYA',
     '48940',
     '944567890',
     '621678901',
     'miguel.gomez@example.com',
     NULL,
     'DOMICILIACION_BANCARIA',
     10000.00,
     'ES5678901234567890123456',
     TRUE,
     'Cliente con domiciliación y crédito aprobado.'
 ),

-- Cliente 7
 (
     'Sofía Navarro Ruiz',
     '78901234X',
     'Plaza de la Constitución 1',
     'Granada',
     'GRANADA',
     '18001',
     '958567890',
     '632789012',
     'sofia.navarro@example.com',
     NULL,
     'TRANSFERENCIA_BANCARIA',
     2000.00,
     'ES6789012345678901234567',
     TRUE,
     'Cliente particular con pedidos regulares.'
 ),

-- Cliente 8
 (
     'Óscar Ruiz Herrera',
     '89012345E',
     'Calle Industria 67',
     'Zaragoza',
     'ZARAGOZA',
     '50013',
     '976567890',
     '643890123',
     'oscar.ruiz@example.com',
     NULL,
     'DOMICILIACION_BANCARIA',
     3000.00,
     'ES7890123456789012345678',
     TRUE,
     'Cliente con domiciliación bancaria.'
 ),

-- Cliente 9
 (
     'Elena Castillo Moreno',
     '90123456A',
     'Avenida de la Paz 89',
     'Murcia',
     'MURCIA',
     '30001',
     '968567890',
     '654901234',
     'elena.castillo@example.com',
     NULL,
     'TARJETA_CREDITO',
     1000.00,
     'ES8901234567890123456789',
     TRUE,
     'Pago con tarjeta; cliente estudiante.'
 ),

-- Cliente 10
 (
     'Pablo Santos Díaz',
     '01234567L',
     'Calle de los Libros 23',
     'Valladolid',
     'VALLADOLID',
     '47001',
     '983567890',
     '665012345',
     'pablo.santos@example.com',
     NULL,
     'CHEQUE',
     800.00,
     'ES9012345678901234567890',
     TRUE,
     'Cliente particular comprador de libros.'
 ),

-- Cliente 11: Inactivo 
 (
     'Laura Molina Peña',
     '11111111H',
     'Calle Cerrada 1',
     'Toledo',
     'TOLEDO',
     '45001',
     '925111111',
     '611111111',
     'laura.molina@example.com',
     NULL,
     'EFECTIVO',
     0.00,
     NULL,
     FALSE,
     'Cliente dado de baja (inactivo).' 
 );
