# Oracle Database Setup Guide for Hibernate Billing System

## Oracle Database Configuration

This guide provides step-by-step instructions for setting up an Oracle database to work with the Hibernate Billing System, making it compatible with SQL*Plus and SQL Developer.

## Prerequisites

### 1. Oracle Database Installation
- Oracle Database 11g XE, 12c, 18c, 19c, or 21c
- Oracle Express Edition (XE) is sufficient for development
- Download from: [Oracle Database Downloads](https://www.oracle.com/database/technologies/oracle-database-software-downloads.html)

### 2. Oracle Client Tools (Optional but Recommended)
- **SQL*Plus**: Command-line interface (included with Oracle Database)
- **SQL Developer**: Graphical database tool
- Download from: [Oracle SQL Developer](https://www.oracle.com/tools/downloads/sqldev-downloads.html)

## Database Setup

### Step 1: Create Database User

Connect to Oracle as SYSDBA and create the application user:

```sql
-- Connect as system administrator
sqlplus sys as sysdba

-- Create user for the billing application
CREATE USER facturacion IDENTIFIED BY facturacion;

-- Grant necessary privileges
GRANT CONNECT, RESOURCE, DBA TO facturacion;
GRANT CREATE SESSION TO facturacion;
GRANT CREATE TABLE TO facturacion;
GRANT CREATE SEQUENCE TO facturacion;
GRANT CREATE VIEW TO facturacion;
GRANT CREATE PROCEDURE TO facturacion;

-- Grant quota on tablespace
ALTER USER facturacion QUOTA UNLIMITED ON USERS;

-- Exit
EXIT;
```

### Step 2: Verify Connection

Test the connection using SQL*Plus:

```bash
sqlplus facturacion/facturacion@localhost:1521/XE
```

Or using SQL Developer:
- **Connection Name**: Billing System
- **Username**: facturacion
- **Password**: facturacion
- **Hostname**: localhost
- **Port**: 1521
- **Service name**: XE (for Express Edition)

### Step 3: Application Configuration

The application uses the Oracle configuration file: `hibernate-oracle.cfg.xml`

Default connection settings:
```xml
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:XE</property>
<property name="hibernate.connection.username">facturacion</property>
<property name="hibernate.connection.password">facturacion</property>
```

## Database Schema

Hibernate will automatically create the following table structure:

### CLIENTES Table
```sql
CREATE TABLE CLIENTES (
    ID NUMBER(19) NOT NULL,
    DNI VARCHAR2(9) NOT NULL,
    FIRST_NAME VARCHAR2(50) NOT NULL,
    LAST_NAME VARCHAR2(100) NOT NULL,
    EMAIL VARCHAR2(100),
    PHONE VARCHAR2(20),
    ADDRESS VARCHAR2(200),
    POSTAL_CODE VARCHAR2(5),
    CITY VARCHAR2(100),
    PROVINCE VARCHAR2(50),
    PAYMENT_METHOD VARCHAR2(20),
    IBAN VARCHAR2(34),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    PRIMARY KEY (ID),
    UNIQUE (DNI)
);

-- Create sequence for ID generation
CREATE SEQUENCE CLIENTES_SEQ START WITH 1 INCREMENT BY 1;

-- Create trigger for auto-increment (if using older Oracle versions)
CREATE OR REPLACE TRIGGER CLIENTES_TRG
BEFORE INSERT ON CLIENTES
FOR EACH ROW
BEGIN
    IF :NEW.ID IS NULL THEN
        SELECT CLIENTES_SEQ.NEXTVAL INTO :NEW.ID FROM DUAL;
    END IF;
END;
/
```

## Configuration Options

### Different Oracle Versions

For different Oracle versions, update the dialect in `hibernate-oracle.cfg.xml`:

```xml
<!-- Oracle 12c and later -->
<property name="hibernate.dialect">org.hibernate.dialect.Oracle12cDialect</property>

<!-- Oracle 11g -->
<property name="hibernate.dialect">org.hibernate.dialect.Oracle10gDialect</property>

<!-- Oracle 19c and later -->
<property name="hibernate.dialect">org.hibernate.dialect.Oracle12cDialect</property>
```

### Connection URL Formats

Different connection URL formats for various setups:

```xml
<!-- For Oracle Express Edition (XE) -->
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:XE</property>

<!-- For Oracle Standard/Enterprise with SID -->
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:ORCL</property>

<!-- For Oracle with Service Name -->
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521/XEPDB1</property>

<!-- For Oracle Cloud or Remote Server -->
<property name="hibernate.connection.url">jdbc:oracle:thin:@your-server:1521:XE</property>
```

## Troubleshooting

### Common Connection Issues

1. **TNS Listener not running**
   ```bash
   # Check listener status
   lsnrctl status
   
   # Start listener if not running
   lsnrctl start
   ```

2. **Port 1521 blocked**
   - Check firewall settings
   - Verify Oracle is listening on port 1521

3. **Service/SID not available**
   ```sql
   -- Check available services
   SELECT name FROM v$database;
   SELECT instance_name FROM v$instance;
   ```

4. **User privileges insufficient**
   ```sql
   -- Grant additional privileges if needed
   GRANT CREATE SESSION TO facturacion;
   GRANT RESOURCE TO facturacion;
   ```

### Testing with SQL*Plus

```bash
# Test basic connection
sqlplus facturacion/facturacion@localhost:1521/XE

# Test if you can create tables
SQL> CREATE TABLE test_table (id NUMBER, name VARCHAR2(50));
SQL> DROP TABLE test_table;
SQL> EXIT;
```

### Testing with SQL Developer

1. Create a new connection
2. Test the connection
3. Browse the FACTURACION schema
4. Verify table creation permissions

## Application Usage

1. **Start the application**
   - The system will prompt you to select database type
   - Choose "Oracle" option
   
2. **Database features**
   - All client management operations work the same
   - Spanish DNI validation applies
   - Provincial postal code validation works as expected

3. **Data compatibility**
   - Data structure is identical between MySQL and Oracle
   - Migration scripts can be created if needed

## Performance Tuning

### Optional Oracle-specific optimizations in `hibernate-oracle.cfg.xml`:

```xml
<!-- Enable batch operations -->
<property name="hibernate.jdbc.batch_size">20</property>
<property name="hibernate.order_inserts">true</property>
<property name="hibernate.order_updates">true</property>

<!-- Connection pooling -->
<property name="hibernate.connection.pool_size">10</property>

<!-- Query optimization -->
<property name="hibernate.cache.use_second_level_cache">false</property>
<property name="hibernate.cache.use_query_cache">false</property>
```

## Conclusion

With this setup, the Hibernate Billing System will work seamlessly with Oracle Database while maintaining full compatibility with SQL*Plus and SQL Developer. The application provides the same functionality regardless of whether you use MySQL or Oracle as the backend database.