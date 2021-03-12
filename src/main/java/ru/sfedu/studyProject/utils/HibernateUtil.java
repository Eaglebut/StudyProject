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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static final List<Class> classList = new ArrayList<>(Arrays.asList(new Class[]{
            TestEntity.class,
            SecondTestEntity.class
    }));

    public static void addClassesToRegister(List<Class> classesToAdd) {
        classList.addAll(classesToAdd);
    }

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
