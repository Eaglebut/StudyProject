package ru.sfedu.studyProject.lab5.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.sfedu.studyProject.lab5.model.Group;
import ru.sfedu.studyProject.utils.HibernateUtil;

import javax.persistence.Query;
import java.util.Optional;

@Log4j2
public class SqlQueryLab5 implements QueryLab5 {

  @Override
  public Optional<Group> getGroup(long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
      Query query = session.createQuery("select group from Group_LAB5 as group where id=:id", Group.class)
              .setParameter("id", id);
      Group group = (Group) query.getSingleResult();
      log.debug(query);
      return Optional.ofNullable(group);
    } catch (Exception exception) {
      session.close();
      log.error(exception);
      return Optional.empty();
    }
  }

}
