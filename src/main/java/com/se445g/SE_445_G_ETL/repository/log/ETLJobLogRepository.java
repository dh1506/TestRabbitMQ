package com.se445g.SE_445_G_ETL.repository.log;

import com.se445g.SE_445_G_ETL.entity.log.ETLJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ETLJobLogRepository extends JpaRepository<ETLJobLog, Long> {
}