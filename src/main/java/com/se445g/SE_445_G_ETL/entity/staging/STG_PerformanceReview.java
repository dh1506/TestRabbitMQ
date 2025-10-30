package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stg_performance_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_PerformanceReview {

    @Id
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(nullable = false, length = 20)
    private String period;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private String description;
}
