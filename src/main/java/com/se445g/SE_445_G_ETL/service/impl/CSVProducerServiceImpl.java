package com.se445g.SE_445_G_ETL.service.impl;

import com.opencsv.CSVReader;
import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.service.interf.CSVProducerService;
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

    // Định nghĩa các hằng số cho recordType
    private static final String TYPE_DEPT = "DEPARTMENT";
    private static final String TYPE_EMP = "EMPLOYEE";
    private static final String TYPE_SALARY = "SALARY";

    @Override
    public void sendCSVData(String departmentPath, String employeePath, String salaryPath) {
        log.info("Bắt đầu quá trình gửi dữ liệu CSV (mô hình 1 queue)...");

        // Chạy 3 quy trình độc lập, TẤT CẢ gửi về 1 queue
        sendDepartments(departmentPath);
        sendEmployees(employeePath);
        sendSalaries(salaryPath);

        log.info("Đã gửi tất cả dữ liệu CSV lên RabbitMQ (1 queue).");
    }

    /**
     * Đọc file departments.csv và gửi DTO loại DEPARTMENT
     */
    private void sendDepartments(String departmentPath) {
        try (CSVReader reader = new CSVReader(new FileReader(departmentPath))) {
            String[] line;
            reader.readNext(); // bỏ tiêu đề
            log.info("Đang gửi dữ liệu Departments...");

            while ((line = reader.readNext()) != null) {
                // Chỉ điền thông tin Department
                EmployeeDTO dto = EmployeeDTO.builder()
                        .recordType(TYPE_DEPT) // ** Đặt loại **
                        .departmentId(Integer.parseInt(line[0]))
                        .departmentName(line[1])
                        .location(line[2])
                        .build();

                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Departments.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file departments.csv: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đọc file employees.csv và gửi DTO loại EMPLOYEE
     */
    private void sendEmployees(String employeePath) {
        try (CSVReader reader = new CSVReader(new FileReader(employeePath))) {
            String[] line;
            reader.readNext(); // bỏ tiêu đề
            log.info("Đang gửi dữ liệu Employees...");

            while ((line = reader.readNext()) != null) {
                // Chỉ điền thông tin Employee (và departmentId)
                EmployeeDTO dto = EmployeeDTO.builder()
                        .recordType(TYPE_EMP) // ** Đặt loại **
                        .employeeId(Integer.parseInt(line[0]))
                        .fullName(line[1])
                        .gender(line[2])
                        .birthDate(LocalDate.parse(line[3]))
                        .position(line[4])
                        .departmentId(Integer.parseInt(line[5])) // Khóa ngoại
                        .build();

                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Employees.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file employees.csv: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đọc file salaries.csv và gửi DTO loại SALARY
     */
    private void sendSalaries(String salaryPath) {
        try (CSVReader reader = new CSVReader(new FileReader(salaryPath))) {
            String[] line;
            reader.readNext(); // bỏ tiêu đề
            log.info("Đang gửi dữ liệu Salaries...");

            while ((line = reader.readNext()) != null) {
                // Chỉ điền thông tin Salary (và employeeId)
                EmployeeDTO dto = EmployeeDTO.builder()
                        .recordType(TYPE_SALARY) // ** Đặt loại **
                        .employeeId(Integer.parseInt(line[1])) // Khóa ngoại
                        .baseSalary(new BigDecimal(line[2]))
                        .bonus(new BigDecimal(line[3]))
                        .month(Integer.parseInt(line[4]))
                        .year(Integer.parseInt(line[5]))
                        .build();

                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Salaries.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file salaries.csv: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phương thức chung để gửi DTO đến 1 queue duy nhất
     */
    private void sendToQueue(EmployeeDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.EMPLOYEES_ROUTING_KEY,
                dto);
    }
}