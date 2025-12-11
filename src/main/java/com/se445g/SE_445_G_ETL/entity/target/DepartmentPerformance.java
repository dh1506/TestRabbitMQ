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

@Entity
@Table(name = "department_performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentPerformance {

    @Id
    @Column(name = "dept_perf_id")
    private Integer deptPerfId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "average_score")
    private BigDecimal averageScore;

    @Column(name = "ranking")
    private String ranking;

    @Column(name = "remarks")
    private String remarks;
}

