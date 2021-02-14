package ru.sfedu.studyProject.lab2;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.lab2.model.TestEntity;
import ru.sfedu.studyProject.utils.HibernateUtil;

import java.util.Optional;

@Log4j2
public class HibernateDataProvider {

    public static void setUp() {
        System.setProperty(Constants.HIBERNATE_CONFIG_PATH, Constants.HIBERNATE_LAB2_CONFIG_PATH);
    }

    public static Optional<TestEntity> getTestEntity(long id) {
        try {
            TestEntity entity;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();
            entity = session.get(TestEntity.class, id);
            session.getTransaction().commit();
            session.close();
            return entity != null
                    ? Optional.of(entity)
                    : Optional.empty();
        } catch (ConstraintViolationException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    public static boolean setTestEntity(TestEntity entity) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (ConstraintViolationException e) {
            log.error(e);
            return false;
        }
    }

    public static boolean deleteTestEntity(TestEntity entity) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();
            session.delete(entity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (ConstraintViolationException e) {
            log.error(e);
            return false;
        }
    }

}
