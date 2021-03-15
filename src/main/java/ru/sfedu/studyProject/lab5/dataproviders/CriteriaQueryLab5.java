package ru.sfedu.studyProject.lab5.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.sfedu.studyProject.lab5.model.Group;
import ru.sfedu.studyProject.utils.HibernateUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Log4j2
public class CriteriaQueryLab5 implements QueryLab5 {

  @Override
  public Optional<Group> getGroup(long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<Group> cq = cb.createQuery(Group.class);
      Root<Group> root = cq.from(Group.class);
      cq.select(root).where(cb.equal(root.get("id"), id));
      Group group = session.createQuery(cq).getSingleResult();
      session.close();
      log.info(group.toString());
      return Optional.ofNullable(group);
    } catch (Exception exception) {
      session.close();
      log.error(exception);
      return Optional.empty();
    }
  }
}
