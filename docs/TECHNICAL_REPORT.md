# Informe Tecnico — Sistema de Facturacion

## 1. Resumen

Este documento describe la arquitectura, las decisiones tecnicas, el modelo de datos y los componentes implementados del proyecto **Hibernate Billing System** (Java, Hibernate, MySQL).

## 2. Arquitectura

- Presentacion: interfaz de escritorio Swing en `com.billing.gui`.
- Logica de negocio: servicios en `com.billing.service`.
- Acceso a datos: repositorios en `com.billing.repository.interfaces` y `com.billing.repository.hibernate`.
- Soporte tecnico: utilidades en `com.billing.util`, gestion transaccional en `com.billing.tx`, configuracion en `com.billing.config`.

El diseno por capas reduce acoplamiento y separa claramente vista, negocio y persistencia.

## 3. Modelo de dominio y mapeo JPA

Entidades principales bajo `com.billing.model`:

- `Party` (base comun).
- `Client` y `Supplier` (herencia `JOINED` sobre `Party`).
- `Item`, `ItemFamily`, `ItemCategory`, `Unit`.
- `BusinessDocument` (base comun).
- `DeliveryNote` e `Invoice` (herencia `JOINED` sobre `BusinessDocument`).
- `DocumentLine` (lineas de documento relacionadas con `BusinessDocument` e `Item`).

Mapeos configurados en `src/main/resources/hibernate.cfg.xml`.

## 4. Persistencia y transacciones

- Persistencia principal con Hibernate y MySQL.
- Factoria de repositorios: `RepositoryFactory`.
- Gestion transaccional centralizada: `TransactionManager` y `HibernateTransactionManager`.

Nota: `ClientJdbcDAO` se mantiene como implementacion complementaria, pero la operativa principal de la aplicacion usa repositorios Hibernate.

## 5. Reglas de negocio relevantes

- Validacion de DNI, provincia y codigo postal.
- Validacion de campos economicos (precios, IVA, stock, limites).
- Generacion y resecuenciacion de codigos funcionales.
- Recalculo automatico de subtotal, impuestos y total en documentos.
- Flujo comercial: presupuesto -> pedido -> albaran -> factura.

## 6. Formatos de intercambio

- Importacion de maestros desde JSON (`ClientImporter`, `SupplierImporter`, `ItemImporter`).
- Exportacion de facturas a JSON/XML (`ExportService`, `InvoiceExporter`).
- Rutas configurables en `src/main/resources/config.json`.

## 7. Ejecucion

Comando de arranque:

```bash
mvn exec:java
```

Punto de entrada: `com.billing.main.MainApplication`.

## 8. Esquema SQL de referencia

El esquema oficial del proyecto esta en:

- `database_schema_jpa.sql`

Tablas principales:

- `party`, `client`, `supplier`
- `units`, `item_families`, `item_categories`, `item`
- `business_document`, `delivery_note`, `invoice`, `document_line`

## 9. Diagrama entidad-relacion

- Mermaid: `docs/SCHEMA.mmd`
- SVG: `docs/ER_DIAGRAM.svg`

## 10. Mejoras futuras

- Aumentar cobertura de tests unitarios e integracion.
- Incorporar pipeline CI para validacion automatica.
- Expandir informes y analitica de ventas/stock.

---

Actualizado: 2026-03-07
