package ru.sfedu.studyProject.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.sfedu.studyProject.Constants;

import java.io.File;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            File configFile = new File(System.getProperty(Constants.HIBERNATE_CONFIG_PATH) == null
                    ? Constants.HIBERNATE_DEFAULT_CONFIG_PATH
                    : System.getProperty(Constants.HIBERNATE_CONFIG_PATH));
            sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        }
        return sessionFactory;
    }

}
