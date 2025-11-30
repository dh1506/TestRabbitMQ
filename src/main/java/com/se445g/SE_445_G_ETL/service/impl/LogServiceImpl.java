package com.se445g.SE_445_G_ETL.service.impl;

import com.se445g.SE_445_G_ETL.entity.log.ETLJobLog;
import com.se445g.SE_445_G_ETL.repository.log.ETLJobLogRepository;
import com.se445g.SE_445_G_ETL.service.interf.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final ETLJobLogRepository logRepository;

    @Override
    public String generateRunId() {
        return UUID.randomUUID().toString();
    }

    private void saveLog(String jobType, String status, String runId, String message, Integer recordCount) {
        ETLJobLog log = new ETLJobLog();
        log.setJobType(jobType);
        log.setStatus(status);
        log.setRunId(runId);
        // Cắt bớt message nếu quá dài
        log.setMessage(message.substring(0, Math.min(message.length(), 990))); 
        log.setRecordCount(recordCount);
        logRepository.save(log);
    }

    @Override
    @Transactional("logTransactionManager") // <-- Sử dụng TM của db_log
    public void logStart(String jobType, String runId, String message) {
        saveLog(jobType, "START", runId, message, null);
    }

    @Override
    @Transactional("logTransactionManager") 
    public void logSuccess(String jobType, String runId, String message, int recordCount) {
        saveLog(jobType, "SUCCESS", runId, message, recordCount);
    }

    @Override
    @Transactional("logTransactionManager") 
    public void logFailure(String jobType, String runId, String message, String errorDetails) {
        String fullMessage = message + " - Chi tiết lỗi: " + errorDetails;
        saveLog(jobType, "FAILURE", runId, fullMessage, null);
    }
    
    @Override
    @Transactional("logTransactionManager")
    public void logInfo(String jobType, String runId, String message) {
        saveLog(jobType, "INFO", runId, message, null);
    }
}