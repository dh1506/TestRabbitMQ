package com.se445g.SE_445_G_ETL.service.impl;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.service.interf.MySQLProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySQLProducerServiceImpl implements MySQLProducerService {

    // JdbcTemplate để đọc từ MySQL nguồn (Source)
    private final JdbcTemplate jdbcTemplate;

    // RabbitTemplate để gửi đến RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    // Định nghĩa các hằng số cho recordType
    private static final String TYPE_EMP = "EMPLOYEEPERFORMANCE";
    private static final String TYPE_REVIEW = "REVIEW";
    private static final String TYPE_TASK = "TASKPERFORMANCE";

    @Override
    public void sendMySQLData() {
        log.info("Bắt đầu quá trình gửi dữ liệu Performance từ MySQL...");

        // Chạy 3 quy trình độc lập, TẤT CẢ gửi về 1 queue
        sendReview();
        sendEmployeePerformance();
        sendTaskPerformance();

        log.info("Đã gửi tất cả dữ liệu Performance lên RabbitMQ.");
    }

    /**
     * Đọc từ bảng 'task_performance' và gửi DTO loại
     * TASKPERFORMANCE
     */
    private void sendTaskPerformance() {
        // Giả định 'id' trong DTO sẽ mang employee_performance_id
        String sql = "SELECT task_id, employee_performance_id, task_name, task_score, note FROM task_performance";

        RowMapper<PerformanceDTO> rowMapper = (rs, rowNum) -> PerformanceDTO.builder()
                .recordType(TYPE_TASK)
                .id(rs.getInt("employee_performance_id")) // ** QUAN TRỌNG: Gửi ID của cha (EmployeePerformance)
                .taskId(rs.getInt("task_id")) // Gửi ID của chính nó
                .taskName(rs.getString("task_name"))
                .taskScore(rs.getBigDecimal("task_score"))
                .taskNote(rs.getString("note"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(this::sendToQueue);
        log.info("Đã gửi dữ liệu TaskPerformances.");
    }

    /**
     * Đọc từ bảng 'performance_review' và gửi DTO loại REVIEW
     */
    private void sendReview() {
        String sql = "SELECT review_id, period, start_date, end_date, description FROM performance_review";

        RowMapper<PerformanceDTO> rowMapper = (rs, rowNum) -> PerformanceDTO.builder()
                .recordType(TYPE_REVIEW)
                .reviewId(rs.getInt("review_id"))
                .period(rs.getString("period"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(rs.getDate("end_date").toLocalDate())
                .reviewDescription(rs.getString("description"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(this::sendToQueue);
        log.info("Đã gửi dữ liệu Reviews.");
    }

    /**
     * Đọc từ bảng 'employee_performance' và gửi DTO loại
     * EMPLOYEEPERFORMANCE
     */
    private void sendEmployeePerformance() {
        String sql = "SELECT id, employee_id, review_id, performance_score, comments, created_at FROM employee_performance";

        RowMapper<PerformanceDTO> rowMapper = (rs, rowNum) -> PerformanceDTO.builder()
                .recordType(TYPE_EMP)
                .id(rs.getInt("id"))
                .employeeId(rs.getInt("employee_id"))
                .reviewId(rs.getInt("review_id")) // Gửi khóa ngoại
                .performanceScore(rs.getBigDecimal("performance_score"))
                .comments(rs.getString("comments"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime()
                        : LocalDateTime.now())
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(this::sendToQueue);
        log.info("Đã gửi dữ liệu EmployeePerformances.");
    }

    /**
     * Phương thức chung để gửi DTO đến 1 queue duy nhất
     */
    private void sendToQueue(PerformanceDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PERFORMANCE_ROUTING_KEY,
                dto);
    }
}
