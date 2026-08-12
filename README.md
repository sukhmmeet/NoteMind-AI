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
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#%EF%B8%8F-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Reference](#-api-reference)
- [Database Schema](#-database-schema)
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

```
┌─────────────────────────────────────────────────────────────┐
│                        Client                               │
│               (Android App / API Consumer)                  │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTPS
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                  Spring Boot REST API                       │
│                   /api/v1/*                                  │
│                                                             │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │ Auth        │  │ Notes        │  │ FlashCards         │ │
│  │ Controller  │  │ Controller   │  │ Controller         │ │
│  └──────┬──────┘  └──────┬───────┘  └────────┬───────────┘ │
│         │                │                    │             │
│  ┌──────┴──────┐  ┌──────┴───────┐  ┌────────┴───────────┐ │
│  │ AuthService │  │ NotesService │  │ FlashCardService   │ │
│  │ JwtService  │  │ AIService    │  │ AIService          │ │
│  │ RefreshToken│  │ ImageManager │  │                    │ │
│  └──────┬──────┘  └──────┬───────┘  └────────┬───────────┘ │
│         │                │                    │             │
│  ┌──────┴────────────────┴────────────────────┴───────────┐ │
│  │              Spring Data JPA Repositories              │ │
│  └────────────────────────┬───────────────────────────────┘ │
└───────────────────────────┼─────────────────────────────────┘
                            │
          ┌─────────────────┼──────────────────┐
          ▼                 ▼                  ▼
   ┌────────────┐   ┌─────────────┐    ┌────────────┐
   │ PostgreSQL │   │   Redis     │    │ Cloudinary │
   │  (Data +   │   │  (Cache)    │    │  (Images)  │
   │   FTS)     │   │             │    │            │
   └────────────┘   └─────────────┘    └────────────┘
          │
          ▼
   ┌──────────────────────────────┐
   │   External AI APIs          │
   │  ┌────────────────────────┐ │
   │  │ Google Gemini API      │ │
   │  │ OpenRouter API         │ │
   │  │  ├ nex-n2-pro (vision) │ │
   │  │  └ nemotron-3-super    │ │
   │  └────────────────────────┘ │
   └──────────────────────────────┘
```

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

## 🚀 Getting Started

### Prerequisites

- **Java 21** (JDK)
- **PostgreSQL** (running locally or remote)
- **Redis** (for caching)
- **Cloudinary account** (free tier works)
- API keys for **Google AI** and/or **OpenRouter**

### 1. Clone the repository

```bash
git clone https://github.com/sukhmmeet/NoteMind-AI.git
cd NoteMind-AI/backend
```

### 2. Create the database

```sql
CREATE DATABASE notemind_ai;
```

### 3. Configure environment variables

Set these environment variables before running:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection string | `jdbc:postgresql://localhost:5432/notemind_ai` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `your_password` |
| `JWT_SECRET_KEY` | Secret key for signing JWTs | `your-256-bit-secret` |
| `GOOGLE_API_KEY` | Google AI (Gemini) API key | `AIza...` |
| `OPENROUTER_API_KEY` | OpenRouter API key | `sk-or-...` |
| `CLOUD_NAME_CLOUDINARY` | Cloudinary cloud name | `your-cloud-name` |
| `CLOUD_API_KEY_CLOUDINARY` | Cloudinary API key | `123456789012345` |
| `CLOUD_API_SECRET_CLOUDINARY` | Cloudinary API secret | `abcDEF...` |

> **💡 Tip:** You can also set these directly in `application.properties` for local development — just uncomment the `${...}` references and replace with values.

### 4. Run the application

```bash
./gradlew bootRun
```

The API will be available at:

```
http://localhost:8080/api/v1
```

---

## 📡 API Reference

> All endpoints (except `/auth/**`) require a valid JWT in the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/signup` | Register a new user |
| `POST` | `/auth/login` | Login and receive JWT + refresh token |
| `POST` | `/auth/refresh-token` | Get a new JWT using a refresh token |
| `POST` | `/auth/logout` | Revoke the refresh token |

<details>
<summary><b>Request/Response examples</b></summary>

**POST /auth/signup**
```json
// Request
{ "username": "john", "password": "securePass123" }

// Response 200
{ "token": "eyJhbG...", "refreshToken": "uuid-refresh-token" }
```

**POST /auth/refresh-token**
```json
// Request
{ "refreshToken": "uuid-refresh-token" }

// Response 200
{ "token": "eyJhbG...(new)" }
```

</details>

---

### Notes

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/notes` | Create a note (multipart: `note` JSON + optional `image`) |
| `GET` | `/notes` | Get all notes for the authenticated user |
| `GET` | `/notes/{id}` | Get a specific note by ID |
| `PUT` | `/notes/{id}` | Update a note (multipart) |
| `DELETE` | `/notes/{id}` | Delete a note |
| `GET` | `/notes/search?query=...` | Full-text search across notes |
| `GET` | `/notes/refresh-summary/{id}?type=SHORT` | Regenerate AI summary (SHORT / DETAILED / BULLET_POINTS) |

<details>
<summary><b>Create note example</b></summary>

```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Authorization: Bearer <token>" \
  -F 'note={"title":"Quantum Computing","content":"Quantum computers use qubits...","summaryType":"DETAILED"};type=application/json' \
  -F 'image=@photo.jpg'
```

The response includes AI-generated `summary` and `tags`.

</details>

---

### Flashcards

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

---

### Folders

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/folder` | Create a folder |
| `GET` | `/folder` | Get all folders (add `?includeNotes=true` for nested notes) |
| `GET` | `/folder/{id}` | Get folder by ID |
| `PATCH` | `/folder/{id}` | Rename a folder |
| `DELETE` | `/folder/{id}` | Delete a folder |

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

- [ ] 📱 Android app with Kotlin & Jetpack Compose
- [ ] 💬 AI chat with notes (conversational Q&A over your notes)
- [ ] 🎙️ Voice notes with speech-to-text
- [ ] ✍️ Markdown editor with live preview
- [ ] 🔗 Note sharing & collaboration links
- [ ] 👥 Real-time collaboration (WebSocket)
- [ ] 🔍 Elasticsearch integration for advanced search
- [ ] 📊 Study analytics & flashcard performance tracking

---

## 👨‍💻 Author

**Dhaliwal** — CSE Student

---

<p align="center">
  <sub>Built with ❤️ using Java, Spring Boot, PostgreSQL & AI</sub>
</p>
