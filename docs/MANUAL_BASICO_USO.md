# E) Manual Basico de Uso de la Aplicacion

## 1. Requisitos previos
- Java 21 instalado.
- MySQL en ejecucion.
- Esquema cargado en BD (`database_schema_jpa.sql`).
- Credenciales correctas en `src/main/resources/hibernate.cfg.xml`.

## 2. Arranque
Ejecutar desde la raiz del proyecto:

```bash
mvn exec:java
```

La aplicacion abre la ventana principal (`MainWindow`).

## 3. Estructura de la pantalla principal
- Menus superiores: `Home`, `Clients`, `Suppliers`, `Items`, `Orders`, `Delivery Notes`, `Invoices`, `Tools`.
- Panel central con accesos directos a modulos.

## 4. Operaciones por modulo
### 4.1 Clients
- `ADD`: crear cliente.
- `EDIT`: modificar cliente seleccionado.
- `DELETE`: eliminar cliente (con confirmacion).
- `DETAILS`: visualizar informacion completa.
- `SEARCH`, `CLEAR`, `REFRESH`: filtrado y recarga.

### 4.2 Suppliers
- Mismo patron de uso que clientes: `ADD`, `EDIT`, `DELETE`, `DETAILS`, `SEARCH`, `CLEAR`, `REFRESH`.

### 4.3 Items
- Gestion completa de articulos con stock, precios e IVA.
- Operaciones: `ADD`, `EDIT`, `DELETE`, `DETAILS`, `SEARCH`, `CLEAR`, `REFRESH`.

### 4.4 Orders
- Seccion de presupuestos:
- `ADD`: nuevo presupuesto.
- `SEND`: marcar presupuesto como enviado.
- `ACCEPT`: aceptar presupuesto y convertir a pedido.
- `REJECT`: rechazar presupuesto.
- `VIEW DETAILS`: ver detalle.

- Seccion de pedidos:
- `SEND`: generar albaran desde pedido.
- `VIEW DETAILS`: ver detalle.

### 4.5 Delivery Notes
- Consulta de albaranes y estado.
- `DETAILS`: ver contenido del albaran.
- `INVOICE`: generar factura desde albaran seleccionado.
- `SEARCH`, `CLEAR`, `REFRESH`.

### 4.6 Invoices
- Consulta de facturas con ordenacion y busqueda.
- `DETAILS`: ver detalle de factura.
- `EXPORT`: exportar factura seleccionada a JSON o XML.

## 5. Menu Tools
- `Regenerate Codes`: regenera codigos de clientes, proveedores y articulos.
- `Import Clients (JSON)`: importa clientes desde fichero JSON.
- `Import Suppliers (JSON)`: importa proveedores desde fichero JSON.
- `Import Items (JSON)`: importa articulos desde fichero JSON.

## 6. Rutas de trabajo
Configuradas en `src/main/resources/config.json`:
- Exportaciones: `data/exports`
- Importaciones: `data/imports`
- Backups: `data/backups`
- Logs: `logs`

## 7. Mensajes y errores comunes
- **Sin conexion a BD**: revisar MySQL y credenciales en `hibernate.cfg.xml`.
- **Error de esquema**: volver a ejecutar `database_schema_jpa.sql`.
- **Fallo de importacion**: validar formato JSON y campos obligatorios.
