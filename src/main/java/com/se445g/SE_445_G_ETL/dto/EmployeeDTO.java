package com.se445g.SE_445_G_ETL.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    // TRƯỜNG MỚI ĐỂ PHÂN LOẠI
    // Sẽ là "DEPARTMENT", "EMPLOYEE", hoặc "SALARY"
    private String recordType;

    // =====================
    // Thông tin nhân viên
    // =====================
    private Integer employeeId;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String position;

    // =====================
    // Thông tin phòng ban
    // =====================
    private Integer departmentId;
    private String departmentName;
    private String location;

    // =====================
    // Thông tin lương
    // =====================
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private Integer month;
    private Integer year;
}
