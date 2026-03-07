# C) Esquemas de la Base de Datos

## 1. Resumen del modelo
El esquema usa herencia `JOINED` en dos jerarquias principales:
- `party` -> `client`, `supplier`
- `business_document` -> `delivery_note`, `invoice`

Las lineas de documento se modelan en `document_line`, asociadas a `business_document` e `item`.

## 2. Tablas principales
- `party`
- `client`
- `supplier`
- `units`
- `item_families`
- `item_categories`
- `item`
- `business_document`
- `delivery_note`
- `invoice`
- `document_line`

## 3. Relaciones clave
- `client.id` FK -> `party.id`
- `supplier.id` FK -> `party.id`
- `item.family_code` FK -> `item_families.code`
- `item.category_code` FK -> `item_categories.code`
- `item.unit_symbol` FK -> `units.symbol`
- `item.supplier_id` FK -> `supplier.id`
- `business_document.party_id` FK -> `party.id`
- `delivery_note.id` FK -> `business_document.id`
- `invoice.id` FK -> `business_document.id`
- `document_line.document_id` FK -> `business_document.id`
- `document_line.item_id` FK -> `item.id`

## 4. Restricciones destacadas
- Unicidad de `dni` y `code` en clientes.
- Unicidad de `tax_id` y `code` en proveedores.
- Unicidad de `code` en items y documentos.
- Borrado en cascada en herencias y lineas de documento segun `database_schema_jpa.sql`.

## 5. Script de referencia
El esquema SQL completo y datos iniciales se encuentran en:
- `database_schema_jpa.sql`

## 6. Diagrama ER
- Mermaid: `docs/SCHEMA.mmd`
- Version SVG: `docs/ER_DIAGRAM.svg`
