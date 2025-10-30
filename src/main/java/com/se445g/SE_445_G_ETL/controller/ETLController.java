package com.se445g.SE_445_G_ETL.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se445g.SE_445_G_ETL.service.interf.CSVProducerService;
import com.se445g.SE_445_G_ETL.service.interf.MySQLProducerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rabbitmq")
@RequiredArgsConstructor
public class ETLController {

    private final CSVProducerService CSVProducer;
    private final MySQLProducerService MySQLProducer;

    @PostMapping("/send-csv")
    public String sendMessageCSV() {
        CSVProducer.sendCSVData("src/main/resources/data/departments.csv", "src/main/resources/data/employees.csv",
                "src/main/resources/data/salaries.csv");
        return "Đã gửi CSV lên rabbitMQ";
    }

    @PostMapping("/send-sql")
    public String sendMessageMySQL() {
        MySQLProducer.sendMySQLData();
        return "Đã gửi CSV lên rabbitMQ";
    }
}
