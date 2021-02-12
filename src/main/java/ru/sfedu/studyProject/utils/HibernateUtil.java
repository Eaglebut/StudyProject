package ru.sfedu.studyProject.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.sfedu.studyProject.Constants;

import java.io.File;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            File configFile = new File(System.getProperty(Constants.HIBERNATE_CONFIG_PATH) == null
                    ? Constants.HIBERNATE_DEFAULT_CONFIG_PATH
                    : System.getProperty(Constants.HIBERNATE_CONFIG_PATH));
            Configuration configuration = new Configuration().configure(configFile);
            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            MetadataSources metadataSources =
                    new MetadataSources(serviceRegistry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
        }
        return sessionFactory;
    }

}
