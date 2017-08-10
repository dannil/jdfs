package com.github.dannil.jdfs.manager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistenceManager {

    private static final Logger LOGGER = LogManager.getLogger(PersistenceManager.class);

    private static PersistenceManager manager;

    private static Object mutex = new Object();

    protected EntityManagerFactory emf;

    public static PersistenceManager getInstance() {
        if (manager == null) {
            synchronized (mutex) {
                if (manager == null) {
                    manager = new PersistenceManager();
                }
            }
        }
        return manager;
    }

    private PersistenceManager() {

    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            createEntityManagerFactory();
        }
        return this.emf;

        // Properties properties = new Properties();
        //
        // properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        // properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        // properties.put("hibernate.connection.url", "jdbc:h2:./db/test");
        // properties.put("hibernate.connection.username", "abc");
        // properties.put("hibernate.connection.password", "");
        // // properties.put("hibernate.show_sql", "true");
        // properties.put("hibernate.show_sql", "false");
        // properties.put("hibernate.hbm2ddl.auto", "update");
        //
        // // Hibernate C3P0 connection pool configuration
        // properties.put("hibernate.c3p0.min_size", "5");
        // properties.put("hibernate.c3p0.max_size", "20");
        // properties.put("hibernate.c3p0.timeout", "300");
        // properties.put("hibernate.c3p0.max_statements", "50");
        // properties.put("hibernate.c3p0.idle_test_period", "3000");
        //
        // StandardServiceRegistryBuilder standardServiceRegistryBuilder = new
        // StandardServiceRegistryBuilder();
        // standardServiceRegistryBuilder.applySettings(properties);
        //
        // MetadataSources metadataSources = new
        // MetadataSources(standardServiceRegistryBuilder.build());
        // metadataSources.addAnnotatedClass(File.class);
        // return
        // metadataSources.getMetadataBuilder().build().buildSessionFactory().createEntityManager();
    }

    public void closeEntityManagerFactory() {
        if (this.emf != null) {
            this.emf.close();
        }
        this.emf = null;
    }

    protected void createEntityManagerFactory() {
        LOGGER.info("Creating database connection...");
        this.emf = Persistence.createEntityManagerFactory("persistence");
    }

}
