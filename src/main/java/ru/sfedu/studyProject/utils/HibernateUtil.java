package ru.sfedu.studyProject.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.lab2.model.SecondTestEntity;
import ru.sfedu.studyProject.lab2.model.TestEntity;

import java.io.File;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static final Class[] classList = new Class[]{
            TestEntity.class,
            SecondTestEntity.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.User.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.Group.class
    };


    private static void registerClasses(MetadataSources metadataSources) {
        for (Class clazz : classList) {
            metadataSources.addAnnotatedClass(clazz);
        }
    }


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
            registerClasses(metadataSources);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
        }
        return sessionFactory;
    }

}
