# A) Memoria Tecnica del Proyecto

## 1. Introduccion
El proyecto **Hibernate Billing System** es una aplicacion de escritorio desarrollada en Java para la gestion de procesos comerciales: clientes, proveedores, articulos, presupuestos, pedidos, albaranes y facturas.

El objetivo principal es disponer de una arquitectura por capas mantenible, con persistencia relacional y una interfaz grafica orientada a uso administrativo.

## 2. Objetivos funcionales
- Gestion de maestros: clientes, proveedores y articulos.
- Flujo de venta: presupuesto -> pedido -> albaran -> factura.
- Seguimiento y consulta de documentos comerciales.
- Importacion de datos maestros desde JSON.
- Exportacion de facturas a JSON y XML.

## 3. Alcance tecnico
- Aplicacion desktop (Swing), no web.
- Persistencia principal mediante Hibernate + MySQL.
- Validaciones de negocio para datos del contexto espanol (DNI, provincias, codigos postales, etc.).
- Configuracion externa para rutas de import/export, backups y logs.

## 4. Arquitectura general
La solucion sigue una arquitectura en capas:

- **Presentacion**: paquete `com.billing.gui`.
- **Negocio**: paquete `com.billing.service`.
- **Acceso a datos**: paquete `com.billing.repository`.
- **Persistencia/Transacciones**: `com.billing.util.HibernateUtil`, `com.billing.tx`.
- **Intercambio de ficheros**: paquete `com.billing.io` y DTOs en `com.billing.dto`.

Esta separacion reduce acoplamiento y facilita evolucion y pruebas.

## 5. Modelo de dominio principal
- **Party**: entidad base para terceros.
- **Client** y **Supplier**: especializaciones de Party con herencia `JOINED`.
- **Item**: catalogo de articulos, stock y precios.
- **BusinessDocument**: base comun para documentos comerciales.
- **DeliveryNote** e **Invoice**: documentos persistidos.
- **DocumentLine**: lineas de detalle asociadas a documento e item.

## 6. Flujo operativo implementado
1. Alta y mantenimiento de clientes, proveedores y articulos.
2. Creacion de presupuesto desde modulo de pedidos.
3. Aceptacion del presupuesto y paso a pedido.
4. Generacion de albaran desde pedido.
5. Generacion de factura desde albaran.
6. Consulta y exportacion de facturas.

## 7. Configuracion y ejecucion
- Entrada principal: `com.billing.main.MainApplication`.
- Configuracion Hibernate: `src/main/resources/hibernate.cfg.xml`.
- Configuracion de rutas: `src/main/resources/config.json`.
- Ejecucion: `mvn exec:java`.

## 8. Resultado
Se obtiene una aplicacion modular y operativa para la gestion del ciclo basico de facturacion, con base tecnica preparada para ampliaciones futuras (mas informes, mas integraciones y test automatizados).
