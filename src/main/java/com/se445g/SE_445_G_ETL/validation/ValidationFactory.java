package com.se445g.SE_445_G_ETL.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.handler.BusinessValidationHandler;
import com.se445g.SE_445_G_ETL.handler.FormatValidationHandler;
import com.se445g.SE_445_G_ETL.handler.ReferenceValidationHandler;
// import com.se445g.SE_445_G_ETL.handler.ValidationHandler;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;
import com.se445g.SE_445_G_ETL.validation.composite.ValidationRuleGroup;
import com.se445g.SE_445_G_ETL.validation.leaf.NotNullRule;
import com.se445g.SE_445_G_ETL.validation.leaf.StringLengthRule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationFactory {

    // Key: Record Type | Value: Map<Tên Handler,RuleGroup>
    private final Map<String, RuleConfiguration> configurations = new HashMap<>();

    private static final String TYPE_DEPT = "DEPARTMENT";
    private static final String TYPE_EMP = "EMPLOYEE";
    // private static final String TYPE_SALARY = "SALARY";

    private record RuleConfiguration(
            Consumer<ValidationRuleGroup<EmployeeDTO>> formatRules,
            Consumer<ValidationRuleGroup<EmployeeDTO>> businessRules,
            Consumer<ValidationRuleGroup<EmployeeDTO>> referenceRules) {
    }

    @PostConstruct
    public void init() {
        // 1. DEPARTMENT CONFIGURATION
        configurations.put(TYPE_DEPT, new RuleConfiguration(
                this::buildDepartmentFormatRules,
                group -> {
                }, // Business (Empty)
                group -> {
                } // Reference (Empty)
        ));

        // 2. EMPLOYEE CONFIGURATION
        configurations.put(TYPE_EMP, new RuleConfiguration(
                this::buildEmployeeFormatRules,
                this::buildEmployeeBusinessRules,
                group -> {
                }));

        // 3. SALARY CONFIGURATION
        // configurations.put(TYPE_SALARY, new RuleConfiguration(
        //         this::buildSalaryFormatRules,
        //         this::buildSalaryBusinessRules,
        //         group -> {
        //         }));
    }

    public ValidationRule<EmployeeDTO> getChain(String recordType) {
        RuleConfiguration config = configurations.get(recordType);

        if (config == null) {
            return null; // Không tìm thấy cấu hình cho recordType
        }

        // Tạo các Handler để có Chain độc lập
        FormatValidationHandler<EmployeeDTO> formatHandler = new FormatValidationHandler<>();
        BusinessValidationHandler<EmployeeDTO> businessHandler = new BusinessValidationHandler<>();
        ReferenceValidationHandler<EmployeeDTO> referenceHandler = new ReferenceValidationHandler<>();

        // Cấu hình Rule Group (Composite) cho từng Handler
        config.formatRules().accept(formatHandler.getValidationRuleGroup());
        config.businessRules().accept(businessHandler.getValidationRuleGroup());
        config.referenceRules().accept(referenceHandler.getValidationRuleGroup());

        // Thiết lập Chain of Responsibility
        formatHandler.setNext(businessHandler);
        businessHandler.setNext(referenceHandler);

        return formatHandler;
    }

    private void buildDepartmentFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
        group.addRule(new NotNullRule<>("departmentId", EmployeeDTO::getDepartmentId));
        group.addRule(new NotNullRule<>("departmentName", EmployeeDTO::getDepartmentName));
    }

    private void buildEmployeeFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
        group.addRule(new NotNullRule<>("employeeId", EmployeeDTO::getEmployeeId));
        group.addRule(new NotNullRule<>("fullName", EmployeeDTO::getFullName));
        group.addRule(new StringLengthRule<>("fullName", EmployeeDTO::getFullName, 5, 100));
    }

    private void buildEmployeeBusinessRules(ValidationRuleGroup<EmployeeDTO> group) {
        // Business Rule cho nhân viên (ví dụ: tuổi)
        // group.addRule(new EmployeeAgeRule());
    }

    // private void buildSalaryFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
    //     group.addRule(new NotNullRule<>("amountVnd", EmployeeDTO::getAmountVnd));
    //     group.addRule(new NotNullRule<>("employeeId", EmployeeDTO::getEmployeeId));
    // }

    // private void buildSalaryBusinessRules(ValidationRuleGroup<EmployeeDTO> group) {
    //     group.addRule(new BusinessRuleExample());
    // }
}