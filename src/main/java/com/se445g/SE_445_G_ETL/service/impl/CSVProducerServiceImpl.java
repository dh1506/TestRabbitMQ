package com.se445g.SE_445_G_ETL.service.impl;

import com.opencsv.CSVReader;
import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Department;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Employee;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Salary;
import com.se445g.SE_445_G_ETL.mapper.EmployeeMapper;
import com.se445g.SE_445_G_ETL.service.interf.CSVProducerService;
import com.se445g.SE_445_G_ETL.service.interf.LogService; // <-- IMPORT MỚI
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVProducerServiceImpl implements CSVProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final EmployeeMapper employeeMapper;
    private final LogService logService; // <-- LOG SERVICE MỚI

    // Định nghĩa các hằng số cho recordType
    private static final String TYPE_DEPT = "DEPARTMENT";
    private static final String TYPE_EMP = "EMPLOYEE";
    private static final String TYPE_SALARY = "SALARY";
    private static final String JOB_TYPE = "CSV_PRODUCER"; // <-- Tên Job

    @Override
    public void sendCSVData(String departmentPath, String employeePath, String salaryPath) {
        
        String runId = logService.generateRunId(); // <-- 1. TẠO RUN ID CHO CẢ LẦN CHẠY
        int totalRecordsSent = 0;

        try {
            logService.logStart(JOB_TYPE, runId, "Bắt đầu quá trình tải và gửi dữ liệu CSV lên RabbitMQ."); // <-- LOG START JOB
            log.info("Bắt đầu quá trình gửi dữ liệu CSV với RUN_ID: {}", runId);

            // Gửi Departments
            totalRecordsSent += sendDepartments(departmentPath, runId); 
            
            // Gửi Employees
            totalRecordsSent += sendEmployees(employeePath, runId);
            
            // Gửi Salaries
            totalRecordsSent += sendSalaries(salaryPath, runId);

            log.info("Đã gửi tổng cộng {} bản ghi CSV lên RabbitMQ.", totalRecordsSent);
            logService.logSuccess(JOB_TYPE, runId, "Hoàn thành gửi tất cả dữ liệu CSV.", totalRecordsSent); // <-- LOG SUCCESS JOB
            
        } catch (Exception e) {
            log.error("LỖI KHÔNG XỬ LÝ: Toàn bộ quá trình gửi CSV thất bại.", e);
            logService.logFailure(JOB_TYPE, runId, "Lỗi nghiêm trọng trong quá trình gửi CSV (kiểm tra logs).", e.getMessage()); // <-- LOG FAILURE JOB
            throw new RuntimeException("CSV Producer failed", e); // Re-throw để Controller biết lỗi
        }
    }

    private int sendDepartments(String departmentPath, String runId) {
        int recordCount = 0;
        try (CSVReader reader = new CSVReader(new FileReader(departmentPath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            logService.logInfo(JOB_TYPE, runId, "Đang gửi dữ liệu Departments..."); // Log INFO chi tiết
            
            while ((line = reader.readNext()) != null) {
                STG_Department tempDept = STG_Department.builder()
                        .departmentId(Integer.parseInt(line[0]))
                        .name(line[1])
                        .location(line[2])
                        .phone(line[3])
                        .budgetVnd(new BigDecimal(line[4]))
                        .managerId(Integer.parseInt(line[5]))
                        .build();

                EmployeeDTO dto = employeeMapper.departmentToDto(tempDept);

                dto.setRecordType(TYPE_DEPT);
                sendToQueue(dto, runId); // Truyền runId
                recordCount++;
            }
            logService.logInfo(JOB_TYPE, runId, String.format("Hoàn thành gửi %d bản ghi Departments.", recordCount));
            return recordCount;
        } catch (Exception e) {
            log.error("Lỗi khi đọc file departments.csv: {}", e.getMessage(), e);
            logService.logFailure(JOB_TYPE, runId, "Lỗi khi đọc hoặc gửi Departments.", e.getMessage());
            return 0;
        }
    }

    private int sendEmployees(String employeePath, String runId) {
        int recordCount = 0;
        try (CSVReader reader = new CSVReader(new FileReader(employeePath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            logService.logInfo(JOB_TYPE, runId, "Đang gửi dữ liệu Employees..."); // Log INFO chi tiết

            while ((line = reader.readNext()) != null) {
                STG_Department deptProxy = new STG_Department();
                deptProxy.setDepartmentId(Integer.parseInt(line[10]));
                STG_Employee tempEmp = STG_Employee.builder()
                        .employeeId(Integer.parseInt(line[0]))
                        .fullName(line[1])
                        .gender(line[2])
                        .dateOfBirth(LocalDate.parse(line[3]))
                        .hometown(line[4])
                        .phone(line[5])
                        .email(line[6])
                        .educationLevel(line[7])
                        .position(line[8])
                        .hireDate(LocalDate.parse(line[9]))
                        .status(line[11])
                        .department(deptProxy) // Gán proxy
                        .build();
                EmployeeDTO dto = employeeMapper.employeeToDto(tempEmp);
                dto.setRecordType(TYPE_EMP);
                sendToQueue(dto, runId); // Truyền runId
                recordCount++;
            }
            logService.logInfo(JOB_TYPE, runId, String.format("Hoàn thành gửi %d bản ghi Employees.", recordCount));
            return recordCount;
        } catch (Exception e) {
            log.error("Lỗi khi đọc file employees.csv: {}", e.getMessage(), e);
            logService.logFailure(JOB_TYPE, runId, "Lỗi khi đọc hoặc gửi Employees.", e.getMessage());
            return 0;
        }
    }

    private int sendSalaries(String salaryPath, String runId) {
        int recordCount = 0;
        try (CSVReader reader = new CSVReader(new FileReader(salaryPath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            logService.logInfo(JOB_TYPE, runId, "Đang gửi dữ liệu Salaries..."); // Log INFO chi tiết

            while ((line = reader.readNext()) != null) {
                STG_Employee empProxy = new STG_Employee();
                empProxy.setEmployeeId(Integer.parseInt(line[1]));
                STG_Salary tempSalary = STG_Salary.builder()
                        .salaryId(Integer.parseInt(line[0]))
                        .amountVnd(new BigDecimal(line[2]))
                        .currency(line[3])
                        .payFrequency(line[4])
                        .bonusVnd(new BigDecimal(line[5]))
                        .effectiveFrom(LocalDate.parse(line[6]))
                        .effectiveTo(line[7].isEmpty() ? null : LocalDate.parse(line[7]))
                        .employee(empProxy)
                        .build();
                EmployeeDTO dto = employeeMapper.salaryToDto(tempSalary);
                dto.setRecordType(TYPE_SALARY);
                sendToQueue(dto, runId); // Truyền runId
                recordCount++;
            }
            logService.logInfo(JOB_TYPE, runId, String.format("Hoàn thành gửi %d bản ghi Salaries.", recordCount));
            return recordCount;
        } catch (Exception e) {
            log.error("Lỗi khi đọc file salaries.csv: {}", e.getMessage(), e);
            logService.logFailure(JOB_TYPE, runId, "Lỗi khi đọc hoặc gửi Salaries.", e.getMessage());
            return 0;
        }
    }

    // Cần cập nhật hàm này để đưa runId vào DTO, giúp Consumer biết log nào thuộc về lần chạy nào
    private void sendToQueue(EmployeeDTO dto, String runId) {
        // Tạm thời, tôi sẽ log runId, nhưng lý tưởng là bạn nên thêm trường runId vào EmployeeDTO
        log.info("Gửi tin nhắn loại {} với RUN_ID: {}", dto.getRecordType(), runId); 
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.EMPLOYEES_ROUTING_KEY,
                dto);
    }
}