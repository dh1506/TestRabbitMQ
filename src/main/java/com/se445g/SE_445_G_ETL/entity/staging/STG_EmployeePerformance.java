package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stg_employee_performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_EmployeePerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer employeeId;

    private Integer reviewId;

    private Integer performanceScore;

    private String comments;

    private LocalDateTime createdAt;
}
