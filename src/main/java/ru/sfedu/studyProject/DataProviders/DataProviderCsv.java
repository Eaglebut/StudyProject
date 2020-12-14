package ru.sfedu.studyProject.DataProviders;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.Metadata;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Data provider csv.
 */
@Log4j2
public class DataProviderCsv extends AbstractDataProvider {

  private static DataProviderCsv INSTANCE = null;

  private DataProviderCsv() {
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static DataProviderCsv getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataProviderCsv();
    }
    return INSTANCE;
  }


  private <T> void insertIntoCsv(Class<T> tClass,
                                 List<T> objectList) throws IOException {
    List<T> tList;
    tList = objectList;
    if (tList.isEmpty()) {
      deleteFile(tClass);
    }
    CSVWriter csvWriter = getCsvWriter(tClass);
    StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
            .withApplyQuotesToAll(false)
            .build();
    try {
      beanToCsv.write(tList);
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      log.error(e);
    }
    csvWriter.close();
  }

  private <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
    FileWriter writer;
    File path = new File(PropertyLoader.getProperty(Constants.CSV_PATH));
    File file = new File(PropertyLoader.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.CSV_EXTENSION));
    log.debug(file.getPath());
    if (!file.exists() && path.mkdirs() && !file.createNewFile()) {
      throw new IOException(
              String.format(PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                      file.getName()));
    }
    writer = new FileWriter(file);
    return new CSVWriter(writer);
  }

  private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
    File file = new File(PropertyLoader.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.CSV_EXTENSION));
    if (!file.exists() && !file.createNewFile()) {
      throw new IOException(
              String.format(
                      PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                      file.getName()));

    }
    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    return new CSVReader(bufferedReader);
  }

  private <T> List<T> getFromCsv(Class<T> tClass) throws IOException {
    List<T> tList;
    try {
      CSVReader csvReader = getCsvReader(tClass);
      CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
              .withType(tClass)
              .withIgnoreLeadingWhiteSpace(true)
              .build();
      tList = csvToBean.parse();
      csvReader.close();
    } catch (IOException e) {
      log.error(e);
      throw e;
    }
    return tList;
  }

  /**
   * Delete all bean csv files.
   */
  public void deleteAll() {
    List<Class> classList = new ArrayList<>();
    classList.add(ExtendedTask.class);
    classList.add(Group.class);
    classList.add(ModificationRecord.class);
    classList.add(PasswordedGroup.class);
    classList.add(Task.class);
    classList.add(User.class);
    classList.add(Metadata.class);
    classList.add(Group.class);
    classList.add(ExtendedTask.class);
    classList.forEach(this::deleteFile);
  }

  private <T> void deleteFile(Class<T> tClass) {
    try {
      log.debug(new File(PropertyLoader.getProperty(Constants.CSV_PATH)
              + tClass.getSimpleName().toLowerCase()
              + PropertyLoader.getProperty(Constants.CSV_EXTENSION)).delete());
    } catch (IOException e) {
      log.error(e);
    }
  }

  @Override
  protected Optional<User> getUserFromDB(long userId) {
    try {
      var userList = getFromCsv(User.class);
      var optionalUser = userList.stream()
              .filter(user -> user.getId() == userId)
              .findAny();
      if (optionalUser.isEmpty()) {
        return optionalUser;
      }
      User user = optionalUser.get();
      user.setTaskList(getTaskListFromDB(user));
      user.setHistoryList(getModificationRecordListFromDB(user));
      return Optional.of(user);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected Optional<User> getUserFromDB(String email, String password) {
    try {
      var userList = getFromCsv(User.class);
      var optionalUser = userList.stream()
              .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
              .findAny();
      if (optionalUser.isEmpty()) {
        return optionalUser;
      }
      User user = optionalUser.get();
      user.setTaskList(getTaskListFromDB(user));
      user.setHistoryList(getModificationRecordListFromDB(user));
      return Optional.of(user);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<User> getUserListFromDB() {
    try {
      var userList = getFromCsv(User.class);
      userList.forEach(user -> {
        user.setTaskList(getTaskListFromDB(user));
        user.setHistoryList(getModificationRecordListFromDB(user));
      });
      return userList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<User, UserRole> getUserListFromDB(Group group) {
    try {
      var userList = getFromCsv(User.class);
      Map<User, UserRole> memberList = new HashMap<>();
      group.getMemberList().forEach((user, userRole) -> {
        var optionalUser = userList.stream()
                .filter(serverUser -> serverUser.getId() == user.getId())
                .findAny();
        if (optionalUser.isPresent()) {
          User groupUser = optionalUser.get();
          groupUser.setHistoryList(getModificationRecordListFromDB(groupUser));
          groupUser.setTaskList(getTaskListFromDB(groupUser));
          memberList.put(optionalUser.get(), userRole);
        }
      });
      return memberList;
    } catch (IOException e) {
      log.error(e);
      return new HashMap<>();
    }
  }

  @Override
  protected Statuses saveUserInDB(User user) {
    try {
      var userList = getFromCsv(User.class);
      var optionalUser = userList.stream()
              .filter(serverUser -> serverUser.getId() == user.getId())
              .findAny();
      Statuses returnStatus;
      if (optionalUser.isPresent()) {
        userList.remove(optionalUser.get());
        returnStatus = Statuses.UPDATED;
      } else {
        returnStatus = Statuses.INSERTED;
      }
      userList.add(user);
      insertIntoCsv(User.class, userList);
      return returnStatus;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Optional<Group> getGroupFromDB(long groupId) {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      var optionalGroup = groupList.stream()
              .filter(group -> group.getId() == groupId)
              .findAny();
      if (optionalGroup.isEmpty()) {
        return optionalGroup;
      }
      Group group = optionalGroup.get();
      group.setMemberList(getUserListFromDB(group));
      group.setTaskList(getTaskListFromDB(group));
      group.setHistoryList(getModificationRecordListFromDB(group));
      return Optional.of(group);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB() {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      groupList.forEach(group -> {
        group.setMemberList(getUserListFromDB(group));
        group.setTaskList(getTaskListFromDB(group));
        group.setHistoryList(getModificationRecordListFromDB(group));
      });
      return groupList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(User user) {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      groupList = groupList.stream()
              .filter(group -> group.getMemberList().keySet()
                      .stream()
                      .anyMatch(groupUser -> groupUser.getId() == user.getId()))
              .collect(Collectors.toList());
      groupList.forEach(group -> {
        group.setMemberList(getUserListFromDB(group));
        group.setTaskList(getTaskListFromDB(group));
        group.setHistoryList(getModificationRecordListFromDB(group));
      });
      return groupList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(String name) {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      groupList = groupList.stream()
              .filter(group -> group.getName().contains(name))
              .collect(Collectors.toList());
      groupList.forEach(group -> {
        group.setMemberList(getUserListFromDB(group));
        group.setTaskList(getTaskListFromDB(group));
        group.setHistoryList(getModificationRecordListFromDB(group));
      });
      return groupList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Statuses saveGroupInDB(Group group) {
    try {
      List<Group> groupList;
      if (group.getGroupType().equals(GroupTypes.PASSWORDED)) {
        groupList = new ArrayList<>(getFromCsv(PasswordedGroup.class));
      } else {
        groupList = getFromCsv(Group.class);
      }
      var optionalGroup = groupList.stream()
              .filter(serverGroup -> serverGroup.getId() == group.getId())
              .findAny();
      Statuses returnStatus;
      if (optionalGroup.isPresent()) {
        groupList.remove(optionalGroup.get());
        returnStatus = Statuses.UPDATED;
      } else {
        returnStatus = Statuses.INSERTED;
      }
      groupList.add(group);

      if (group.getGroupType().equals(GroupTypes.PASSWORDED)) {
        List<PasswordedGroup> passwordedGroupList = new ArrayList<>();
        groupList.forEach(basicGroup -> passwordedGroupList.add((PasswordedGroup) basicGroup));
        insertIntoCsv(PasswordedGroup.class, passwordedGroupList);
      } else {
        insertIntoCsv(Group.class, groupList);
      }

      return returnStatus;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Statuses deleteGroupFromDB(long groupId) {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      var optionalGroup = groupList.stream()
              .filter(serverGroup -> serverGroup.getId() == groupId)
              .findAny();
      if (optionalGroup.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      groupList.remove(optionalGroup.get());
      if (optionalGroup.get().getGroupType().equals(GroupTypes.PASSWORDED)) {
        List<PasswordedGroup> passwordedGroupList = new ArrayList<>();
        groupList.forEach(group -> {
          if (group.getGroupType().equals(GroupTypes.PASSWORDED)) {
            passwordedGroupList.add((PasswordedGroup) group);
          }
        });
        insertIntoCsv(PasswordedGroup.class, passwordedGroupList);
      } else {
        groupList = groupList.stream()
                .filter(group -> !group.getGroupType().equals(GroupTypes.PASSWORDED))
                .collect(Collectors.toList());
        insertIntoCsv(Group.class, groupList);
      }
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Optional<Task> getTaskFromDB(long taskId) {
    try {
      var taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      var optionalTask = taskList.stream()
              .filter(task -> task.getId() == taskId)
              .findAny();
      if (optionalTask.isEmpty()) {
        return optionalTask;
      }
      Task task = optionalTask.get();
      task.setHistoryList(getModificationRecordListFromDB(task));
      return Optional.of(task);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB() {
    try {
      var taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      taskList.forEach(task -> task.setHistoryList(getModificationRecordListFromDB(task)));
      return taskList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB(User user) {
    try {
      var taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      taskList = taskList.stream()
              .filter(task -> user.getTaskList()
                      .stream()
                      .anyMatch(usersTask -> usersTask.getId() == task.getId()))
              .collect(Collectors.toList());
      taskList.forEach(task -> task.setHistoryList(getModificationRecordListFromDB(task)));
      return taskList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<Task, TaskState> getTaskListFromDB(Group group) {
    try {
      var taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      Map<Task, TaskState> taskMap = new HashMap<>();
      group.getTaskList().forEach((task, taskState) -> {
        var optionalTask = taskList.stream()
                .filter(serverTask -> serverTask.getId() == task.getId())
                .findAny();
        if (optionalTask.isPresent()) {
          Task groupTask = optionalTask.get();
          groupTask.setHistoryList(getModificationRecordListFromDB(groupTask));
          taskMap.put(optionalTask.get(), taskState);
        }
      });
      return taskMap;
    } catch (IOException e) {
      log.error(e);
      return new HashMap<>();
    }
  }

  @Override
  protected Statuses saveTaskInDB(Task task) {
    try {
      List<Task> taskList;
      if (task.getTaskType().equals(TaskTypes.EXTENDED)) {
        taskList = new ArrayList<>(getFromCsv(ExtendedTask.class));
      } else {
        taskList = getFromCsv(Task.class);
      }
      var optionalTask = taskList.stream()
              .filter(serverTask -> serverTask.getId() == task.getId())
              .findAny();
      Statuses returnStatus;
      if (optionalTask.isPresent()) {
        taskList.remove(optionalTask.get());
        returnStatus = Statuses.UPDATED;
      } else {
        returnStatus = Statuses.INSERTED;
      }
      taskList.add(task);
      if (task.getTaskType().equals(TaskTypes.EXTENDED)) {
        List<ExtendedTask> extendedTaskList = new ArrayList<>();
        taskList.forEach(basicTask -> extendedTaskList.add((ExtendedTask) basicTask));
        insertIntoCsv(ExtendedTask.class, extendedTaskList);
      } else {
        insertIntoCsv(Task.class, taskList);
      }
      return returnStatus;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Statuses deleteTaskFromDB(long taskId) {
    try {
      var taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      var optionalTask = taskList.stream()
              .filter(serverTask -> serverTask.getId() == taskId)
              .findAny();
      if (optionalTask.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      taskList.remove(optionalTask.get());
      if (optionalTask.get().getTaskType().equals(TaskTypes.EXTENDED)) {
        List<ExtendedTask> extendedTaskList = new ArrayList<>();
        taskList.forEach(task -> {
          if (task.getTaskType().equals(TaskTypes.EXTENDED)) {
            extendedTaskList.add((ExtendedTask) task);
          }
        });
        insertIntoCsv(ExtendedTask.class, extendedTaskList);
      } else {
        taskList = taskList.stream()
                .filter(group -> group.getTaskType().equals(TaskTypes.BASIC))
                .collect(Collectors.toList());
        insertIntoCsv(Task.class, taskList);
      }
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(User user) {
    try {
      List<ModificationRecord> objectHistoryList = user.getHistoryList();
      List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
      return historyList
              .stream()
              .filter(modificationRecord -> objectHistoryList
                      .stream()
                      .anyMatch(userModificationRecord ->
                              userModificationRecord.getId() == modificationRecord.getId()))
              .collect(Collectors.toList());
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Task task) {
    try {
      List<ModificationRecord> objectHistoryList = task.getHistoryList();
      List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
      return historyList
              .stream()
              .filter(modificationRecord -> objectHistoryList
                      .stream()
                      .anyMatch(userModificationRecord ->
                              userModificationRecord.getId() == modificationRecord.getId()))
              .collect(Collectors.toList());
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Group group) {
    try {
      List<ModificationRecord> objectHistoryList = group.getHistoryList();
      List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
      return historyList
              .stream()
              .filter(modificationRecord -> objectHistoryList
                      .stream()
                      .anyMatch(userModificationRecord ->
                              userModificationRecord.getId() == modificationRecord.getId()))
              .collect(Collectors.toList());
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected void createModificationRecordInDB(ModificationRecord modificationRecord) {
    try {
      var modificationRecordList = getFromCsv(ModificationRecord.class);
      modificationRecordList.add(modificationRecord);
      insertIntoCsv(ModificationRecord.class, modificationRecordList);
    } catch (IOException e) {
      log.error(e);
    }
  }

  @Override
  protected <T> long getNextId(Class<T> tClass) {
    try {
      List<Metadata> metadataList = getFromCsv(Metadata.class);
      Optional<Metadata> optionalMetadata = metadataList.stream()
              .filter(metadata -> metadata.getClassName().equals(tClass.getSimpleName().toLowerCase()))
              .findAny();
      if (optionalMetadata.isEmpty()) {
        nextId(tClass);
        return 0;
      }
      nextId(tClass);
      return optionalMetadata.get().getLastId();
    } catch (IOException e) {
      log.error(e);
      return 0;
    }
  }

  private <T> void nextId(Class<T> tClass) throws IOException {
    List<Metadata> metadataList = getFromCsv(Metadata.class);
    Optional<Metadata> optionalMetadata = metadataList.stream()
            .filter(metadata -> metadata.getClassName().equals(tClass.getSimpleName().toLowerCase()))
            .findAny();
    optionalMetadata.ifPresentOrElse(metadata -> {
              metadata.setLastId(metadata.getLastId() + 1);
              metadataList.set(metadataList.indexOf(metadata), metadata);
            },
            () -> {
              Metadata metadata = new Metadata();
              metadata.setClassName(tClass.getSimpleName().toLowerCase());
              metadata.setLastId(1L);
              metadataList.add(metadata);
            }
    );
    insertIntoCsv(Metadata.class, metadataList);
  }

}