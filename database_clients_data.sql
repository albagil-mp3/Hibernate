-- ===============================================
-- TEST DATABASE SCRIPT - MySQL
-- Hibernate Billing System
-- ===============================================

-- Drop table if it exists
USE Facturacio;
DROP TABLE IF EXISTS clients;

-- Create clients table
CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) DEFAULT NULL,
    name VARCHAR(50) NOT NULL,
    dni VARCHAR(9) NOT NULL UNIQUE,
    address VARCHAR(50) NOT NULL,
    city VARCHAR(30) NOT NULL,
    province ENUM(
        'ALAVA', 'ALBACETE', 'ALICANTE', 'ALMERIA', 'ASTURIAS', 'AVILA', 'BADAJOZ', 'BALEARES',
        'BARCELONA', 'BURGOS', 'CACERES', 'CADIZ', 'CANTABRIA', 'CASTELLON', 'CIUDAD_REAL',
        'CORDOBA', 'CORUNA', 'CUENCA', 'GIRONA', 'GRANADA', 'GUADALAJARA', 'GUIPUZCOA',
        'HUELVA', 'HUESCA', 'JAEN', 'LEON', 'LLEIDA', 'LUGO', 'MADRID', 'MALAGA', 'MURCIA',
        'NAVARRA', 'OURENSE', 'PALENCIA', 'PALMAS', 'PONTEVEDRA', 'RIOJA', 'SALAMANCA',
        'SANTA_CRUZ_TENERIFE', 'SEGOVIA', 'SEVILLA', 'SORIA', 'TARRAGONA', 'TERUEL', 'TOLEDO',
        'VALENCIA', 'VALLADOLID', 'VIZCAYA', 'ZAMORA', 'ZARAGOZA', 'CEUTA', 'MELILLA'
    ) NOT NULL,
    postal_code VARCHAR(5),
    landline_phone VARCHAR(9),
    mobile_phone VARCHAR(9),
    email VARCHAR(80),
    website VARCHAR(50),
    payment_method ENUM('EFECTIVO', 'TRANSFERENCIA_BANCARIA', 'TARJETA_CREDITO', 'DOMICILIACION_BANCARIA', 'CHEQUE') NOT NULL,
    credit_limit DECIMAL(10,2) DEFAULT 0.00,
    bank_account_number VARCHAR(34),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    observations VARCHAR(500),
    image LONGBLOB
);

DELIMITER $$
CREATE TRIGGER clients_code_insert 
BEFORE INSERT ON clients 
FOR EACH ROW 
BEGIN
    IF @next_client_id IS NULL THEN
        SET @next_client_id = (SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='clients');
    END IF;
    SET NEW.code = CONCAT('CLI', LPAD(@next_client_id, 3, '0'));
    SET @next_client_id = @next_client_id + 1;
END$$
DELIMITER ;


-- ===============================================
-- TEST DATA
-- ===============================================

-- Insert test clients
-- Initialize session sequence for client codes so multi-row INSERTs get sequential codes
SET @next_client_id = (SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='clients');

INSERT INTO clients (
    name, dni, address, city, province, postal_code, 
    landline_phone, mobile_phone, email, website,
    payment_method, credit_limit, bank_account_number, active, observations
) VALUES

-- Client 1
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
     'Particular Client, contact by email.'
 ),

-- Client 2
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
     'Monthly payment by direct debit.'
 ),

-- Client 3
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
     'Client with card payments.'
 ),

-- Client 4
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
     'Regular client, quarterly billing.'
 ),

-- Client 5
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
     'Particular client, cash payments.'
 ),

-- Client 6
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
     'Client with direct debit and approved credit.'
 ),

-- Client 7
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
     'Particular client with regular orders.'
 ),

-- Client 8
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
     'Client with direct debit.'
 ),

-- Client 9
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
     'Card payment; student client.'
 ),

-- Client 10
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
     'Particular client, book buyer.'
 ),

-- Client 11: Inactive 
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
     'Client deactivated (inactive).' 
 );
