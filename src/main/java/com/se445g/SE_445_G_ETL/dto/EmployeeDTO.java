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

    // Thông tin nhân viên
    private Integer employeeId;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String hometown;
    private String phone;
    private String email;
    private String educationLevel;
    private String position;
    private LocalDate hireDate;
    private String employeeStatus;

    // Thông tin phòng ban
    private Integer departmentId;
    private String departmentName;
    private String departmentLocation;
    private String departmentPhone;
    private BigDecimal departmentBudgetVnd;
    private Integer managerId;

    // Thông tin lương
    private Integer salaryId;
    private BigDecimal amountVnd;
    private String currency;
    private String payFrequency;
    private BigDecimal bonusVnd;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}
