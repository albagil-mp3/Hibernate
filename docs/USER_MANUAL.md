# User Manual — Billing System (Quick Start)

## Overview

This quick user manual explains how to start the application, basic operations for managing clients, items, delivery notes and invoices, and how to switch persistence modes.

## Starting the Application

Run from Maven (examples):

- Default (Hibernate):
```
mvn exec:java
```

- File-based (no DB required):
```
mvn -Dbilling.useFileDao=true exec:java
```

- JDBC (connects to MySQL):
```
mvn -Dbilling.useJdbcDao=true \
    -Dbilling.jdbc.url=jdbc:mysql://localhost:3306/Facturacio \
    -Dbilling.jdbc.user=root \
    -Dbilling.jdbc.password=secret \
    exec:java
```

## Main Window

- Use the dashboard buttons or the top menus to navigate modules (Clients, Items, Delivery Notes, Invoices, Suppliers).
- Tools → Persistence lets you switch the active persistence mechanism at runtime (File, JDBC, Hibernate).

## Managing Clients

1. Open `Clients` module.
2. Use `ADD` to create a new client. Mandatory fields: Name, DNI, Address, City, Province, Payment Method.
3. Use `EDIT` to modify selected client.
4. Use `DELETE` to remove a client (confirmation required).
5. Use `SEARCH` to find clients by name, DNI, phone, or website.

## Delivery Notes and Invoices

- Delivery Notes: create delivery notes containing multiple lines. Use `Convert` to generate an invoice from a delivery note.
- Invoices: list and view generated invoices. Automatic totals are calculated based on line unit prices and quantities.

## File-based Persistence (JSON)

- File storage location: `data/clients.json` (created automatically when using File DAO).
- This mode is useful for demonstrations and offline use.

## Troubleshooting

- If Hibernate fails to initialize, check `src/main/resources/hibernate.cfg.xml` for correct DB URL/credentials and ensure MySQL is accessible.
- For JDBC mode, supply `billing.jdbc.*` system properties.

## Support

For code-level questions, inspect the `com.billing.*` packages: DAOs, services, and GUI.
