package com.se445g.SE_445_G_ETL.repository.staging;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se445g.SE_445_G_ETL.entity.staging.STG_ErrorRecord;

public interface STG_ErrorRecordRepository extends JpaRepository<STG_ErrorRecord, Integer> {
}
