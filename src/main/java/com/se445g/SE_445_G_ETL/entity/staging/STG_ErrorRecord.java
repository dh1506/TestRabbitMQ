package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stg_error_record")
@Data
public class STG_ErrorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String recordType;

    // JSON bản ghi lỗi
    @Column(columnDefinition = "TEXT")
    private String rawData;

    @Column(columnDefinition = "TEXT")
    private String errors;

    private String status = "PENDING_REVIEW"; // PENDING_REVIEW, RESOLVED, DELETED
    private LocalDateTime validationTime = LocalDateTime.now();
}
