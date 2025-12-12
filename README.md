# Hibernate Billing System - Client Management

A comprehensive Java application for client management in billing systems, built with Hibernate ORM, MySQL database support, and Java Swing GUI.

## 📋 Project Overview

This application provides a complete client management system for billing operations, featuring:

- **Client CRUD Operations**: Create, Read, Update, Delete clients
- **Advanced Search**: Search by name, DNI, phone, website
- **Spanish Validations**: DNI validation, postal code validation by province
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

### Validations
- ✅ Spanish DNI validation (8 digits + letter)
- ✅ Postal code validation by Spanish province
- ✅ Email format validation
- ✅ Phone number validation (9 digits)
- ✅ Spanish IBAN format validation
- ✅ Credit limit range validation (€0.00 - €1,000,000.00)

### Technical Features
- ✅ Hibernate ORM for database operations
- ✅ DAO pattern implementation
- ✅ Service layer for business logic
- ✅ Clean GUI with proper field sizing
- ✅ Calibri font throughout the application
- ✅ Professional aesthetics

## 🏗️ Architecture

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── billing/
│   │           ├── dao/           # Data Access Objects
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
CREATE DATABASE hibernate;
CREATE USER 'usuario'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON hibernate.* TO 'usuario'@'localhost';
FLUSH PRIVILEGES;
```

For detailed MySQL setup and test data, see [DATABASE_SETUP.md](DATABASE_SETUP.md)

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