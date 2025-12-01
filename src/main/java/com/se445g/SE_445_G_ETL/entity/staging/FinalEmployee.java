package com.se445g.SE_445_G_ETL.entity.staging;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "final_employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalEmployee {

    @Id
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

    private String status;

    private Integer departmentId;
}
