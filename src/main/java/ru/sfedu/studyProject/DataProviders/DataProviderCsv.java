package ru.sfedu.studyProject.DataProviders;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.Metadata;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataProviderCsv implements DataProvider {

  private static final Logger log = LogManager.getLogger(DataProviderCsv.class);
  private static DataProvider INSTANCE = null;



  private DataProviderCsv() {
  }


  public static DataProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataProviderCsv();
    }
    return INSTANCE;
  }


  private <T> void insertIntoCsv(T object) throws IOException {
    insertIntoCsv(object.getClass(), Collections.singletonList(object), false);
  }


  private <T> void insertIntoCsv(Class<?> tClass,
                                 List<T> objectList,
                                 boolean overwrite) throws IOException {
    List<T> tList;
    if (!overwrite) {
      tList = (List<T>) getFromCsv(tClass);
      tList.addAll(objectList);
    } else {
      tList = objectList;
    }
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
    if (!file.exists()) {
      if (path.mkdirs()) {
        if (!file.createNewFile()) {
          throw new IOException(
                  String.format(PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                          file.getName()));
        }
      }
    }
    writer = new FileWriter(file);
    return new CSVWriter(writer);
  }



  private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
    File file = new File(PropertyLoader.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.CSV_EXTENSION));

    if (!file.exists()) {
      if (!file.createNewFile()) {
        throw new IOException(
                String.format(
                        PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                        file.getName()));
      }
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


  private Optional<User> getUserProfile(long userId) {
    try {
      List<User> userList = getFromCsv(User.class);
      return userList
              .stream()
              .filter(user -> user.getId() == userId)
              .findFirst();
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private Optional<User> getUserProfile(String email,
                                        String password) {
    try {
      List<User> userList = getFromCsv(User.class);
      return userList
              .stream()
              .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
              .findFirst();

    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private <T> List<ModificationRecord> getHistoryList(Class<T> tClass, T object) {
    try {
      List<ModificationRecord> objectHistoryList;
      if (tClass.equals(ExtendedTask.class)) {
        ExtendedTask extendedTask = (ExtendedTask) object;
        objectHistoryList = extendedTask.getHistoryList();
      } else if (tClass.equals(Group.class)) {
        Group group = (Group) object;
        objectHistoryList = group.getHistoryList();
      } else if (tClass.equals(PasswordedGroup.class)) {
        PasswordedGroup passwordedGroup = (PasswordedGroup) object;
        objectHistoryList = passwordedGroup.getHistoryList();
      } else if (tClass.equals(Task.class)) {
        Task task = (Task) object;
        objectHistoryList = task.getHistoryList();
      } else if (tClass.equals(User.class)) {
        User user = (User) object;
        objectHistoryList = user.getHistoryList();
      } else {
        return null;
      }


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
  public Optional<User> getUser(long userId) {
    Optional<User> optionalUser = getUserProfile(userId);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      user.setTaskList(getTasks(user));
      user.setHistoryList(getHistoryList(User.class, user));
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }


  @Override
  public Optional<User> getUser(@NonNull String email,
                                @NonNull String password) {
    Optional<User> optionalUser = getUserProfile(email, password);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      user.setTaskList(getTasks(user));
      user.setHistoryList(getHistoryList(User.class, user));
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }


  private Optional<Task> getTask(long id) {
    try {
      List<Task> taskList = getFromCsv(Task.class);
      taskList.addAll(getFromCsv(ExtendedTask.class));
      var optionalTask = taskList.stream()
              .filter(task -> task.getId() == id)
              .findAny();
      if (optionalTask.isEmpty()) {
        return optionalTask;
      }
      var task = optionalTask.get();
      switch (task.getTaskType()) {
        case EXTENDED -> task.setHistoryList(
                getHistoryList(ExtendedTask.class, (ExtendedTask) task));
        case BASIC -> task.setHistoryList(getHistoryList(Task.class, task));
        default -> {
          return Optional.empty();
        }
      }
      return Optional.of(task);

    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private List<Task> getTasks(@NonNull User user) throws NoSuchElementException {
    List<Task> usersTaskIdList = user.getTaskList();
    List<Task> taskList = new ArrayList<>();
    usersTaskIdList.forEach(task -> {
      var optionalTask = getTask(task.getId());
      optionalTask.ifPresent(taskList::add);
    });
    return taskList;
  }


  private <T> long getNextId(Class<T> tClass) throws IOException {
    List<Metadata> metadataList = getFromCsv(Metadata.class);
    Optional<Metadata> optionalMetadata = metadataList.stream()
            .filter(metadata -> metadata.getClassName().equals(tClass.getSimpleName().toLowerCase()))
            .findAny();
    if (optionalMetadata.isEmpty()) {
      return 0;
    }
    return optionalMetadata.get().getLastId();
  }


  private <T> void nextId(Class<T> tClass) throws IOException {
    List<Metadata> metadataList = getFromCsv(Metadata.class);
    Optional<Metadata> optionalMetadata = metadataList.stream()
            .filter(metadata -> metadata.getClassName().equals(tClass.getSimpleName().toLowerCase()))
            .findAny();
    Metadata metadata;
    if (optionalMetadata.isEmpty()) {
      metadata = new Metadata();
      metadata.setClassName(tClass.getSimpleName().toLowerCase());
      metadata.setLastId(1L);
    } else {
      metadata = optionalMetadata.get();
      metadataList.remove(metadata);
      metadata.setLastId(metadata.getLastId() + 1);
    }
    metadataList.add(metadata);
    insertIntoCsv(Metadata.class, metadataList, true);
  }


  private ModificationRecord addHistoryRecord(String changedValueName, OperationType operationType, String changedValue) {
    try {
      ModificationRecord record = new ModificationRecord();
      record.setId(getNextId(ModificationRecord.class));
      record.setChangedValueName(changedValueName);
      record.setOperationType(operationType);
      record.setChangedDate(new Date(System.currentTimeMillis()));
      record.setChangedValue(changedValue);
      insertIntoCsv(record);
      nextId(ModificationRecord.class);
      return record;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }


  @Override
  public Statuses createTask(long userId,
                             @NonNull String taskName,
                             @NonNull TaskStatuses status) {
    try {
      Task createdTask = new Task();
      createdTask.setId(getNextId(Task.class));
      createdTask.setName(taskName);
      createdTask.setTaskType(TaskTypes.BASIC);
      createdTask.setCreated(new Date(System.currentTimeMillis()));
      createdTask.setHistoryList(new ArrayList<>());
      createdTask.setStatus(status);
      log.debug(createdTask);
      insertIntoCsv(createdTask);
      nextId(Task.class);
      Optional<User> optionalUser = getUser(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      } else {
        User user = optionalUser.get();
        user.getTaskList().add(createdTask);
        user.getHistoryList()
                .add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                        OperationType.ADD,
                        String.valueOf(createdTask.getId())));
        var updateUserStatus = updateUser(user);
        if (updateUserStatus != Statuses.UPDATED) {
          return updateUserStatus;
        }
      }

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
    return Statuses.INSERTED;
  }


  @Override
  public Statuses createTask(long userId,
                             @NonNull String taskName,
                             @NonNull TaskStatuses status,
                             @NonNull RepetitionTypes repetitionType,
                             @NonNull RemindTypes remindType,
                             @NonNull Importances importance,
                             @NonNull String description,
                             @NonNull Date time) {
    try {
      var optionalUser = getUser(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      User user = optionalUser.get();
      ExtendedTask task = new ExtendedTask();
      task.setId(getNextId(Task.class));
      task.setName(taskName);
      task.setTaskType(TaskTypes.EXTENDED);
      task.setStatus(status);
      task.setRepetitionType(repetitionType);
      task.setRemindType(remindType);
      task.setImportance(importance);
      task.setDescription(description);
      task.setTime(time);
      task.setCreated(new Date(System.currentTimeMillis()));
      task.setHistoryList(new ArrayList<>());
      user.getTaskList().add(task);
      nextId(Task.class);
      var updateUserStatus = updateUser(user);
      if (updateUserStatus != Statuses.UPDATED) {
        return updateUserStatus;
      }
      insertIntoCsv(task);
      return Statuses.INSERTED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses deleteTask(long userId, long taskId) {
    try {
      var optionalUser = getUser(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      User user = optionalUser.get();
      Optional<Task> optionalTaskToDelete = user.getTaskList().stream()
              .filter(task -> task.getId() == taskId)
              .findAny();
      if (optionalTaskToDelete.isEmpty()) {
        return Statuses.FORBIDDEN;
      } else {
        Task taskToDelete = optionalTaskToDelete.get();
        user.getTaskList().remove(taskToDelete);

        switch (taskToDelete.getTaskType()) {
          case BASIC -> insertIntoCsv(Task.class, getFromCsv(Task.class)
                  .stream()
                  .filter(task -> taskId != task.getId())
                  .collect(Collectors.toList()), true);

          case EXTENDED -> insertIntoCsv(ExtendedTask.class, getFromCsv(ExtendedTask.class)
                  .stream()
                  .filter(task -> task.getId() != taskId)
                  .collect(Collectors.toList()), true);

          default -> {
            return Statuses.FAILED;
          }
        }

        user.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                OperationType.DELETE,
                String.valueOf(taskId)));
        var updateUserStatus = updateUser(user);
        if (updateUserStatus != Statuses.UPDATED) {
          return updateUserStatus;
        }
        return Statuses.DELETED;
      }
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private void saveChangesHistory(Task task, Task editedTask) {
    try {
      if (!task.getName().equals(editedTask.getName())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                OperationType.EDIT,
                task.getName()));
      }
      if (!task.getStatus().equals(editedTask.getStatus())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_STATUS),
                OperationType.EDIT,
                task.getStatus().name()));
      }
    } catch (IOException e) {
      log.error(e);
    }
  }


  private void saveChangesHistory(ExtendedTask task, ExtendedTask editedTask) {
    try {
      saveChangesHistory((Task) task, editedTask);
      if (!task.getDescription().equals(editedTask.getDescription())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_DESCRIPTION),
                OperationType.EDIT,
                task.getDescription()));
      }
      if (!task.getImportance().equals(editedTask.getImportance())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_IMPORTANCE),
                OperationType.EDIT,
                task.getImportance().name()));
      }
      if (!task.getRemindType().equals(editedTask.getRemindType())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_REMIND_TYPE),
                OperationType.EDIT,
                task.getRemindType().name()));
      }
      if (!task.getRepetitionType().equals(editedTask.getRepetitionType())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_REPETITION_TYPE),
                OperationType.EDIT,
                task.getRepetitionType().name()));
      }
      if (!task.getTime().equals(editedTask.getTime())) {
        editedTask.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_TIME),
                OperationType.EDIT,
                task.getTime().toString()));
      }
    } catch (IOException e) {
      log.error(e);
    }
  }


  private boolean isEditValid(Task task, Task editedTask) {
    return task.getHistoryList().equals(editedTask.getHistoryList())
            && task.getCreated().getTime() == editedTask.getCreated().getTime()
            && task.getTaskType().equals(editedTask.getTaskType());
  }


  @Override
  public Statuses editTask(long userId, @NonNull Task editedTask) {
    try {
      var optionalUser = getUser(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      User user = optionalUser.get();
      var optionalUserTask = user.getTaskList().stream()
              .filter(task -> task.getId() == editedTask.getId())
              .findAny();
      if (optionalUserTask.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      if (!isEditValid(editedTask, optionalUserTask.get())) {
        return Statuses.FORBIDDEN;
      }
      switch (optionalUserTask.get().getTaskType()) {
        case BASIC -> {
          Task userTask = optionalUserTask.get();
          List<Task> taskList = getFromCsv(Task.class);
          taskList.remove(userTask);
          saveChangesHistory(userTask, editedTask);
          taskList.add(editedTask);
          insertIntoCsv(Task.class, taskList, true);
          return Statuses.UPDATED;
        }
        case EXTENDED -> {
          ExtendedTask userTask = (ExtendedTask) optionalUserTask.get();
          List<ExtendedTask> taskList = getFromCsv(ExtendedTask.class);
          taskList.remove(userTask);
          saveChangesHistory(userTask, (ExtendedTask) editedTask);
          taskList.add((ExtendedTask) editedTask);
          insertIntoCsv(ExtendedTask.class, taskList, true);
          return Statuses.UPDATED;
        }
        default -> {
          return Statuses.FAILED;
        }
      }
    } catch (IOException e) {
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses createUser(@NonNull String email,
                             @NonNull String password,
                             @NonNull String name,
                             @NonNull String surname,
                             @NonNull SignUpTypes signUpType) {
    try {
      List<User> userList = getFromCsv(User.class);
      if (userList.stream().anyMatch(listUser -> listUser.getEmail().equals(email))) {
        return Statuses.FORBIDDEN;
      }
      User user = new User();
      user.setId(getNextId(User.class));
      user.setEmail(email);
      user.setPassword(password);
      user.setName(name);
      user.setSurname(surname);
      user.setHistoryList(new ArrayList<>());
      user.setTaskList(new ArrayList<>());
      user.setSignUpType(signUpType);
      user.setCreated(new Date(System.currentTimeMillis()));
      insertIntoCsv(user);
      nextId(User.class);
      return Statuses.INSERTED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private Statuses updateUser(@NonNull User editedUser) {
    try {
      var userList = getFromCsv(User.class);

      Optional<User> optionalUser = userList.stream()
              .filter(user -> user.getId() == editedUser.getId())
              .findFirst();
      if (optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }

      userList.remove(optionalUser.get());
      userList.add(editedUser);
      insertIntoCsv(User.class, userList, true);
      return Statuses.UPDATED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private void saveChangesHistory(User user, User editedUser) {
    try {
      if (!user.getName().equals(editedUser.getName())) {
        editedUser.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                OperationType.EDIT,
                user.getName()));
      }
      if (!user.getSurname().equals(editedUser.getSurname())) {
        editedUser.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_SURNAME),
                OperationType.EDIT,
                user.getSurname()));
      }
      if (!user.getEmail().equals(editedUser.getEmail())) {
        editedUser.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_EMAIL),
                OperationType.EDIT,
                user.getEmail()));
      }
      if (!user.getPassword().equals(editedUser.getPassword())) {
        editedUser.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD),
                OperationType.EDIT,
                user.getPassword()));
      }
    } catch (IOException e) {
      log.error(e);
    }

  }


  private boolean isEditValid(User user, User editedUser) {
    return user.getHistoryList().equals(editedUser.getHistoryList())
            && user.getCreated().getTime() == editedUser.getCreated().getTime()
            && user.getTaskList().equals(editedUser.getTaskList())
            && user.getSignUpType().equals(editedUser.getSignUpType());
  }


  @Override
  public Statuses editUser(@NonNull User editedUser) {
    try {
      var userList = getFromCsv(User.class);

      Optional<User> optionalUser = userList.stream()
              .filter(user -> user.getId() == editedUser.getId())
              .findFirst();

      if (optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }

      if (!isEditValid(editedUser, optionalUser.get())) {
        return Statuses.FORBIDDEN;
      }
      userList.remove(optionalUser.get());
      saveChangesHistory(optionalUser.get(), editedUser);
      userList.add(editedUser);
      insertIntoCsv(User.class, userList, true);
      return Statuses.UPDATED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses createGroup(@NonNull String groupName, long creatorId, @NonNull GroupTypes groupType) {
    try {
      var user = getUser(creatorId);
      if (user.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      Group group = new Group();
      group.setCreated(new Date(System.currentTimeMillis()));
      group.setGroupType(groupType);
      group.setHistoryList(new ArrayList<>());
      group.setId(getNextId(Group.class));
      group.setMemberList(new HashMap<>());
      group.getMemberList().put(user.get(), UserRole.CREATOR);
      group.setTaskList(new HashMap<>());
      group.setName(groupName);
      insertIntoCsv(group);
      return Statuses.INSERTED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses addUserToGroup(long userId, long groupId) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();

      if (group.getMemberList().containsKey(user)) {
        return Statuses.FORBIDDEN;
      }
      switch (group.getGroupType()) {
        case PUBLIC, PASSWORDED -> {
          group.getMemberList().put(user, UserRole.MEMBER);
          group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
                  OperationType.ADD,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                          user.getId(),
                          UserRole.MEMBER.toString())));
        }
        case WITH_CONFIRMATION -> {
          group.getMemberList().put(user, UserRole.REQUIRES_CONFIRMATION);
          group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
                  OperationType.ADD,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                          user.getId(),
                          UserRole.REQUIRES_CONFIRMATION.toString())));
        }
        default -> {
          return Statuses.FAILED;
        }
      }
      return editGroup(group);
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private void saveChangesHistory(Group group, Group editedGroup) {
    try {
      if (!group.getName().equals(editedGroup.getName())) {
        editedGroup.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                OperationType.EDIT,
                group.getName()));
      }
    } catch (IOException e) {
      log.error(e);
    }
  }


  private void saveChangesHistory(PasswordedGroup group, PasswordedGroup editedGroup) {
    try {
      saveChangesHistory((Group) group, editedGroup);
      if (!group.getPassword().equals(editedGroup.getPassword())) {
        editedGroup.getHistoryList().add(addHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD),
                OperationType.EDIT,
                group.getPassword()));
      }
    } catch (IOException e) {
      log.error(e);
    }
  }


  private Statuses editGroup(Group editedGroup) {
    try {
      var optionalGroup = getGroup(editedGroup.getId());
      if (optionalGroup.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }

      switch (editedGroup.getGroupType()) {
        case PASSWORDED -> {
          var groupList = getFromCsv(PasswordedGroup.class);
          var optGroup = groupList.stream()
                  .filter(passwordedGroup -> passwordedGroup.getId() == editedGroup.getId())
                  .findAny();
          if (optGroup.isEmpty()) {
            return Statuses.NOT_FOUNDED;
          }
          groupList.remove(optGroup.get());
          saveChangesHistory(optGroup.get(), (PasswordedGroup) editedGroup);
          groupList.add((PasswordedGroup) editedGroup);
          insertIntoCsv(PasswordedGroup.class, groupList, true);
          return Statuses.INSERTED;
        }
        case PUBLIC, WITH_CONFIRMATION -> {
          var groupList = getFromCsv(Group.class);
          var optGroup = groupList.stream()
                  .filter(group -> group.getId() == editedGroup.getId())
                  .findAny();
          if (optGroup.isEmpty()) {
            return Statuses.NOT_FOUNDED;
          }
          groupList.remove(optGroup.get());
          saveChangesHistory(optGroup.get(), editedGroup);
          groupList.add(editedGroup);
          insertIntoCsv(Group.class, groupList, true);
          return Statuses.INSERTED;
        }
        default -> {
          return Statuses.FAILED;
        }
      }

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private Statuses changeGroupTypeToPassworded(Group group) {
    try {
      PasswordedGroup passwordedGroup = new PasswordedGroup();
      passwordedGroup.setId(group.getId());
      passwordedGroup.setCreated(group.getCreated());
      passwordedGroup.setGroupType(GroupTypes.PASSWORDED);
      passwordedGroup.setHistoryList(group.getHistoryList());
      passwordedGroup.setMemberList(group.getMemberList());
      passwordedGroup.setName(group.getName());
      passwordedGroup.setTaskList(group.getTaskList());
      var groupList = getFromCsv(Group.class);

      var optionalGroup = groupList.stream()
              .filter(serverGroup -> serverGroup.getId() == group.getId())
              .findAny();
      if (optionalGroup.isEmpty() || !groupList.remove(optionalGroup.get())) {
        return Statuses.FAILED;
      }
      insertIntoCsv(Group.class, groupList, true);
      var passwordedGroupList = getFromCsv(PasswordedGroup.class);
      passwordedGroupList.add(passwordedGroup);
      insertIntoCsv(PasswordedGroup.class, passwordedGroupList, true);
      return Statuses.UPDATED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private Statuses changeGroupTypeFromPassworded(Group group, GroupTypes groupType) {
    try {
      group.setGroupType(groupType);
      var passwordedGroupList = getFromCsv(PasswordedGroup.class);
      var optionalPasswordedGroup = passwordedGroupList.stream()
              .filter(serverGroup -> serverGroup.getId() == group.getId())
              .findAny();
      if (optionalPasswordedGroup.isEmpty() || !passwordedGroupList.remove(optionalPasswordedGroup.get())) {
        return Statuses.FAILED;
      }
      insertIntoCsv(PasswordedGroup.class, passwordedGroupList, true);
      var groupList = getFromCsv(Group.class);
      groupList.add(group);
      insertIntoCsv(Group.class, groupList, true);
      return Statuses.UPDATED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();

      if (!group.getMemberList().get(user).equals(UserRole.CREATOR)) {
        return Statuses.FORBIDDEN;
      }
      if (groupType.equals(group.getGroupType())) {
        return Statuses.UPDATED;
      }
      group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_GROUP_TYPE),
              OperationType.EDIT,
              group.getGroupType().name()));

      if (groupType.equals(GroupTypes.PASSWORDED) &&
              (group.getGroupType().equals(GroupTypes.PUBLIC)
                      || (group.getGroupType().equals(GroupTypes.WITH_CONFIRMATION)))) {
        return changeGroupTypeToPassworded(group);
      } else if (group.getGroupType().equals(GroupTypes.PASSWORDED) &&
              (groupType.equals(GroupTypes.PUBLIC)
                      || (groupType.equals(GroupTypes.WITH_CONFIRMATION)))) {
        return changeGroupTypeFromPassworded(group, groupType);
      } else {
        group.setGroupType(groupType);
        editGroup(group);
        return Statuses.UPDATED;
      }

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  //TODO
  @Override
  public List<Group> searchGroupByName(@NonNull String name) {
    return null;
  }


  @Override
  public List<Group> getFullGroupList() {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));

      groupList.forEach(group -> {
        group.setTaskList(getTaskMap(group.getId()));
        group.setMemberList(getUserMap(group.getId()));
        group.setHistoryList(getHistoryList(Group.class, group));
      });

      return groupList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }


  private Optional<Group> getUnfilledGroup(long groupId) {
    try {
      List<Group> groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      return groupList.stream().filter(group -> group.getId() == groupId).findAny();
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private Map<User, UserRole> getUserMap(long groupId) {
    var optionalGroup = getUnfilledGroup(groupId);
    Map<User, UserRole> userMap = new HashMap<>();
    if (optionalGroup.isEmpty()) {
      return userMap;
    }
    var group = optionalGroup.get();
    group.getMemberList().forEach((user, role) -> {
      var optionalUser = getUser(user.getId());
      optionalUser.ifPresent(value -> userMap.put(value, role));
    });
    return userMap;
  }


  private Map<Task, TaskState> getTaskMap(long groupId) {
    var optionalGroup = getUnfilledGroup(groupId);
    Map<Task, TaskState> taskMap = new HashMap<>();
    if (optionalGroup.isEmpty()) {
      return taskMap;
    }
    var group = optionalGroup.get();
    group.getTaskList().forEach((task, state) -> {
      var optionalTask = getTask(task.getId());
      optionalTask.ifPresent(value -> taskMap.put(value, state));
    });
    return taskMap;
  }


  @Override
  public Optional<Group> getGroup(long groupId) {
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
      group.setHistoryList(getHistoryList(Group.class, group));
      group.setMemberList(getUserMap(groupId));
      group.setTaskList(getTaskMap(groupId));
      return Optional.of(group);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  @Override
  public Statuses deleteUserFromGroup(long userId, long groupId) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();

      if (!group.getMemberList().containsKey(user)) {
        return Statuses.FORBIDDEN;
      }

      group.getMemberList().remove(user);
      group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
              OperationType.DELETE,
              String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                      user.getId(),
                      UserRole.MEMBER.toString())));
      var status = editGroup(group);
      if (status.equals(Statuses.INSERTED)) {
        return Statuses.DELETED;
      } else {
        return status;
      }
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  //TODO
  @Override
  public Statuses suggestTask(long userId, long groupId, long taskId) {
    return null;
  }

  //TODO
  @Override
  public Statuses createTask(long userId, long groupId, @NonNull String name) {
    return null;
  }

  //TODO
  @Override
  public Statuses createTask(long userId, long groupId, @NonNull String name, @NonNull RepetitionTypes repetitionType, @NonNull RemindTypes remindType, @NonNull Importances importance, @NonNull String description, @NonNull Date time) {
    return null;
  }

  //TODO
  @Override
  public Statuses updateGroup(long userId, @NonNull Group editedGroup) {
    return null;
  }

  //TODO
  @Override
  public Statuses setUserRole(long userId, long groupId, long userIdToSet, @NonNull UserRole role) {
    return null;
  }

  //TODO
  @Override
  public Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state) {
    return null;
  }

  //TODO
  @Override
  public Statuses deleteGroup(long userId, long groupId) {
    return null;
  }

  @Override
  public List<Group> getUsersGroups(long userId) {
    return null;
  }
}
