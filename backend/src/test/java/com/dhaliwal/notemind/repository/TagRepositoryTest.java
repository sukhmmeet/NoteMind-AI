package com.dhaliwal.notemind.repository;

import com.dhaliwal.notemind.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void cleanDatabase() {
        tagRepository.deleteAll();
    }

    @Test
    void findByName_shouldReturnTag() {
        Tag tag = new Tag();
        tag.setName("Java");
        tagRepository.save(tag);

        Optional<Tag> result = tagRepository.findByName("Java");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Java");
    }

    @Test
    void findByName_shouldReturnEmptyWhenTagDoesNotExist() {
        Optional<Tag> result = tagRepository.findByName("Python");

        assertThat(result).isEmpty();
    }

    @Test
    void findByNameIn_shouldReturnMatchingTags() {
        Tag java = new Tag();
        java.setName("Java");

        Tag spring = new Tag();
        spring.setName("Spring");

        Tag kotlin = new Tag();
        kotlin.setName("Kotlin");

        tagRepository.saveAll(List.of(java, spring, kotlin));

        List<Tag> result =
                tagRepository.findByNameIn(Set.of("Java", "Spring"));

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void findByNameIn_shouldReturnEmptyWhenNoTagsMatch() {
        Tag java = new Tag();
        java.setName("Java");

        tagRepository.save(java);

        List<Tag> result =
                tagRepository.findByNameIn(Set.of("Python", "C++"));

        assertThat(result).isEmpty();
    }

    @Test
    void findByNameIn_shouldReturnAllMatchingTags() {
        Tag java = new Tag();
        java.setName("Java");

        Tag spring = new Tag();
        spring.setName("Spring");

        Tag docker = new Tag();
        docker.setName("Docker");

        Tag postgres = new Tag();
        postgres.setName("PostgreSQL");

        tagRepository.saveAll(List.of(java, spring, docker, postgres));

        List<Tag> result =
                tagRepository.findByNameIn(
                        Set.of("Java", "Docker", "PostgreSQL"));

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder(
                        "Java",
                        "Docker",
                        "PostgreSQL"
                );
    }
}