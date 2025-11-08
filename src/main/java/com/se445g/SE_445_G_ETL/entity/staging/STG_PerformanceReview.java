package com.se445g.SE_445_G_ETL.entity.staging;

import java.time.LocalDate;

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
@Table(name = "stg_performance_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    private String period;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
