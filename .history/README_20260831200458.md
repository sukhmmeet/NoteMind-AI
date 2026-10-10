<p align="center">
  <h1 align="center">🧠 NoteMind AI</h1>
  <p align="center">
    An intelligent, AI-powered note-taking platform that automatically generates summaries, tags, and flashcards from your notes.
  </p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 4.0"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge" alt="Status"/>
</p>

---

## 📋 Quick Navigation

| Documentation | Purpose |
|---|---|
| [📖 Setup Guide](docs/SETUP.md) | Installation & environment configuration |
| [🔗 API Reference](docs/API.md) | Complete API endpoints & curl examples |
| [🛡️ Security](docs/SECURITY.md) | Authentication, best practices & configuration |
| [🐛 Troubleshooting](docs/TROUBLESHOOTING.md) | Common issues & solutions |
| [🤝 Contributing](docs/CONTRIBUTING.md) | Development guidelines & code style |

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#%EF%B8%8F-tech-stack)
- [Quick Start](#-quick-start)
- [API Basics](#-api-basics)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 💡 Overview

**NoteMind AI** is a full-stack intelligent notes platform built with **Spring Boot 4.0** and **PostgreSQL**. It goes beyond basic CRUD — every note you create is automatically enriched by AI with a generated summary, contextual tags, and optional study flashcards.

The backend features a clean, layered architecture with JWT + refresh token authentication, Redis caching, Cloudinary-based image storage, PostgreSQL full-text search, and AI integration through both Google Gemini and OpenRouter APIs.

### What makes it different?

- 🤖 **AI-first** — Summaries and tags are generated automatically on every save
- 🃏 **Flashcard generation** — AI creates Q&A flashcards from any note for active recall study
- 🔍 **Full-text search** — PostgreSQL `tsvector`/`tsquery` with GIN indexing searches across titles, content, and AI summaries
- 📁 **Folder organization** — Organize notes into user-scoped folders
- 🖼️ **Multimodal AI** — Vision models analyze attached images alongside text content
- ⚡ **Redis caching** — Notes are cached with smart eviction on mutations

---

## ✨ Features

### 🔐 Authentication & Security

| Feature | Details |
|---|---|
| JWT Authentication | Stateless, token-based auth with HMAC-SHA signed JWTs |
| Refresh Tokens | Persistent refresh tokens with expiry & revocation support |
| Password Encryption | BCrypt hashing via Spring Security |
| Session Management | Fully stateless — no server-side sessions |
| Access Control | Every note, folder, and flashcard is scoped to the authenticated user |
| Logout | Revokes refresh token on logout |

### 📝 Smart Notes

- **Create / Read / Update / Delete** notes with title, content, and optional image
- **AI-generated summaries** are created automatically on every create & update
- **AI-generated tags** are extracted and persisted as reusable entities
- **Image attachments** — upload images that get stored on Cloudinary; vision-capable AI models analyze them
- **Summary types** — choose between `SHORT`, `DETAILED`, or `BULLET_POINTS` summaries
- **Refresh summary** — regenerate the summary with a different type on demand

### 🃏 AI Flashcards

- **Generate** Q&A flashcards from any note using AI (configurable count, 1–50)
- **Regenerate** — wipe existing flashcards and generate a fresh set
- **Manual CRUD** — create, update, and delete individual flashcards
- **Bulk delete** — remove all flashcards for a note at once
- AI uses both text content and attached images to produce flashcards

### 📁 Folders

- Create, rename, and delete folders (unique name per user)
- Move notes into/out of folders
- Fetch folders with or without nested notes (`?includeNotes=true`)

### 🔎 Full-Text Search

- Powered by PostgreSQL's native full-text search engine
- Uses `tsvector` columns with `GIN` indexing for fast ranked results
- Searches across note **title**, **content**, and **AI-generated summary**
- Results ranked by `ts_rank` relevance scoring

### 🖼️ Image Storage

```
Client uploads image
        │
        ▼
  Cloudinary CDN
        │
        ▼
  Returns secure URL
        │
        ▼
  URL stored in PostgreSQL
```

- Max upload size: **10 MB**
- Cloud-hosted on Cloudinary for fast CDN delivery
- Image URLs are persisted alongside the note

### ⚡ Caching (Redis)

- Individual notes cached by ID (`@Cacheable`)
- Cache updated on create/update (`@CachePut`)
- Smart eviction on delete and folder mutations (`@CacheEvict`)
- Jackson-serialized values with `StringRedisSerializer` keys

---

## 🏗 Architecture

The project follows a **layered 3-tier architecture**:

```
Controllers → Services → Repositories → Database
     ↓          ↓
  Exceptions, Security, Caching
```

**Key components:**
- **Controllers** - REST endpoints at `/api/v1/`
- **Services** - Business logic & AI integration
- **Repositories** - JPA data access layer
- **Security** - JWT authentication & authorization
- **Caching** - Redis for performance
- **External APIs** - Google Gemini, OpenRouter for AI

See [docs/API.md](docs/API.md) for endpoint details and [docs/SECURITY.md](docs/SECURITY.md) for authentication strategy.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.0.5 |
| **Security** | Spring Security, JWT (jjwt 0.13.0), BCrypt |
| **Data** | Spring Data JPA, Hibernate |
| **Database** | PostgreSQL (with Full-Text Search + GIN indexes) |
| **Caching** | Redis + Spring Cache |
| **AI** | Google Gemini SDK (`google-genai`), OpenRouter API |
| **Image Storage** | Cloudinary (`cloudinary-http44`) |
| **Mapping** | MapStruct 1.5.5 |
| **Utilities** | Lombok, Jackson |
| **Testing** | JUnit 5, Spring Test, Testcontainers (PostgreSQL) |
| **Build** | Gradle (Kotlin DSL) |
| **Android** *(planned)* | Kotlin, Jetpack Compose |

---

## 📂 Project Structure

```
NoteMind-AI/
├── android-app/                     # Planned Android client
├── backend/
│   ├── build.gradle.kts             # Dependencies & plugins
│   ├── settings.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/dhaliwal/notemind/
│       │   │   ├── NotemindApiApplication.java
│       │   │   ├── config/
│       │   │   │   ├── CloudinaryConfig.java
│       │   │   │   ├── RedisConfig.java
│       │   │   │   ├── RequiredBeans.java
│       │   │   │   └── StaticResourceConfig.java
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── NoteController.java
│       │   │   │   ├── FlashCardController.java
│       │   │   │   └── FolderController.java
│       │   │   ├── dto/
│       │   │   │   ├── NoteDto.java
│       │   │   │   ├── AINoteResponse.java
│       │   │   │   ├── TagDto.java
│       │   │   │   ├── flashCard/
│       │   │   │   └── folder/
│       │   │   ├── entity/
│       │   │   │   ├── Note.java
│       │   │   │   ├── User.java
│       │   │   │   ├── FlashCard.java
│       │   │   │   ├── Folder.java
│       │   │   │   ├── Tag.java
│       │   │   │   ├── RefreshToken.java
│       │   │   │   └── type/
│       │   │   │       └── SummaryType.java
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   └── ... (16 custom exceptions)
│       │   │   ├── mapper/
│       │   │   │   ├── NoteMapper.java
│       │   │   │   ├── FlashCardMapper.java
│       │   │   │   ├── FolderMapper.java
│       │   │   │   └── TagMapper.java
│       │   │   ├── repository/
│       │   │   │   ├── NoteRepository.java
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── FlashCardRepository.java
│       │   │   │   ├── FolderRepository.java
│       │   │   │   └── TagRepository.java
│       │   │   ├── security/
│       │   │   │   ├── config/SecurityConfig.java
│       │   │   │   ├── AuthFilter/JwtAuthFilter.java
│       │   │   │   ├── service/AuthService.java
│       │   │   │   ├── refresh/RefreshTokenService.java
│       │   │   │   ├── util/SecurityUtils.java
│       │   │   │   └── dto/
│       │   │   ├── service/
│       │   │   │   ├── AIService.java
│       │   │   │   ├── NotesService.java
│       │   │   │   ├── FlashCardService.java
│       │   │   │   ├── FolderService.java
│       │   │   │   ├── ImageManagerService.java
│       │   │   │   └── impl/
│       │   │   │       ├── AIServiceImpl.java
│       │   │   │       ├── NotesServiceImpl.java
│       │   │   │       ├── FlashCardServiceImpl.java
│       │   │   │       ├── FolderServiceImpl.java
│       │   │   │       └── ImageManagerServiceImpl.java
│       │   │   └── util/
│       │   │       └── Util.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
└── README.md
```

---

## 🚀 Quick Start

Get the project running in **5 minutes**:

```bash
# 1. Clone repository
git clone https://github.com/sukhmmeet/NoteMind-AI.git
cd NoteMind-AI/backend

# 2. Set environment variables (see docs/SETUP.md for details)
export DB_URL="jdbc:postgresql://localhost:5432/notemind_ai"
export JWT_SECRET_KEY="your-256-bit-secret-key"
export GOOGLE_API_KEY="AIza..."

# 3. Start services (PostgreSQL & Redis must be running)
./gradlew bootRun

# 4. Test the API
curl http://localhost:8080/api/v1/auth/signup
```

**Full setup instructions** → [📖 Setup Guide](docs/SETUP.md)
- Step-by-step installation for all platforms
- Environment variable configuration
- Docker Compose setup
- Verification checklist
| `PUT` | `/notes/{id}` | Update a note (multipart) |
| `DELETE` | `/notes/{id}` | Delete a note |
| `GET` | `/notes/search?query=...` | Full-text search across notes |
| `GET` | `/notes/refresh-summary/{id}?type=SHORT` | Regenerate AI summary |

<details>
<summary><b>📝 Detailed Notes Examples</b></summary>

**POST /notes** — Create a new note with AI-generated summary
```bash
# Basic note (without image)
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Quantum Computing","content":"Quantum computers use qubits instead of bits...","summaryType":"DETAILED"};type=application/json'

# Response 201
{
  "id": 1,
  "title": "Quantum Computing",
  "content": "Quantum computers use qubits instead of bits...",
  "summary": "Quantum computing is a revolutionary computing paradigm...",
  "summaryType": "DETAILED",
  "tags": [
    { "id": 1, "name": "quantum-computing" },
    { "id": 2, "name": "technology" }
  ],
  "imageUrl": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "folderId": null
}
```

**POST /notes (with image)** — Create a note with an image
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Architecture Diagram","content":"This is our microservices architecture...","summaryType":"BULLET_POINTS"};type=application/json' \
  -F 'image=@diagram.png'

# Response 201 (with image URL from Cloudinary)
{
  "id": 2,
  "title": "Architecture Diagram",
  "imageUrl": "https://res.cloudinary.com/.../diagram.png",
  "summary": "• Microservices architecture\n• API Gateway pattern\n• Database per service",
  ...
}
```

**GET /notes** — Retrieve all notes (paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/notes?page=0&size=10" \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "content": [
    {
      "id": 1,
      "title": "Quantum Computing",
      "summary": "...",
      "tags": [...],
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 42,
    "totalPages": 5
  }
}
```

**GET /notes/{id}** — Retrieve a specific note
```bash
curl -X GET http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "id": 1,
  "title": "Quantum Computing",
  "content": "...",
  "summary": "...",
  "tags": [...],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**PUT /notes/{id}** — Update a note
```bash
curl -X PUT http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Updated Title","content":"Updated content...","summaryType":"SHORT"};type=application/json' \
  -F 'image=@new-image.jpg'

# Response 200 (with regenerated summary and tags)
```

**GET /notes/search?query=...** — Full-text search
```bash
curl -X GET "http://localhost:8080/api/v1/notes/search?query=quantum&limit=20" \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "results": [
    {
      "id": 1,
      "title": "Quantum Computing",
      "summary": "...",
      "relevanceScore": 0.95
    }
  ],
  "totalResults": 5
}
```

**GET /notes/refresh-summary/{id}?type=...** — Regenerate summary
```bash
# Available types: SHORT, DETAILED, BULLET_POINTS
curl -X GET "http://localhost:8080/api/v1/notes/1/refresh-summary?type=BULLET_POINTS" \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "id": 1,
  "summary": "• Point 1\n• Point 2\n• Point 3",
  "summaryType": "BULLET_POINTS"
}
```

**DELETE /notes/{id}** — Delete a note
```bash
curl -X DELETE http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 204 (No Content)
```

</details>

---

### Flashcard Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/notes/{noteId}/flashcards/generate?count=5` | AI-generate flashcards from a note |
| `POST` | `/notes/{noteId}/flashcards/regenerate?count=5` | Delete existing & regenerate flashcards |
| `GET` | `/notes/{noteId}/flashcards` | Get all flashcards for a note |
| `GET` | `/flashcards/{flashCardId}` | Get a specific flashcard |
| `POST` | `/notes/{noteId}/flashcards` | Manually create a flashcard |
| `PATCH` | `/flashcards/{flashCardId}` | Update a flashcard |
| `DELETE` | `/flashcards/{flashCardId}` | Delete a flashcard |
| `DELETE` | `/notes/{noteId}/flashcards` | Delete all flashcards of a note |

<details>
<summary><b>🃏 Detailed Flashcard Examples</b></summary>

**POST /notes/{noteId}/flashcards/generate** — Generate flashcards from note
```bash
curl -X POST "http://localhost:8080/api/v1/notes/1/flashcards/generate?count=5" \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 201
{
  "noteId": 1,
  "flashcards": [
    {
      "id": 1,
      "question": "What is a qubit?",
      "answer": "A qubit (quantum bit) is the fundamental unit of quantum computing...",
      "createdAt": "2024-01-15T10:35:00Z"
    },
    {
      "id": 2,
      "question": "How does quantum superposition work?",
      "answer": "Quantum superposition allows a qubit to exist in multiple states...",
      "createdAt": "2024-01-15T10:35:00Z"
    }
  ],
  "totalGenerated": 5
}
```

**GET /notes/{noteId}/flashcards** — List all flashcards for a note
```bash
curl -X GET http://localhost:8080/api/v1/notes/1/flashcards \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "flashcards": [
    {
      "id": 1,
      "question": "What is a qubit?",
      "answer": "...",
      "createdAt": "2024-01-15T10:35:00Z"
    }
  ],
  "totalCount": 5
}
```

**PATCH /flashcards/{flashCardId}** — Update a flashcard
```bash
curl -X PATCH http://localhost:8080/api/v1/flashcards/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is a quantum bit?",
    "answer": "Updated answer with more detail..."
  }'

# Response 200
```

**DELETE /notes/{noteId}/flashcards** — Delete all flashcards for a note
```bash
curl -X DELETE http://localhost:8080/api/v1/notes/1/flashcards \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 204 (No Content)
```

</details>

---

### Folder Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/folder` | Create a folder |
| `GET` | `/folder` | Get all folders (add `?includeNotes=true` for nested notes) |
| `GET` | `/folder/{id}` | Get folder by ID |
| `PATCH` | `/folder/{id}` | Rename a folder |
| `DELETE` | `/folder/{id}` | Delete a folder |

<details>
<summary><b>📁 Detailed Folder Examples</b></summary>

**POST /folder** — Create a new folder
```bash
curl -X POST http://localhost:8080/api/v1/folder \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Study Notes"
  }'

# Response 201
{
  "id": 1,
  "name": "Study Notes",
  "createdAt": "2024-01-15T10:40:00Z"
}
```

**GET /folder** — Get all folders with nested notes
```bash
curl -X GET "http://localhost:8080/api/v1/folder?includeNotes=true" \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 200
{
  "folders": [
    {
      "id": 1,
      "name": "Study Notes",
      "notes": [
        {
          "id": 1,
          "title": "Quantum Computing",
          "summary": "..."
        }
      ],
      "noteCount": 1
    }
  ]
}
```

**PATCH /folder/{id}** — Rename a folder
```bash
curl -X PATCH http://localhost:8080/api/v1/folder/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Advanced Study Notes"
  }'

# Response 200
```

**DELETE /folder/{id}** — Delete a folder
```bash
curl -X DELETE http://localhost:8080/api/v1/folder/1 \
  -H "Authorization: Bearer <your-jwt-token>"

# Response 204 (No Content)
```

</details>

---

## 🧪 Testing

The project includes comprehensive test coverage using JUnit 5, Spring Test, and Testcontainers.

### Running Tests

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests com.dhaliwal.notemind.controller.NoteControllerTest

# Run with coverage report
./gradlew test jacocoTestReport

# View coverage report (generated in build/reports/jacoco/test/html/index.html)
```

### Test Structure

```
src/test/java/com/dhaliwal/notemind/
├── controller/           # Controller integration tests
├── service/             # Service unit tests
├── repository/          # Repository tests with Testcontainers
├── security/            # JWT and Auth filter tests
└── resources/
    └── application-test.properties
```

### Key Testing Patterns

- **Integration Tests**: Use `@SpringBootTest` with `Testcontainers` for PostgreSQL
- **Unit Tests**: Mock dependencies using Mockito
- **Security Tests**: Use `@WithMockUser` for testing secured endpoints
- **Repository Tests**: Test complex JPA queries with real database

### Example Test

```java
@SpringBootTest
@Testcontainers
public class NoteControllerTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("notemind_ai_test");

    @Test
    void testCreateNote_shouldGenerateSummary() {
        // Test implementation
    }
}
```

---

## ⚡ Performance & Optimization

### Caching Strategy

- **L1 Cache (Redis)**: Individual notes cached with TTL
- **Cache Invalidation**: Automatic eviction on update/delete operations
- **Cache Keys**: Structured as `note::{id}` for easy management

### Database Optimization

| Optimization | Implementation |
|---|---|
| **Full-Text Search** | PostgreSQL `tsvector` with `GIN` indexes |
| **Query Indexing** | Indexes on `user_id`, `folder_id`, `created_at` |
| **Pagination** | All list endpoints support `page` and `size` parameters |
| **N+1 Prevention** | JPA lazy loading with `@Fetch(FetchMode.JOIN)` |

### Database Indexes

```sql
-- Automatically created indexes
CREATE INDEX idx_note_user_id ON note(user_id);
CREATE INDEX idx_note_folder_id ON note(folder_id);
CREATE INDEX idx_note_search_vector ON note USING GIN(search_vector);
CREATE INDEX idx_flashcard_note_id ON flash_card(note_id);
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
```

### Load Testing Recommendations

1. Use tools like JMeter or Locust
2. Test with concurrent users (10, 50, 100)
3. Monitor Redis memory usage and eviction rates
4. Monitor PostgreSQL query performance with `pg_stat_statements`

**Example Load Test Command:**
```bash
# Using Apache JMeter
jmeter -n -t load-test.jmx -l results.jtl -j logs.log -Jthreads=50 -Jduration=300
```

---

## 🔐 Security Best Practices

### Authentication & Authorization

| Practice | Implementation |
|---|---|
| **Password Storage** | BCrypt hashing (strength 10) |
| **JWT Signing** | HMAC-SHA256 with 256-bit key |
| **Token Expiry** | 15-minute access tokens, 7-day refresh tokens |
| **Refresh Token Revocation** | Database tracking with revocation flag |
| **User Isolation** | All queries automatically filtered by `@AuthenticationPrincipal` |

### API Security

```java
// Example: All user notes are scoped
@GetMapping
public ResponseEntity<?> getAllNotes() {
    User currentUser = securityUtils.getCurrentUser(); // Automatic
    List<Note> notes = noteRepository.findByUser(currentUser);
    // Only current user's notes are returned
}
```

### Data Protection

- **HTTPS Only**: Configure SSL/TLS certificates in production
- **CORS Configuration**: Restrict to trusted origins only
- **Rate Limiting**: Implement to prevent brute force attacks
- **Input Validation**: All DTOs validated with `@Valid` and custom validators
- **SQL Injection Prevention**: All queries use JPA parameterized queries

### Environment Variable Security

**Never commit secrets! Use environment variables:**

```bash
# ✅ Good - Environment variable
export GOOGLE_API_KEY="${GOOGLE_API_KEY}"

# ❌ Bad - Hardcoded in code
String apiKey = "AIza...";
```

### Security Configuration

Key configuration in [SecurityConfig.java](backend/src/main/java/com/dhaliwal/notemind/security/config/SecurityConfig.java):

- CORS policies
- CSRF protection disabled for stateless APIs
- JWT filter registration
- Password encoder configuration

---

## 🐛 Troubleshooting

### Common Issues & Solutions

#### 1. **"Connection refused" - PostgreSQL**

**Problem:** `org.postgresql.util.PSQLException: Connection refused`

**Solution:**
```bash
# Verify PostgreSQL is running
psql -U postgres -d postgres -c "SELECT version();"

# If not running, start it
# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql

# Verify connection string in application.properties
# Should be: jdbc:postgresql://localhost:5432/notemind_ai
```

#### 2. **"Connection refused" - Redis**

**Problem:** `io.lettuce.core.RedisConnectionException`

**Solution:**
```bash
# Verify Redis is running
redis-cli ping  # Should return PONG

# If not running, start it
# macOS
brew services start redis

# Linux
sudo systemctl start redis-server
```

#### 3. **JWT Token Expired or Invalid**

**Problem:** `401 Unauthorized`

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

#### 4. **"Environment variable not found"**

**Problem:** `java.lang.NullPointerException` or missing config values

**Solution:**
```bash
# Verify environment variables are set
echo $JWT_SECRET_KEY
echo $GOOGLE_API_KEY

# If missing, set them
export JWT_SECRET_KEY="your-secret"
export GOOGLE_API_KEY="your-key"

# Or uncomment values in application.properties for local development
```

#### 5. **Image Upload Fails (Cloudinary)**

**Problem:** `403 Forbidden` or `Invalid Cloudinary credentials`

**Solution:**
```bash
# Verify Cloudinary credentials
export CLOUD_NAME_CLOUDINARY="your-cloud-name"
export CLOUD_API_KEY_CLOUDINARY="your-api-key"
export CLOUD_API_SECRET_CLOUDINARY="your-api-secret"

# Test API key validity
curl -X GET "https://api.cloudinary.com/v1_1/${CLOUD_NAME_CLOUDINARY}/resources/image" \
  -u "${CLOUD_API_KEY_CLOUDINARY}:${CLOUD_API_SECRET_CLOUDINARY}"
```

#### 6. **Out of Memory (Gradle Build)**

**Problem:** `java.lang.OutOfMemoryError: Java heap space`

**Solution:**
```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx1024m"

# Or configure in gradle.properties
echo "org.gradle.jvmargs=-Xmx1024m" >> gradle.properties

# Run build
./gradlew build
```

#### 7. **Tests Failing with "Testcontainer not found"**

**Problem:** Docker not running or Testcontainers configuration issue

**Solution:**
```bash
# Ensure Docker is running
docker --version
docker ps

# If Docker is not installed, install it
# Visit: https://www.docker.com/products/docker-desktop

# Run tests again
./gradlew test
```

### Debug Mode

Enable debug logging to troubleshoot issues:

```bash
# Set debug level in application.properties
logging.level.root=INFO
logging.level.com.dhaliwal.notemind=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG

# Or via environment variable
export LOGGING_LEVEL_COM_DHALIWAL_NOTEMIND=DEBUG
./gradlew bootRun
```

### Health Check Endpoint

```bash
# Check application health (once added to config)
curl http://localhost:8080/actuator/health

# Expected response
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

### Getting Help

1. **Check logs**: Look in `build/reports/tests/test/index.html` for test failures
2. **Database logs**: Check PostgreSQL logs in `/var/log/postgresql/`
3. **Redis logs**: Check Redis logs with `redis-cli monitor`
4. **Spring Boot logs**: Look for stack traces in terminal output or logs directory

---

## 🤝 Contributing

### Getting Started

1. **Fork the repository**
   ```bash
   git clone https://github.com/sukhmmeet/NoteMind-AI.git
   cd NoteMind-AI
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make changes and commit**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

4. **Push and create a Pull Request**
   ```bash
   git push origin feature/your-feature-name
   ```

### Code Style Guidelines

- **Java**: Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **Naming**: Use descriptive names for variables, methods, and classes
- **Comments**: Add comments for complex logic
- **Tests**: Write tests for new features
- **Formatting**: Use IDE auto-format (Ctrl+Alt+L in IntelliJ)

### Git Commit Messages

```
type(scope): description

feat(auth): add OAuth2 support
fix(notes): resolve N+1 query issue
docs(readme): add Docker setup instructions
test(flashcards): add test for regenerate endpoint
refactor(services): extract AI logic to separate class
```

**Commit Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `test`: Tests
- `refactor`: Code refactoring
- `perf`: Performance improvement
- `chore`: Dependency/build updates

### Submitting Issues

When reporting bugs, include:
1. Clear title and description
2. Steps to reproduce
3. Expected vs actual behavior
4. Environment details (OS, Java version, etc.)
5. Error logs/stack traces

---

## 🗄 Database Schema

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   app_users  │       │     note     │       │  flash_card  │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │──┐    │ id (PK)      │──┐    │ id (PK)      │
│ username     │  │    │ title        │  │    │ question     │
│ password     │  │    │ content      │  │    │ answer       │
└──────────────┘  │    │ summary      │  │    │ created_at   │
                  │    │ summary_type │  │    │ note_id (FK) │──→ note
                  │    │ image_url    │  │    └──────────────┘
                  │    │ search_vector│  │
                  │    │ created_at   │  │    ┌──────────────┐
                  │    │ updated_at   │  │    │     tag      │
                  └──→ │ user_id (FK) │  │    ├──────────────┤
                       │ folder_id(FK)│──┼──→ │ id (PK)      │
                       └──────────────┘  │    │ name (UNIQUE)│
                              │          │    └──────────────┘
                              │          │           │
                       ┌──────┴───────┐  │    ┌──────┴───────┐
                       │  note_tags   │  │    │   folder     │
                       │  (join table)│  │    ├──────────────┤
                       ├──────────────┤  │    │ id (PK)      │
                       │ note_id (FK) │  │    │ name         │
                       │ tag_id (FK)  │  │    │ user_id (FK) │
                       └──────────────┘  │    └──────────────┘
                                         │
                                         │    ┌───────────────┐
                                         │    │ refresh_token │
                                         │    ├───────────────┤
                                         │    │ id (PK)       │
                                         │    │ token (UNIQUE)│
                                         └──→ │ user_id (FK)  │
                                              │ expiry_date   │
                                              │ revoked       │
                                              └───────────────┘
```

**Key indexes:**
- `GIN` index on `note.search_vector` for full-text search
- Unique constraint on `(user_id, name)` in `folder` table
- Unique constraint on `tag.name`

---

## 🔮 Roadmap

### Phase 1: Core Completion ✅
- [x] Authentication & JWT
- [x] Note CRUD operations
- [x] AI summaries & tags
- [x] Flashcard generation
- [x] Image storage (Cloudinary)
- [x] Full-text search

### Phase 2: Android App 📱
- [ ] Android app with Kotlin & Jetpack Compose
- [ ] Offline mode with local database
- [ ] Push notifications
- [ ] Mobile-optimized UI

### Phase 3: Advanced Features 🚀
- [ ] 💬 AI chat with notes (conversational Q&A over your notes)
- [ ] 🎙️ Voice notes with speech-to-text transcription
- [ ] ✍️ Rich Markdown editor with live preview
- [ ] 📊 Study analytics & spaced repetition scheduling
- [ ] 🎯 Flashcard performance tracking

### Phase 4: Collaboration & Scaling 🌟
- [ ] 🔗 Note sharing & collaboration links
- [ ] 👥 Real-time collaboration (WebSocket)
- [ ] 🔍 Elasticsearch integration for advanced search
- [ ] 🗂️ Tags and labels system enhancements
- [ ] 📤 Export notes to PDF, Word, Markdown

### Phase 5: Integration & APIs 🔌
- [ ] Notion API integration
- [ ] Google Drive sync
- [ ] Slack bot for quick notes
- [ ] Browser extension for web clipping

---

## 📊 Project Statistics

| Metric | Value |
|---|---|
| **Backend Controllers** | 4 |
| **Service Classes** | 5+ |
| **Entities** | 8 |
| **API Endpoints** | 20+ |
| **Test Coverage** | 16+ test classes |
| **External Integrations** | 3 (Google AI, OpenRouter, Cloudinary) |

---

## 📚 Additional Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **PostgreSQL Full-Text Search**: https://www.postgresql.org/docs/current/textsearch.html
- **JWT Best Practices**: https://tools.ietf.org/html/rfc8725
- **Google Gemini API**: https://ai.google.dev/
- **OpenRouter**: https://openrouter.ai/
- **Cloudinary**: https://cloudinary.com/documentation

---

## 👨‍💻 Author

**Sukhmeet Dhaliwal** — Computer Science Engineering Student

- 📧 Email: sukhmmeet@gmail.com
- 🐙 GitHub: [@sukhmmeet](https://github.com/sukhmmeet)
- 💼 LinkedIn: [Sukhmeet Dhaliwal](https://linkedin.com/in/sukhmmeet)

### Acknowledgments

Special thanks to the open-source community for amazing tools like:
- Spring Boot framework
- PostgreSQL database
- Redis caching
- Google AI & OpenRouter for AI capabilities
- Cloudinary for image storage

---

## 📄 License

This project is open source and available under the MIT License.

---

<p align="center">
  <sub>⭐ If you find this project useful, please consider giving it a star!</sub>
</p>

<p align="center">
  Built with ❤️ using Java, Spring Boot, PostgreSQL & AI
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/sukhmmeet/NoteMind-AI?style=social" alt="Stars"/>
  <img src="https://img.shields.io/github/forks/sukhmmeet/NoteMind-AI?style=social" alt="Forks"/>
  <img src="https://img.shields.io/github/issues/sukhmmeet/NoteMind-AI" alt="Issues"/>
  <img src="https://img.shields.io/github/license/sukhmmeet/NoteMind-AI" alt="License"/>
</p>
