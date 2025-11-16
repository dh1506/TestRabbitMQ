package com.se445g.SE_445_G_ETL.entity.staging;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "stg_departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_Department implements Persistable<Integer> {

    @Id
    private Integer departmentId;

    private String name;
    private String location;
    private String phone;
    private BigDecimal budgetVnd;
    private Integer managerId;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<STG_Employee> employees;

    @Transient // Đánh dấu để JPA không lưu trường này vào DB
    @Builder.Default
    private boolean isNew = true;

    @Override
    public Integer getId() {
        return this.departmentId;
    }

    @Override
    public boolean isNew() {
        // Luôn trả về true vì luồng này chỉ insert
        return this.isNew;
    }

    @PostLoad // Được gọi sau khi một entity được load từ DB
    @PrePersist // Được gọi trước khi một entity được lưu (lần đầu)
    void markNotNew() {
        this.isNew = false;
    }
}