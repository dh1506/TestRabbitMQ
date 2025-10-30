package com.se445g.SE_445_G_ETL.service.interf;

import com.se445g.SE_445_G_ETL.dto.EmployeeDTO;
import com.se445g.SE_445_G_ETL.dto.PerformanceDTO;

public interface ConsumerService {

    void receiveCSVData(EmployeeDTO dto);

    void receiveMySQLData(PerformanceDTO dto);
}
