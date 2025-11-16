package com.se445g.SE_445_G_ETL.mapper;

import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.entity.staging.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    // MAPPINGS: ENTITY -> DTO (For Producer)
    @Mapping(source = "description", target = "reviewDescription")
    @Mapping(target = "averageScore", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "deptPerfId", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "employeePerformanceId", ignore = true)
    @Mapping(target = "kpiDescription", ignore = true)
    @Mapping(target = "kpiId", ignore = true)
    @Mapping(target = "kpiName", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "ranking", ignore = true)
    @Mapping(target = "recordType", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskName", ignore = true)
    @Mapping(target = "taskScore", ignore = true)
    @Mapping(target = "weight", ignore = true)
    PerformanceDTO reviewToDto(STG_PerformanceReview entity);

    @Mapping(source = "id", target = "employeePerformanceId")
    @Mapping(target = "averageScore", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "deptPerfId", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "kpiDescription", ignore = true)
    @Mapping(target = "kpiId", ignore = true)
    @Mapping(target = "kpiName", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "period", ignore = true)
    @Mapping(target = "ranking", ignore = true)
    @Mapping(target = "recordType", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "reviewDescription", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskName", ignore = true)
    @Mapping(target = "taskScore", ignore = true)
    @Mapping(target = "weight", ignore = true)
    PerformanceDTO employeePerformanceToDto(STG_EmployeePerformance entity);

    @Mapping(target = "averageScore", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "deptPerfId", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "kpiDescription", ignore = true)
    @Mapping(target = "kpiId", ignore = true)
    @Mapping(target = "kpiName", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "period", ignore = true)
    @Mapping(target = "ranking", ignore = true)
    @Mapping(target = "recordType", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "reviewDescription", ignore = true)
    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "weight", ignore = true)
    PerformanceDTO taskPerformanceToDto(STG_TaskPerformance entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "employeePerformanceId", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "kpiDescription", ignore = true)
    @Mapping(target = "kpiId", ignore = true)
    @Mapping(target = "kpiName", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "period", ignore = true)
    @Mapping(target = "recordType", ignore = true)
    @Mapping(target = "reviewDescription", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskName", ignore = true)
    @Mapping(target = "taskScore", ignore = true)
    @Mapping(target = "weight", ignore = true)
    PerformanceDTO departmentPerformanceToDto(STG_DepartmentPerformance entity);

    @Mapping(source = "description", target = "kpiDescription")
    @Mapping(target = "averageScore", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "deptPerfId", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "employeePerformanceId", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "period", ignore = true)
    @Mapping(target = "ranking", ignore = true)
    @Mapping(target = "recordType", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "reviewDescription", ignore = true)
    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskName", ignore = true)
    @Mapping(target = "taskScore", ignore = true)
    PerformanceDTO kpiMetricsToDto(STG_KpiMetrics entity);

    // MAPPINGS: DTO -> ENTITY (For Consumer)
    @Mapping(source = "reviewDescription", target = "description")
    STG_PerformanceReview dtoToReview(PerformanceDTO dto);

    @Mapping(source = "employeePerformanceId", target = "id")
    STG_EmployeePerformance dtoToEmployeePerformance(PerformanceDTO dto);

    STG_TaskPerformance dtoToTaskPerformance(PerformanceDTO dto);

    STG_DepartmentPerformance dtoToDepartmentPerformance(PerformanceDTO dto);

    @Mapping(source = "kpiDescription", target = "description")
    STG_KpiMetrics dtoToKpiMetrics(PerformanceDTO dto);
}