package com.se445g.SE_445_G_ETL.service.impl;

import com.opencsv.CSVReader;
import com.se445g.SE_445_G_ETL.config.RabbitMQConfig;
import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Department;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Employee;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Salary;
import com.se445g.SE_445_G_ETL.mapper.EmployeeMapper;
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
    private final EmployeeMapper employeeMapper;

    // Định nghĩa các hằng số cho recordType
    private static final String TYPE_DEPT = "DEPARTMENT";
    private static final String TYPE_EMP = "EMPLOYEE";
    private static final String TYPE_SALARY = "SALARY";

    @Override
    public void sendCSVData(String departmentPath, String employeePath, String salaryPath) {
        log.info("Bắt đầu quá trình gửi dữ liệu CSV...");

        sendDepartments(departmentPath);
        sendEmployees(employeePath);
        sendSalaries(salaryPath);

        log.info("Đã gửi tất cả dữ liệu CSV lên RabbitMQ.");
    }

    private void sendDepartments(String departmentPath) {
        try (CSVReader reader = new CSVReader(new FileReader(departmentPath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            log.info("Đang gửi dữ liệu Departments...");
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
                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Departments.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file departments.csv: {}", e.getMessage(), e);
        }
    }

    private void sendEmployees(String employeePath) {
        try (CSVReader reader = new CSVReader(new FileReader(employeePath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            log.info("Đang gửi dữ liệu Employees...");

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
                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Employees.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file employees.csv: {}", e.getMessage(), e);
        }
    }

    private void sendSalaries(String salaryPath) {
        try (CSVReader reader = new CSVReader(new FileReader(salaryPath))) {
            String[] line;
            reader.readNext(); // Bỏ qua dòng tiêu đề
            log.info("Đang gửi dữ liệu Salaries...");

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
                sendToQueue(dto);
            }
            log.info("Hoàn thành gửi dữ liệu Salaries.");
        } catch (Exception e) {
            log.error("Lỗi khi đọc file salaries.csv: {}", e.getMessage(), e);
        }
    }

    private void sendToQueue(EmployeeDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.EMPLOYEES_ROUTING_KEY,
                dto);
    }
}