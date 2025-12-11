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

@Entity
@Table(name = "task_performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskPerformance {

    @Id
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "employee_performance_id")
    private Integer employeePerformanceId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "task_score")
    private Double taskScore;

    @Column(name = "note")
    private String note;
}

