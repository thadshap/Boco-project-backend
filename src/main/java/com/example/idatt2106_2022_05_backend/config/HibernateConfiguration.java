package com.example.idatt2106_2022_05_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.Properties;

//@Configuration
//public class HibernateConfiguration {
//    Properties hibernateProperties() {
//        Properties properties = new Properties();
//        properties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
//
//        @Bean
//        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//            LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//            emf.setDataSource(dataSource);
//            emf.setJpaProperties(hibernateProperties());
//}
