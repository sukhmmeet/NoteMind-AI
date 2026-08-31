# Troubleshooting Guide

## Common Issues & Solutions

### 1. "Connection refused" - PostgreSQL

**Problem:** 
```
org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
```bash
# Verify PostgreSQL is running
psql -U postgres -d postgres -c "SELECT version();"

# If not running, start it
# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql

# Windows - Open pgAdmin 4 or PostgreSQL installer
```

**Verify connection string:**
```bash
# Check application.properties or environment variable
echo $DB_URL
# Should be: jdbc:postgresql://localhost:5432/notemind_ai
```

---

### 2. "Connection refused" - Redis

**Problem:**
```
io.lettuce.core.RedisConnectionException
```

**Solution:**
```bash
# Verify Redis is running
redis-cli ping  # Should return PONG

# If not running, start it
# macOS
brew services start redis

# Linux
sudo systemctl start redis-server

# Windows (WSL or native)
redis-server
```

---

### 3. JWT Token Expired or Invalid

**Problem:** 
```
401 Unauthorized
```

**Solution:**
```bash
# Get a new token using refresh token
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "your-refresh-token"}'

# Or login again
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "pass"}'
```

---

### 4. "Environment variable not found"

**Problem:**
```
java.lang.NullPointerException or missing config values
```

**Solution:**
```bash
# Verify environment variables are set
echo $JWT_SECRET_KEY
echo $GOOGLE_API_KEY

# If missing, set them
# Linux/macOS
export JWT_SECRET_KEY="your-secret"
export GOOGLE_API_KEY="your-key"

# Windows PowerShell
$env:JWT_SECRET_KEY="your-secret"
$env:GOOGLE_API_KEY="your-key"

# Or uncomment values in application.properties for local development
```

---

### 5. Image Upload Fails (Cloudinary)

**Problem:**
```
403 Forbidden or Invalid Cloudinary credentials
```

**Solution:**
```bash
# Verify Cloudinary credentials
export CLOUD_NAME_CLOUDINARY="your-cloud-name"
export CLOUD_API_KEY_CLOUDINARY="your-api-key"
export CLOUD_API_SECRET_CLOUDINARY="your-api-secret"

# Test API key validity
curl -X GET "https://api.cloudinary.com/v1_1/${CLOUD_NAME_CLOUDINARY}/resources/image" \
  -u "${CLOUD_API_KEY_CLOUDINARY}:${CLOUD_API_SECRET_CLOUDINARY}"

# Check Cloudinary dashboard for account status
# Visit: https://cloudinary.com/console
```

---

### 6. Out of Memory (Gradle Build)

**Problem:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution:**
```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx1024m"

# Or configure in gradle.properties
echo "org.gradle.jvmargs=-Xmx1024m" >> gradle.properties

# Run build again
./gradlew clean build
```

---

### 7. Tests Failing with "Testcontainer not found"

**Problem:**
```
Docker not running or Testcontainers configuration issue
```

**Solution:**
```bash
# Ensure Docker is running
docker --version
docker ps

# If Docker is not installed
# Visit: https://www.docker.com/products/docker-desktop

# Start Docker service
# macOS/Windows - Open Docker Desktop
# Linux - sudo systemctl start docker

# Run tests again
./gradlew test
```

---

### 8. Port Already in Use (8080)

**Problem:**
```
Address already in use: bind
```

**Solution:**
```bash
# Find what's using port 8080
# macOS/Linux
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# Kill the process (replace PID with actual)
# macOS/Linux
kill -9 <PID>

# Windows
taskkill /PID <PID> /F

# Or change the port in application.properties
server.port=8081
```

---

### 9. Build Fails with "Module not found"

**Problem:**
```
Could not find com.google.genai:google-genai:1.0.0
```

**Solution:**
```bash
# Clear Gradle cache
./gradlew clean

# Rebuild
./gradlew build

# If still fails, check internet connection and Maven Central availability
# Try updating gradle wrapper
./gradlew wrapper --gradle-version=9.6.1
```

---

### 10. PostgreSQL Connection Pool Exhausted

**Problem:**
```
HikariPool-1 - Unable to acquire a new connection
```

**Solution:**
```bash
# Check PostgreSQL max connections
psql -U postgres -c "SHOW max_connections;"

# If low, increase it in postgresql.conf
# Location: /etc/postgresql/14/main/postgresql.conf (Linux)
# Or: C:\Program Files\PostgreSQL\14\data\postgresql.conf (Windows)

# Add/modify: max_connections = 200
# Then restart PostgreSQL

# Also check for connection leaks in application logs
```

---

## Debug Mode

Enable debug logging to troubleshoot issues:

### application.properties

```properties
logging.level.root=INFO
logging.level.com.dhaliwal.notemind=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Via Environment Variable

```bash
# Linux/macOS
export LOGGING_LEVEL_COM_DHALIWAL_NOTEMIND=DEBUG
./gradlew bootRun

# Windows PowerShell
$env:LOGGING_LEVEL_COM_DHALIWAL_NOTEMIND="DEBUG"
.\gradlew.bat bootRun
```

---

## Health Check Endpoint

Once configured, check application health:

```bash
curl http://localhost:8080/actuator/health

# Response should show:
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

---

## Logs Location

| Log Type | Location |
|---|---|
| Spring Boot Logs | Console output or `build/logs/` |
| Test Results | `build/test-results/test/` |
| PostgreSQL Logs | `/var/log/postgresql/` (Linux) |
| Redis Logs | Shown in console output |

---

## Database Issues

### Reset Database

```bash
# Connect to PostgreSQL
psql -U postgres

-- Drop and recreate database
DROP DATABASE IF EXISTS notemind_ai;
CREATE DATABASE notemind_ai;

-- Verify
\l
```

Restart the application - Hibernate will recreate schema automatically.

### Check Schema

```bash
# Connect to database
psql -U postgres -d notemind_ai

-- List all tables
\dt

-- Describe a table
\d note

-- Check indexes
\di
```

---

## Getting Help

1. **Check logs** - Look in console output or `build/reports/tests/test/`
2. **Search issues** - Check [GitHub Issues](https://github.com/sukhmmeet/NoteMind-AI/issues)
3. **Review docs** - Check [Setup](SETUP.md), [Security](SECURITY.md), [Contributing](CONTRIBUTING.md)
4. **Enable debug** - Enable DEBUG level logging and check output
5. **Database check** - Verify PostgreSQL is running and accessible
6. **API test** - Use curl or Postman to test endpoints
7. **Open issue** - If problem persists, open a GitHub issue with:
   - Clear description and steps to reproduce
   - Error logs/stack traces
   - Environment details (OS, Java version, etc.)
   - Commands you ran
