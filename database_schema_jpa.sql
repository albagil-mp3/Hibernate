-- =====================================================
-- DATABASE SCHEMA FOR HIBERNATE BILLING SYSTEM
-- =====================================================

USE Facturacio; 

-- Drop existing tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS sales_order_line;
DROP TABLE IF EXISTS sales_order;
DROP TABLE IF EXISTS document_line;
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS delivery_note;
DROP TABLE IF EXISTS business_document;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS item_categories;
DROP TABLE IF EXISTS item_families;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS party;

-- =====================================================
-- PARTY HIERARCHY (JOINED Inheritance)
-- =====================================================

-- Base table for Party entity
CREATE TABLE party (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(100),
    city VARCHAR(100),
    province ENUM('ALAVA', 'ALBACETE', 'ALICANTE', 'ALMERIA', 'ASTURIAS', 'AVILA', 'BADAJOZ', 
                  'BARCELONA', 'BURGOS', 'CACERES', 'CADIZ', 'CANTABRIA', 'CASTELLON', 'CEUTA', 
                  'CIUDAD_REAL', 'CORDOBA', 'CUENCA', 'GERONA', 'GRANADA', 'GUADALAJARA', 
                  'GUIPUZCOA', 'HUELVA', 'HUESCA', 'ISLAS_BALEARES', 'JAEN', 'LA_CORUNA', 
                  'LA_RIOJA', 'LAS_PALMAS', 'LEON', 'LERIDA', 'LUGO', 'MADRID', 'MALAGA', 
                  'MELILLA', 'MURCIA', 'NAVARRA', 'ORENSE', 'PALENCIA', 'PONTEVEDRA', 
                  'SALAMANCA', 'SANTA_CRUZ_DE_TENERIFE', 'SEGOVIA', 'SEVILLA', 'SORIA', 
                  'TARRAGONA', 'TERUEL', 'TOLEDO', 'VALENCIA', 'VALLADOLID', 'VIZCAYA', 'ZAMORA', 'ZARAGOZA'),
    postal_code VARCHAR(5),
    fixed_phone VARCHAR(20),
    mobile_phone VARCHAR(20),
    website VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Client table (extends Party via JOINED inheritance)
CREATE TABLE client (
    id BIGINT PRIMARY KEY,
    dni VARCHAR(9) UNIQUE NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'TRANSFER', 'CHECK') NOT NULL DEFAULT 'CASH',
    credit_limit DECIMAL(10, 2) DEFAULT 0.00,
    bank_account_number VARCHAR(64),
    active BOOLEAN DEFAULT TRUE,
    observations VARCHAR(500),
    FOREIGN KEY (id) REFERENCES party(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Supplier table (extends Party via JOINED inheritance)
CREATE TABLE supplier (
    id BIGINT PRIMARY KEY,
    tax_id VARCHAR(20) UNIQUE NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES party(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- ITEM AND RELATED TABLES
-- =====================================================

-- Units table (measurement units)
CREATE TABLE units (
    symbol VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Item families table (product families)
CREATE TABLE item_families (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Item categories table (product categories)
CREATE TABLE item_categories (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Item table (products/inventory)
CREATE TABLE item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    family_code VARCHAR(20),
    category_code VARCHAR(20),
    unit_symbol VARCHAR(10),
    supplier_id BIGINT,
    cost_price DECIMAL(10, 2),
    sale_price DECIMAL(10, 2),
    iva_percent DECIMAL(5, 2) DEFAULT 21.00,
    current_stock INT DEFAULT 0,
    minimum_stock INT DEFAULT 0,
    barcode VARCHAR(100),
    observations VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    image VARCHAR(500),
    date_added DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_code) REFERENCES item_families(code),
    FOREIGN KEY (category_code) REFERENCES item_categories(code),
    FOREIGN KEY (unit_symbol) REFERENCES units(symbol),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- BUSINESS DOCUMENT HIERARCHY (JOINED Inheritance)
-- =====================================================

-- Base table for BusinessDocument entity
CREATE TABLE business_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_date DATE NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    party_id BIGINT,
    subtotal DECIMAL(10, 2) DEFAULT 0.00,
    taxes DECIMAL(10, 2) DEFAULT 0.00,
    total DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (party_id) REFERENCES party(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DeliveryNote table (extends BusinessDocument via JOINED inheritance)
CREATE TABLE delivery_note (
    id BIGINT PRIMARY KEY,
    delivered BOOLEAN DEFAULT FALSE,
    invoiced BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES business_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Invoice table (extends BusinessDocument via JOINED inheritance)
CREATE TABLE invoice (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES business_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Document lines table (line items for documents)
CREATE TABLE document_line (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    tax_rate DECIMAL(5, 2) DEFAULT 21.00,
    FOREIGN KEY (document_id) REFERENCES business_document(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- INITIAL DATA
-- =====================================================

-- Insert default units
INSERT INTO units (symbol, name) VALUES
('ud', 'Unidad'),
('kg', 'Kilogramo'),
('l', 'Litro'),
('m', 'Metro'),
('m2', 'Metro cuadrado'),
('m3', 'Metro cúbico'),
('caja', 'Caja'),
('paq', 'Paquete');

-- Insert default item families
INSERT INTO item_families (code, name) VALUES
('ALIMENTACION', 'Alimentación'),
('BEBIDAS', 'Bebidas'),
('LIMPIEZA', 'Limpieza'),
('HIGIENE', 'Higiene'),
('OTROS', 'Otros');

-- Insert default item categories
INSERT INTO item_categories (code, name) VALUES
('FRESCOS', 'Productos frescos'),
('CONSERVAS', 'Conservas'),
('LACTEOS', 'Lácteos'),
('PANADERIA', 'Panadería'),
('CONGELADOS', 'Congelados'),
('OTROS', 'Otros');

-- Insert sample parties (3 suppliers + 10 clients = 13 total)
INSERT INTO party (id, name, address, email, city, province, postal_code, fixed_phone, mobile_phone, website) VALUES
-- Suppliers (ids 1-3)
(1, 'Distribuciones García S.L.', 'Calle Mayor 123', 'contacto@garcia.com', 'Madrid', 'MADRID', '28001', '912345678', '612345678', 'www.garcia.com'),
(2, 'Proveedores Alimentarios S.A.', 'Polígono Industrial 5', 'ventas@proalim.es', 'Sevilla', 'SEVILLA', '41001', '954123456', '654123456', 'www.proalim.es'),
(3, 'Suministros Industriales Gómez', 'Avenida Industria 87', 'info@sigomez.com', 'Zaragoza', 'ZARAGOZA', '50001', '976543210', '676543210', 'www.sigomez.com'),
-- Clients (ids 4-13)
(4, 'Juan Pérez López', 'Avenida Libertad 45', 'juan.perez@email.com', 'Barcelona', 'BARCELONA', '08001', '934567890', '634567890', NULL),
(5, 'María González Ruiz', 'Plaza España 12', 'maria.gonzalez@email.com', 'Valencia', 'VALENCIA', '46001', '963456789', '663456789', NULL),
(6, 'Carlos Martínez Sánchez', 'Calle Real 78', 'carlos.martinez@email.com', 'Málaga', 'MALAGA', '29001', '952111222', '652111222', NULL),
(7, 'Ana Rodríguez Fernández', 'Paseo Marítimo 34', 'ana.rodriguez@email.com', 'Alicante', 'ALICANTE', '03001', '965333444', '665333444', NULL),
(8, 'Pedro López García', 'Calle Colón 56', 'pedro.lopez@email.com', 'Bilbao', 'VIZCAYA', '48001', '944555666', '644555666', NULL),
(9, 'Laura Sánchez Díaz', 'Avenida Constitución 90', 'laura.sanchez@email.com', 'Salamanca', 'SALAMANCA', '37001', '923777888', '623777888', NULL),
(10, 'Restaurante El Buen Sabor S.L.', 'Plaza Mayor 15', 'pedidos@buensabor.com', 'Toledo', 'TOLEDO', '45001', '925999000', '625999000', 'www.buensabor.com'),
(11, 'Comercial Fernández Hnos.', 'Polígono Sur 23', 'comercial@fernandez.es', 'Granada', 'GRANADA', '18001', '958222333', '658222333', 'www.fernandezhermanos.es'),
(12, 'Isabel Torres Ramírez', 'Calle San Juan 67', 'isabel.torres@email.com', 'Murcia', 'MURCIA', '30001', '968444555', '668444555', NULL),
(13, 'Supermercados Costa S.A.', 'Avenida del Mar 101', 'compras@supercosta.com', 'Santander', 'CANTABRIA', '39001', '942666777', '642666777', 'www.supercosta.com');

-- Insert sample suppliers (3 suppliers)
INSERT INTO supplier (id, tax_id, code, active) VALUES
(1, 'B12345678', 'SUP001', TRUE),
(2, 'A98765432', 'SUP002', TRUE),
(3, 'B87654321', 'SUP003', TRUE);

-- Insert sample clients (10 clients with varied data)
INSERT INTO client (id, dni, code, payment_method, credit_limit, bank_account_number, active, observations) VALUES
(4, '12345678A', 'CLI001', 'TRANSFER', 5000.00, 'ES7921000813610123456789', TRUE, 'Cliente preferente con descuento del 10%'),
(5, '87654321B', 'CLI002', 'CASH', 1000.00, NULL, TRUE, 'Cliente de confianza'),
(6, '23456789C', 'CLI003', 'CARD', 3000.00, 'ES1234567890123456789012', TRUE, NULL),
(7, '34567890D', 'CLI004', 'TRANSFER', 8000.00, 'ES9876543210987654321098', TRUE, 'Pago a 30 días'),
(8, '45678901E', 'CLI005', 'CHECK', 2000.00, NULL, TRUE, NULL),
(9, '56789012F', 'CLI006', 'CASH', 500.00, NULL, TRUE, 'Pagos solo en efectivo'),
(10, '67890123G', 'CLI007', 'TRANSFER', 10000.00, 'ES5544332211009988776655', TRUE, 'Cliente corporativo - Facturación mensual'),
(11, '78901234H', 'CLI008', 'CARD', 4000.00, 'ES1122334455667788990011', TRUE, 'Descuento del 5%'),
(12, '89012345I', 'CLI009', 'CASH', 1500.00, NULL, TRUE, NULL),
(13, '90123456J', 'CLI010', 'TRANSFER', 15000.00, 'ES9988776655443322110099', TRUE, 'Cliente VIP - Máxima prioridad');

-- Insert sample items (10 items with varied categories and suppliers)
INSERT INTO item (id, code, description, family_code, category_code, unit_symbol, supplier_id, cost_price, sale_price, iva_percent, current_stock, minimum_stock, barcode, observations, active, image) VALUES
(1, 'ITEM001', 'Leche Entera 1L', 'ALIMENTACION', 'LACTEOS', 'l', 1, 0.80, 1.20, 10.00, 100, 20, '8412345678901', 'Caducidad próxima revisar stock', TRUE, NULL),
(2, 'ITEM002', 'Pan Integral 500g', 'ALIMENTACION', 'PANADERIA', 'ud', 1, 1.50, 2.50, 10.00, 50, 10, '8423456789012', 'Producto diario', TRUE, NULL),
(3, 'ITEM003', 'Detergente Lavadora 3L', 'LIMPIEZA', 'OTROS', 'l', 3, 5.00, 8.50, 21.00, 30, 5, '8434567890123', NULL, TRUE, NULL),
(4, 'ITEM004', 'Yogur Natural Pack 8 unidades', 'ALIMENTACION', 'LACTEOS', 'paq', 1, 2.00, 3.50, 10.00, 75, 15, '8445678901234', 'Mantener refrigerado', TRUE, NULL),
(5, 'ITEM005', 'Jamón Serrano 100g', 'ALIMENTACION', 'FRESCOS', 'kg', 2, 12.00, 18.00, 10.00, 25, 5, '8456789012345', 'Producto premium', TRUE, NULL),
(6, 'ITEM006', 'Agua Mineral 1.5L', 'BEBIDAS', 'OTROS', 'l', 2, 0.30, 0.60, 10.00, 200, 50, '8467890123456', 'Pack de 6 unidades disponible', TRUE, NULL),
(7, 'ITEM007', 'Gel de Baño 750ml', 'HIGIENE', 'OTROS', 'l', 3, 2.50, 4.20, 21.00, 40, 10, '8478901234567', NULL, TRUE, NULL),
(8, 'ITEM008', 'Arroz Largo 1kg', 'ALIMENTACION', 'CONSERVAS', 'kg', 2, 1.20, 2.00, 10.00, 80, 20, '8489012345678', 'Variedad extra', TRUE, NULL),
(9, 'ITEM009', 'Pizza Congelada 400g', 'ALIMENTACION', 'CONGELADOS', 'ud', 1, 2.80, 4.50, 10.00, 35, 10, '8490123456789', 'Mantener a -18°C', TRUE, NULL),
(10, 'ITEM010', 'Lejía 2L', 'LIMPIEZA', 'OTROS', 'l', 3, 1.50, 2.80, 21.00, 45, 10, '8401234567890', 'Uso doméstico', TRUE, NULL);

-- =====================================================
-- END OF SCHEMA
-- =====================================================
