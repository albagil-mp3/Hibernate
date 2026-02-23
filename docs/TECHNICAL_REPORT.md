# Technical Report — Multi-Tier Billing System

## 1. Overview

This document summarizes the architecture, design decisions, persistence options, and components implemented for the Multi-Tier Billing System (Java, Hibernate, MySQL). The goal is to provide a clear technical description suitable for evaluation and maintenance.

## 2. Architecture

- Presentation: Swing-based desktop GUI under `com.billing.gui`.
- Service/Business Logic: `com.billing.service` contains business rules and validation.
- Data Access Layer: `com.billing.dao` contains multiple DAO implementations (Hibernate, JDBC, File-based).
- Utilities: `com.billing.util` contains helpers like `HibernateUtil`, `JdbcUtil`, and `FilePersistenceUtil`.

The system follows a layered design to decouple UI, business rules, and persistence.

## 3. Entities and Mappings

Primary entities implemented under `com.billing.entity`:
- `Client` — client/customer information (mapped to `clients` table).
- `Item` — product/service catalogue.
- `DeliveryNote`, `DeliveryLine` — delivery notes and related lines.
- `Invoice` — invoices (can be generated from delivery notes).

JPA/Hibernate annotations are used for ORM mapping. See source files for column names and constraints.

## 4. Persistence Options

The project implements three persistence mechanisms to illustrate differences and trade-offs:

1. Hibernate (ORM) — `ClientDAOImpl`, `DeliveryNoteDAOImpl`, `InvoiceDAOImpl`.
   - Pros: automatic object mapping, managed relationships, HQL queries.
   - Cons: requires correct configuration and adds complexity.

2. JDBC (Direct SQL) — `ClientJdbcDAO` using `JdbcUtil`.
   - Pros: full SQL control, explicit transaction/resource management.
   - Cons: more boilerplate, manual mapping.

3. File-based JSON — `FileClientDAO` using Jackson via `FilePersistenceUtil`.
   - Pros: easy to run without a DB, good for demonstrations and offline mode.
   - Cons: not suitable for concurrent production use.

DAO selection is configurable at startup via system properties (`billing.useFileDao`, `billing.useJdbcDao`) and can be switched at runtime from the UI (Tools → Persistence).

## 5. Transactions and Error Handling

- Hibernate DAOs use session/transaction patterns with rollback on exceptions.
- JDBC DAOs use `try-with-resources` and parameterized queries to avoid SQL injection; transaction handling can be added where multi-statement atomicity is required.
- File DAO synchronizes access to the underlying file to avoid simple race conditions.

## 6. File Formats and Interchange

- JSON: used for file persistence (`data/clients.json`) via Jackson.
- XML: Jackson XML mapper is available for exports/imports if needed.

## 7. How to Run (examples)

- Run with Hibernate (default):
```
mvn exec:java
```

- Run using File DAO:
```
mvn -Dbilling.useFileDao=true exec:java
```

- Run using JDBC DAO (provide DB credentials):
```
mvn -Dbilling.useJdbcDao=true \
   -Dbilling.jdbc.url=jdbc:mysql://localhost:3306/Facturacio \
    -Dbilling.jdbc.user=root \
    -Dbilling.jdbc.password=secret \
    exec:java
```

## 8. Decisions & Rationale

- Multiple DAOs implemented to meet the exercise learning outcomes: compare ORM vs JDBC vs file-based.
- Swing UI kept separated from services; services perform validation (Bean Validation + Spanish-specific checks).
- Created a runtime DAO switch to help testing and demonstration without restarting the application.

## 9. Next Improvements

- Add proper connection pooling (HikariCP) for JDBC.
- Add comprehensive unit/integration tests for Hibernate DAOs (requires DB or testcontainer).
- Provide import/export UI and a more feature-complete DeliveryNote/Invoice form.

## 10. SQL DDL (MySQL)

The following DDL is a suggested schema compatible with the JPA entities implemented in the project. It can be used to create the required MySQL tables.

```sql
-- Clients
CREATE TABLE clients (
   id INT AUTO_INCREMENT PRIMARY KEY,
   code VARCHAR(20),
   name VARCHAR(50) NOT NULL,
   dni VARCHAR(9) NOT NULL UNIQUE,
   address VARCHAR(50) NOT NULL,
   city VARCHAR(30) NOT NULL,
   province VARCHAR(50) NOT NULL,
   postal_code VARCHAR(5),
   fixed_phone VARCHAR(9),
   mobile_phone VARCHAR(9),
   email VARCHAR(80),
   website VARCHAR(50),
   payment_method VARCHAR(30) NOT NULL,
   credit_limit DECIMAL(12,2) DEFAULT 0.00,
   bank_account_number VARCHAR(34),
   active BOOLEAN DEFAULT TRUE,
   observations VARCHAR(500),
   image LONGBLOB,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items
CREATE TABLE items (
   id INT AUTO_INCREMENT PRIMARY KEY,
   code VARCHAR(20),
   name VARCHAR(100) NOT NULL,
   description TEXT,
   unit_price DECIMAL(12,2) DEFAULT 0.00,
   tax DECIMAL(5,2) DEFAULT 0.00,
   stock INT DEFAULT 0,
   active BOOLEAN DEFAULT TRUE
);

-- Delivery notes
CREATE TABLE delivery_notes (
   id INT AUTO_INCREMENT PRIMARY KEY,
   code VARCHAR(40),
   client_id INT,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE SET NULL
);

-- Invoices
CREATE TABLE invoices (
   id INT AUTO_INCREMENT PRIMARY KEY,
   code VARCHAR(40),
   client_id INT,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   total DECIMAL(14,2) DEFAULT 0.00,
   FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE SET NULL
);

-- Delivery / Invoice lines
CREATE TABLE delivery_lines (
   id INT AUTO_INCREMENT PRIMARY KEY,
   delivery_note_id INT,
   invoice_id INT,
   item_id INT,
   quantity INT DEFAULT 1,
   unit_price DECIMAL(12,2) DEFAULT 0.00,
   tax DECIMAL(5,2) DEFAULT 0.00,
   FOREIGN KEY (delivery_note_id) REFERENCES delivery_notes(id) ON DELETE CASCADE,
   FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
   FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE SET NULL
);

-- Indexes for faster searches
CREATE INDEX idx_clients_dni ON clients(dni);
CREATE INDEX idx_items_code ON items(code);
CREATE INDEX idx_delivery_notes_client ON delivery_notes(client_id);
CREATE INDEX idx_invoices_client ON invoices(client_id);
```

Place this DDL into your MySQL client (adjust database name and character set as required). The generated schema matches the fields used by the JPA entities and the JDBC DAO.

## 11. ER Diagram (Printable)

A printable ER diagram in SVG format is included in the `docs/ER_DIAGRAM.svg` file. It visualizes the main entities (`Client`, `Item`, `DeliveryNote`, `DeliveryLine`, `Invoice`) and their relationships. You can open the SVG in a browser or an image editor and export to PNG for inclusion in reports.

---
Generated on: 2026-02-13
