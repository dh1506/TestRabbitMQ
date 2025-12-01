package com.se445g.SE_445_G_ETL.service.impl;

import com.se445g.SE_445_G_ETL.entity.staging.FinalEmployee;
import com.se445g.SE_445_G_ETL.entity.staging.STG_Employee;
import com.se445g.SE_445_G_ETL.repository.staging.STG_EmployeeRepository;
import com.se445g.SE_445_G_ETL.repo.finaldb.*;
import com.se445g.SE_445_G_ETL.transformer.DataTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceToFinalService {

    private final STG_EmployeeRepository stgEmployeeRepo;
    private final FinalEmployeeRepository finalEmployeeRepo;

    public void transferData() {
        List<STG_Employee> employees = stgEmployeeRepo.findAll();
        log.info("Đang chuyển {} nhân viên sang DB Final", employees.size());

        for (STG_Employee emp : employees) {
            // Transform
            String gender = DataTransformer.normalizeGender(emp.getGender());
            String email = DataTransformer.normalizeEmail(emp.getEmail());
            String education = DataTransformer.normalizeEducation(emp.getEducationLevel());
            String position = DataTransformer.normalizePosition(emp.getPosition());
            String status = DataTransformer.normalizeStatus(emp.getStatus());

            // Nếu dữ liệu không hợp lệ → skip
            if (gender == null || email == null || education == null || position == null) {
                log.warn("Bỏ qua nhân viên lỗi: {}", emp.getEmployeeId());
                continue;
            }

            FinalEmployee finalEmp = FinalEmployee.builder()
                    .employeeId(emp.getEmployeeId())
                    .fullName(emp.getFullName())
                    .gender(gender)
                    .dateOfBirth(emp.getDateOfBirth())
                    .hometown(emp.getHometown())
                    .phone(emp.getPhone())
                    .email(email)
                    .educationLevel(education)
                    .position(position)
                    .hireDate(emp.getHireDate())
                    .status(status)
                    .departmentId(emp.getDepartment() != null ? emp.getDepartment().getDepartmentId() : null)
                    .build();

            finalEmployeeRepo.save(finalEmp);
        }

        log.info("Hoàn tất chuyển dữ liệu sang DB Final");
    }
}
