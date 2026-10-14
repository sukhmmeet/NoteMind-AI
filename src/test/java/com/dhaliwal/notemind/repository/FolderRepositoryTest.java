package com.dhaliwal.notemind.repository;

import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByName_shouldReturnFolder() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("123");
        user = userRepository.save(user);

        Folder folder = new Folder();
        folder.setName("Test Work");
        folder.setUser(user);
        folderRepository.save(folder);

        Optional<Folder> result = folderRepository.findByName("Test Work");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Work");
        folderRepository.delete(folder);
    }

    @Test
    void findByName_shouldReturnEmptyWhenFolderDoesNotExist() {

        Optional<Folder> result = folderRepository.findByName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByUserId_shouldReturnUsersFolders() {

        User user1 = new User();
        user1.setUsername("john");
        user1.setPassword("123");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("alice");
        user2.setPassword("123");
        user2 = userRepository.save(user2);

        Folder folder1 = new Folder();
        folder1.setName("Work");
        folder1.setUser(user1);

        Folder folder2 = new Folder();
        folder2.setName("College");
        folder2.setUser(user1);

        Folder folder3 = new Folder();
        folder3.setName("Private");
        folder3.setUser(user2);

        folderRepository.save(folder1);
        folderRepository.save(folder2);
        folderRepository.save(folder3);

        List<Folder> folders = folderRepository.findAllByUserId(user1.getId());
        System.out.println(folders);
        assertThat(folders).hasSize(2);
        assertThat(folders)
                .extracting(Folder::getName)
                .containsExactlyInAnyOrder("Work", "College");
        folderRepository.delete(folder1);
        folderRepository.delete(folder2);
        folderRepository.delete(folder3);
    }

    @Test
    void findAllByUserId_shouldReturnEmptyWhenUserHasNoFolders() {

        User user = new User();
        user.setUsername("empty");
        user.setPassword("123");
        user = userRepository.save(user);

        List<Folder> folders = folderRepository.findAllByUserId(user.getId());

        assertThat(folders).isEmpty();
    }

    @Test
    void findAllByUserId_shouldIgnoreOtherUsersFolders() {

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("123");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("123");
        user2 = userRepository.save(user2);

        Folder folder1 = new Folder();
        folder1.setName("Mine");
        folder1.setUser(user1);

        Folder folder2 = new Folder();
        folder2.setName("Other");
        folder2.setUser(user2);

        folderRepository.save(folder1);
        folderRepository.save(folder2);

        List<Folder> folders = folderRepository.findAllByUserId(user1.getId());

        assertThat(folders).hasSize(1);
        assertThat(folders.getFirst().getName()).isEqualTo("Mine");
        folderRepository.delete(folder1);
        folderRepository.delete(folder2);
    }
}