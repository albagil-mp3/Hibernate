# Hibernate Billing System - Complete Management Solution

A comprehensive Java application for billing system management, built with Hibernate ORM, MySQL database support, and Java Swing GUI.

## 📋 Project Overview

This application provides complete management systems for billing operations, featuring:

- **Client Management**: Complete CRUD operations for client data
- **Article Management**: Full inventory management with stock control
- **Advanced Search**: Search capabilities across all modules
- **Spanish Validations**: DNI, postal codes, and business rules
- **Professional GUI**: Clean Swing interface with Calibri font
- **MySQL Database**: Full MySQL database integration with Hibernate ORM

## 🚀 Features

### Client Management
- ✅ Create new clients with comprehensive information
- ✅ Edit existing client details
- ✅ Delete clients with confirmation
- ✅ View detailed client information
- ✅ Search and filter clients
- ✅ Sort clients by ID, DNI, or name

### Article Management
- ✅ Complete article inventory management
- ✅ Supplier, family, and category organization
- ✅ Price management with IVA calculation
- ✅ Stock control with minimum stock alerts
- ✅ Barcode support (13 digits)
- ✅ Image support for articles (300x300px)
- ✅ Low stock monitoring and alerts
- ✅ Active/inactive status management

### Validations
- ✅ Spanish DNI validation (8 digits + letter)
- ✅ Postal code validation by Spanish province
- ✅ Email format validation
- ✅ Phone number validation (9 digits)
- ✅ Spanish IBAN format validation
- ✅ Credit limit range validation (€0.00 - €1,000,000.00)
- ✅ Price validation (sale price >= cost price)
- ✅ Stock validation (non-negative values)
- ✅ Barcode format validation (13 digits)
- ✅ IVA percentage validation (0%, 4%, 10%, 21%)

### Technical Features
- ✅ Hibernate ORM for database operations
- ✅ DAO pattern implementation
- ✅ Service layer for business logic
- ✅ Clean GUI with proper field sizing
- ✅ Calibri font throughout the application
- ✅ Professional aesthetics
- ✅ Modular architecture
- ✅ Entity relationships (ManyToOne, OneToMany)
- ✅ Image handling and storage

## 🏗️ Architecture

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── billing/
│   │           ├── dao/           # Data Access Objects
│   │           │   ├── GenericDAO.java
│   │           │   ├── ClientDAO.java & ClientDAOImpl.java
│   │           │   ├── ArticleDAO.java & ArticleDAOImpl.java
│   │           │   ├── SupplierDAO.java & SupplierDAOImpl.java
│   │           │   ├── ArticleFamilyDAO.java & ArticleFamilyDAOImpl.java
│   │           │   ├── ArticleCategoryDAO.java & ArticleCategoryDAOImpl.java
│   │           │   └── UnitDAO.java & UnitDAOImpl.java
│   │           ├── entity/        # JPA Entities
│   │           │   ├── Client.java
│   │           │   ├── Article.java
│   │           │   ├── Supplier.java
│   │           │   ├── ArticleFamily.java
│   │           │   ├── ArticleCategory.java
│   │           │   ├── Unit.java
│   │           │   ├── PaymentMethod.java
│   │           │   └── SpanishProvince.java
│   │           ├── gui/           # User Interface
│   │           │   ├── MainWindow.java
│   │           │   ├── ClientManagementPanel.java
│   │           │   ├── ClientFormDialog.java
│   │           │   ├── ClientDetailsDialog.java
│   │           │   ├── ArticleManagementPanel.java
│   │           │   ├── ArticleFormDialog.java
│   │           │   ├── ArticleDetailsDialog.java
│   │           │   └── UIConstants.java
│   │           ├── main/          # Application Entry Point
│   │           │   └── MainApplication.java
│   │           ├── service/       # Business Logic Layer
│   │           │   ├── ClientService.java
│   │           │   └── ArticleService.java
│   │           └── util/          # Utilities
│   │               ├── HibernateUtil.java
│   │               └── SpanishValidationUtil.java
│   └── resources/
│       ├── hibernate.cfg.xml     # Hibernate Configuration
│       └── icons/               # Application Icons
└── article_test_data.sql         # Test Data for Articles
```
│   │           ├── entity/        # JPA Entities
│   │           ├── gui/           # Swing GUI Components
│   │           ├── main/          # Main Application
│   │           ├── service/       # Business Logic Layer
│   │           └── util/          # Utility Classes
│   └── resources/
│       └── hibernate.cfg.xml     # Hibernate Configuration
└── test/
    └── java/                     # Test Classes
```

## 🛠️ Technology Stack

- **Java 21 LTS**
- **Hibernate 5.6.15.Final**
- **MySQL 8.0+**
- **Maven** (Build Tool)
- **Java Swing** (GUI Framework)
- **Jakarta Bean Validation** (Validation)
- **MySQL Connector/J 8.2.0** (MySQL JDBC Driver)

## 📊 Database Schema

### Table: `clientes`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Client ID |
| nom | VARCHAR(50) | NOT NULL | Client name |
| dni | VARCHAR(9) | NOT NULL, UNIQUE | Spanish DNI |
| direccio | VARCHAR(50) | NOT NULL | Address |
| poblacio | VARCHAR(30) | NOT NULL | City |
| provincia | VARCHAR(50) | NOT NULL | Spanish province |
| codi_postal | VARCHAR(5) | | 5-digit postal code |
| telefon_fixe | VARCHAR(9) | | Fixed phone (9 digits) |
| telefon_mobil | VARCHAR(9) | | Mobile phone (9 digits) |
| correu_electronic | VARCHAR(80) | | Email address |
| plana_web | VARCHAR(50) | | Website |
| forma_pagament | VARCHAR(20) | NOT NULL | Payment method (Credit/Cash) |
| limit_credit | DECIMAL(10,2) | | Credit limit (€0.00-€1,000,000.00) |
| numero_conta_bancari | VARCHAR(34) | | Spanish IBAN |
| actiu | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| observacions | VARCHAR(500) | | Observations |
| imatge | LONGBLOB | | Client logo (300x300 DPI) |

### Database Schema - Articles Module

#### articles table
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Article ID |
| code | VARCHAR(20) | NOT NULL, UNIQUE | Article code (max 20 chars) |
| name | VARCHAR(80) | NOT NULL | Article name (max 80 chars) |
| description | VARCHAR(500) | | Description (max 500 chars) |
| family_id | INT | FOREIGN KEY to article_families | Article family |
| category_id | INT | FOREIGN KEY to article_categories | Article category |
| unit_id | INT | FOREIGN KEY to units | Unit of sale |
| supplier_id | INT | FOREIGN KEY to suppliers | Supplier |
| cost_price | DECIMAL(10,2) | NOT NULL, >= 0.00 | Cost price (€) |
| sale_price | DECIMAL(10,2) | NOT NULL, >= cost_price | Sale price (€) |
| iva_percent | INT | NOT NULL | IVA % (0, 4, 10, 21) |
| current_stock | INT | NOT NULL, >= 0 | Current stock |
| minimum_stock | INT | NOT NULL, >= 0 | Minimum stock |
| barcode | VARCHAR(13) | | Barcode (13 digits, optional) |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| image | LONGBLOB | | Article image (300x300px) |
| creation_date | DATETIME | NOT NULL | Creation date/time |
| notes | VARCHAR(500) | | Observations (max 500 chars) |

#### suppliers table
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Supplier ID |
| code | VARCHAR(20) | NOT NULL, UNIQUE | Supplier code |
| name | VARCHAR(100) | NOT NULL | Supplier name |
| address | VARCHAR(100) | | Address |
| phone | VARCHAR(20) | | Phone number |
| email | VARCHAR(80) | | Email address |
| notes | VARCHAR(500) | | Notes |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |

#### article_families table
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Family ID |
| code | VARCHAR(20) | NOT NULL, UNIQUE | Family code |
| name | VARCHAR(80) | NOT NULL | Family name |
| description | VARCHAR(250) | | Description |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |

#### article_categories table
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Category ID |
| code | VARCHAR(20) | NOT NULL, UNIQUE | Category code |
| name | VARCHAR(80) | NOT NULL | Category name |
| description | VARCHAR(250) | | Description |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |

#### units table
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Unit ID |
| code | VARCHAR(10) | NOT NULL, UNIQUE | Unit code |
| name | VARCHAR(50) | NOT NULL | Unit name |
| abbreviation | VARCHAR(20) | | Abbreviation |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |

## 🚦 Getting Started

### Prerequisites

1. **Java 21 LTS**
2. **MySQL 8.0+**
3. **Maven 3.6+**

### Database Setup

#### MySQL Configuration
1. **Install and start MySQL Server 8.0+**
2. **Create database:**
```sql
CREATE DATABASE facturacio;
CREATE USER 'usuario'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON facturacio.* TO 'usuario'@'localhost';
FLUSH PRIVILEGES;
```

For detailed MySQL setup and test data, see [DATABASE_SETUP.md](DATABASE_SETUP.md)

#### Test Data
- Run `article_test_data.sql` to populate article-related tables with sample data:
  - 5 suppliers with complete information
  - 5 article families (Electronics, IT, Office, Furniture, Consumables)
  - 6 categories (Components, Peripherals, Software, Accessories, Tools, Stationery)
  - 7 units (Unit, Box, Pack, Kg, Meter, Liter, Pair)
  - 14 sample articles including products with low stock for testing alerts

### Running the Application

1. **Clone the repository**
2. **Configure MySQL connection** - Update `hibernate.cfg.xml` with your MySQL credentials
3. **Build and run** - The application will connect to MySQL automatically
4. **Build and run**:
```bash
mvn clean compile exec:java
```

## 🎯 Usage

### Database Selection
- Application automatically connects to MySQL database
- Configuration managed through `hibernate.cfg.xml`

### Main Window
- Access client management through the menu
- Navigate to different modules of the billing system

### Client Management
- **Add Client**: Click "Add Client" button and fill the form
- **Edit Client**: Select a client and click "Edit Client"
- **Delete Client**: Select a client and click "Delete Client"
- **View Details**: Double-click a client or use "View Details"
- **Search**: Use the search field to find clients
- **Sort**: Use the sort dropdown to order clients

### Form Validations
- **DNI**: Must be 8 digits followed by correct letter
- **Postal Code**: Must match selected province
- **Phones**: Must be exactly 9 digits
- **Email**: Must be valid email format
- **Credit Limit**: Between €0.00 and €1,000,000.00
- **IBAN**: Must be valid Spanish IBAN format (ES + 22 digits)

## 🔧 Development Guidelines

### Code Standards
- All class names, variables, and comments in English
- Follow Java naming conventions
- Use proper validation for Spanish business rules
- Maintain clean and aesthetic UI design
- Use Calibri font size 12 (bold for titles)

### Adding New Features
1. Create entity classes in `com.billing.entity`
2. Implement DAO interfaces and implementations
3. Add business logic in service layer
4. Create GUI components following existing patterns
5. Update Hibernate configuration if needed

## 📝 License

This project is developed for educational purposes as part of a billing system implementation.

## 👥 Contributing

1. Follow the established code patterns
2. Maintain Spanish validation rules
3. Keep GUI consistent with Calibri font
4. Add proper error handling
5. Document new features

## 🆘 Support

For issues or questions about the application:
1. Check database connection settings
2. Verify MySQL service is running
3. Ensure proper Java and Maven versions
4. Review log files for error details