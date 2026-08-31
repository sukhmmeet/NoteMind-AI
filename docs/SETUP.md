# Setup & Installation Guide

## Prerequisites

| Requirement | Version | Link |
|---|---|---|
| Java | 21+ | [Download OpenJDK](https://adoptium.net/temurin/releases/?version=21) |
| PostgreSQL | 13+ | [Download](https://www.postgresql.org/download/) |
| Redis | 6+ | [Download](https://redis.io/download/) |
| Gradle | 7.6+ | [Download](https://gradle.org/releases/) |
| Git | Latest | [Download](https://git-scm.com/) |
| Cloudinary Account | Free | [Sign Up](https://cloudinary.com/users/register/free) |

**API Keys Required:**
- Google AI Gemini API key ([Get it here](https://makersuite.google.com/app/apikey))
- OpenRouter API key ([Get it here](https://openrouter.ai/))

---

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://github.com/sukhmmeet/NoteMind-AI.git
cd NoteMind-AI/backend
```

### 2. Create PostgreSQL Database

Connect to PostgreSQL and create a new database:

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE notemind_ai;

-- Verify
\l
```

### 3. Configure Environment Variables

#### Linux/macOS

```bash
export DB_URL="jdbc:postgresql://localhost:5432/notemind_ai"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
export JWT_SECRET_KEY="your-256-bit-secret-key-min-32-chars"
export GOOGLE_API_KEY="AIza..."
export OPENROUTER_API_KEY="sk-or-..."
export CLOUD_NAME_CLOUDINARY="your-cloud-name"
export CLOUD_API_KEY_CLOUDINARY="123456789012345"
export CLOUD_API_SECRET_CLOUDINARY="abcDEF..."
```

#### Windows PowerShell

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/notemind_ai"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET_KEY="your-256-bit-secret-key-min-32-chars"
$env:GOOGLE_API_KEY="AIza..."
$env:OPENROUTER_API_KEY="sk-or-..."
$env:CLOUD_NAME_CLOUDINARY="your-cloud-name"
$env:CLOUD_API_KEY_CLOUDINARY="123456789012345"
$env:CLOUD_API_SECRET_CLOUDINARY="abcDEF..."
```

#### Windows Command Prompt

```cmd
set DB_URL=jdbc:postgresql://localhost:5432/notemind_ai
set DB_USERNAME=postgres
set DB_PASSWORD=your_password
set JWT_SECRET_KEY=your-256-bit-secret-key-min-32-chars
REM ... and so on
```

> **💡 Tip:** For local development only, you can uncomment the values in `application.properties` and set them directly.

### 4. Start Services

#### PostgreSQL

```bash
# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql

# Windows
# Open pgAdmin 4 or use PostgreSQL installer
```

#### Redis

```bash
# macOS
brew services start redis

# Linux
sudo systemctl start redis-server

# Windows (WSL or native)
redis-server
```

### 5. Run the Application

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Or run directly with Java
java -jar build/libs/notemind-api-0.0.1-SNAPSHOT.jar
```

### 6. Verify Installation

```bash
curl http://localhost:8080/api/v1/auth/signup
# Should return 400 (missing body) - means server is running!
```

The API will be available at: **http://localhost:8080/api/v1**

---

## Docker Setup (Optional)

For a complete containerized setup:

### 1. Create docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: notemind-db
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: notemind_ai
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: notemind-cache
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: notemind-api
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/notemind_ai
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      REDIS_HOST: redis
      REDIS_PORT: 6379
      JWT_SECRET_KEY: ${JWT_SECRET_KEY}
      GOOGLE_API_KEY: ${GOOGLE_API_KEY}
      OPENROUTER_API_KEY: ${OPENROUTER_API_KEY}
      CLOUD_NAME_CLOUDINARY: ${CLOUD_NAME_CLOUDINARY}
      CLOUD_API_KEY_CLOUDINARY: ${CLOUD_API_KEY_CLOUDINARY}
      CLOUD_API_SECRET_CLOUDINARY: ${CLOUD_API_SECRET_CLOUDINARY}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - notemind-network

volumes:
  postgres_data:

networks:
  notemind-network:
    driver: bridge
```

### 2. Create Dockerfile

```dockerfile
FROM openjdk:21-slim

WORKDIR /app

COPY build/libs/notemind-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3. Build and Run

```bash
# Create .env file
echo "JWT_SECRET_KEY=your-secret" > .env
echo "GOOGLE_API_KEY=AIza..." >> .env
# ... add other keys

# Build and start
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop
docker-compose down
```

---

## Next Steps

- 📖 Read [API Documentation](API.md)
- 🧪 Run [Tests](../README.md#-testing)
- 🔐 Review [Security Practices](SECURITY.md)
- 🐛 Check [Troubleshooting Guide](TROUBLESHOOTING.md)
