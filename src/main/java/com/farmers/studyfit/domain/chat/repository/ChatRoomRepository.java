package com.farmers.studyfit.domain.chat.repository;

import com.farmers.studyfit.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.connection.id = :connectionId")
    Optional<ChatRoom> findByConnectionId(@Param("connectionId") Long connectionId);
    
    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr WHERE cr.connection.id = :connectionId")
    boolean existsByConnectionId(@Param("connectionId") Long connectionId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.teacher.id = :teacherId")
    List<ChatRoom> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.student.id = :studentId")
    List<ChatRoom> findByStudentId(@Param("studentId") Long studentId);
}
