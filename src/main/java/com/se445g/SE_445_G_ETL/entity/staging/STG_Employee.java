package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDate;

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
@Table(name = "stg_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_Employee {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 10)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 100)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private STG_Department department;
}
