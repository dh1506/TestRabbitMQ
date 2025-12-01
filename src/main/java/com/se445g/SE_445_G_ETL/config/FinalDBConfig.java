package com.se445g.SE_445_G_ETL.config;

import javax.sql.DataSource;

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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.se445g.SE_445_G_ETL.repository.finaldb",
        entityManagerFactoryRef = "finalEntityManager",
        transactionManagerRef = "finalTransactionManager"
)
public class FinalDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.final")
    public DataSource finalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean finalEntityManager(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(finalDataSource())
                .packages("com.se445g.SE_445_G_ETL.entity.finaldb")
                .persistenceUnit("finalDB")
                .build();
    }

    @Bean
    public PlatformTransactionManager finalTransactionManager(
            @Qualifier("finalEntityManager") EntityManagerFactory finalEntityManager) {
        return new JpaTransactionManager(finalEntityManager);
    }
}
