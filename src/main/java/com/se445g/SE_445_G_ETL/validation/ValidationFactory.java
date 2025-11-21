package com.se445g.SE_445_G_ETL.validation;

import java.math.BigDecimal; // Import quan trọng cho BigDecimal
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.handler.BusinessValidationHandler;
import com.se445g.SE_445_G_ETL.handler.FormatValidationHandler;
import com.se445g.SE_445_G_ETL.handler.ReferenceValidationHandler;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;
import com.se445g.SE_445_G_ETL.validation.composite.ValidationRuleGroup;
// Import tất cả các Leaf Rules bạn đã tạo
import com.se445g.SE_445_G_ETL.validation.leaf.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationFactory {

    private final Map<String, RuleConfiguration> configurations = new HashMap<>();

    private static final String TYPE_DEPT = "DEPARTMENT";
    private static final String TYPE_EMP = "EMPLOYEE";
    private static final String TYPE_SALARY = "SALARY"; // Đã bỏ comment

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
                this::buildDepartmentBusinessRules, // Đã thêm Business Rules
                group -> {} // Reference (Tạm để trống hoặc thêm ForeignKeyRule)
        ));

        // 2. EMPLOYEE CONFIGURATION
        configurations.put(TYPE_EMP, new RuleConfiguration(
                this::buildEmployeeFormatRules,
                this::buildEmployeeBusinessRules,
                group -> {}
        ));

        // 3. SALARY CONFIGURATION (Đã mở comment và map hàm)
        configurations.put(TYPE_SALARY, new RuleConfiguration(
                this::buildSalaryFormatRules,
                this::buildSalaryBusinessRules,
                group -> {}
        ));
    }

    public ValidationRule<EmployeeDTO> getChain(String recordType) {
        RuleConfiguration config = configurations.get(recordType);
        if (config == null) return null;

        FormatValidationHandler<EmployeeDTO> formatHandler = new FormatValidationHandler<>();
        BusinessValidationHandler<EmployeeDTO> businessHandler = new BusinessValidationHandler<>();
        ReferenceValidationHandler<EmployeeDTO> referenceHandler = new ReferenceValidationHandler<>();

        config.formatRules().accept(formatHandler.getValidationRuleGroup());
        config.businessRules().accept(businessHandler.getValidationRuleGroup());
        config.referenceRules().accept(referenceHandler.getValidationRuleGroup());

        formatHandler.setNext(businessHandler);
        businessHandler.setNext(referenceHandler);

        return formatHandler;
    }

    // =================================================================
    //                      DEPARTMENT RULES
    // =================================================================

    private void buildDepartmentFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
        // ID & Name
        group.addRule(new NotNullRule<>("departmentId", EmployeeDTO::getDepartmentId));
        group.addRule(new NotNullRule<>("departmentName", EmployeeDTO::getDepartmentName));

        // Regex cho Tên phòng ban (Chữ, số, &, dấu chấm, gạch ngang)
        group.addRule(new RegexRule<>(
                "departmentName", EmployeeDTO::getDepartmentName,
                "^[\\p{L}\\p{N}\\s&.-]+$",
                "Tên phòng ban chứa ký tự không hợp lệ."
        ));

        // Regex cho Phone (10-11 số)
        group.addRule(new RegexRule<>(
                "departmentPhone", EmployeeDTO::getDepartmentPhone,
                "^[0-9]{10,11}$",
                "SĐT phòng ban phải là số (10-11 chữ số)."
        ));

        // Regex cho Location (Địa chỉ chấp nhận nhiều dấu câu)
        group.addRule(new NotNullRule<>("departmentLocation", EmployeeDTO::getDepartmentLocation));
        group.addRule(new RegexRule<>(
                "departmentLocation", EmployeeDTO::getDepartmentLocation,
                "^[\\p{L}\\p{N}\\s,./-]+$",
                "Địa chỉ phòng ban chứa ký tự đặc biệt."
        ));

        // Format check cho Budget (Chỉ check NotNull, chưa check giá trị âm dương)
        group.addRule(new NotNullRule<>("departmentBudgetVnd", EmployeeDTO::getDepartmentBudgetVnd));
    }

    private void buildDepartmentBusinessRules(ValidationRuleGroup<EmployeeDTO> group) {
        // Check Ngân sách >= 0 và <= 1000 Tỷ
        // Sử dụng BigDecimalRangeRule (Rule này quan trọng vì DTO dùng BigDecimal)
        group.addRule(new BigDecimalRangeRule<>(
                "departmentBudgetVnd",
                EmployeeDTO::getDepartmentBudgetVnd,
                "0",               // Min String
                "1000000000000"    // Max String (1 triệu tỷ)
        ));
    }

    // =================================================================
    //                      EMPLOYEE RULES
    // =================================================================

    private void buildEmployeeFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
        group.addRule(new NotNullRule<>("employeeId", EmployeeDTO::getEmployeeId));

        // Fullname
        group.addRule(new NotNullRule<>("fullName", EmployeeDTO::getFullName));
        group.addRule(new StringLengthRule<>("fullName", EmployeeDTO::getFullName, 2, 100));
        group.addRule(new RegexRule<>(
                "fullName", EmployeeDTO::getFullName,
                "^[\\p{L}\\s\\-']+$",
                "Họ tên chứa ký tự không hợp lệ."
        ));

        // Gender (M, F, O)
        group.addRule(new RegexRule<>(
                "gender", EmployeeDTO::getGender,
                "^(M|F|O)$",
                "Giới tính phải là: M, F, O."
        ));

        // Date NotNull
        group.addRule(new NotNullRule<>("dateOfBirth", EmployeeDTO::getDateOfBirth));
        group.addRule(new NotNullRule<>("hireDate", EmployeeDTO::getHireDate));

        // Phone VN format
        group.addRule(new RegexRule<>(
                "phone", EmployeeDTO::getPhone,
                "^0\\d{9}$",
                "Số điện thoại không hợp lệ."
        ));

        // Email format
        group.addRule(new RegexRule<>(
                "email", EmployeeDTO::getEmail,
                "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
                "Email không đúng định dạng."
        ));

        // Check Status theo List cứng
        group.addRule(new AllowedValuesRule<>(
                "employeeStatus", EmployeeDTO::getEmployeeStatus,
                "ACTIVE", "PROBATION", "RESIGNED", "TERMINATED", "ON_LEAVE"
        ));

        // Hometown
        group.addRule(new RegexRule<>(
                "hometown", EmployeeDTO::getHometown,
                "^[\\p{L}\\p{N}\\s,./-]+$",
                "Quê quán chứa ký tự đặc biệt."
        ));
    }

    private void buildEmployeeBusinessRules(ValidationRuleGroup<EmployeeDTO> group) {
        // Logic: Ngày sinh < Ngày vào làm
        group.addRule(new DateComparisonRule<>(
                EmployeeDTO::getDateOfBirth,
                EmployeeDTO::getHireDate,
                "Lỗi Logic: Ngày sinh phải trước ngày vào làm."
        ));
    }

    // =================================================================
    //                      SALARY RULES (MỚI THÊM)
    // =================================================================

    private void buildSalaryFormatRules(ValidationRuleGroup<EmployeeDTO> group) {
        // 1. Check ID & Lương cơ bản (Bắt buộc)
        group.addRule(new NotNullRule<>("salaryId", EmployeeDTO::getSalaryId));
        group.addRule(new NotNullRule<>("amountVnd", EmployeeDTO::getAmountVnd));

        // 2. Check Đơn vị tiền tệ (Currency)
        // Dùng AllowedValuesRule thay vì Regex để kiểm soát chặt chẽ các mã tiền tệ hỗ trợ
        group.addRule(new AllowedValuesRule<>(
                "currency",
                EmployeeDTO::getCurrency,
                "VND", "USD", "EUR", "JPY" // Danh sách ISO 4217 cho phép
        ));

        // 3. Check Tần suất trả lương (Pay Frequency)
        group.addRule(new AllowedValuesRule<>(
                "payFrequency",
                EmployeeDTO::getPayFrequency,
                "MONTHLY", "WEEKLY", "BI-WEEKLY", "HOURLY"
        ));

        // 4. Check Ngày hiệu lực
        group.addRule(new NotNullRule<>("effectiveFrom", EmployeeDTO::getEffectiveFrom));
    }

    private void buildSalaryBusinessRules(ValidationRuleGroup<EmployeeDTO> group) {
        group.addRule(new BigDecimalRangeRule<>(
                "amountVnd",
                EmployeeDTO::getAmountVnd,
                "4000000",          // Min (Dùng String để đảm bảo độ chính xác cho BigDecimal)
                "2000000000"        // Max (Ví dụ: 2 tỷ)
        ));

        // 2. Logic Thưởng (Bonus) - Xử lý Optional
        // Rule BigDecimalRangeRule tự động bỏ qua nếu giá trị là null.
        // Nhưng nếu có giá trị, nó phải >= 0.
        group.addRule(new BigDecimalRangeRule<>(
                "bonusVnd",
                EmployeeDTO::getBonusVnd,
                "0",                // Min = 0 (Không được thưởng âm)
                "10000000000"       // Max
        ));

        // 3. Logic Thời gian hiệu lực (From < To)
        // Rule DateComparisonRule sẽ kiểm tra: Nếu cả 2 ngày đều không null thì From phải < To.
        // Nếu effectiveTo là null (tức là đến hiện tại), rule này sẽ bỏ qua (Valid).
        group.addRule(new DateComparisonRule<>(
                EmployeeDTO::getEffectiveFrom,
                EmployeeDTO::getEffectiveTo,
                "Lỗi Logic Lương: Ngày bắt đầu hiệu lực phải trước ngày kết thúc."
        ));
    }
}