# Contributing Guide

We love contributions! Here's how you can help.

## Getting Started

### 1. Fork the Repository

```bash
git clone https://github.com/sukhmmeet/NoteMind-AI.git
cd NoteMind-AI
```

### 2. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions
- `perf/` - Performance improvements

### 3. Set Up Development Environment

Follow the [Setup Guide](SETUP.md) to get everything running locally.

---

## Development Workflow

### Code Style Guidelines

#### Java

Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html):

- 2-space indentation (not tabs)
- Max line length: 100 characters
- CamelCase for class/method names
- UPPER_SNAKE_CASE for constants
- Meaningful variable names

```java
// ✅ Good
public class NoteService {
    private static final int MAX_TITLE_LENGTH = 255;
    
    public Note createNote(String title, String content) {
        // Implementation
    }
}

// ❌ Bad
public class NS {
    private static final int MTL = 255;
    
    public Note cn(String t, String c) {
        // Unclear naming
    }
}
```

#### File Organization

```
src/main/java/com/dhaliwal/notemind/
├── controller/          # REST endpoints
├── service/            # Business logic
├── repository/         # Data access
├── entity/             # JPA entities
├── dto/                # Data transfer objects
├── exception/          # Custom exceptions
├── security/           # Auth & JWT
├── config/             # Spring configuration
└── util/               # Utility classes
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

[optional body]

[optional footer]
```

#### Types

- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `test` - Adding/updating tests
- `refactor` - Code refactoring
- `perf` - Performance improvement
- `chore` - Dependency/build updates
- `ci` - CI/CD changes
- `style` - Code style/formatting

#### Examples

```
feat(auth): add OAuth2 support
fix(notes): resolve N+1 query in full-text search
docs(readme): add Docker setup instructions
test(flashcards): add regenerate endpoint tests
refactor(services): extract AI logic to separate class
perf(caching): implement Redis caching for notes
```

**Good commit message format:**
```
feat(notes): add AI summary regeneration

- Implement refresh-summary endpoint
- Support multiple summary types (SHORT, DETAILED, BULLET_POINTS)
- Add caching for regenerated summaries
- Write comprehensive tests

Fixes #42
```

### Writing Tests

Tests are required for all new features:

```java
@SpringBootTest
@DisplayName("Note Service Tests")
public class NotesServiceTest {
    
    @InjectMocks
    private NotesService notesService;
    
    @Mock
    private NoteRepository noteRepository;
    
    @Test
    @DisplayName("Should create note with AI summary")
    void testCreateNote_withAISummary() {
        // Arrange
        String title = "Quantum Computing";
        String content = "Quantum computers use qubits...";
        
        // Act
        Note result = notesService.createNote(title, content);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getSummary());
        assertTrue(result.getSummary().length() > 0);
    }
}
```

**Test naming conventions:**
- `testMethodName_expectedBehavior_when_precondition()`
- Use `@DisplayName` for readable descriptions
- One assertion per test or related assertions

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests NoteControllerTest

# Run with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

---

## Pull Request Process

### Before Submitting

1. **Update your branch** with latest main:
   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Run all tests**:
   ```bash
   ./gradlew clean test
   ```

3. **Check code quality** (use IDE analysis):
   - Remove unused imports
   - Fix warnings
   - Check formatting

4. **Update documentation**:
   - Update README if needed
   - Add comments for complex logic
   - Update API docs if endpoints changed

### Create Pull Request

**PR Title Format:**
```
[Type] Brief description

feat: add note sharing feature
fix: resolve caching issue with updated notes
docs: improve API documentation
```

**PR Description Template:**
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Documentation update
- [ ] Breaking change

## Changes Made
- Change 1
- Change 2
- Change 3

## Tests Added
- Test 1
- Test 2

## Related Issues
Fixes #123

## Screenshots (if applicable)
[Add screenshots for UI changes]

## Checklist
- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Commit messages follow convention
```

### Review Process

1. **Wait for CI checks** to pass
2. **Address review comments** promptly
3. **Respond to feedback** with explanations or fixes
4. **Keep PR focused** on single feature/fix
5. **Rebase** if requested

---

## Project Structure Deep Dive

### Controller Layer

Handle HTTP requests and responses:

```java
@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    @PostMapping
    public ResponseEntity<?> createNote(@Valid @RequestBody NoteRequest request) {
        // Validate input, call service, return response
    }
}
```

- Always use `@Valid` for request validation
- Use appropriate HTTP status codes
- Document endpoints with comments

### Service Layer

Implement business logic:

```java
@Service
public class NoteService {
    public Note createNote(NoteRequest request) {
        // 1. Validate inputs
        // 2. Call repositories
        // 3. Perform business logic
        // 4. Return result
    }
}
```

- One service per entity/feature
- Inject dependencies via constructor
- Use `@Transactional` for database operations

### Repository Layer

Data access abstraction:

```java
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);
    
    @Query(value = "SELECT * FROM note WHERE ...", nativeQuery = true)
    List<Note> customQuery();
}
```

- Use JPA for standard operations
- Add `@Query` for complex queries
- Avoid N+1 query problems

### Entity Layer

Database models:

```java
@Entity
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

- Use appropriate annotations
- Add validation constraints
- Document relationships

---

## Common Contributions

### Adding a New Feature

1. Create entity in `entity/`
2. Create repository in `repository/`
3. Create service in `service/`
4. Create controller in `controller/`
5. Create DTOs in `dto/`
6. Add comprehensive tests
7. Update documentation

### Fixing a Bug

1. Create a test that reproduces the bug
2. Implement the fix
3. Verify test passes
4. Add regression test if needed
5. Document the issue in commit message

### Improving Documentation

1. Update relevant `.md` files
2. Add code examples
3. Include before/after if applicable
4. Run spell check
5. Test markdown formatting

### Performance Improvements

1. Profile the application to identify bottleneck
2. Implement optimization
3. Benchmark before/after
4. Add performance tests
5. Document improvements

---

## Code Review Expectations

### Reviewers Will Check

- ✅ Code follows style guidelines
- ✅ Tests are comprehensive
- ✅ No duplicate code
- ✅ Performance impact
- ✅ Security issues
- ✅ Documentation completeness
- ✅ Backward compatibility

### Be Prepared For

- Suggestions for improvements
- Requests for additional tests
- Documentation requirements
- Performance concerns
- Security best practices

### Responding to Feedback

```
// ✅ Good response
"Good point! I'll add validation for that edge case."

// ✅ Good response
"I disagree slightly because... Would this alternative work better?"

// ❌ Bad response
"That's how I want it."
```

---

## Before Pushing Final PR

Checklist:

- [ ] `./gradlew clean test` passes
- [ ] No new warnings
- [ ] Commits follow convention
- [ ] PR description is clear
- [ ] Documentation is updated
- [ ] No hardcoded values (use properties)
- [ ] No commented-out code
- [ ] No debug statements
- [ ] Environment variables properly used
- [ ] Related issues are referenced

---

## Questions?

Feel free to:
- Open an issue for discussion
- Ask in PR comments
- Email: sukhmmeet@gmail.com
- Check [Security Policy](SECURITY.md) for security issues

---

## License

By contributing, you agree your contributions will be licensed under the MIT License.

Thank you for contributing! 🚀
