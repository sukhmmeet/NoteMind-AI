# 🧠 NoteMind AI

AI-powered notes application built with **Spring Boot + PostgreSQL**.

---

# 🚀 Features

## 🔐 Authentication
- JWT based authentication
- Secure password encryption using BCrypt
- Stateless Spring Security
- Multi-user support
- User-specific private notes

---

## 📝 Smart Notes

- Create notes
- Update notes
- Delete notes
- Add title, content
- Upload note images
- Store images using Cloudinary

---

## 🤖 AI Features

### Automatic Summary Generation

Users can write long notes and NoteMind AI can generate summaries automatically.

Example:

Input:

```
Spring Boot is a framework used to build Java applications.
It provides auto configuration, embedded servers and easy API development.
```

AI generates:

```
Spring Boot helps developers build Java backend applications faster.
```

Powered by:

- Google AI API
- OpenRouter AI API

---

## 🖼️ Image Support

Notes support images.

Features:

- Image upload
- Cloud storage
- Image URL stored with notes
- Optimized media handling

Storage:

```
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

---

## 🔎 Full Text Search

Implemented using PostgreSQL:

- `tsvector`
- `tsquery`
- GIN indexing

Searches:

- Title
- Content
- Summary


Example:

```
Spring Boot Security
```

Stored internally:

```
'spring'
'boot'
'secur'
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

## AI

- Google AI API
- OpenRouter API

## Storage

- Cloudinary

## Authentication

- JWT

## Build

- Gradle

---

## ⚙️ Required Configuration

Before running the project, configure these environment variables:

### Database

PostgreSQL is required.

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

### JWT Authentication

Used for generating and validating user tokens.

```
JWT_SECRET_KEY
```

Example:

```
JWT_SECRET_KEY=your_long_secret_key
```

---

### AI Configuration

NoteMind AI uses AI services for automatic note summarization.

Required:

```
GOOGLE_API_KEY
OPENROUTER_API_KEY
```

---

### Image Storage

Images are stored using Cloudinary.

Required:

```
CLOUD_NAME_CLOUDINARY
CLOUD_API_KEY_CLOUDINARY
CLOUD_API_SECRET_CLOUDINARY
```

---

## Environment Setup Example

Create environment variables:

```
DB_URL=jdbc:postgresql://localhost:5432/notemind_ai
DB_USERNAME=postgres
DB_PASSWORD=password

JWT_SECRET_KEY=your_secret

GOOGLE_API_KEY=your_key
OPENROUTER_API_KEY=your_key

CLOUD_NAME_CLOUDINARY=your_cloud_name
CLOUD_API_KEY_CLOUDINARY=your_api_key
CLOUD_API_SECRET_CLOUDINARY=your_api_secret
```

---

## Run Application

Using Gradle:

```bash
./gradlew bootRun
```

Application starts at:

```
http://localhost:8080/api/v1
```

---

# 🏗️ Architecture

```
Client
  |
  v
Spring Boot API
  |
  +---- Security (JWT)
  |
  +---- AI Service
  |
  +---- Cloudinary
  |
  +---- PostgreSQL
```

---

# 🔍 Search Architecture

```
Create Note
     |
     v
PostgreSQL Trigger
     |
     v
Generate tsvector
     |
     v
GIN Index
     |
     v
Fast Search
```

---

# 🔮 Future Improvements

- AI chat with notes
- Voice notes
- Markdown editor
- Note sharing
- Real-time collaboration
- Redis caching
- Mobile application
- Elasticsearch integration

---

# 👨‍💻 Author

Dhaliwal

CSE Student

---

⭐ Built with Spring Boot, PostgreSQL
