package ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.Group;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.Task;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.User;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Optional;


@Log4j2
public abstract class AbstractHibernateDataProvider implements DataProvider {

  protected abstract <T> Optional<T> getEntityById(Class<T> tClass, long id);

  protected abstract <T> Statuses saveOrUpdateEntity(T entity);

  protected abstract <T> Statuses deleteEntity(Class<T> tClass, long id);

  @Override
  public Optional<Task> getTask(long id) {
    return Optional.empty();
  }

  @Override
  public Optional<Group> getGroup(long id) {
    return getEntityById(Group.class, id);
  }

  @Override
  public Optional<User> getUser(long id) {
    return getEntityById(User.class, id);
  }

  @Override
  public Statuses saveTask(Task task) {
    return null;
  }

  @Override
  public Statuses saveGroup(Group group) {
    return saveOrUpdateEntity(group);
  }

  @Override
  public Statuses saveUser(User user) {
    return saveOrUpdateEntity(user);
  }

  @Override
  public Statuses deleteTask(long taskId) {
    return null;
  }

  @Override
  public Statuses deleteGroup(long groupId) {
    return deleteEntity(Group.class, groupId);
  }

  @Override
  public Statuses deleteUser(long userId) {
    return deleteEntity(User.class, userId);
  }
}
