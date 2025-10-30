package com.se445g.SE_445_G_ETL.service.impl;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Department;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Employee;
import com.se445g.SE_445_G_ETL.entity.staging.STG_EmployeePerformance;
import com.se445g.SE_445_G_ETL.entity.staging.STG_PerformanceReview;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Salary;
import com.se445g.SE_445_G_ETL.entity.staging.STG_TaskPerformance;
import com.se445g.SE_445_G_ETL.repository.staging.STG_DepartmentRepository;
import com.se445g.SE_445_G_ETL.repository.staging.STG_EmployeePerformanceRepository;
import com.se445g.SE_445_G_ETL.repository.staging.STG_EmployeeRepository;
import com.se445g.SE_445_G_ETL.repository.staging.STG_PerformanceReviewRepository;
import com.se445g.SE_445_G_ETL.repository.staging.STG_SalaryRepository;
import com.se445g.SE_445_G_ETL.repository.staging.STG_TaskPerformanceRepository;
import com.se445g.SE_445_G_ETL.service.interf.ConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {
    
    private final STG_DepartmentRepository departmentRepository;
    private final STG_EmployeeRepository employeeRepository;
    private final STG_SalaryRepository salaryRepository;

    private final STG_PerformanceReviewRepository reviewRepository;
    private final STG_EmployeePerformanceRepository employeePerformanceRepository;
    private final STG_TaskPerformanceRepository taskPerformanceRepository;

    /**
     * Listener duy nhất lắng nghe EMPLOYEES_QUEUE
     */
    @RabbitListener(queues = RabbitMQConfig.EMPLOYEES_QUEUE)
    @Transactional // Đặt Transactional ở đây để xử lý từng message một cách an toàn
    public void receiveCSVData(EmployeeDTO dto) {

        // Kiểm tra recordType (nên kiểm tra null để tránh lỗi)
        String type = dto.getRecordType();
        if (type == null) {
            log.warn("Đã nhận message không có recordType: {}", dto);
            return;
        }

        // Phân luồng xử lý dựa trên recordType
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
            // Ném lại lỗi để RabbitMQ biết xử lý thất bại (ví dụ: đưa vào Dead Letter
            // Queue)
            // Hoặc không ném lỗi (như hiện tại) để message được ACK (công nhận) và loại bỏ
        }
    }

    /**
     * Xử lý lưu Department
     */
    private void processDepartment(EmployeeDTO dto) {
        log.info("Processing DEPARTMENT: {}", dto.getDepartmentId());
        STG_Department department = STG_Department.builder()
                .departmentId(dto.getDepartmentId())
                .departmentName(dto.getDepartmentName())
                .location(dto.getLocation())
                .build();
        // Dùng save() cho logic "Upsert"
        departmentRepository.save(department);
    }

    /**
     * Xử lý lưu Employee
     */
    private void processEmployee(EmployeeDTO dto) {
        log.info("Processing EMPLOYEE: {}", dto.getEmployeeId());

        // Tạo proxy cho Department để set khóa ngoại
        STG_Department deptProxy = new STG_Department();
        deptProxy.setDepartmentId(dto.getDepartmentId());

        STG_Employee employee = STG_Employee.builder()
                .employeeId(dto.getEmployeeId())
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .birthDate(dto.getBirthDate())
                .position(dto.getPosition())
                .department(deptProxy)
                .build();

        // Dùng save() cho logic "Upsert"
        employeeRepository.save(employee);
    }

    /**
     * Xử lý lưu Salary
     */
    private void processSalary(EmployeeDTO dto) {
        log.info("Processing SALARY for Employee: {}", dto.getEmployeeId());

        // Tạo proxy cho Employee để set khóa ngoại
        STG_Employee empProxy = new STG_Employee();
        empProxy.setEmployeeId(dto.getEmployeeId());

        // Salary là INSERT-ONLY (do có @GeneratedValue)
        STG_Salary salary = STG_Salary.builder()
                .employee(empProxy)
                .baseSalary(dto.getBaseSalary())
                .bonus(dto.getBonus())
                .month(dto.getMonth())
                .year(dto.getYear())
                .build();

        salaryRepository.save(salary);
    }

    /**
     * Một Listener duy nhất lắng nghe PERFORMANCE_QUEUE
     */
    @RabbitListener(queues = RabbitMQConfig.PERFORMANCE_QUEUE)
    @Transactional // Đảm bảo mỗi tin nhắn được xử lý như 1 giao dịch
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
                case "EMPLOYEEPERFORMANCE":
                    processEmployeePerformance(dto);
                    break;
                case "TASKPERFORMANCE":
                    processTaskPerformance(dto);
                    break;
                default:
                    log.warn("Không nhận diện được recordType: '{}'", type);
            }
        } catch (Exception e) {
            // === THÊM LOGGING CHI TIẾT VÀO ĐÂY ===
            String parentId = (type.equals("TASKPERFORMANCE")) ? String.valueOf(dto.getId()) : "N/A";
            String childId = (type.equals("TASKPERFORMANCE")) ? String.valueOf(dto.getTaskId())
                    : String.valueOf(dto.getId());

            log.error("LỖI XỬ LÝ ETL (Type: {}). ParentID: {}, ChildID: {}. DTO: {}. Lỗi: {}",
                    type,
                    parentId,
                    childId,
                    dto.toString(),
                    e.getMessage());
            // Chúng ta không ném lại lỗi để tránh message bị requeue (vòng lặp lỗi)
            // Thay vào đó, message này sẽ được "ack" (coi như đã xử lý)
            // Bạn có thể thiết lập Dead Letter Queue (DLQ) để giữ lại các message lỗi này
        }
    }

    /**
     * Xử lý lưu STG_PerformanceReview
     */
    private void processReview(PerformanceDTO dto) {
        log.info("Processing REVIEW: {}", dto.getReviewId());
        STG_PerformanceReview review = STG_PerformanceReview.builder()
                .reviewId(dto.getReviewId())
                .period(dto.getPeriod())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getReviewDescription())
                .build();
        // Dùng save() cho logic "Upsert" (Update nếu ID tồn tại, Insert nếu chưa)
        reviewRepository.save(review);
    }

    /**
     * Xử lý lưu STG_EmployeePerformance
     */
    private void processEmployeePerformance(PerformanceDTO dto) {
        log.info("Processing EMPLOYEEPERFORMANCE: {}", dto.getId());

        // 1. Tạo proxy cho Review (để set khóa ngoại)
        STG_PerformanceReview reviewProxy = new STG_PerformanceReview();
        reviewProxy.setReviewId(dto.getReviewId());

        // 2. Build đối tượng chính
        STG_EmployeePerformance empPerf = STG_EmployeePerformance.builder()
                .id(dto.getId())
                .employeeId(dto.getEmployeeId())
                .review(reviewProxy) // Gán proxy
                .performanceScore(dto.getPerformanceScore())
                .comments(dto.getComments())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .build();
        
        // 3. Dùng save() cho logic "Upsert"
        employeePerformanceRepository.save(empPerf);
    }

    /**
     * Xử lý lưu STG_TaskPerformance
     */
    private void processTaskPerformance(PerformanceDTO dto) {
        log.info("Processing TASKPERFORMANCE: {}", dto.getTaskId());

        // 1. Tạo proxy cho EmployeePerformance (để set khóa ngoại)
        // Dựa trên logic của Producer, dto.getId() đang giữ employee_performance_id
        STG_EmployeePerformance empPerfProxy = new STG_EmployeePerformance();
        empPerfProxy.setId(dto.getId()); 

        // 2. Build đối tượng chính
        STG_TaskPerformance taskPerf = STG_TaskPerformance.builder()
                .taskId(dto.getTaskId())
                .employeePerformance(empPerfProxy) // Gán proxy
                .taskName(dto.getTaskName())
                .taskScore(dto.getTaskScore())
                .note(dto.getTaskNote())
                .build();

        // 3. Dùng save() cho logic "Upsert"
        taskPerformanceRepository.save(taskPerf);
    }

}
