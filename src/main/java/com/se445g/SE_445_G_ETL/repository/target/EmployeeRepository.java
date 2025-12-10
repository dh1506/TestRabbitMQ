package com.se445g.SE_445_G_ETL.repository.target;

import com.se445g.SE_445_G_ETL.entity.target.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}

