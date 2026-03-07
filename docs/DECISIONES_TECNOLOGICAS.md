# B) Explicacion de las Decisiones Tecnologicas

## 1. Lenguaje y version
- **Java 21**.

### Justificacion
- Version LTS actual con soporte prolongado.
- Buen equilibrio entre estabilidad, rendimiento y APIs modernas.
- Integracion sencilla con Maven y tooling de VS Code/IDEA/Eclipse.

## 2. Interfaz de usuario
- **Java Swing**.

### Justificacion
- Permite una aplicacion desktop completa sin servidor web.
- Adecuado para escenarios de gestion interna local.
- Facilita formularios CRUD, tablas y dialogos modales.

## 3. Persistencia
- **Hibernate ORM (JPA)** sobre **MySQL**.

### Justificacion
- Mapeo objeto-relacional y gestion de relaciones sin SQL repetitivo.
- Herencia de entidades (`JOINED`) implementada de forma directa.
- Reduce complejidad en operaciones CRUD y consultas tipicas.

## 4. Base de datos
- **MySQL 8.x**.

### Justificacion
- Motor maduro y ampliamente extendido.
- Buen soporte JDBC/Hibernate.
- Adecuado para modelos transaccionales de facturacion.

## 5. Estructura por capas
- GUI -> Servicios -> Repositorios -> BD.

### Justificacion
- Separa responsabilidades y evita logica de negocio en la vista.
- Facilita mantenimiento, refactor y pruebas.
- Permite evolucionar una capa sin romper el resto.

## 6. Gestion transaccional
- Abstraccion `TransactionManager` con implementacion `HibernateTransactionManager`.

### Justificacion
- Unifica ejecucion transaccional en servicios.
- Mejora consistencia ante errores y rollback.
- Evita duplicar codigo de sesion/transaccion.

## 7. Formatos de intercambio
- **JSON/XML** mediante Jackson.

### Justificacion
- Importacion de datos maestros de forma simple.
- Exportacion interoperable de facturas.
- Formatos estandar para integraciones futuras.

## 8. Validaciones de negocio
- Validaciones en capa de servicio y utilidades (`SpanishValidationUtil`).

### Justificacion
- Protege integridad antes de persistir.
- Centraliza reglas para reutilizacion en todos los modulos.
- Evita inconsistencias entre distintos formularios.

## 9. Conclusion
Las decisiones adoptadas priorizan mantenibilidad, claridad arquitectonica y viabilidad academica/profesional, manteniendo una base preparada para crecimiento funcional.
