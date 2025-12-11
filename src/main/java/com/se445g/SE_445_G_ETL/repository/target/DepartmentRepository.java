package com.se445g.SE_445_G_ETL.repository.target;

import com.se445g.SE_445_G_ETL.entity.target.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}

