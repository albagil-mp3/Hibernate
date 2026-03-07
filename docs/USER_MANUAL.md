# Manual de Usuario — Sistema de Facturacion

## 1. Introduccion

Este manual describe el uso basico de la aplicacion: arranque, navegacion por modulos y operaciones principales de gestion.

## 2. Requisitos previos

- Java 21 instalado.
- MySQL en ejecucion.
- Esquema de base de datos cargado con `database_schema_jpa.sql`.
- Credenciales validas en `src/main/resources/hibernate.cfg.xml`.

## 3. Arranque de la aplicacion

Desde la raiz del proyecto:

```bash
mvn exec:java
```

La clase de entrada es `com.billing.main.MainApplication`.

## 4. Ventana principal

La barra de menu incluye:

- `Home`
- `Clients`
- `Suppliers`
- `Items`
- `Orders`
- `Delivery Notes`
- `Invoices`
- `Tools`

Tambien puede accederse a los modulos desde el panel central tipo dashboard.

## 5. Operaciones por modulo

### 5.1 Clientes (`Clients`)

- `ADD`: alta de cliente.
- `EDIT`: modificar cliente seleccionado.
- `DELETE`: eliminar cliente con confirmacion.
- `DETAILS`: visualizar informacion completa.
- `SEARCH`, `CLEAR`, `REFRESH`: filtrado y recarga.

### 5.2 Proveedores (`Suppliers`)

Operativa equivalente a clientes:

- `ADD`, `EDIT`, `DELETE`, `DETAILS`, `SEARCH`, `CLEAR`, `REFRESH`.

### 5.3 Articulos (`Items`)

- Gestion de catalogo, stock, precios e IVA.
- Operaciones disponibles:
- `ADD`, `EDIT`, `DELETE`, `DETAILS`, `SEARCH`, `CLEAR`, `REFRESH`.

### 5.4 Pedidos (`Orders`)

En el bloque de presupuestos:

- `ADD`: crear presupuesto.
- `SEND`: marcar presupuesto como enviado.
- `ACCEPT`: aceptar presupuesto y convertirlo en pedido.
- `REJECT`: rechazar presupuesto.
- `VIEW DETAILS`: detalle del presupuesto.

En el bloque de pedidos:

- `SEND`: generar albaran desde el pedido.
- `VIEW DETAILS`: detalle del pedido.

### 5.5 Albaranes (`Delivery Notes`)

- Consulta de albaranes y estados.
- `DETAILS`: ver contenido del albaran.
- `INVOICE`: generar factura del albaran seleccionado.
- `SEARCH`, `CLEAR`, `REFRESH`.

### 5.6 Facturas (`Invoices`)

- Consulta, busqueda y ordenacion.
- `DETAILS`: ver informacion completa.
- `EXPORT`: exportar factura seleccionada a JSON o XML.

## 6. Menu Tools

- `Regenerate Codes`: regenera codigos de clientes, proveedores y articulos.
- `Import Clients (JSON)`.
- `Import Suppliers (JSON)`.
- `Import Items (JSON)`.

## 7. Carpetas de trabajo

Definidas en `src/main/resources/config.json`:

- Exportaciones: `data/exports`
- Importaciones: `data/imports`
- Backups: `data/backups`
- Logs: `logs`

## 8. Resolucion de problemas

- Si falla la conexion: revisar servidor MySQL y credenciales en `hibernate.cfg.xml`.
- Si hay error de estructura: ejecutar nuevamente `database_schema_jpa.sql`.
- Si falla una importacion: verificar formato JSON y campos obligatorios.
