package com.dhaliwal.notemind.repository;

import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FolderRepository folderRepository;

    @Test
    void findAllByUserId_shouldReturnOnlyUsersNotes() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("123");
        user = userRepository.save(user);

        User anotherUser = new User();
        anotherUser.setUsername("alice");
        anotherUser.setPassword("123");
        anotherUser = userRepository.save(anotherUser);

        Note note1 = new Note();
        note1.setTitle("Java");
        note1.setUser(user);

        Note note2 = new Note();
        note2.setTitle("Spring");
        note2.setUser(user);

        Note note3 = new Note();
        note3.setTitle("Python");
        note3.setUser(anotherUser);

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);

        List<Note> notes = noteRepository.findAllByUserId(user.getId());

        assertThat(notes).hasSize(2);
        assertThat(notes)
                .extracting(Note::getTitle)
                .containsExactlyInAnyOrder("Java", "Spring");
    }
    @Test
    void findAllByUserId_shouldReturnEmptyListWhenNoNotesExist() {
        User user = new User();
        user.setUsername("empty");
        user.setPassword("123");
        user = userRepository.save(user);

        List<Note> notes = noteRepository.findAllByUserId(user.getId());

        assertThat(notes).isEmpty();
    }
    @Test
    void findNotesWithoutFolderByUserId_shouldReturnOnlyNotesWithoutFolder() {

        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user = userRepository.save(user);

        Folder folder = new Folder();
        folder.setName("Work");
        folder.setUser(user);
        folder = folderRepository.save(folder);

        Note note1 = new Note();
        note1.setTitle("Without Folder");
        note1.setUser(user);

        Note note2 = new Note();
        note2.setTitle("Inside Folder");
        note2.setUser(user);
        note2.setFolder(folder);

        noteRepository.save(note1);
        noteRepository.save(note2);

        List<Note> notes =
                noteRepository.findNotesWithoutFolderByUserId(user.getId());

        assertThat(notes).hasSize(1);
        assertThat(notes.getFirst().getTitle())
                .isEqualTo("Without Folder");
        noteRepository.delete(note1);
        noteRepository.delete(note2);
        folderRepository.delete(folder);
    }
    @Test
    void findNotesWithoutFolderByUserId_shouldReturnEmptyWhenEveryNoteHasFolder() {

        User user = new User();
        user.setUsername("john1");
        user.setPassword("123");
        user = userRepository.save(user);

        Folder folder = new Folder();
        folder.setName("Test College");
        folder.setUser(user);
        folder = folderRepository.save(folder);

        Note note = new Note();
        note.setTitle("OS");
        note.setUser(user);
        note.setFolder(folder);

        noteRepository.save(note);

        List<Note> notes =
                noteRepository.findNotesWithoutFolderByUserId(user.getId());

        assertThat(notes).isEmpty();
    }
    @Test
    void findNotesWithoutFolderByUserId_shouldIgnoreOtherUsersNotes() {

        User user1 = new User();
        user1.setUsername("u1");
        user1.setPassword("123");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("u2");
        user2.setPassword("123");
        user2 = userRepository.save(user2);

        Note note1 = new Note();
        note1.setTitle("Mine");
        note1.setUser(user1);

        Note note2 = new Note();
        note2.setTitle("Not Mine");
        note2.setUser(user2);

        noteRepository.save(note1);
        noteRepository.save(note2);

        List<Note> notes =
                noteRepository.findNotesWithoutFolderByUserId(user1.getId());

        assertThat(notes).hasSize(1);
        assertThat(notes.getFirst().getTitle()).isEqualTo("Mine");
    }
    @Test
    void searchNotes_shouldReturnMatchingNotes() {

        User user = new User();
        user.setUsername("searchUser");
        user.setPassword("123");
        user = userRepository.save(user);

        Note note1 = new Note();
        note1.setTitle("Spring Boot");
        note1.setContent("Spring Boot REST API");
        note1.setUser(user);

        Note note2 = new Note();
        note2.setTitle("Python");
        note2.setContent("Flask Framework");
        note2.setUser(user);

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.flush();

        List<Note> notes = noteRepository.searchNotes(user.getId(), "Spring");

        assertThat(notes).hasSize(1);
        assertThat(notes.getFirst().getTitle()).isEqualTo("Spring Boot");
    }

    @Test
    void searchNotes_shouldReturnEmptyWhenNoMatch() {

        User user = new User();
        user.setUsername("searchEmpty");
        user.setPassword("123");
        user = userRepository.save(user);

        Note note = new Note();
        note.setTitle("Java");
        note.setContent("Collections Framework");
        note.setUser(user);

        noteRepository.save(note);
        noteRepository.flush();

        List<Note> notes = noteRepository.searchNotes(user.getId(), "Blockchain");

        assertThat(notes).isEmpty();
    }

    @Test
    void searchNotes_shouldIgnoreOtherUsersNotes() {

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("123");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("123");
        user2 = userRepository.save(user2);

        Note note1 = new Note();
        note1.setTitle("Spring");
        note1.setContent("Spring Boot Guide");
        note1.setUser(user1);

        Note note2 = new Note();
        note2.setTitle("Spring");
        note2.setContent("Spring Security");
        note2.setUser(user2);

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.flush();

        List<Note> notes = noteRepository.searchNotes(user1.getId(), "Spring");

        assertThat(notes).hasSize(1);
        assertThat(notes.getFirst().getUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    void searchNotes_shouldReturnMultipleMatchingNotes() {

        User user = new User();
        user.setUsername("multi");
        user.setPassword("123");
        user = userRepository.save(user);

        Note note1 = new Note();
        note1.setTitle("Spring Boot");
        note1.setContent("REST API");
        note1.setUser(user);

        Note note2 = new Note();
        note2.setTitle("Spring Security");
        note2.setContent("JWT Authentication");
        note2.setUser(user);

        Note note3 = new Note();
        note3.setTitle("Docker");
        note3.setContent("Containers");
        note3.setUser(user);

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        noteRepository.flush();

        List<Note> notes = noteRepository.searchNotes(user.getId(), "Spring");

        assertThat(notes).hasSize(2);
        assertThat(notes)
                .extracting(Note::getTitle)
                .containsExactlyInAnyOrder("Spring Boot", "Spring Security");
    }
}