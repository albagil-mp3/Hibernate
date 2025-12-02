# MySQL Database Setup Guide

## Prerequisites
- MySQL Server 8.0 or higher installed
- MySQL server running on localhost:3306

## Setup Steps

### 1. Install MySQL Server
If you don't have MySQL installed:
- Download MySQL Community Server from https://dev.mysql.com/downloads/mysql/
- Install with default settings
- Set root password during installation

### 2. Start MySQL Service
**Windows:**
```cmd
net start mysql80
```
Or use Services.msc to start "MySQL80" service

**Alternative:** Start MySQL through XAMPP, WAMP, or similar tools

### 3. Create Database
Open MySQL Command Line or MySQL Workbench and run:
```sql
CREATE DATABASE facturacion;
USE facturacion;
```

### 4. Create MySQL User (Optional)
If you want to use a different user than root:
```sql
CREATE USER 'billing_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON facturacion.* TO 'billing_user'@'localhost';
FLUSH PRIVILEGES;
```

### 5. Update Application Configuration
Edit `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">your_username</property>
<property name="hibernate.connection.password">your_password</property>
```

### 6. Verify Connection
The application will automatically create the required tables when it starts.

## Common Issues

### Connection Refused Error
- **Cause:** MySQL server is not running
- **Solution:** Start MySQL service or check if it's installed

### Access Denied Error
- **Cause:** Wrong username/password
- **Solution:** Check credentials in hibernate.cfg.xml

### Database Does Not Exist
- **Cause:** Database 'facturacion' not created
- **Solution:** Run `CREATE DATABASE facturacion;` in MySQL

### Port 3306 Already in Use
- **Cause:** Another service is using port 3306
- **Solution:** Change MySQL port or stop conflicting service

## Testing Database Connection
You can test the connection using MySQL Command Line:
```cmd
mysql -u root -p -h localhost -P 3306
```

## Application Behavior
- **With Database:** Full functionality available
- **Without Database:** Application starts in offline mode with limited features
- **Connection Lost:** Graceful error messages and offline mode activation