package com.se445g.SE_445_G_ETL.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDTO {

    // TRƯỜNG PHÂN LOẠI
    // Sẽ là "REVIEW", "EMPLOYEE_PERFORMANCE", "TASK_PERFORMANCE",
    // "DEPT_PERFORMANCE", "KPI_METRIC"
    private String recordType;

    // =====================
    // Performance Review Fields
    // =====================
    private Integer reviewId;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reviewDescription; // Đổi tên để tránh trùng lặp

    // =====================
    // Employee Performance Fields
    // =====================
    private Integer employeePerformanceId; // Đổi tên từ 'id' cho rõ ràng
    private Integer employeeId;
    // reviewId đã có ở trên
    private Integer performanceScore;
    private String comments;
    private LocalDateTime createdAt;

    // =====================
    // Task Performance Fields
    // =====================
    private Integer taskId;
    // employeePerformanceId đã có ở trên
    private String taskName;
    private Integer taskScore;
    private String note;

    // =====================
    // Department Performance Fields
    // =====================
    private Integer deptPerfId;
    private Integer departmentId;
    // reviewId đã có ở trên
    private Double averageScore;
    private String ranking;
    private String remarks;

    // =====================
    // Kpi Metrics Fields
    // =====================
    private Integer kpiId;
    private String kpiName;
    private String kpiDescription; // Đổi tên để tránh trùng lặp
    private Double weight;
    private String category;
}