package com.se445g.SE_445_G_ETL.service.impl;

import com.se445g.SE_445_G_ETL.entity.staging.*;
import com.se445g.SE_445_G_ETL.entity.target.*;
import com.se445g.SE_445_G_ETL.repository.staging.*;
import com.se445g.SE_445_G_ETL.repository.target.*;
import com.se445g.SE_445_G_ETL.service.interf.LogService;
import com.se445g.SE_445_G_ETL.service.interf.TargetTransformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TargetTransformServiceImpl implements TargetTransformService {

    private static final String JOB_TYPE = "STAGING_TO_TARGET";

    // Staging repositories
    private final STG_DepartmentRepository stgDepartmentRepository;
    private final STG_EmployeeRepository stgEmployeeRepository;
    private final STG_SalaryRepository stgSalaryRepository;
    private final STG_PerformanceReviewRepository stgPerformanceReviewRepository;
    private final STG_EmployeePerformanceRepository stgEmployeePerformanceRepository;
    private final STG_DepartmentPerformanceRepository stgDepartmentPerformanceRepository;
    private final STG_KpiMetricsRepository stgKpiMetricsRepository;
    private final STG_TaskPerformanceRepository stgTaskPerformanceRepository;

    // Target repositories
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeePerformanceRepository employeePerformanceRepository;
    private final DepartmentPerformanceRepository departmentPerformanceRepository;
    private final KpiMetricRepository kpiMetricRepository;
    private final TaskPerformanceRepository taskPerformanceRepository;

    private final LogService logService;

    /**
     * Copy toàn bộ dữ liệu từ schema staging sang schema target (hr_target schema).
     * Lưu ý: sử dụng cùng ID từ staging để bảo toàn quan hệ khóa ngoại.
     */
    @Override
    @Transactional(transactionManager = "targetTransactionManager")
    public void loadToTarget() {
        String runId = logService.generateRunId();
        logService.logStart(JOB_TYPE, runId, "Bắt đầu transform dữ liệu từ staging sang target.");

        AtomicInteger totalCount = new AtomicInteger(0);
        try {
            // 1. Departments
            List<Department> departments = stgDepartmentRepository.findAll().stream()
                    .map(this::mapDepartment)
                    .toList();
            departmentRepository.saveAll(departments);
            totalCount.addAndGet(departments.size());

            // 2. Employees
            List<Employee> employees = stgEmployeeRepository.findAll().stream()
                    .map(this::mapEmployee)
                    .toList();
            employeeRepository.saveAll(employees);
            totalCount.addAndGet(employees.size());

            // 3. Salaries
            List<Salary> salaries = stgSalaryRepository.findAll().stream()
                    .map(this::mapSalary)
                    .toList();
            salaryRepository.saveAll(salaries);
            totalCount.addAndGet(salaries.size());

            // 4. Performance Review
            List<PerformanceReview> reviews = stgPerformanceReviewRepository.findAll().stream()
                    .map(this::mapReview)
                    .toList();
            performanceReviewRepository.saveAll(reviews);
            totalCount.addAndGet(reviews.size());

            // 5. Employee Performance
            List<EmployeePerformance> empPerformances = stgEmployeePerformanceRepository.findAll().stream()
                    .map(this::mapEmployeePerformance)
                    .toList();
            employeePerformanceRepository.saveAll(empPerformances);
            totalCount.addAndGet(empPerformances.size());

            // 6. Department Performance
            List<DepartmentPerformance> deptPerformances = stgDepartmentPerformanceRepository.findAll().stream()
                    .map(this::mapDepartmentPerformance)
                    .toList();
            departmentPerformanceRepository.saveAll(deptPerformances);
            totalCount.addAndGet(deptPerformances.size());

            // 7. KPI Metrics
            List<KpiMetric> kpiMetrics = stgKpiMetricsRepository.findAll().stream()
                    .map(this::mapKpiMetric)
                    .toList();
            kpiMetricRepository.saveAll(kpiMetrics);
            totalCount.addAndGet(kpiMetrics.size());

            // 8. Task Performance
            List<TaskPerformance> taskPerformances = stgTaskPerformanceRepository.findAll().stream()
                    .map(this::mapTaskPerformance)
                    .toList();
            taskPerformanceRepository.saveAll(taskPerformances);
            totalCount.addAndGet(taskPerformances.size());

            logService.logSuccess(JOB_TYPE, runId,
                    "Hoàn thành transform staging -> target.", totalCount.get());
        } catch (Exception ex) {
            log.error("Lỗi khi transform staging -> target: {}", ex.getMessage(), ex);
            logService.logFailure(JOB_TYPE, runId, "Lỗi khi transform staging -> target", ex.getMessage());
            throw ex;
        }
    }

    private Department mapDepartment(STG_Department src) {
        return Department.builder()
                .departmentId(src.getDepartmentId())
                .departmentName(src.getName())
                .managerId(src.getManagerId())
                .location(src.getLocation())
                .build();
    }

    private Employee mapEmployee(STG_Employee src) {
        NameParts parts = splitName(src.getFullName());
        return Employee.builder()
                .employeeId(src.getEmployeeId())
                .firstName(parts.firstName())
                .lastName(parts.lastName())
                .gender(normalizeGender(src.getGender()))
                .birthDate(src.getDateOfBirth())
                .hireDate(src.getHireDate())
                .departmentId(src.getDepartment() != null ? src.getDepartment().getDepartmentId() : null)
                .email(src.getEmail())
                .phoneNumber(src.getPhone())
                .build();
    }

    private Salary mapSalary(STG_Salary src) {
        return Salary.builder()
                .salaryId(src.getSalaryId())
                .employeeId(src.getEmployee() != null ? src.getEmployee().getEmployeeId() : null)
                .baseSalary(src.getAmountVnd())
                .bonus(src.getBonusVnd())
                .effectiveDate(src.getEffectiveFrom())
                .build();
    }

    private PerformanceReview mapReview(STG_PerformanceReview src) {
        return PerformanceReview.builder()
                .reviewId(src.getReviewId())
                .period(src.getPeriod())
                .startDate(src.getStartDate())
                .endDate(src.getEndDate())
                .description(src.getDescription())
                .build();
    }

    private EmployeePerformance mapEmployeePerformance(STG_EmployeePerformance src) {
        return EmployeePerformance.builder()
                .id(src.getId())
                .employeeId(src.getEmployeeId())
                .reviewId(src.getReviewId())
                .performanceScore(src.getPerformanceScore() != null
                        ? BigDecimal.valueOf(src.getPerformanceScore())
                        : null)
                .comments(src.getComments())
                .createdAt(src.getCreatedAt())
                .build();
    }

    private DepartmentPerformance mapDepartmentPerformance(STG_DepartmentPerformance src) {
        return DepartmentPerformance.builder()
                .deptPerfId(src.getDeptPerfId())
                .departmentId(src.getDepartmentId())
                .reviewId(src.getReviewId())
                .averageScore(src.getAverageScore() != null
                        ? BigDecimal.valueOf(src.getAverageScore())
                        : null)
                .ranking(mapRankingToText(src.getRanking()))
                .remarks(src.getRemarks())
                .build();
    }

    private KpiMetric mapKpiMetric(STG_KpiMetrics src) {
        return KpiMetric.builder()
                .kpiId(src.getKpiId())
                .kpiName(src.getKpiName())
                .description(src.getDescription())
                .weight(src.getWeight())
                .category(src.getCategory())
                .build();
    }

    private TaskPerformance mapTaskPerformance(STG_TaskPerformance src) {
        return TaskPerformance.builder()
                .taskId(src.getTaskId())
                .employeePerformanceId(src.getEmployeePerformanceId())
                .taskName(src.getTaskName())
                .taskScore(src.getTaskScore() != null ? src.getTaskScore().doubleValue() : null)
                .note(src.getNote())
                .build();
    }

    private record NameParts(String firstName, String lastName) {}

    private NameParts splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new NameParts(null, null);
        }
        String trimmed = fullName.trim();
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new NameParts(parts[0], parts[0]);
        }
        String first = parts[parts.length - 1];
        String last = String.join(" ", Arrays.copyOf(parts, parts.length - 1));
        return new NameParts(first, last);
    }

    private String normalizeGender(String gender) {
        if (gender == null) return "O";
        String normalized = gender.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "nam", "male", "m" -> "M";
            case "nữ", "nu", "female", "f" -> "F";
            default -> "O";
        };
    }

    /**
     * Staging đang lưu ranking dạng số (0-5). Convert sang text lưu vào target.
     * 5=A, 4=B, 3=C, 2=D, 1=E, 0=F. Nếu ngoài range -> null.
     */
    private String mapRankingToText(String rankingNumber) {
        if (rankingNumber == null || rankingNumber.isBlank()) return null;
        try {
            int num = Integer.parseInt(rankingNumber.trim());
            return switch (num) {
                case 5 -> "A";
                case 4 -> "B";
                case 3 -> "C";
                case 2 -> "D";
                case 1 -> "E";
                case 0 -> "F";
                default -> null;
            };
        } catch (NumberFormatException ex) {
            log.warn("Ranking không phải số hợp lệ: {}", rankingNumber);
            return null;
        }
    }
}

