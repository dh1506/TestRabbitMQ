package com.se445g.SE_445_G_ETL.entity.log;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "etl_job_log") // <-- Tên bảng trong db_log
@Data
@NoArgsConstructor
public class ETLJobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UUID duy nhất cho một lần chạy Job (liên kết các log)
    @Column(name = "run_id", nullable = false)
    private String runId; 

    // Loại Job (CSV_PRODUCER, EMPLOYEE_CONSUMER, ...)
    @Column(name = "job_type", nullable = false)
    private String jobType; 

    // Trạng thái (START, SUCCESS, FAILURE, INFO)
    @Column(name = "status", nullable = false)
    private String status; 

    // Mô tả chi tiết
    @Column(name = "message", length = 1000)
    private String message;

    // Tổng số bản ghi được xử lý
    @Column(name = "record_count")
    private Integer recordCount; 

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}