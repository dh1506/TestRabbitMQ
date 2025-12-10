package com.se445g.SE_445_G_ETL.entity.target;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @Column(name = "salary_id")
    private Integer salaryId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "bonus")
    private BigDecimal bonus;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;
}

