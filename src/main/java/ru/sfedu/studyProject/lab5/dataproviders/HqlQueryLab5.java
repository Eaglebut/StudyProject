package ru.sfedu.studyProject.lab5.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.sfedu.studyProject.lab5.model.Group;
import ru.sfedu.studyProject.utils.HibernateUtil;

import javax.persistence.Query;
import java.util.Optional;

@Log4j2
public class HqlQueryLab5 implements QueryLab5 {

  public Optional<Group> getGroup(long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
      Query query = session.createQuery("from Group_LAB5 where id=:id").setParameter("id", id);
      Group group = (Group) query.getSingleResult();
      log.debug(group.toString());
      return Optional.ofNullable(group);
    } catch (Exception exception) {
      session.close();
      log.error(exception);
      return Optional.empty();
    }
  }

}
