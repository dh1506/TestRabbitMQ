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
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = "com.se445g.SE_445_G_ETL.repository.staging", entityManagerFactoryRef = "stagingEntityManagerFactory", transactionManagerRef = "stagingTransactionManager")
public class StagingDataSourceConfig {

    @Bean(name = "stagingDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.staging")
    public DataSource stagingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "stagingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean stagingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("stagingDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.se445g.SE_445_G_ETL.entity.staging")
                .persistenceUnit("staging")
                .properties(properties)
                .build();
    }

    @Bean(name = "stagingTransactionManager")
    public PlatformTransactionManager stagingTransactionManager(
            @Qualifier("stagingEntityManagerFactory") LocalContainerEntityManagerFactoryBean stagingEntityManagerFactory) {
        return new JpaTransactionManager(stagingEntityManagerFactory.getObject());
    }
}
