package com.se445g.SE_445_G_ETL.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDTO {

    // TRƯỜNG MỚI ĐỂ PHÂN LOẠI
    // Sẽ là "EMPLOYEEPERFORMANCE", "REVIEW", hoặc "TASKPERFORMANCE"
    private String recordType;

    // --- Thông tin EmployeePerformance ---
    private Integer id;
    private Integer employeeId;
    private BigDecimal performanceScore;
    private String comments;
    private LocalDateTime createdAt;

    // --- Thông tin Review (được flatten ra) ---
    private Integer reviewId;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reviewDescription;

    // --- Thông tin TaskPerformance (nếu nhiều task thì gửi từng task 1 hoặc ghép
    // chuỗi JSON) ---
    private Integer taskId;
    private String taskName;
    private BigDecimal taskScore;
    private String taskNote;
}
