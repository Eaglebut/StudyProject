package ru.sfedu.studyProject.lab3.tablePerClass.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.lab3.tablePerClass.model.User;
import ru.sfedu.studyProject.utils.HibernateUtil;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Optional;

@Log4j2
public class HibernateDataProvider extends AbstractHibernateDataProvider {

  public HibernateDataProvider() {
    System.setProperty(Constants.HIBERNATE_CONFIG_PATH, Constants.HIBERNATE_LAB3_CONFIG_PATH);
  }

  @Override
  protected <T> Optional<T> getEntityById(Class<T> tClass, long id) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      T entity = session.get(tClass, id);
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

  @Override
  protected <T> Statuses saveOrUpdateEntity(T entity) {
    try {
      Session session = HibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      session.saveOrUpdate(entity);
      session.getTransaction().commit();
      session.close();
      return Statuses.SUCCESSFUL;
    } catch (ConstraintViolationException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected <T> Statuses deleteEntity(Class<T> tClass, long id) {
    try {
      var optEntity = getEntityById(tClass, id);
      if (optEntity.isEmpty()) {
        return Statuses.FAILED;
      }
      Session session = HibernateUtil.getSessionFactory().openSession();
      session.getTransaction().begin();
      session.delete(optEntity.get());
      session.getTransaction().commit();
      session.close();
      return Statuses.SUCCESSFUL;
    } catch (ConstraintViolationException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  public Optional<User> getUser(String email, String password) {
    return Optional.empty();
  }
}
