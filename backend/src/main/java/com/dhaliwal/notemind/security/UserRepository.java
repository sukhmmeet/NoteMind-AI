package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserDetails> findByUsername(String username);

    @Query(value = "SELECT * FROM note " +
            "WHERE user_id = :userId AND " +
            "search_vector @@ plainto_tsquery(:keyword) " +
            "ORDER BY ts_rank( search_vector, plainto_tsquery(:keyword)) DESC",
            nativeQuery = true)
    List<Note> searchNotes(
            Long userId,
            String keyword
    );
}