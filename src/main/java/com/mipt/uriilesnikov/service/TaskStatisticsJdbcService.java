package com.mipt.uriilesnikov.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.mipt.uriilesnikov.dto.PriorityTaskCountDto;
import com.mipt.uriilesnikov.model.Priority;

/**
 * Provides task statistics via JdbcTemplate.
 */
@Service
public class TaskStatisticsJdbcService {

    private static final String COUNT_BY_PRIORITY_SQL = """
            SELECT priority, COUNT(*) AS task_count
            FROM tasks
            GROUP BY priority
            ORDER BY priority
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<PriorityTaskCountDto> rowMapper = new PriorityTaskCountRowMapper();

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityTaskCountDto> getTasksCountByPriority() {
        return jdbcTemplate.query(COUNT_BY_PRIORITY_SQL, rowMapper);
    }

    private static class PriorityTaskCountRowMapper implements RowMapper<PriorityTaskCountDto> {
        @Override
        public PriorityTaskCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Priority priority = Priority.valueOf(rs.getString("priority"));
            long count = rs.getLong("task_count");
            return new PriorityTaskCountDto(priority, count);
        }
    }
}
