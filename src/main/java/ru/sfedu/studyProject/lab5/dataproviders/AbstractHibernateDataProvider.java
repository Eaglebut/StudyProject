package ru.sfedu.studyProject.lab5.dataproviders;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;
import ru.sfedu.studyProject.lab5.model.*;
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

  @Override
  public Statuses saveAddress(Address address) {
    return saveOrUpdateEntity(address);
  }

  @Override
  public Statuses deleteAddress(long addressId) {
    return deleteEntity(Address.class, addressId);
  }

  @Override
  public Optional<Address> getAddress(long id) {
    return getEntityById(Address.class, id);
  }

  @Override
  public Statuses saveAssigment(Assigment assigment) {
    return saveOrUpdateEntity(assigment);
  }

  @Override
  public Statuses deleteAssigment(long addressId) {
    return deleteEntity(Assigment.class, addressId);
  }

  @Override
  public Optional<Assigment> getAssigment(long id) {
    return getEntityById(Assigment.class, id);
  }

  @Override
  public Statuses saveMetadata(Metadata metadata) {
    return saveOrUpdateEntity(metadata);
  }

  @Override
  public Statuses deleteMetadata(long addressId) {
    return deleteEntity(Metadata.class, addressId);
  }

  @Override
  public Optional<Metadata> getMetadata(long id) {
    return getEntityById(Metadata.class, id);
  }

}
