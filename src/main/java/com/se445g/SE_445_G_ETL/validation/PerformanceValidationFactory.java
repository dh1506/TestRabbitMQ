package com.se445g.SE_445_G_ETL.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.se445g.SE_445_G_ETL.dto.PerformanceDTO; // Import DTO mới
import com.se445g.SE_445_G_ETL.handler.BusinessValidationHandler;
import com.se445g.SE_445_G_ETL.handler.FormatValidationHandler;
import com.se445g.SE_445_G_ETL.handler.ReferenceValidationHandler;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;
import com.se445g.SE_445_G_ETL.validation.composite.ValidationRuleGroup;
import com.se445g.SE_445_G_ETL.validation.leaf.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceValidationFactory {

    // Map cấu hình dành riêng cho PerformanceDTO
    private final Map<String, RuleConfiguration> configurations = new HashMap<>();

    // Định nghĩa các loại Record Type khớp với DTO
    private static final String TYPE_REVIEW = "REVIEW";
    private static final String TYPE_EMP_PERF = "EMPLOYEE_PERFORMANCE";
    private static final String TYPE_TASK_PERF = "TASK_PERFORMANCE";
    private static final String TYPE_DEPT_PERF = "DEPT_PERFORMANCE";
    private static final String TYPE_KPI = "KPI_METRIC";

    private record RuleConfiguration(
            Consumer<ValidationRuleGroup<PerformanceDTO>> formatRules,
            Consumer<ValidationRuleGroup<PerformanceDTO>> businessRules,
            Consumer<ValidationRuleGroup<PerformanceDTO>> referenceRules) {
    }

    @PostConstruct
    public void init() {
        // 1. Cấu hình KPI METRICS
        configurations.put(TYPE_KPI, new RuleConfiguration(
                this::buildKpiFormatRules,
                this::buildKpiBusinessRules,
                group -> {}
        ));

        // 2. Cấu hình REVIEW (Kỳ đánh giá)
        configurations.put(TYPE_REVIEW, new RuleConfiguration(
                this::buildReviewFormatRules,
                this::buildReviewBusinessRules,
                group -> {}
        ));

        // 3. Cấu hình EMPLOYEE PERFORMANCE
        configurations.put(TYPE_EMP_PERF, new RuleConfiguration(
                this::buildEmpPerfFormatRules,
                this::buildEmpPerfBusinessRules,
                this::buildEmpPerfRefRules
        ));

        // 4. Cấu hình TASK PERFORMANCE
        configurations.put(TYPE_TASK_PERF, new RuleConfiguration(
                this::buildTaskFormatRules,
                this::buildTaskBusinessRules,
                group -> {}
        ));

        // 5. Cấu hình DEPT PERFORMANCE
        configurations.put(TYPE_DEPT_PERF, new RuleConfiguration(
                this::buildDeptPerfFormatRules,
                this::buildDeptPerfBusinessRules,
                this::buildDeptPerfRefRules
        ));
    }

    public ValidationRule<PerformanceDTO> getChain(String recordType) {
        RuleConfiguration config = configurations.get(recordType);
        if (config == null) return null;

        // Tạo Handler mới với Generics <PerformanceDTO>
        FormatValidationHandler<PerformanceDTO> formatHandler = new FormatValidationHandler<>();
        BusinessValidationHandler<PerformanceDTO> businessHandler = new BusinessValidationHandler<>();
        ReferenceValidationHandler<PerformanceDTO> referenceHandler = new ReferenceValidationHandler<>();

        config.formatRules().accept(formatHandler.getValidationRuleGroup());
        config.businessRules().accept(businessHandler.getValidationRuleGroup());
        config.referenceRules().accept(referenceHandler.getValidationRuleGroup());

        formatHandler.setNext(businessHandler);
        businessHandler.setNext(referenceHandler);

        return formatHandler;
    }

    // =================================================================
    //                  1. KPI METRICS RULES
    // =================================================================
    private void buildKpiFormatRules(ValidationRuleGroup<PerformanceDTO> group) {
        group.addRule(new NotNullRule<>("kpiId", PerformanceDTO::getKpiId));
        group.addRule(new NotNullRule<>("kpiName", PerformanceDTO::getKpiName));

        // Category: Ví dụ chỉ cho phép chữ in hoa và gạch dưới (SALES_TARGET)
        group.addRule(new RegexRule<>(
                "category", PerformanceDTO::getCategory,
                "^[A-Z0-9_]+$",
                "Category KPI phải viết hoa, không dấu."
        ));
    }

    private void buildKpiBusinessRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Trọng số (Weight): Thường là 0.0 đến 1.0 (hoặc 0-100)
        // Dùng NumericRangeRule (class này handle được cả Double lẫn Integer)
        group.addRule(new NumericRangeRule<>(
                "weight", PerformanceDTO::getWeight,
                0.0, 1.0
        ));
    }

    // =================================================================
    //                  2. REVIEW RULES (Kỳ đánh giá)
    // =================================================================
    private void buildReviewFormatRules(ValidationRuleGroup<PerformanceDTO> group) {
        group.addRule(new NotNullRule<>("reviewId", PerformanceDTO::getReviewId));

        // Period: Ví dụ format "Q1-2024" hoặc "2024"
        group.addRule(new RegexRule<>(
                "period", PerformanceDTO::getPeriod,
                "^Q[1-4]-\\d{4}$", // Regex: Q1-2024, Q2-2025...
                "Kỳ đánh giá phải theo định dạng Qx-YYYY (Ví dụ: Q1-2024)."
        ));
    }

    private void buildReviewBusinessRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Logic ngày tháng: Start < End
        group.addRule(new DateComparisonRule<>(
                PerformanceDTO::getStartDate,
                PerformanceDTO::getEndDate,
                "Ngày bắt đầu kỳ đánh giá phải trước ngày kết thúc."
        ));
    }

    // =================================================================
    //                  3. EMPLOYEE PERFORMANCE RULES
    // =================================================================
    private void buildEmpPerfFormatRules(ValidationRuleGroup<PerformanceDTO> group) {
        group.addRule(new NotNullRule<>("employeePerformanceId", PerformanceDTO::getEmployeePerformanceId));
        group.addRule(new NotNullRule<>("employeeId", PerformanceDTO::getEmployeeId));
        group.addRule(new NotNullRule<>("performanceScore", PerformanceDTO::getPerformanceScore));
    }

    private void buildEmpPerfBusinessRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Điểm số nhân viên: 0 - 100 (Integer)
        // NumericRangeRule tự động hiểu Integer -> double để so sánh
        group.addRule(new NumericRangeRule<>(
                "performanceScore", PerformanceDTO::getPerformanceScore,
                0.0, 100.0
        ));
    }

    private void buildEmpPerfRefRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Check xem employeeId có tồn tại trong hệ thống HR không
        // Cần inject cache hoặc service vào đây như bài trước
        /*
        group.addRule(new ForeignKeyRule<>(
             "employeeId", PerformanceDTO::getEmployeeId,
             (id) -> validEmployeeIdsCache.contains(id),
             "Nhân viên"
        ));
        */
    }

    // =================================================================
    //                  4. DEPARTMENT PERFORMANCE RULES
    // =================================================================
    private void buildDeptPerfFormatRules(ValidationRuleGroup<PerformanceDTO> group) {
        group.addRule(new NotNullRule<>("deptPerfId", PerformanceDTO::getDeptPerfId));

        // Ranking: Xếp loại A, B, C, D, F
        group.addRule(new RegexRule<>(
                "ranking", PerformanceDTO::getRanking,
                "^[A-F]$",
                "Xếp loại phòng ban chỉ chấp nhận từ A đến F."
        ));
    }

    private void buildDeptPerfBusinessRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Điểm trung bình (Double): 0.0 - 10.0 (hoặc 100)
        group.addRule(new NumericRangeRule<>(
                "averageScore", PerformanceDTO::getAverageScore,
                0.0, 10.0
        ));
    }

    private void buildDeptPerfRefRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Check departmentId có tồn tại không
        /*
        group.addRule(new ForeignKeyRule<>(
             "departmentId", PerformanceDTO::getDepartmentId,
             (id) -> validDeptIdsCache.contains(id),
             "Phòng ban"
        ));
        */
    }

    // =================================================================
    //                  5. TASK PERFORMANCE RULES
    // =================================================================
    private void buildTaskFormatRules(ValidationRuleGroup<PerformanceDTO> group) {
        group.addRule(new NotNullRule<>("taskId", PerformanceDTO::getTaskId));
        group.addRule(new NotNullRule<>("taskName", PerformanceDTO::getTaskName));

        // Check note không quá dài
        group.addRule(new StringLengthRule<>("note", PerformanceDTO::getNote, 0, 500));
    }

    private void buildTaskBusinessRules(ValidationRuleGroup<PerformanceDTO> group) {
        // Điểm Task: 0 - 100
        group.addRule(new NumericRangeRule<>(
                "taskScore", PerformanceDTO::getTaskScore,
                0.0, 100.0
        ));
    }
}