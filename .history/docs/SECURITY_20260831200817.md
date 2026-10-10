# Security Best Practices

## Authentication & Authorization

### Password Security

| Practice | Implementation |
|---|---|
| **Password Hashing** | BCrypt with strength 10 |
| **Minimum Length** | 8 characters required |
| **Complexity** | No additional rules enforced (adjust as needed) |

```java
// Password encoded using BCrypt
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

### JWT Token Management

| Feature | Details |
|---|---|
| **Algorithm** | HMAC-SHA256 |
| **Key Strength** | 256-bit minimum |
| **Access Token TTL** | 15 minutes |
| **Refresh Token TTL** | 7 days |
| **Signing** | JJWT library (0.13.0) |

**Generate a secure JWT secret:**
```bash
# Linux/macOS
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((New-Guid).Guid + (New-Guid).Guid))
```

### Refresh Token Strategy

- Stored in PostgreSQL with expiry tracking
- Revocation support via database flag
- User logout invalidates token immediately
- Cannot be reused after revocation

```java
// Refresh token revocation on logout
public void logout(String refreshToken) {
    RefreshToken token = repository.findByToken(refreshToken);
    token.setRevoked(true);
    repository.save(token);
}
```

### User Isolation

All queries are automatically scoped to authenticated user:

```java
// Example: All user notes are scoped
@GetMapping
public ResponseEntity<?> getAllNotes(@AuthenticationPrincipal User currentUser) {
    // Only current user's notes are returned
    List<Note> notes = noteRepository.findByUser(currentUser);
    return ResponseEntity.ok(notes);
}
```

---

## API Security

### HTTPS Configuration

**In production, always use HTTPS:**

```bash
# Generate SSL certificate
keytool -genkey -alias tomcat -storetype PKCS12 \
  -keyalg RSA -keysize 2048 -keystore keystore.p12 \
  -validity 3650
```

**application.properties:**
```properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
server.port=8443
```

### CORS Configuration

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of(
                "https://yourdomain.com",
                "https://app.yourdomain.com"
            ));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true);
            return config;
        }));
        return http.build();
    }
}
```

### CSRF Protection

For stateless APIs, CSRF is disabled (safe with tokens):

```java
http.csrf(csrf -> csrf.disable());
```

### Rate Limiting (Recommended Enhancement)

Implement to prevent brute force attacks:

```java
@Bean
public RateLimitingFilter rateLimitingFilter() {
    return new RateLimitingFilter(
        maxRequests = 100,      // 100 requests
        windowSize = 60         // per 60 seconds
    );
}
```

---

## Data Protection

### Input Validation

All DTOs are validated with `@Valid` and custom validators:

```java
public class NoteCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 10000)
    private String content;
}
```

### SQL Injection Prevention

All queries use JPA parameterized queries (no string concatenation):

```java
// ✅ Safe - Parameterized query
@Query("SELECT n FROM Note n WHERE n.title = ?1 AND n.user = ?2")
List<Note> findByTitleAndUser(String title, User user);

// ❌ Unsafe - String concatenation (NEVER DO THIS)
// String query = "SELECT * FROM note WHERE title = '" + title + "'";
```

### Sensitive Data Handling

```java
// ✅ Never log sensitive data
logger.debug("User login attempt: {}", username);  // OK

// ❌ Never log passwords or tokens
// logger.debug("Login with password: {}", password);  // WRONG!

// Store secrets in environment variables only
String apiKey = System.getenv("GOOGLE_API_KEY");
```

---

## Environment Variable Security

### ✅ Best Practices

1. **Never commit secrets** to version control
2. **Use environment variables** for all sensitive data
3. **Rotate API keys** periodically
4. **Use `.gitignore`** for config files

**Example .gitignore:**
```
# Environment variables
.env
.env.local
.env.*.local

# Application properties with secrets
application-secret.properties

# IDE configuration with secrets
.vscode/settings.json
.idea/workspace.xml
```

### ❌ Never Do This

```java
// ❌ WRONG - Hardcoded secrets
String apiKey = "AIza1234567890...";

// ❌ WRONG - In properties file
# application.properties
google.api.key=AIza1234567890...

// ❌ WRONG - Logged to console
System.out.println("API Key: " + apiKey);

// ❌ WRONG - In comments
// Old password was: "password123"
```

### ✅ Correct Approach

```java
// ✅ CORRECT - From environment
String apiKey = System.getenv("GOOGLE_API_KEY");

// ✅ CORRECT - Injected configuration
@Value("${google.api.key}")
private String apiKey;

// ✅ CORRECT - Environment-specific properties
# application-prod.properties (not in git)
google.api.key=${GOOGLE_API_KEY}
```

---

## External Service Security

### Google Gemini API

- Keep API key private
- Use server-side API calls only
- Implement request rate limiting
- Validate API responses

### OpenRouter API

- Store secret key in environment variable only
- Use `sk-or-...` token format (confirmed valid)
- Implement timeout for API calls
- Monitor API usage for suspicious patterns

### Cloudinary

- Store credentials in environment variables
- Use URL-signed uploads for client-side uploads
- Implement file type validation
- Scan uploaded images for malware (optional)

```java
// Secure Cloudinary upload
Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap(
    "resource_type", "auto",
    "folder", "notemind/notes",
    "allowed_formats", "jpg,jpeg,png,gif",
    "max_file_size", 10485760  // 10MB
));
```

---

## Security Headers

Add security headers to all responses:

```java
@Configuration
public class SecurityHeadersFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Database Security

### Connection Security

```properties
# Use SSL connections to PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/notemind_ai?sslmode=require
```

### User Permissions

```sql
-- Create database user with limited permissions
CREATE USER notemind_user WITH PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE notemind_ai TO notemind_user;
GRANT USAGE ON SCHEMA public TO notemind_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO notemind_user;

-- Don't use superuser for application
-- Only use superuser for migrations
```

### Backup Security

- Store backups encrypted
- Keep backups offline/secure location
- Test restore procedures regularly
- Implement backup rotation policy

---

## Dependency Security

### Keep Dependencies Updated

```bash
# Check for known vulnerabilities
./gradlew dependencyCheckAnalyze

# Update dependencies
./gradlew dependencyUpdates
```

### Vulnerable Dependencies to Avoid

- Old versions of `org.apache.log4j` (Log4Shell)
- Old versions of `commons-io` (deserialization issues)
- Unmaintained libraries

---

## Security Checklist

- [ ] Change default passwords (database, admin accounts)
- [ ] Use HTTPS in production
- [ ] Implement rate limiting
- [ ] Enable request logging (without sensitive data)
- [ ] Set up intrusion detection
- [ ] Regular security updates for dependencies
- [ ] Database backup encryption
- [ ] API key rotation schedule
- [ ] Security audit logs
- [ ] CORS configuration review
- [ ] CSFR tokens (if needed)
- [ ] Session timeout configuration
- [ ] Password policy enforcement
- [ ] Multi-factor authentication (future feature)
- [ ] API versioning for backward compatibility

---

## Reporting Security Issues

**Do NOT create public issues for security vulnerabilities!**

Instead, email security concerns to: `sukhmmeet@gmail.com`

Include:
- Description of vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (optional)

We appreciate responsible disclosure! 🛡️
