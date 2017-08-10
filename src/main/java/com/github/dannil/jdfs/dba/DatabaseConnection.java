package com.github.dannil.jdfs.dba;

import java.util.Properties;

import com.github.dannil.jdfs.model.File;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class DatabaseConnection {

    public static SessionFactory getSessionFactory() throws HibernateException {
        Properties properties = new Properties();

        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.url", "jdbc:h2:./db/test");
        properties.put("hibernate.connection.username", "abc");
        properties.put("hibernate.connection.password", "");
        // properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "update");

        // Hibernate C3P0 connection pool configuration
        properties.put("hibernate.c3p0.min_size", "5");
        properties.put("hibernate.c3p0.max_size", "20");
        properties.put("hibernate.c3p0.timeout", "300");
        properties.put("hibernate.c3p0.max_statements", "50");
        properties.put("hibernate.c3p0.idle_test_period", "3000");

        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        standardServiceRegistryBuilder.applySettings(properties);

        MetadataSources metadataSources = new MetadataSources(standardServiceRegistryBuilder.build());
        metadataSources.addAnnotatedClass(File.class);
        return metadataSources.getMetadataBuilder().build().buildSessionFactory();
    }

}
