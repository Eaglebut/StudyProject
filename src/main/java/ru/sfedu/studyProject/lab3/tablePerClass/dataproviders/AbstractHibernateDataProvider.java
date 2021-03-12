package ru.sfedu.studyProject.lab3.tablePerClass.dataproviders;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;
import ru.sfedu.studyProject.lab3.tablePerClass.model.*;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Optional;


@Log4j2
public abstract class AbstractHibernateDataProvider implements DataProvider {

  protected abstract <T> Optional<T> getEntityById(Class<T> tClass, long id);

  protected abstract <T> Statuses saveOrUpdateEntity(T entity);

  protected abstract <T> Statuses deleteEntity(Class<T> tClass, long id);

  @Override
  public Optional<? extends Task> getTask(long id, TaskTypes taskType) {
    switch (taskType) {
      case LESSON:
        return getEntityById(Lesson.class, id);

      case EXTENDED:
        return getEntityById(ExtendedTask.class, id);

      case WORK_TASK:
        return getEntityById(WorkTask.class, id);

      case BASIC:
        return getEntityById(Task.class, id);

      default:
        return Optional.empty();
    }
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
    return saveOrUpdateEntity(task);
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
  public Statuses deleteTask(long taskId, TaskTypes taskType) {
    switch (taskType) {
      case LESSON:
        return deleteEntity(Lesson.class, taskId);

      case EXTENDED:
        return deleteEntity(ExtendedTask.class, taskId);

      case WORK_TASK:
        return deleteEntity(WorkTask.class, taskId);

      case BASIC:
        return deleteEntity(Task.class, taskId);

      default:
        return Statuses.FAILED;
    }
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
