package com.se445g.SE_445_G_ETL.entity.staging;

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
@Table(name = "stg_department_performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_DepartmentPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deptPerfId;

    private Integer departmentId;

    private Integer reviewId;

    private Double averageScore;

    private String ranking;

    private String remarks;

}
