package com.se445g.SE_445_G_ETL.repository.target;

import com.se445g.SE_445_G_ETL.entity.target.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {
}

