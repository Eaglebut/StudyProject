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
import java.lang.reflect.InvocationTargetException;
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

  private <T> void insertIntoCsv(Class<?> tClass, List<T> objectList, boolean overwrite) throws IOException {
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
          throw new IOException(String.format(PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE), file.getName()));
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
      if (!file.createNewFile())
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

  public void deleteAll() {
    List<Class> classList = new ArrayList<>();
    classList.add(ExtendedTask.class);
    classList.add(Group.class);
    classList.add(ModificationRecord.class);
    classList.add(PasswordedGroup.class);
    classList.add(Task.class);
    classList.add(User.class);
    classList.add(Metadata.class);
    classList.forEach(this::deleteFile);
  }

  private <T> void deleteFile(Class<T> tClass) {
    try {
      new File(PropertyLoader.getProperty(Constants.CSV_PATH)
              + tClass.getSimpleName().toLowerCase()
              + PropertyLoader.getProperty(Constants.CSV_EXTENSION)).delete();
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
              .findFirst()
              .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password));
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private <T> List<ModificationRecord> getHistoryList(T object) {
    Class<?> tClass = object.getClass();
    try {
      List<ModificationRecord> objectHistoryList = (List<ModificationRecord>) tClass
              .getDeclaredMethod(
                      PropertyLoader.getProperty(
                              Constants.MODIFICATION_RECORD_GET_HISTORY)).invoke(object);
      List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
      return historyList
              .stream()
              .filter(modificationRecord -> objectHistoryList
                      .stream()
                      .findFirst()
                      .filter(userModificationRecord ->
                              userModificationRecord.getId() == modificationRecord.getId())
                      .isPresent())
              .collect(Collectors.toList());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e) {
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
      user.setHistoryList(getHistoryList(user));
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
      user.setHistoryList(getHistoryList(user));
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }

  private List<Task> getTasks(@NonNull User user) throws NoSuchElementException {
    try {
      List<Task> taskList = getFromCsv(Task.class);
      List<ExtendedTask> extendedTasks = getFromCsv(ExtendedTask.class);
      List<Task> usersTaskList = user.getTaskList();
      List<Task> usersFilledTaskList = new ArrayList<>();
      List<Long> extendedTaskIdList = new ArrayList<>();
      usersTaskList.forEach(usersTask -> taskList.forEach(task -> {
        if (usersTask.getId() == task.getId()) {
          task.setHistoryList(getHistoryList(task));
          if (task.getTaskType() == TaskTypes.EXTENDED) {
            extendedTaskIdList.add(task.getId());
          } else {
            usersFilledTaskList.add(task);
          }
        }
      }));
      usersFilledTaskList.addAll(extendedTasks.stream()
              .filter(extendedTask -> extendedTaskIdList.contains(extendedTask.getId()))
              .collect(Collectors.toList()));
      return usersFilledTaskList;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  private <T> long getNextId(Class<T> tClass) throws IOException {
    List<Metadata> metadataList = getFromCsv(Metadata.class);
    Optional<Metadata> optionalMetadata = metadataList.stream()
            .filter(metadata -> metadata.getClassName().equals(tClass.getSimpleName().toLowerCase()))
            .findAny();
    if (optionalMetadata.isEmpty()) {
      return 0;
    }
    return optionalMetadata.get().getLastId() + 1;
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

  private Statuses createTask(long userId, String taskName, TaskStatuses status, TaskTypes taskType) {
    try {
      Task createdTask = new Task();
      createdTask.setId(getNextId(Task.class));
      createdTask.setName(taskName);
      createdTask.setTaskType(taskType);
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
        updateUser(user);
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
                             @NonNull TaskStatuses status) {
    return createTask(userId, taskName, status, TaskTypes.BASIC);
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
      var insertionStatus = createTask(user.getId(), taskName, status, TaskTypes.EXTENDED);
      if (insertionStatus != Statuses.INSERTED) {
        return insertionStatus;
      }
      user.getTaskList().add(task);
      updateUser(user);
      insertIntoCsv(task);
      return insertionStatus;
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

        insertIntoCsv(Task.class, getFromCsv(Task.class)
                .stream()
                .filter(task -> taskId != task.getId())
                .collect(Collectors.toList()), true);
        if (taskToDelete.getTaskType() == TaskTypes.EXTENDED) {
          insertIntoCsv(ExtendedTask.class, getFromCsv(ExtendedTask.class)
                  .stream()
                  .filter(task -> task.getId() != taskId)
                  .collect(Collectors.toList()), true);
        }
        user.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                OperationType.DELETE,
                String.valueOf(taskId)));
        updateUser(user);
        return Statuses.DELETED;
      }
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  //TODO
  @Override
  public Statuses editTask(long userId, @NonNull Task editedTask) {
    return null;
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

  //TODO
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

      if (!optionalUser.get().getTaskList().equals(editedUser.getTaskList())) {
        return Statuses.FORBIDDEN;
      }

      userList.remove(optionalUser.get());
      userList.add(editedUser);
      insertIntoCsv(userList);
      return Statuses.UPDATED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  //TODO
  @Override
  public Statuses addUserToGroup(long userId, long groupId) {
    return null;
  }

  //TODO
  @Override
  public Statuses createGroup(@NonNull String groupName, long creatorId, @NonNull GroupTypes groupType) {
    return null;
  }

  //TODO
  @Override
  public Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType) {
    return null;
  }

  //TODO
  @Override
  public List<Group> searchGroupByName(@NonNull String name) {
    return null;
  }

  //TODO
  @Override
  public Group searchGroupById(long id) throws NoSuchElementException {
    return null;
  }

  //TODO
  @Override
  public List<Group> getFullGroupList() {
    return null;
  }

  //TODO
  @Override
  public Optional<Group> getGroup(long groupId) {
    return Optional.empty();
  }

  //TODO
  @Override
  public Optional<Group> getGroup(long userId, long groupId) {
    return Optional.empty();
  }

  //TODO
  @Override
  public Statuses deleteUserFromGroup(long userId, long groupId) {
    return null;
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
}
