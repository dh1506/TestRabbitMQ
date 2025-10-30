package com.se445g.SE_445_G_ETL.entity.staging;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stg_salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Integer salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private STG_Employee employee;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "bonus")
    private BigDecimal bonus;

    private Integer month;
    private Integer year;
}
