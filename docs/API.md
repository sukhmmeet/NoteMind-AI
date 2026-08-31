# API Reference

> All endpoints (except `/auth/**`) require a valid JWT in the `Authorization: Bearer <token>` header.

## Table of Contents
- [Authentication](#authentication-endpoints)
- [Notes](#notes-endpoints)
- [Flashcards](#flashcard-endpoints)
- [Folders](#folder-endpoints)

---

## Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/signup` | Register a new user |
| `POST` | `/auth/login` | Login and receive JWT + refresh token |
| `POST` | `/auth/refresh-token` | Get a new JWT using a refresh token |
| `POST` | `/auth/logout` | Revoke the refresh token |

### POST /auth/signup

Register a new user

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePass123"
  }'
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600
}
```

### POST /auth/login

Login with credentials

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePass123"
  }'
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600
}
```

### POST /auth/refresh-token

Get a new access token

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### POST /auth/logout

Revoke refresh token

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response (200):**
```json
{ "message": "Logged out successfully" }
```

---

## Notes Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/notes` | Create a note (multipart: `note` JSON + optional `image`) |
| `GET` | `/notes` | Get all notes (paginated) |
| `GET` | `/notes/{id}` | Get a specific note by ID |
| `PUT` | `/notes/{id}` | Update a note (multipart) |
| `DELETE` | `/notes/{id}` | Delete a note |
| `GET` | `/notes/search?query=...` | Full-text search |
| `GET` | `/notes/refresh-summary/{id}?type=SHORT` | Regenerate AI summary |

### POST /notes

Create a new note with AI-generated summary

**Basic note (without image):**
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Quantum Computing","content":"Quantum computers use qubits...","summaryType":"DETAILED"};type=application/json'
```

**With image:**
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Architecture Diagram","content":"Microservices architecture...","summaryType":"BULLET_POINTS"};type=application/json' \
  -F 'image=@diagram.png'
```

**Response (201):**
```json
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
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### GET /notes

Retrieve all notes (paginated)

```bash
curl -X GET "http://localhost:8080/api/v1/notes?page=0&size=10" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
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

### GET /notes/{id}

Retrieve a specific note

```bash
curl -X GET http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "id": 1,
  "title": "Quantum Computing",
  "content": "...",
  "summary": "...",
  "tags": [...],
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### PUT /notes/{id}

Update a note

```bash
curl -X PUT http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F 'note={"title":"Updated Title","content":"Updated content...","summaryType":"SHORT"};type=application/json' \
  -F 'image=@new-image.jpg'
```

**Response (200):** Updated note with regenerated summary and tags

### GET /notes/search?query=...

Full-text search across notes

```bash
curl -X GET "http://localhost:8080/api/v1/notes/search?query=quantum&limit=20" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
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

### GET /notes/refresh-summary/{id}?type=...

Regenerate AI summary (types: `SHORT`, `DETAILED`, `BULLET_POINTS`)

```bash
curl -X GET "http://localhost:8080/api/v1/notes/1/refresh-summary?type=BULLET_POINTS" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "id": 1,
  "summary": "• Point 1\n• Point 2\n• Point 3",
  "summaryType": "BULLET_POINTS"
}
```

### DELETE /notes/{id}

Delete a note

```bash
curl -X DELETE http://localhost:8080/api/v1/notes/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (204):** No Content

---

## Flashcard Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/notes/{noteId}/flashcards/generate?count=5` | AI-generate flashcards |
| `POST` | `/notes/{noteId}/flashcards/regenerate?count=5` | Regenerate flashcards |
| `GET` | `/notes/{noteId}/flashcards` | Get all flashcards for note |
| `GET` | `/flashcards/{flashCardId}` | Get specific flashcard |
| `POST` | `/notes/{noteId}/flashcards` | Manually create flashcard |
| `PATCH` | `/flashcards/{flashCardId}` | Update a flashcard |
| `DELETE` | `/flashcards/{flashCardId}` | Delete a flashcard |
| `DELETE` | `/notes/{noteId}/flashcards` | Delete all flashcards |

### POST /notes/{noteId}/flashcards/generate?count=5

Generate flashcards from a note

```bash
curl -X POST "http://localhost:8080/api/v1/notes/1/flashcards/generate?count=5" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (201):**
```json
{
  "noteId": 1,
  "flashcards": [
    {
      "id": 1,
      "question": "What is a qubit?",
      "answer": "A qubit is the fundamental unit of quantum computing...",
      "createdAt": "2024-01-15T10:35:00Z"
    }
  ],
  "totalGenerated": 5
}
```

### GET /notes/{noteId}/flashcards

List all flashcards for a note

```bash
curl -X GET http://localhost:8080/api/v1/notes/1/flashcards \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "flashcards": [...],
  "totalCount": 5
}
```

### PATCH /flashcards/{flashCardId}

Update a flashcard

```bash
curl -X PATCH http://localhost:8080/api/v1/flashcards/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is a quantum bit?",
    "answer": "Updated answer..."
  }'
```

**Response (200):** Updated flashcard

---

## Folder Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/folder` | Create a folder |
| `GET` | `/folder` | Get all folders |
| `GET` | `/folder/{id}` | Get folder by ID |
| `PATCH` | `/folder/{id}` | Rename a folder |
| `DELETE` | `/folder/{id}` | Delete a folder |

### POST /folder

Create a new folder

```bash
curl -X POST http://localhost:8080/api/v1/folder \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Study Notes"}'
```

**Response (201):**
```json
{
  "id": 1,
  "name": "Study Notes",
  "createdAt": "2024-01-15T10:40:00Z"
}
```

### GET /folder?includeNotes=true

Get all folders with nested notes

```bash
curl -X GET "http://localhost:8080/api/v1/folder?includeNotes=true" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "folders": [
    {
      "id": 1,
      "name": "Study Notes",
      "notes": [{"id": 1, "title": "Quantum Computing"}],
      "noteCount": 1
    }
  ]
}
```

### PATCH /folder/{id}

Rename a folder

```bash
curl -X PATCH http://localhost:8080/api/v1/folder/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Advanced Study Notes"}'
```

**Response (200):** Updated folder

### DELETE /folder/{id}

Delete a folder

```bash
curl -X DELETE http://localhost:8080/api/v1/folder/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (204):** No Content
