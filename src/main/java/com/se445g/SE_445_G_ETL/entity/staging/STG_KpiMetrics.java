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
@Table(name = "stg_kpi_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STG_KpiMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kpiId;

    private String kpiName;

    private String description;

    private Double weight;

    private String category;
}
