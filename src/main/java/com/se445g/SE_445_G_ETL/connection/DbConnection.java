package com.se445g.SE_445_G_ETL.connection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbConnection {

    private static SessionFactory sessionFactory = null;

    private DbConnection() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            sessionFactory = configuration.configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return sessionFactory;
    }
}
