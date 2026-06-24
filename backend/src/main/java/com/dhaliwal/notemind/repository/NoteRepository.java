package com.dhaliwal.notemind.repository;

import com.dhaliwal.notemind.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("SELECT n FROM Note n JOIN FETCH n.user u WHERE u.id = :id")
    List<Note> findAllByUserId(@Param("id") Long id);
}