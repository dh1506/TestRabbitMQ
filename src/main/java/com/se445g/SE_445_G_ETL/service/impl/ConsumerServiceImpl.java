package com.se445g.SE_445_G_ETL.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.entity.staging.*;
import com.se445g.SE_445_G_ETL.mapper.EmployeeMapper;
import com.se445g.SE_445_G_ETL.mapper.PerformanceMapper;
import com.se445g.SE_445_G_ETL.repository.staging.*;
import com.se445g.SE_445_G_ETL.service.interf.ConsumerService;
import com.se445g.SE_445_G_ETL.service.interf.LogService; // <-- IMPORT MỚI
import com.se445g.SE_445_G_ETL.validation.ValidationFactory;
import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    // Mapper
    private final EmployeeMapper employeeMapper;
    private final PerformanceMapper performanceMapper;

    // Repositories
    private final STG_DepartmentRepository departmentRepository;
    private final STG_EmployeeRepository employeeRepository;
    private final STG_SalaryRepository salaryRepository;

    private final STG_PerformanceReviewRepository reviewRepository;
    private final STG_EmployeePerformanceRepository employeePerformanceRepository;
    private final STG_TaskPerformanceRepository taskPerformanceRepository;
    private final STG_DepartmentPerformanceRepository departmentPerformanceRepository;
    private final STG_KpiMetricsRepository kpiMetricsRepository;

    // Validation Factory
    private final ValidationFactory validationFactory;
    
    // Error Record Repository
    private final STG_ErrorRecordRepository errorRecordRepository;
    private final ObjectMapper objectMapper;
    
    private final LogService logService; // <-- LOG SERVICE MỚI

    private static final String JOB_TYPE_CSV = "CSV_CONSUMER";
    private static final String JOB_TYPE_PERF = "PERF_CONSUMER";

    /**
     * Consumer xử lý tin nhắn từ CSV (Employee, Department, Salary)
     */
    @RabbitListener(queues = RabbitMQConfig.EMPLOYEES_QUEUE)
    @Transactional("stagingTransactionManager") // Đảm bảo dùng Transaction Manager của Staging
    public void receiveCSVData(EmployeeDTO dto) {
        // Tạm thời tạo Run ID mới cho mỗi tin nhắn. 
        // LÝ TƯỞNG: Run ID nên được truyền từ Producer qua DTO.
        String runId = logService.generateRunId(); 
        String type = dto.getRecordType();
        
        if (type == null) {
            logService.logFailure(JOB_TYPE_CSV, runId, "Message không có RecordType.", "DTO rỗng hoặc thiếu trường RecordType.");
            log.warn("Đã nhận message không có recordType: {}", dto);
            return;
        }
        
        logService.logStart(JOB_TYPE_CSV, runId, String.format("Bắt đầu xử lý bản ghi loại: %s", type));
        
        try {
            ValidationRule<EmployeeDTO> employeeChain = validationFactory.getChain(type);

            if (employeeChain == null) {
                log.warn("Không tìm thấy cấu hình Validation cho recordType: {}", type);
                logService.logFailure(JOB_TYPE_CSV, runId, String.format("Không có Validation Rule cho loại: %s", type), "Validation Chain is null.");
            } else {
                ValidationResult result = employeeChain.validate(dto);

                if (!result.isValid()) {
                    // Xử lý lỗi validation và ghi log (FAILURE)
                    handleValidationError(dto, result, runId);
                    
                    // Ghi log SUCCESS (vì đã xử lý xong, chỉ là đẩy vào bảng lỗi)
                    logService.logSuccess(JOB_TYPE_CSV, runId, String.format("Xử lý thành công (Ghi vào STG_ErrorRecord) cho loại: %s", type), 1);
                    return; // Dừng xử lý ETL nếu có lỗi validation
                }
            }
            
            // --- Logic xử lý thành công (Valid Data) ---
            switch (type) {
                case "DEPARTMENT":
                    processDepartment(dto);
                    break;
                case "EMPLOYEE":
                    processEmployee(dto);
                    break;
                case "SALARY":
                    processSalarySafe(dto, runId);
                    break;
                default:
                    log.warn("Không nhận diện được recordType: '{}'", type);
                    logService.logFailure(JOB_TYPE_CSV, runId, String.format("RecordType không xác định: %s", type), "Unknown Record Type.");
                    return;
            }
            
            // Log SUCCESS nếu mọi thứ thành công
            logService.logSuccess(JOB_TYPE_CSV, runId, String.format("Xử lý thành công (Ghi vào STG_Data) cho loại: %s", type), 1);

        } catch (Exception e) {
            // Log FAILURE nếu có lỗi hệ thống hoặc lỗi DB
            log.error("Lỗi khi xử lý DTO (type: {}): {}", type, e.getMessage(), e);
            logService.logFailure(JOB_TYPE_CSV, runId, String.format("Lỗi hệ thống khi xử lý loại: %s", type), e.getMessage());
            // Quan trọng: Ném lại lỗi để RabbitMQ biết cần phải thử lại.
            throw new RuntimeException(e); 
        }
    }
    
    // Các phương thức processDepartment, processEmployee, processSalary giữ nguyên...
    private void processDepartment(EmployeeDTO dto) {
        log.info("Processing DEPARTMENT: {}", dto.getDepartmentId());
        STG_Department department = employeeMapper.dtoToDepartment(dto);
        departmentRepository.save(department);
    }

    private void processEmployee(EmployeeDTO dto) {
        log.info("Processing EMPLOYEE: {}", dto.getEmployeeId());
        STG_Employee employee = employeeMapper.dtoToEmployee(dto);
        employeeRepository.save(employee);
    }

    /**
     * Ghi lương nhưng tránh mất dữ liệu khi employee chưa tồn tại hoặc lỗi FK.
     * Nếu thiếu employee -> ghi vào STG_ErrorRecord để không mất record lương.
     */
    private void processSalarySafe(EmployeeDTO dto, String runId) {
        log.info("Processing SALARY for Employee: {}", dto.getEmployeeId());

        // Nếu employee chưa có trong staging, đẩy sang bảng lỗi thay vì mất message
        Integer empId = dto.getEmployeeId();
        if (empId == null || !employeeRepository.existsById(empId)) {
            ValidationResult vr = new ValidationResult();
            vr.addError(String.format("Không tìm thấy employeeId %s trong staging, chưa thể ghi lương.", empId));
            handleValidationError(dto, vr, runId);
            // log thông tin để tracking
            logService.logInfo(JOB_TYPE_CSV, runId,
                    String.format("Salary của employeeId %s được lưu vào STG_ErrorRecord do thiếu employee.", empId));
            return;
        }

        try {
            STG_Salary salary = employeeMapper.dtoToSalary(dto);
            salary.setSalaryId(null);
            salaryRepository.save(salary);
        } catch (DataIntegrityViolationException ex) {
            ValidationResult vr = new ValidationResult();
            vr.addError(String.format("Lỗi khóa ngoại khi ghi lương cho employeeId %s: %s", empId, ex.getMostSpecificCause().getMessage()));
            handleValidationError(dto, vr, runId);
            logService.logFailure(JOB_TYPE_CSV, runId,
                    String.format("Salary employeeId %s bị lỗi FK, đã lưu vào STG_ErrorRecord.", empId),
                    ex.getMessage());
        }
    }

    /**
     * Consumer xử lý tin nhắn từ MySQL (Performance Data) - Giữ nguyên logic cơ bản
     */
    @RabbitListener(queues = RabbitMQConfig.PERFORMANCE_QUEUE)
    @Transactional("stagingTransactionManager") // Đảm bảo dùng Transaction Manager của Staging
    public void receiveMySQLData(PerformanceDTO dto) {
        // Tạm thời tạo Run ID mới cho mỗi tin nhắn
        String runId = logService.generateRunId(); 
        String type = dto.getRecordType();
        
        if (type == null) {
            logService.logFailure(JOB_TYPE_PERF, runId, "Message không có RecordType (Performance).", "DTO rỗng hoặc thiếu trường RecordType.");
            log.warn("Đã nhận message không có recordType (Performance): {}", dto);
            return;
        }

        logService.logStart(JOB_TYPE_PERF, runId, String.format("Bắt đầu xử lý bản ghi loại: %s", type));
        
        try {
            switch (type) {
                case "REVIEW":
                    processReview(dto);
                    break;
                case "EMPLOYEE_PERFORMANCE":
                    processEmployeePerformance(dto);
                    break;
                case "TASK_PERFORMANCE":
                    processTaskPerformance(dto);
                    break;
                case "DEPT_PERFORMANCE":
                    processDepartmentPerformance(dto);
                    break;
                case "KPI_METRIC":
                    processKpiMetric(dto);
                    break;
                default:
                    log.warn("Không nhận diện được recordType: '{}'", type);
                    logService.logFailure(JOB_TYPE_PERF, runId, String.format("RecordType không xác định: %s", type), "Unknown Record Type.");
                    return;
            }
            logService.logSuccess(JOB_TYPE_PERF, runId, String.format("Xử lý thành công (Performance) cho loại: %s", type), 1);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý DTO (type: {}). DTO: {}. Lỗi: {}", type, dto, e.getMessage(), e);
            logService.logFailure(JOB_TYPE_PERF, runId, String.format("Lỗi hệ thống khi xử lý Performance loại: %s", type), e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    // Các phương thức processReview, processEmployeePerformance, processTaskPerformance, etc. giữ nguyên...
    private void processReview(PerformanceDTO dto) {
        log.info("Processing REVIEW: {}", dto.getReviewId());
        STG_PerformanceReview review = performanceMapper.dtoToReview(dto);
        review.setReviewId(null); // Luôn INSERT
        reviewRepository.save(review);
    }

    private void processEmployeePerformance(PerformanceDTO dto) {
        log.info("Processing EMPLOYEE_PERFORMANCE: {}", dto.getEmployeePerformanceId());
        STG_EmployeePerformance empPerf = performanceMapper.dtoToEmployeePerformance(dto);
        empPerf.setId(null); // Luôn INSERT
        employeePerformanceRepository.save(empPerf);
    }

    private void processTaskPerformance(PerformanceDTO dto) {
        log.info("Processing TASK_PERFORMANCE: {}", dto.getTaskId());
        STG_TaskPerformance taskPerf = performanceMapper.dtoToTaskPerformance(dto);
        taskPerf.setTaskId(null); // Luôn INSERT
        taskPerformanceRepository.save(taskPerf);
    }

    private void processDepartmentPerformance(PerformanceDTO dto) {
        log.info("Processing DEPT_PERFORMANCE: {}", dto.getDeptPerfId());
        STG_DepartmentPerformance deptPerf = performanceMapper.dtoToDepartmentPerformance(dto);
        deptPerf.setDeptPerfId(null); // Luôn INSERT
        departmentPerformanceRepository.save(deptPerf);
    }

    private void processKpiMetric(PerformanceDTO dto) {
        log.info("Processing KPI_METRIC: {}", dto.getKpiId());
        STG_KpiMetrics kpi = performanceMapper.dtoToKpiMetrics(dto);
        kpi.setKpiId(null); // Luôn INSERT
        kpiMetricsRepository.save(kpi);
    }

    /**
     * Xử lý lỗi Validation và ghi log vào DB.
     */
    private <T> void handleValidationError(T dto, ValidationResult result, String runId) {
        String errors = String.join("\n", result.getErrors());
        
        // Ghi log chi tiết cho Tracking
        logService.logInfo(JOB_TYPE_CSV, runId, 
            String.format("Lỗi Validation đã xảy ra. Số lỗi: %d. Đang lưu vào STG_ErrorRecord.", result.getErrors().size()));
        
        log.warn("Bản ghi có lỗi validation. Lưu vào STG_ErrorRecord.");
        try {
            STG_ErrorRecord errorRecord = new STG_ErrorRecord();
            
            String recordType = "UNKNOWN";
            if (dto instanceof EmployeeDTO) {
                recordType = ((EmployeeDTO) dto).getRecordType();
            } else if (dto instanceof PerformanceDTO) {
                recordType = ((PerformanceDTO) dto).getRecordType();
            }
            
            errorRecord.setRecordType(recordType);
            errorRecord.setRawData(objectMapper.writeValueAsString(dto)); 
            errorRecord.setErrors(errors);
            
            errorRecordRepository.save(errorRecord);
            
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi chuyển đổi DTO sang JSON để lưu STG_ErrorRecord: {}", e.getMessage(), e);
            // Ghi log thất bại nếu không thể lưu lỗi
            logService.logFailure(JOB_TYPE_CSV, runId, "Lỗi khi chuyển đổi DTO sang JSON để lưu STG_ErrorRecord.", e.getMessage());
        }
    }
}