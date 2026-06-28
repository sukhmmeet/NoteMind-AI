# 🧠 NoteMind AI

AI-powered notes application built with **Spring Boot + PostgreSQL** with a planned Android client.

NoteMind AI focuses on building a modern notes platform with AI-powered features, secure authentication, intelligent search, and scalable backend architecture.

---

# 🚀 Features

## 🔐 Authentication

- JWT based authentication
- Secure password encryption using BCrypt
- Stateless Spring Security architecture
- Multi-user support
- User-specific private notes

---

## 📝 Smart Notes

- Create notes
- Update notes
- Delete notes
- Add title and content
- Upload note images
- Store images using Cloudinary

---

## 🤖 AI Features

### Automatic Summary Generation

NoteMind AI uses external AI services to generate summaries from long notes.

Powered by:

- Google AI API
- OpenRouter AI API

---

## 🖼️ Image Support

Notes support image attachments with cloud storage.

Flow:

```text
Application
     |
     v
Cloudinary
     |
     v
Image URL
     |
     v
PostgreSQL
```

Features:

- Image upload
- Cloud storage
- URL persistence
- Media handling

---

## 🔎 Full Text Search

Implemented using PostgreSQL Full Text Search.

Uses:

- tsvector
- tsquery
- GIN indexing

Search works across:

- Title
- Content
- AI generated summaries

---

# 🏗️ Architecture

```text
Client
  |
  v
Spring Boot REST API
  |
  +---- Spring Security (JWT)
  |
  +---- AI Service
  |
  +---- Cloudinary Storage
  |
  +---- PostgreSQL Database
```

---

# 🛠️ Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- PostgreSQL
- PostgreSQL Full Text Search

## AI Integration

- Google AI API
- OpenRouter API

## Storage

- Cloudinary

## Authentication

- JWT

## Build Tool

- Gradle

## Android Client (Planned)

- Kotlin
- Jetpack Compose

---

# ⚙️ Configuration

Before running the project, configure these environment variables.

## Database

```
DB_URL
DB_USERNAME
DB_PASSWORD
```

Example:

```
DB_URL=jdbc:postgresql://localhost:5432/notemind_ai
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

---

## JWT

```
JWT_SECRET_KEY
```

---

## AI Configuration

```
GOOGLE_API_KEY
OPENROUTER_API_KEY
```

---

## Cloudinary

```
CLOUD_NAME_CLOUDINARY
CLOUD_API_KEY_CLOUDINARY
CLOUD_API_SECRET_CLOUDINARY
```

---

# ▶️ Run Application

Using Gradle:

```bash
./gradlew bootRun
```

Application starts at:

```text
http://localhost:8080/api/v1
```

---

# 🔮 Future Improvements

- Android application using Kotlin and Jetpack Compose
- AI chat with notes
- Voice notes
- Markdown editor
- Note sharing
- Real-time collaboration
- Redis caching
- Elasticsearch integration

---

# 👨‍💻 Author

Dhaliwal

CSE Student

---

⭐ Built with Java, Spring Boot, PostgreSQL & AI
