package com.se445g.SE_445_G_ETL.repo.finaldb;

import com.se445g.SE_445_G_ETL.entity.staging.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalEmployeeRepository extends JpaRepository<FinalEmployee, Integer> {

}