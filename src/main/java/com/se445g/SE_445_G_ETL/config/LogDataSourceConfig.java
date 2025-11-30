package com.se445g.SE_445_G_ETL.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap; // <-- IMPORT MỚI

@Configuration
@EnableJpaRepositories(
    basePackages = "com.se445g.SE_445_G_ETL.repository.log",
    entityManagerFactoryRef = "logEntityManagerFactory",
    transactionManagerRef = "logTransactionManager"
)
public class LogDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.log")
    public DataSource logDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean logEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("logDataSource") DataSource dataSource) {
                
        // 1. Định nghĩa các thuộc tính Hibernate
        HashMap<String, Object> properties = new HashMap<>();
        // Thuộc tính này cho phép Hibernate tự động tạo/cập nhật các bảng
        properties.put("hibernate.hbm2ddl.auto", "update"); 
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return builder
            .dataSource(dataSource)
            .packages("com.se445g.SE_445_G_ETL.entity.log")
            .persistenceUnit("log")
            // 2. Thêm các thuộc tính vào EntityManagerFactory
            .properties(properties) 
            .build();
    }

    @Bean
    public PlatformTransactionManager logTransactionManager(
            @Qualifier("logEntityManagerFactory") LocalContainerEntityManagerFactoryBean logEntityManagerFactory) {
        // Đảm bảo Log Transaction Manager được tạo từ Log EntityManager Factory
        return new JpaTransactionManager(logEntityManagerFactory.getObject());
    }
}