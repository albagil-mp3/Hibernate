-- Test data for Articles module
-- Database: Facturacio

-- Use Facturacio
USE Facturacio;


-- Drop tables if they exist (in reverse order due to foreign keys)
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS article_categories;
DROP TABLE IF EXISTS article_families;
DROP TABLE IF EXISTS units;

-- Create units table
CREATE TABLE units (
    symbol VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create article families table
CREATE TABLE article_families (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create article categories table
CREATE TABLE article_categories (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create suppliers table
CREATE TABLE suppliers (
    name VARCHAR(200) PRIMARY KEY,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),
    observations TEXT,
    active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create articles table
CREATE TABLE articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) DEFAULT NULL,
    name VARCHAR(80) NOT NULL,
    description VARCHAR(500),
    family VARCHAR(10) NOT NULL,
    category VARCHAR(10) NOT NULL,
    unit VARCHAR(10) NOT NULL,
    supplier VARCHAR(200) NOT NULL,
    cost_price DECIMAL(10,2) NOT NULL CHECK (cost_price >= 0.00),
    sale_price DECIMAL(10,2) NOT NULL,
    vat_percent INT NOT NULL CHECK (vat_percent IN (0, 4, 10, 21)),
    current_stock INT NOT NULL DEFAULT 0 CHECK (current_stock >= 0),
    minimum_stock INT NOT NULL DEFAULT 0 CHECK (minimum_stock >= 0),
    barcode VARCHAR(13),
    active BOOLEAN DEFAULT true,
    image VARCHAR(500),
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observations VARCHAR(500),
    CONSTRAINT chk_sale_price CHECK (sale_price >= cost_price),
    FOREIGN KEY (family) REFERENCES article_families(code),
    FOREIGN KEY (category) REFERENCES article_categories(code),
    FOREIGN KEY (unit) REFERENCES units(symbol),
    FOREIGN KEY (supplier) REFERENCES suppliers(name)
);

DELIMITER $$
CREATE TRIGGER articles_code_insert 
BEFORE INSERT ON articles 
FOR EACH ROW 
BEGIN
    IF @next_article_id IS NULL THEN
        SET @next_article_id = (SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='articles');
    END IF;
    SET NEW.code = CONCAT('ART', LPAD(@next_article_id, 3, '0'));
    SET @next_article_id = @next_article_id + 1;
END$$
DELIMITER ;

-- Insert sales units
INSERT INTO units (symbol, name) VALUES 
('UN', 'Unit'),
('KG', 'Kilogram'),
('LT', 'Liter'),
('M', 'Meter'),
('M2', 'Square meter'),
('M3', 'Cubic meter'),
('PQ', 'Package'),
('CX', 'Box'),
('DZ', 'Dozen'),
('CT', 'Hundred');

-- Insert article families
INSERT INTO article_families (code, name) VALUES 
('ELEC', 'Electronics'),
('INFOR', 'Computing'),
('MOBL', 'Furniture'),
('ESCR', 'Office'),
('NETEJA', 'Cleaning'),
('ALIM', 'Food'),
('TXTIL', 'Textile'),
('CONS', 'Construction'),
('JARD', 'Gardening'),
('ESPORT', 'Sports');

-- Insert article categories
INSERT INTO article_categories (code, name) VALUES 
('COMP', 'Components'),
('ACCESO', 'Accessories'),
('CONSUM', 'Consumables'),
('EQUIP', 'Equipment'),
('EINA', 'Tools'),
('SERV', 'Services'),
('MAT', 'Materials'),
('PECES', 'Spare parts'),
('SOFT', 'Software'),
('LICEN', 'Licenses');

-- Insert suppliers
INSERT INTO suppliers (name, address, phone, email, observations, active) VALUES 
('TechnoSupply S.L.', 'C/ Tecnologia, 15, Barcelona', '932123456', 'info@technosupply.es', 'Main supplier of electronic components', true),
('Oficina Total', 'Av. Catalunya, 245, Girona', '972456789', 'vendes@oficinatotal.cat', 'Office supplies and furniture', true),
('InfoSoft Solutions', 'C/ Innovació, 8, Tarragona', '977321654', 'comercial@infosoft.com', 'Software licenses and IT services', true),
('ElectroComercial', 'Pol. Industrial Les Comes, Lleida', '973654321', 'pedidos@electrocomercial.es', 'Electronics distributor', true),
('Neteja Professional', 'C/ Higiene, 22, Sabadell', '937789123', 'info@netejaprofessional.cat', 'Cleaning and maintenance products', true);

-- Insert articles
-- Insert articles
-- Initialize session sequence for article codes so multi-row INSERTs get sequential codes
SET @next_article_id = (SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='articles');

INSERT INTO articles (name, description, family, category, unit, supplier, cost_price, sale_price, vat_percent, current_stock, minimum_stock, barcode, active, date_added, observations) VALUES 
('Dell Latitude 5520 Laptop', 'Professional laptop with Intel i7 processor, 16GB RAM, 512GB SSD', 'INFOR', 'EQUIP', 'UN', 'TechnoSupply S.L.', 850.00, 1200.00, 21, 25, 5, '1234567890123', true, NOW(), '3-year warranty included'),
('Samsung 24" Full HD Monitor', '24-inch LED monitor, 1920x1080 resolution, HDMI and VGA connections', 'ELEC', 'EQUIP', 'UN', 'TechnoSupply S.L.', 180.00, 250.00, 21, 15, 3, '1234567890124', true, NOW(), 'Ideal for offices'),
('Logitech MX Keys Mechanical Keyboard', 'Wireless keyboard with backlight and silent keys', 'INFOR', 'ACCESO', 'UN', 'TechnoSupply S.L.', 85.00, 120.00, 21, 40, 10, '1234567890125', true, NOW(), ''),
('Herman Miller Ergonomic Chair', 'Office chair with adjustable lumbar support and adjustable armrests', 'MOBL', 'EQUIP', 'UN', 'Oficina Total', 450.00, 650.00, 21, 8, 2, '1234567890126', true, NOW(), '10-year warranty'),
('Office Desk 160x80', 'Rectangular beech wood desk with metal legs', 'MOBL', 'EQUIP', 'UN', 'Oficina Total', 320.00, 480.00, 21, 12, 3, '1234567890127', true, NOW(), 'Available in different colors'),
('A4 Paper 80gr (500 sheets)', 'Ream of white paper for printer and photocopier', 'ESCR', 'CONSUM', 'PQ', 'Oficina Total', 4.50, 8.00, 21, 200, 50, '1234567890128', true, NOW(), 'FSC certified'),
('Microsoft Office 365 License', 'Annual subscription per individual user', 'INFOR', 'SOFT', 'UN', 'InfoSoft Solutions', 69.00, 99.00, 21, 100, 20, '', true, NOW(), 'Auto-renewal'),
('Multi-purpose detergent 5L', 'Universal cleaner for surfaces and floors', 'NETEJA', 'CONSUM', 'LT', 'Neteja Professional', 8.50, 15.00, 10, 30, 10, '1234567890129', true, NOW(), 'Ecological and biodegradable'),
('HDMI Cable 2m', 'High-speed HDMI cable with 4K support', 'ELEC', 'ACCESO', 'UN', 'ElectroComercial', 12.00, 20.00, 21, 75, 15, '1234567890130', true, NOW(), ''),
('HP LaserJet Pro Printer', 'Monochrome laser printer with WiFi and duplex printing', 'ELEC', 'EQUIP', 'UN', 'TechnoSupply S.L.', 280.00, 420.00, 21, 6, 2, '1234567890131', true, NOW(), 'Includes trial toner'),
('External Hard Drive 2TB', 'Portable USB 3.0 hard drive with metal housing', 'ELEC', 'ACCESO', 'UN', 'TechnoSupply S.L.', 85.00, 130.00, 21, 22, 5, '1234567890132', true, NOW(), 'Compatible with Windows and Mac'),
('WiFi 6 Modem TP-Link', 'Wireless router with WiFi 6 technology and 4 antennas', 'ELEC', 'EQUIP', 'UN', 'ElectroComercial', 120.00, 180.00, 21, 18, 4, '1234567890133', true, NOW(), 'Speed up to 1200 Mbps'),
('A4 Spiral Notebook', 'Notepad with side spiral, 200 pages', 'ESCR', 'CONSUM', 'UN', 'Oficina Total', 3.20, 6.50, 21, 150, 30, '1234567890134', true, NOW(), 'Recycled paper'),
('JBL Bluetooth Speakers', 'Portable speakers with wireless connection and 8h battery', 'ELEC', 'ACCESO', 'PQ', 'ElectroComercial', 45.00, 75.00, 21, 35, 8, '1234567890135', true, NOW(), 'Water resistant'),
('Metal Shelf 5 shelves', 'Removable shelf for filing and storage', 'MOBL', 'EQUIP', 'UN', 'Oficina Total', 75.00, 120.00, 21, 10, 2, '1234567890136', true, NOW(), 'Maximum load 50kg per shelf'),
('HP 85A LaserJet Toner', 'Original toner for HP P1100 series printers', 'ELEC', 'CONSUM', 'UN', 'TechnoSupply S.L.', 65.00, 95.00, 21, 25, 8, '1234567890137', true, NOW(), 'Yield 1600 pages'),
('Ceiling Fan 132cm', 'Silent fan with integrated LED light and remote control', 'ELEC', 'EQUIP', 'UN', 'ElectroComercial', 95.00, 150.00, 21, 12, 3, '1234567890138', true, NOW(), 'Energy consumption A++'),
('Laundry Detergent 3L', 'Concentrated liquid detergent for washing machine', 'NETEJA', 'CONSUM', 'LT', 'Neteja Professional', 6.80, 12.00, 10, 45, 12, '1234567890139', true, NOW(), 'Active up to 40°C'),
('1080p Logitech Webcam', 'Full HD web camera with integrated microphone', 'ELEC', 'ACCESO', 'UN', 'TechnoSupply S.L.', 55.00, 85.00, 21, 28, 6, '1234567890140', true, NOW(), 'Ideal for video conferences'),
('Desk with Drawers', 'Wooden desk with 3 drawers and CPU compartment', 'MOBL', 'EQUIP', 'UN', 'Oficina Total', 180.00, 280.00, 21, 7, 2, '1234567890141', true, NOW(), 'Assembly included');
