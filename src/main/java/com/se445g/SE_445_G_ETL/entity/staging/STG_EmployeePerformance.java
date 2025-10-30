package com.se445g.SE_445_G_ETL.entity.staging;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stg_employee_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_EmployeePerformance {

    @Id
    private Integer id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId; // Liên kết đến hệ thống staging

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private STG_PerformanceReview review;

    @Column(name = "performance_score", precision = 4, scale = 2)
    private BigDecimal performanceScore;

    private String comments;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "employeePerformance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<STG_TaskPerformance> tasks;
}
