package com.se445g.SE_445_G_ETL.entity.staging;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stg_task_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_TaskPerformance {

    @Id
    @Column(name = "task_id")
    private Integer taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_performance_id", nullable = false)
    private STG_EmployeePerformance employeePerformance;

    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;

    @Column(name = "task_score", precision = 4, scale = 2)
    private BigDecimal taskScore;

    private String note;
}
