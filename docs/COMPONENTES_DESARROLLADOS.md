# D) Descripcion de los Componentes Desarrollados

## 1. Capa de Presentacion (`com.billing.gui`)
### Componentes principales
- `MainWindow`: ventana principal, navegacion por modulos y menu de herramientas.
- `ClientManagementPanel`, `SupplierManagementPanel`, `ItemManagementPanel`: CRUD de maestros.
- `OrderManagementPanel`: gestion de presupuestos y pedidos.
- `DeliveryManagementPanel`: gestion de albaranes y paso a factura.
- `InvoiceTrackingPanel`: consulta, ordenacion, detalle y exportacion de facturas.
- Dialogos de formulario y detalle (`*FormDialog`, `*DetailsDialog`) para captura/visualizacion de datos.

## 2. Capa de Negocio (`com.billing.service`)
- `ClientService`, `SupplierService`, `ItemService`: validacion y operaciones de negocio CRUD.
- `SalesService`: flujo comercial (quotation/order/delivery/invoice).
- `DeliveryService`: operaciones de seguimiento de entregas y facturacion.
- `DocumentQueryService`, `InvoiceQueryService`: consulta y filtrado de documentos.
- `ExportService`: exportacion de facturas.
- `CodeGenerator`: generacion y resecuenciacion de codigos de entidad.

## 3. Capa de Persistencia (`com.billing.repository`)
- Interfaces: `PartyRepository`, `ItemRepository`, `DocumentRepository`.
- Implementaciones Hibernate: `PartyHibernateRepository`, `ItemHibernateRepository`, `DocumentHibernateRepository`.
- `RepositoryFactory`: factoria de repositorios para desacoplar servicios de implementaciones concretas.

## 4. Modelo de Dominio (`com.billing.model`)
### Party
- `Party`, `Client`, `Supplier`.

### Item
- `Item`, `ItemFamily`, `ItemCategory`, `Unit`.

### Document
- `BusinessDocument`, `DeliveryNote`, `Invoice`, `DocumentLine`.

### Enums y soporte
- `PaymentMethod`, `SpanishProvince`.

## 5. Utilidades y configuracion
- `HibernateUtil`: inicializacion y gestion de SessionFactory/transacciones.
- `JdbcUtil`: soporte para operaciones JDBC puntuales.
- `BackupUtil`, `LogUtil`, `FilePersistenceUtil`.
- `AppConfig`, `ConfigLoader`: carga de configuracion externa.

## 6. Importacion y exportacion (`com.billing.io`)
- `ClientImporter`, `SupplierImporter`, `ItemImporter`.
- `InvoiceExporter`.
- DTOs de transferencia en `com.billing.dto`.

## 7. Entrada de aplicacion
- `MainApplication`: inicializacion de Look & Feel, fuentes, Hibernate y arranque de GUI.
