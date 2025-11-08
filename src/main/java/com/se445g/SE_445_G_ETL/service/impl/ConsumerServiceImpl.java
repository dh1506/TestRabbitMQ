package com.se445g.SE_445_G_ETL.service.impl;

import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.entity.staging.*;
import com.se445g.SE_445_G_ETL.mapper.EmployeeMapper;
import com.se445g.SE_445_G_ETL.mapper.PerformanceMapper;
import com.se445g.SE_445_G_ETL.repository.staging.*;
import com.se445g.SE_445_G_ETL.service.interf.ConsumerService;
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

    @RabbitListener(queues = RabbitMQConfig.EMPLOYEES_QUEUE)
    @Transactional
    public void receiveCSVData(EmployeeDTO dto) {
        String type = dto.getRecordType();
        if (type == null) {
            log.warn("Đã nhận message không có recordType: {}", dto);
            return;
        }

        try {
            switch (type) {
                case "DEPARTMENT":
                    processDepartment(dto);
                    break;
                case "EMPLOYEE":
                    processEmployee(dto);
                    break;
                case "SALARY":
                    processSalary(dto);
                    break;
                default:
                    log.warn("Không nhận diện được recordType: '{}'", type);
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý DTO (type: {}): {}", type, e.getMessage(), e);
        }
    }

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

    private void processSalary(EmployeeDTO dto) {
        log.info("Processing SALARY for Employee: {}", dto.getEmployeeId());
        STG_Salary salary = employeeMapper.dtoToSalary(dto);
        salary.setSalaryId(null);
        salaryRepository.save(salary);
    }

    @RabbitListener(queues = RabbitMQConfig.PERFORMANCE_QUEUE)
    @Transactional
    public void receiveMySQLData(PerformanceDTO dto) {
        String type = dto.getRecordType();
        if (type == null) {
            log.warn("Đã nhận message không có recordType: {}", dto);
            return;
        }

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
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý DTO (type: {}). DTO: {}. Lỗi: {}", type, dto, e.getMessage(), e);
            throw e; // Ném lại lỗi để transaction rollback và đưa vào DLQ (nếu có)
        }
    }

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
}