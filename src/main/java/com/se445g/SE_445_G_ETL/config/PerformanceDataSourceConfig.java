package com.se445g.SE_445_G_ETL.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = "com.se445g.SE_445_G_ETL.repository.performance", entityManagerFactoryRef = "performanceEntityManagerFactory", transactionManagerRef = "performanceTransactionManager")
public class PerformanceDataSourceConfig {

    @Primary
    @Bean(name = "performanceDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.performance")
    public DataSource performanceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "performanceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean performanceEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("performanceDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.se445g.SE_445_G_ETL.entity.performance")
                .persistenceUnit("performance")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "performanceTransactionManager")
    public PlatformTransactionManager performanceTransactionManager(
            @Qualifier("performanceEntityManagerFactory") LocalContainerEntityManagerFactoryBean performanceEntityManagerFactory) {
        return new JpaTransactionManager(performanceEntityManagerFactory.getObject());
    }
}
