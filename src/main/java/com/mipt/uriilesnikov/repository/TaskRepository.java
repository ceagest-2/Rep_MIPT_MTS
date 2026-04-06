package com.mipt.uriilesnikov.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;

/**
 * JPA repository for task persistence and custom queries.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate BETWEEN :fromDate AND :toDate
            ORDER BY t.dueDate ASC
            """)
    List<Task> findTasksDueBetween(@Param("fromDate") LocalDate fromDate,
                                   @Param("toDate") LocalDate toDate);

    @EntityGraph(attributePaths = "attachments")
    @Query("SELECT DISTINCT t FROM Task t")
    List<Task> findAllWithAttachments();
}