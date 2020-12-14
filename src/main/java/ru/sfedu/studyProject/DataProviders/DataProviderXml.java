package ru.sfedu.studyProject.DataProviders;

import ru.sfedu.studyProject.enums.Statuses;
import ru.sfedu.studyProject.enums.TaskState;
import ru.sfedu.studyProject.enums.UserRole;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataProviderXml extends AbstractDataProvider {
  @Override
  protected Optional<User> getUserFromDB(long userId) {
    return Optional.empty();
  }

  @Override
  protected Optional<User> getUserFromDB(String email, String password) {
    return Optional.empty();
  }

  @Override
  protected List<User> getUserListFromDB() {
    return null;
  }

  @Override
  protected Map<User, UserRole> getUserListFromDB(Group group) {
    return null;
  }

  @Override
  protected Statuses saveUserInDB(User user) {
    return null;
  }

  @Override
  protected Optional<Group> getGroupFromDB(long groupId) {
    return Optional.empty();
  }

  @Override
  protected List<Group> getGroupListFromDB() {
    return null;
  }

  @Override
  protected List<Group> getGroupListFromDB(User user) {
    return null;
  }

  @Override
  protected List<Group> getGroupListFromDB(String name) {
    return null;
  }

  @Override
  protected Statuses saveGroupInDB(Group group) {
    return null;
  }

  @Override
  protected Statuses deleteGroupFromDB(long groupId) {
    return null;
  }

  @Override
  protected Optional<Task> getTaskFromDB(long TaskId) {
    return Optional.empty();
  }

  @Override
  protected List<Task> getTaskListFromDB() {
    return null;
  }

  @Override
  protected List<Task> getTaskListFromDB(User user) {
    return null;
  }

  @Override
  protected Map<Task, TaskState> getTaskListFromDB(Group group) {
    return null;
  }

  @Override
  protected Statuses saveTaskInDB(Task task) {
    return null;
  }

  @Override
  protected Statuses deleteTaskFromDB(long taskId) {
    return null;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(User user) {
    return null;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Task task) {
    return null;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Group group) {
    return null;
  }

  @Override
  protected void createModificationRecordInDB(ModificationRecord modificationRecord) {

  }

  @Override
  protected <T> long getNextId(Class<T> tClass) {
    return 0;
  }
}
