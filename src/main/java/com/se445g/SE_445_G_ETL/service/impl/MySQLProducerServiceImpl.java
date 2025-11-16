package com.se445g.SE_445_G_ETL.service.impl;

import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.entity.staging.*;
import com.se445g.SE_445_G_ETL.mapper.PerformanceMapper;
import com.se445g.SE_445_G_ETL.service.interf.MySQLProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySQLProducerServiceImpl implements MySQLProducerService {

    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final PerformanceMapper performanceMapper;

    // Định nghĩa các hằng số recordType
    private static final String TYPE_REVIEW = "REVIEW";
    private static final String TYPE_EMPLOYEE_PERFORMANCE = "EMPLOYEE_PERFORMANCE";
    private static final String TYPE_TASK_PERFORMANCE = "TASK_PERFORMANCE";
    private static final String TYPE_DEPT_PERFORMANCE = "DEPT_PERFORMANCE";
    private static final String TYPE_KPI_METRIC = "KPI_METRIC";

    @Override
    public void sendMySQLData() {
        log.info("Bắt đầu quá trình gửi dữ liệu Performance từ MySQL...");

        // Gửi theo thứ tự phụ thuộc: Các bảng không có khóa ngoại trước
        sendReviews();
        sendKpiMetrics();
        sendDepartmentPerformance();
        sendEmployeePerformance();
        sendTaskPerformance();

        log.info("Đã gửi tất cả dữ liệu Performance lên RabbitMQ.");
    }

    private void sendReviews() {
        String sql = "SELECT review_id, period, start_date, end_date, description FROM performance_review";
        RowMapper<STG_PerformanceReview> rowMapper = (rs, rowNum) -> STG_PerformanceReview.builder()
                .reviewId(rs.getInt("review_id"))
                .period(rs.getString("period"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(rs.getDate("end_date").toLocalDate())
                .description(rs.getString("description"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(entity -> {
            PerformanceDTO dto = performanceMapper.reviewToDto(entity);
            dto.setRecordType(TYPE_REVIEW);
            sendToQueue(dto);
        });
        log.info("Đã gửi dữ liệu PerformanceReviews.");
    }

    private void sendKpiMetrics() {
        String sql = "SELECT kpi_id, kpi_name, description, weight, category FROM kpi_metrics";
        RowMapper<STG_KpiMetrics> rowMapper = (rs, rowNum) -> STG_KpiMetrics.builder()
                .kpiId(rs.getInt("kpi_id"))
                .kpiName(rs.getString("kpi_name"))
                .description(rs.getString("description"))
                .weight(rs.getDouble("weight"))
                .category(rs.getString("category"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(entity -> {
            PerformanceDTO dto = performanceMapper.kpiMetricsToDto(entity);
            dto.setRecordType(TYPE_KPI_METRIC);
            sendToQueue(dto);
        });
        log.info("Đã gửi dữ liệu KpiMetrics.");
    }

    private void sendDepartmentPerformance() {
        String sql = "SELECT dept_perf_id, department_id, review_id, average_score, ranking, remarks FROM department_performance";
        RowMapper<STG_DepartmentPerformance> rowMapper = (rs, rowNum) -> STG_DepartmentPerformance.builder()
                .deptPerfId(rs.getInt("dept_perf_id"))
                .departmentId(rs.getInt("department_id"))
                .reviewId(rs.getInt("review_id"))
                .averageScore(rs.getDouble("average_score"))
                .ranking(rs.getString("ranking"))
                .remarks(rs.getString("remarks"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(entity -> {
            PerformanceDTO dto = performanceMapper.departmentPerformanceToDto(entity);
            dto.setRecordType(TYPE_DEPT_PERFORMANCE);
            sendToQueue(dto);
        });
        log.info("Đã gửi dữ liệu DepartmentPerformance.");
    }

    private void sendEmployeePerformance() {
        String sql = "SELECT id, employee_id, review_id, performance_score, comments, created_at FROM employee_performance";
        RowMapper<STG_EmployeePerformance> rowMapper = (rs, rowNum) -> STG_EmployeePerformance.builder()
                .id(rs.getInt("id"))
                .employeeId(rs.getInt("employee_id"))
                .reviewId(rs.getInt("review_id"))
                .performanceScore(rs.getInt("performance_score"))
                .comments(rs.getString("comments"))
                .createdAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(entity -> {
            PerformanceDTO dto = performanceMapper.employeePerformanceToDto(entity);
            dto.setRecordType(TYPE_EMPLOYEE_PERFORMANCE);
            sendToQueue(dto);
        });
        log.info("Đã gửi dữ liệu EmployeePerformance.");
    }

    private void sendTaskPerformance() {
        String sql = "SELECT task_id, employee_performance_id, task_name, task_score, note FROM task_performance";
        RowMapper<STG_TaskPerformance> rowMapper = (rs, rowNum) -> STG_TaskPerformance.builder()
                .taskId(rs.getInt("task_id"))
                .employeePerformanceId(rs.getInt("employee_performance_id"))
                .taskName(rs.getString("task_name"))
                .taskScore(rs.getInt("task_score"))
                .note(rs.getString("note"))
                .build();

        jdbcTemplate.query(sql, rowMapper).forEach(entity -> {
            PerformanceDTO dto = performanceMapper.taskPerformanceToDto(entity);
            dto.setRecordType(TYPE_TASK_PERFORMANCE);
            sendToQueue(dto);
        });
        log.info("Đã gửi dữ liệu TaskPerformance.");
    }

    private void sendToQueue(PerformanceDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PERFORMANCE_ROUTING_KEY,
                dto);
    }
}