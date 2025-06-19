package com.ygh.infra.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ygh.infra.persistence.transaction",
        entityManagerFactoryRef = "transactionEntityManagerFactory",
        transactionManagerRef = "transactionTransactionManager"
)
@EnableTransactionManagement
public class TransactionDataSourceConfig {
    @Value("${transaction.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    @ConfigurationProperties("spring.datasource.transaction")
    public DataSourceProperties transactionDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource transactionDataSource() {
        return transactionDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "transactionEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean transactionEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(transactionDataSource());
        emf.setPackagesToScan("com.ygh.transaction");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);  // DDL 생성 활성화
        vendorAdapter.setShowSql(true);      // SQL 출력 활성화
        emf.setJpaVendorAdapter(vendorAdapter);

        // JPA 프로퍼티 설정
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);  // 테이블 자동 생성/업데이트 설정
        emf.setJpaPropertyMap(properties);

        emf.setPersistenceUnitName("transaction");
        return emf;
    }

    @Bean(name = "transactionTransactionManager")
    public PlatformTransactionManager transactionTransactionManager(
            @Qualifier("transactionEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }

}
