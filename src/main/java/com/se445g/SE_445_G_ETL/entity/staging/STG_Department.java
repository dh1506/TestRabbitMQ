package com.se445g.SE_445_G_ETL.entity.staging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stg_departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_Department {

    @Id
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @Column(length = 100)
    private String location;
}
