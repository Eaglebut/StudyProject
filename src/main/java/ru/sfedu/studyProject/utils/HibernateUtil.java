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
            SecondTestEntity.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab3.singleTable.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab4.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab5.model.ExtendedTask.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.Group.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.Group.class,
            ru.sfedu.studyProject.lab3.singleTable.model.Group.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.Group.class,
            ru.sfedu.studyProject.lab5.model.Group.class,
            ru.sfedu.studyProject.lab4.model.Group.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.Lesson.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.Lesson.class,
            ru.sfedu.studyProject.lab3.singleTable.model.Lesson.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.Lesson.class,
            ru.sfedu.studyProject.lab5.model.Lesson.class,
            ru.sfedu.studyProject.lab4.model.Lesson.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.Task.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.Task.class,
            ru.sfedu.studyProject.lab3.singleTable.model.Task.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.Task.class,
            ru.sfedu.studyProject.lab5.model.Task.class,
            ru.sfedu.studyProject.lab4.model.Task.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.User.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.User.class,
            ru.sfedu.studyProject.lab3.singleTable.model.User.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.User.class,
            ru.sfedu.studyProject.lab5.model.User.class,
            ru.sfedu.studyProject.lab4.model.User.class,
            ru.sfedu.studyProject.lab3.mappedSuperclass.model.WorkTask.class,
            ru.sfedu.studyProject.lab3.joinedTable.model.WorkTask.class,
            ru.sfedu.studyProject.lab3.singleTable.model.WorkTask.class,
            ru.sfedu.studyProject.lab3.tablePerClass.model.WorkTask.class,
            ru.sfedu.studyProject.lab5.model.WorkTask.class,
            ru.sfedu.studyProject.lab4.model.WorkTask.class,
            ru.sfedu.studyProject.lab4.model.Adress.class,
            ru.sfedu.studyProject.lab4.model.Assigment.class
    }));

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
