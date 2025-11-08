package com.se445g.SE_445_G_ETL.mapper;

import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Department;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Employee;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Salary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

        // =================================================================
        // MAPPING TỪ ENTITY -> DTO (Sử dụng trong Producer)
        // =================================================================

        @Mappings({
                        @Mapping(source = "name", target = "departmentName"),
                        @Mapping(source = "location", target = "departmentLocation"),
                        @Mapping(source = "phone", target = "departmentPhone"),
                        @Mapping(source = "budgetVnd", target = "departmentBudgetVnd"),
                        @Mapping(target = "amountVnd", ignore = true),
                        @Mapping(target = "bonusVnd", ignore = true),
                        @Mapping(target = "currency", ignore = true),
                        @Mapping(target = "dateOfBirth", ignore = true),
                        @Mapping(target = "educationLevel", ignore = true),
                        @Mapping(target = "effectiveFrom", ignore = true),
                        @Mapping(target = "effectiveTo", ignore = true),
                        @Mapping(target = "email", ignore = true),
                        @Mapping(target = "employeeId", ignore = true),
                        @Mapping(target = "employeeStatus", ignore = true),
                        @Mapping(target = "fullName", ignore = true),
                        @Mapping(target = "gender", ignore = true),
                        @Mapping(target = "hireDate", ignore = true),
                        @Mapping(target = "hometown", ignore = true),
                        @Mapping(target = "payFrequency", ignore = true),
                        @Mapping(target = "position", ignore = true),
                        @Mapping(target = "recordType", ignore = true),
                        @Mapping(target = "salaryId", ignore = true)
        })
        EmployeeDTO departmentToDto(STG_Department department);

        @Mappings({
                        @Mapping(source = "status", target = "employeeStatus"),
                        @Mapping(source = "department.departmentId", target = "departmentId"),
                        @Mapping(target = "amountVnd", ignore = true),
                        @Mapping(target = "bonusVnd", ignore = true),
                        @Mapping(target = "currency", ignore = true),
                        @Mapping(target = "departmentBudgetVnd", ignore = true),
                        @Mapping(target = "departmentLocation", ignore = true),
                        @Mapping(target = "departmentName", ignore = true),
                        @Mapping(target = "departmentPhone", ignore = true),
                        @Mapping(target = "effectiveFrom", ignore = true),
                        @Mapping(target = "effectiveTo", ignore = true),
                        @Mapping(target = "managerId", ignore = true),
                        @Mapping(target = "payFrequency", ignore = true),
                        @Mapping(target = "recordType", ignore = true),
                        @Mapping(target = "salaryId", ignore = true)
        })
        EmployeeDTO employeeToDto(STG_Employee employee);

        @Mappings({
                        @Mapping(source = "employee.employeeId", target = "employeeId"), // Lấy ID từ object employee
                        @Mapping(target = "dateOfBirth", ignore = true),
                        @Mapping(target = "departmentBudgetVnd", ignore = true),
                        @Mapping(target = "departmentId", ignore = true),
                        @Mapping(target = "departmentLocation", ignore = true),
                        @Mapping(target = "departmentName", ignore = true),
                        @Mapping(target = "departmentPhone", ignore = true),
                        @Mapping(target = "educationLevel", ignore = true),
                        @Mapping(target = "email", ignore = true),
                        @Mapping(target = "employeeStatus", ignore = true),
                        @Mapping(target = "fullName", ignore = true),
                        @Mapping(target = "gender", ignore = true),
                        @Mapping(target = "hireDate", ignore = true),
                        @Mapping(target = "hometown", ignore = true),
                        @Mapping(target = "managerId", ignore = true),
                        @Mapping(target = "phone", ignore = true),
                        @Mapping(target = "position", ignore = true),
                        @Mapping(target = "recordType", ignore = true)
        })
        EmployeeDTO salaryToDto(STG_Salary salary);

        // =================================================================
        // MAPPING TỪ DTO -> ENTITY (Sử dụng trong Consumer)
        // =================================================================

        @Mappings({
                        @Mapping(source = "departmentName", target = "name"),
                        @Mapping(source = "departmentLocation", target = "location"),
                        @Mapping(source = "departmentPhone", target = "phone"),
                        @Mapping(source = "departmentBudgetVnd", target = "budgetVnd"),
                        @Mapping(target = "employees", ignore = true), // Bỏ qua list để tránh lỗi mapping
                        @Mapping(target = "isNew", constant = "false")
        })
        STG_Department dtoToDepartment(EmployeeDTO dto);

        @Mappings({
                        @Mapping(source = "employeeStatus", target = "status"),
                        @Mapping(source = "departmentId", target = "department.departmentId"), // Map ID vào object
                                                                                               // department
                        @Mapping(target = "salaries", ignore = true), // Bỏ qua list để tránh lỗi mapping
                        @Mapping(target = "isNew", constant = "false")
        })
        STG_Employee dtoToEmployee(EmployeeDTO dto);

        @Mappings({
                        @Mapping(source = "employeeId", target = "employee.employeeId") // Map ID vào object employee
        })
        STG_Salary dtoToSalary(EmployeeDTO dto);
}