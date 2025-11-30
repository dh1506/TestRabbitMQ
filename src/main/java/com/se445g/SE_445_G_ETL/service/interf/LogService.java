package com.se445g.SE_445_G_ETL.service.interf;

public interface LogService {
    String generateRunId(); 

    void logStart(String jobType, String runId, String message);
    void logSuccess(String jobType, String runId, String message, int recordCount);
    void logFailure(String jobType, String runId, String message, String errorDetails);
    void logInfo(String jobType, String runId, String message);
}