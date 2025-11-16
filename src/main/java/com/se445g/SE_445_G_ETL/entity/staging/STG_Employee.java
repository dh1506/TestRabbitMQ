package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "stg_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_Employee implements Persistable<Integer> {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private STG_Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<STG_Salary> salaries;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public Integer getId() {
        return this.employeeId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostLoad
    @PrePersist
    void markNotNew() {
        this.isNew = false;
    }
}