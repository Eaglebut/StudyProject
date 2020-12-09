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
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.Metadata;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class DataProviderCsv implements DataProvider {


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


  private ModificationRecord addHistoryRecord(String changedValueName,
                                              OperationType operationType,
                                              String changedValue) {
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


  private Optional<Task> createTask(@NonNull String taskName,
                                    @NonNull TaskStatuses status) {
    try {
      Task createdTask = new Task();
      createdTask.setId(getNextId(Task.class));
      createdTask.setName(taskName);
      createdTask.setTaskType(TaskTypes.BASIC);
      createdTask.setCreated(new Date(System.currentTimeMillis()));
      createdTask.setHistoryList(new ArrayList<>());
      createdTask.setStatus(status);
      insertIntoCsv(createdTask);
      nextId(Task.class);
      return Optional.of(createdTask);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  private Optional<Task> createTask(@NonNull String taskName,
                                    @NonNull TaskStatuses status,
                                    @NonNull RepetitionTypes repetitionType,
                                    @NonNull RemindTypes remindType,
                                    @NonNull Importances importance,
                                    @NonNull String description,
                                    @NonNull Date time) {
    try {
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
      nextId(Task.class);
      insertIntoCsv(task);
      return Optional.of(task);
    } catch (IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }


  @Override
  public Statuses createTask(long userId,
                             @NonNull String taskName,
                             @NonNull TaskStatuses status) {
    try {
      var optionalTask = createTask(taskName, status);
      if (optionalTask.isEmpty()) {
        return Statuses.FAILED;
      }
      var createdTask = optionalTask.get();
      Optional<User> optionalUser = getUser(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
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
    var optionalUser = getUser(userId);
    if (optionalUser.isEmpty()) {
      return Statuses.FORBIDDEN;
    }
    User user = optionalUser.get();
    var optionalTask = createTask(taskName,
            status,
            repetitionType,
            remindType,
            importance,
            description,
            time);
    if (optionalTask.isEmpty()) {
      return Statuses.FAILED;
    }
    user.getTaskList().add(optionalTask.get());
    var updateUserStatus = updateUser(user);
    if (updateUserStatus != Statuses.UPDATED) {
      return updateUserStatus;
    }
    return Statuses.INSERTED;
  }


  private Statuses deleteTask(List<Task> taskList) {
    try {
      var dbTaskList = getFromCsv(Task.class);
      dbTaskList.addAll(getFromCsv(ExtendedTask.class));
      dbTaskList.removeAll(taskList);
      List<ExtendedTask> extendedTaskList = new ArrayList<>();
      List<Task> basicTaskList = new ArrayList<>();
      dbTaskList.forEach(task -> {
        switch (task.getTaskType()) {
          case EXTENDED -> extendedTaskList.add((ExtendedTask) task);
          case BASIC -> basicTaskList.add(task);
        }
      });
      insertIntoCsv(ExtendedTask.class, extendedTaskList, true);
      insertIntoCsv(Task.class, basicTaskList, true);
      for (var task : taskList) {
        var status = deleteHistoryList(task.getHistoryList());
        if (!status.equals(Statuses.DELETED)) {
          return status;
        }
      }
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private Statuses deleteTask(long taskId) {
    try {
      var basicTaskList = getFromCsv(Task.class);
      var optTask = basicTaskList.stream()
              .filter(task -> task.getId() == taskId)
              .findAny();
      if (optTask.isPresent()) {
        basicTaskList.remove(optTask.get());
        var status = deleteHistoryList(optTask.get().getHistoryList());
        if (!status.equals(Statuses.DELETED)) {
          return status;
        }
        insertIntoCsv(Task.class, basicTaskList, true);
      } else {
        var extendedTaskList = getFromCsv(ExtendedTask.class);
        var optExtendedTask = extendedTaskList.stream()
                .filter(task -> task.getId() == taskId)
                .findAny();
        if (optExtendedTask.isEmpty()) {
          return Statuses.NOT_FOUNDED;
        }
        extendedTaskList.remove(optExtendedTask.get());
        var status = deleteHistoryList(optExtendedTask.get().getHistoryList());
        if (!status.equals(Statuses.DELETED)) {
          return status;
        }
        insertIntoCsv(ExtendedTask.class, extendedTaskList, true);
      }
      return Statuses.DELETED;

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses deleteTask(long userId, long taskId) {
    try {
      var optionalUser = getUser(userId);
      var optionalTaskToDelete = getTask(taskId);
      if (optionalUser.isEmpty() || optionalTaskToDelete.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      User user = optionalUser.get();

      Task taskToDelete = optionalTaskToDelete.get();
      user.getTaskList().remove(taskToDelete);
      var status = deleteTask(taskId);
      if (!status.equals(Statuses.DELETED)) {
        return status;
      }
      user.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
              OperationType.DELETE,
              String.valueOf(taskId)));
      var updateUserStatus = updateUser(user);
      if (updateUserStatus != Statuses.UPDATED) {
        return updateUserStatus;
      }
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses deleteTask(long userId, long groupId, long taskId) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      var optionalTask = getTask(taskId);

      if (optionalUser.isEmpty() || optionalGroup.isEmpty() || optionalTask.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }

      var user = optionalUser.get();
      var group = optionalGroup.get();
      var task = optionalTask.get();

      if (!group.getMemberList().containsKey(user) || !group.getTaskList().containsKey(task)) {
        return Statuses.FORBIDDEN;
      }
      var role = group.getMemberList().get(user);

      switch (role) {
        case ADMINISTRATOR, CREATOR -> {
        }
        default -> {
          return Statuses.FORBIDDEN;
        }
      }

      group.getTaskList().remove(task);

      group.getHistoryList().add(addHistoryRecord(
              PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
              OperationType.DELETE,
              String.valueOf(task.getId())));

      var status = saveGroup(group);
      if (status.equals(Statuses.UPDATED)) {
        return deleteTask(task.getId());
      } else {
        return status;
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
    return task.getName() != null
            && !task.getName().isEmpty()
            && task.getCreated() != null
            && task.getStatus() != null
            && task.getTaskType() != null
            && task.getHistoryList() != null
            && task.getHistoryList().equals(editedTask.getHistoryList())
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
    return editedUser.getEmail() != null
            && editedUser.getCreated() != null
            && editedUser.getHistoryList() != null
            && editedUser.getName() != null
            && editedUser.getPassword() != null
            && editedUser.getToken() != null
            && editedUser.getSurname() != null
            && editedUser.getTaskList() != null
            && editedUser.getSignUpType() != null
            && user.getHistoryList().equals(editedUser.getHistoryList())
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

      var optUser = getUser(editedUser.getId());

      if (optionalUser.isEmpty() || optUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }

      if (!isEditValid(optUser.get(), editedUser)) {
        log.debug(optUser.get().getHistoryList().equals(editedUser.getHistoryList()));
        log.debug(optUser.get().getCreated().getTime() == editedUser.getCreated().getTime());
        log.debug(optUser.get().getTaskList().equals(editedUser.getTaskList()));
        log.debug(optUser.get().getSignUpType().equals(editedUser.getSignUpType()));
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
      Group group;
      switch (groupType) {
        case PASSWORDED -> group = new PasswordedGroup();
        case PUBLIC, WITH_CONFIRMATION -> group = new Group();
        default -> {
          return Statuses.FAILED;
        }
      }
      group.setCreated(new Date(System.currentTimeMillis()));
      group.setGroupType(groupType);
      group.setHistoryList(new ArrayList<>());
      group.setId(getNextId(Group.class));
      group.setMemberList(new HashMap<>());
      group.getMemberList().put(user.get(), UserRole.CREATOR);
      group.setTaskList(new HashMap<>());
      group.setName(groupName);
      insertIntoCsv(group);
      nextId(Group.class);
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
      var status = saveGroup(group);
      if (status.equals(Statuses.UPDATED)) {
        return Statuses.INSERTED;
      } else {
        return saveGroup(group);
      }
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


  private Statuses saveGroup(Group editedGroup) {
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
          return Statuses.UPDATED;
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
      if (!group.getMemberList().containsKey(user)) {
        return Statuses.FORBIDDEN;
      }

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
        return saveGroup(group);
      }

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public List<Group> searchGroupByName(@NonNull String name) {
    try {
      var groupList = getFromCsv(Group.class);
      groupList.addAll(getFromCsv(PasswordedGroup.class));
      return groupList.stream()
              .filter(group -> group.getName().toLowerCase().contains(name.toLowerCase()))
              .collect(Collectors.toList());
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
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
      var status = saveGroup(group);
      if (status.equals(Statuses.UPDATED)) {
        return Statuses.DELETED;
      } else {
        return status;
      }
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Statuses suggestTask(User user, Group group, Task task) {
    try {
      var userRole = group.getMemberList().get(user);
      if (userRole == UserRole.REQUIRES_CONFIRMATION) {
        return Statuses.FORBIDDEN;
      }
      Optional<Task> optionalTask;
      switch (task.getTaskType()) {
        case BASIC -> optionalTask = createTask(task.getName(), task.getStatus());
        case EXTENDED -> {
          ExtendedTask extendedTask = (ExtendedTask) task;
          optionalTask = createTask(extendedTask.getName(),
                  extendedTask.getStatus(),
                  extendedTask.getRepetitionType(),
                  extendedTask.getRemindType(),
                  extendedTask.getImportance(),
                  extendedTask.getDescription(),
                  extendedTask.getTime());
        }
        default -> {
          return Statuses.FAILED;
        }
      }
      if (optionalTask.isEmpty()) {
        return Statuses.FAILED;
      }
      switch (userRole) {
        case CREATOR -> group.getTaskList().put(optionalTask.get(), TaskState.APPROVED);
        case MEMBER -> group.getTaskList().put(optionalTask.get(), TaskState.SUGGESTED);
        default -> {
          return Statuses.FAILED;
        }
      }
      group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
              OperationType.ADD,
              String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                      optionalTask.get().getId(),
                      group.getTaskList().get(optionalTask.get()))));
      var status = saveGroup(group);
      if (status.equals(Statuses.UPDATED)) {
        return Statuses.INSERTED;
      }
      return status;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses suggestTask(long userId, long groupId, long taskId) {
    var optionalUser = getUser(userId);
    var optionalGroup = getGroup(groupId);
    var optionalTask = getTask(taskId);
    if (optionalGroup.isEmpty() || optionalUser.isEmpty() || optionalTask.isEmpty()) {
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();

    switch (group.getGroupType()) {
      case PUBLIC -> {
        return Statuses.FORBIDDEN;
      }
      case WITH_CONFIRMATION, PASSWORDED -> {
        return suggestTask(optionalUser.get(), group, optionalTask.get());
      }
      default -> {
        return Statuses.FAILED;
      }
    }
  }

  @Override
  public Statuses createTask(long userId, long groupId, @NonNull String name, @NonNull TaskStatuses taskStatus) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var role = optionalGroup.get().getMemberList().get(optionalUser.get());
      if (role == null) {
        return Statuses.FORBIDDEN;
      }
      switch (role) {

        case CREATOR, ADMINISTRATOR -> {
          var optionalTask = createTask(name, taskStatus);
          if (optionalTask.isEmpty()) {
            return Statuses.FAILED;
          }
          var status = saveGroup(group, optionalTask.get());
          if (status.equals(Statuses.UPDATED)) {
            return Statuses.INSERTED;
          } else {
            return status;
          }
        }
        case MEMBER, REQUIRES_CONFIRMATION -> {
          return Statuses.FORBIDDEN;
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

  @Override
  public Statuses createTask(long userId,
                             long groupId,
                             @NonNull String name,
                             @NonNull TaskStatuses taskStatus,
                             @NonNull RepetitionTypes repetitionType,
                             @NonNull RemindTypes remindType,
                             @NonNull Importances importance,
                             @NonNull String description,
                             @NonNull Date time) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();

      switch (optionalGroup.get().getMemberList().get(optionalUser.get())) {

        case CREATOR, ADMINISTRATOR -> {
          var optionalTask = createTask(name,
                  taskStatus,
                  repetitionType,
                  remindType,
                  importance,
                  description,
                  time);
          if (optionalTask.isEmpty()) {
            return Statuses.FAILED;
          }
          var status = saveGroup(group, optionalTask.get());
          if (status.equals(Statuses.UPDATED)) {
            return Statuses.INSERTED;
          } else {
            return status;
          }
        }
        case MEMBER, REQUIRES_CONFIRMATION -> {
          return Statuses.FORBIDDEN;
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

  @NotNull
  private Statuses saveGroup(Group group, Task task) throws IOException {
    group.getTaskList().put(task, TaskState.APPROVED);
    group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
            OperationType.ADD,
            String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                    task.getId(),
                    TaskState.APPROVED)));
    return saveGroup(group);
  }


  @Override
  public Statuses updateGroup(long userId, @NonNull Group editedGroup) {
    try {
      var optionalUser = getUser(userId);
      var optionalGroup = getGroup(editedGroup.getId());
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var role = optionalGroup.get().getMemberList().get(optionalUser.get());
      if (role == null) {
        return Statuses.FORBIDDEN;
      }
      switch (role) {

        case CREATOR, ADMINISTRATOR -> {
          if (!(group.getHistoryList().equals(editedGroup.getHistoryList())
                  && group.getTaskList().equals(editedGroup.getTaskList())
                  && group.getMemberList().equals(editedGroup.getMemberList())
                  && group.getGroupType().equals(editedGroup.getGroupType())
                  && group.getCreated().equals(editedGroup.getCreated()))) {
            return Statuses.FORBIDDEN;
          }
          if (!group.getName().equals(editedGroup.getName())) {
            editedGroup.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                    OperationType.EDIT,
                    group.getName()));
          }
          if (group.getGroupType() == GroupTypes.PASSWORDED
                  && ((PasswordedGroup) group).getPassword().equals(((PasswordedGroup) editedGroup).getPassword())) {
            editedGroup.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD),
                    OperationType.EDIT,
                    ((PasswordedGroup) group).getPassword()));
          }
          return saveGroup(editedGroup);
        }
        case MEMBER, REQUIRES_CONFIRMATION -> {
          return Statuses.FORBIDDEN;
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

  private Statuses setRole(Group group, User user, UserRole role) {
    try {
      group.getMemberList().replace(user, role);
      group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
              OperationType.EDIT,
              String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING), user.getId(), role.name())));
      return saveGroup(group);

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  @Override
  public Statuses setUserRole(long administratorId, long groupId, long userIdToSet, @NonNull UserRole role) {

    Optional<Group> optionalGroup = getGroup(groupId);
    Optional<User> optionalAdministrator = getUser(administratorId);
    Optional<User> optionalUser = getUser(userIdToSet);

    if (optionalGroup.isEmpty() || optionalAdministrator.isEmpty() || optionalUser.isEmpty()) {
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();
    var administrator = optionalAdministrator.get();
    var user = optionalUser.get();

    if (!group.getMemberList().containsKey(user) || !group.getMemberList().containsKey(administrator)) {
      return Statuses.FORBIDDEN;
    }
    var userRole = group.getMemberList().get(user);
    var administratorRole = group.getMemberList().get(administrator);

    if (userRole.equals(UserRole.CREATOR)) {
      return Statuses.FORBIDDEN;
    }

    switch (role) {
      case CREATOR -> {
        return Statuses.FORBIDDEN;
      }
      case MEMBER -> {
        switch (userRole) {
          case REQUIRES_CONFIRMATION -> {
            if (!administratorRole.equals(UserRole.ADMINISTRATOR) && !administratorRole.equals(UserRole.CREATOR)) {
              return Statuses.FORBIDDEN;
            }
            return setRole(group, user, role);
          }
          case ADMINISTRATOR -> {
            if (!administratorRole.equals(UserRole.CREATOR)) {
              return Statuses.FORBIDDEN;
            }
            return setRole(group, user, role);
          }
          default -> {
            return Statuses.FORBIDDEN;
          }
        }
      }
      case ADMINISTRATOR -> {
        if (!administratorRole.equals(UserRole.CREATOR)) {
          return Statuses.FORBIDDEN;
        }
        return setRole(group, user, role);
      }
      default -> {
        return Statuses.FAILED;
      }
    }
  }


  @Override
  public Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state) {
    try {
      Optional<Group> optionalGroup = getGroup(groupId);
      Optional<User> optionalAdministrator = getUser(userId);
      Optional<Task> optionalTask = getTask(taskId);

      if (optionalGroup.isEmpty() || optionalAdministrator.isEmpty() || optionalTask.isEmpty()) {
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var administrator = optionalAdministrator.get();
      var task = optionalTask.get();

      if (!group.getTaskList().containsKey(task) || !group.getMemberList().containsKey(administrator)) {
        return Statuses.FORBIDDEN;
      }
      var administratorRole = group.getMemberList().get(administrator);

      switch (administratorRole) {
        case ADMINISTRATOR, CREATOR -> {
          group.getTaskList().replace(task, state);
          group.getHistoryList().add(addHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                  OperationType.EDIT,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING), taskId, state)));
          var status = saveGroup(group);
          if (status.equals(Statuses.INSERTED)) {
            return Statuses.UPDATED;
          }
          return status;
        }
        default -> {
          return Statuses.FORBIDDEN;
        }
      }
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Statuses deleteHistoryList(List<ModificationRecord> historyList) {
    try {
      if (historyList.isEmpty()) {
        return Statuses.DELETED;
      }
      var recordList = getFromCsv(ModificationRecord.class);
      recordList.removeAll(historyList);
      insertIntoCsv(ModificationRecord.class, recordList, true);
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Statuses deleteGroup(Group group) {
    try {
      switch (group.getGroupType()) {
        case PUBLIC, WITH_CONFIRMATION -> {
          List<Group> groupList = getFromCsv(Group.class);
          groupList = groupList.stream().filter(group1 -> group1.getId() != group.getId()).collect(Collectors.toList());
          insertIntoCsv(Group.class, groupList, true);
        }
        case PASSWORDED -> {
          List<PasswordedGroup> groupList = getFromCsv(PasswordedGroup.class);
          groupList = groupList.stream().filter(group1 -> group1.getId() != group.getId()).collect(Collectors.toList());
          insertIntoCsv(PasswordedGroup.class, groupList, true);
        }
        default -> {
          return Statuses.FAILED;
        }
      }

      var status = deleteTask(new ArrayList<>(group.getTaskList().keySet()));
      if (!status.equals(Statuses.DELETED)) {
        return status;
      }
      return deleteHistoryList(group.getHistoryList());
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses deleteGroup(long userId, long groupId) {
    Optional<Group> optionalGroup = getGroup(groupId);
    Optional<User> optionalUser = getUser(userId);
    if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
      return Statuses.NOT_FOUNDED;
    }
    User user = optionalUser.get();
    Group group = optionalGroup.get();

    if (!group.getMemberList().containsKey(user)) {
      return Statuses.FORBIDDEN;
    }

    if (!group.getMemberList().get(user).equals(UserRole.CREATOR)) {
      return Statuses.FORBIDDEN;
    }

    return deleteGroup(group);
  }

  @Override
  public List<Group> getUsersGroups(long userId) {
    var optUser = getUser(userId);
    if (optUser.isEmpty()) {
      return new ArrayList<>();
    }
    var groupList = getFullGroupList();
    return groupList.stream()
            .filter(group -> group.getMemberList().containsKey(optUser.get()))
            .collect(Collectors.toList());

  }

  private String userToString(User user) throws IOException {
    return String.format(PropertyLoader.getProperty(Constants.FORMAT_USER_TO_STRING),
            user.getId(),
            user.getName(),
            user.getSurname(),
            user.getEmail(),
            user.getPassword(),
            user.getCreated(),
            user.getSignUpType(),
            user.getToken());
  }

  private String historyListToString(List<ModificationRecord> historyList, int tabNum) {
    StringBuilder builder = new StringBuilder();
    historyList.forEach(modificationRecord -> {
      try {
        builder.append(modificationRecordToString(modificationRecord, tabNum));
        builder.append(PropertyLoader.getProperty(Constants.NEW_LINE));
      } catch (IOException e) {
        log.error(e);
      }
    });
    return builder.toString();
  }

  private String modificationRecordToString(ModificationRecord record, int tabNum) throws IOException {

    StringBuilder tabsBuilder = new StringBuilder();
    tabsBuilder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)));

    return String.format(PropertyLoader.getProperty(Constants.FORMAT_MODIFICATION_RECORD_TO_STRING),
            tabsBuilder.toString(), record.getId(),
            tabsBuilder.toString(),
            record.getChangedDate(),
            tabsBuilder.toString(),
            record.getOperationType(),
            tabsBuilder.toString(),
            record.getChangedValueName(),
            tabsBuilder.toString(),
            record.getChangedValue());
  }

  private String extendedTaskToString(ExtendedTask task, int tabNum) throws IOException {
    StringBuilder tabsBuilder = new StringBuilder();
    tabsBuilder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)));
    return basicTaskToString(task, tabNum) +
            String.format(PropertyLoader.getProperty(Constants.FORMAT_EXTENDED_TASK_TO_STRING),
                    tabsBuilder.toString(),
                    task.getDescription(),
                    tabsBuilder.toString(),
                    task.getRemindType(),
                    tabsBuilder.toString(),
                    task.getImportance(),
                    tabsBuilder.toString(),
                    task.getRepetitionType(),
                    tabsBuilder.toString(),
                    task.getTime());

  }

  private String basicTaskToString(Task task, int tabNum) throws IOException {
    StringBuilder tabsBuilder = new StringBuilder();
    tabsBuilder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)));
    return String.format(PropertyLoader.getProperty(Constants.FORMAT_BASIC_TASK_TO_STRING),
            tabsBuilder.toString(),
            task.getId(),
            tabsBuilder.toString(),
            task.getTaskType(),
            tabsBuilder.toString(),
            task.getCreated(),
            tabsBuilder.toString(),
            task.getName(),
            tabsBuilder.toString(),
            task.getStatus(),
            tabsBuilder.toString(),
            historyListToString(task.getHistoryList(), tabNum + 1));
  }

  private String taskListToString(List<Task> taskList, int tabNum) {
    StringBuilder builder = new StringBuilder();
    taskList.forEach(task -> {
      switch (task.getTaskType()) {
        case BASIC -> {
          try {
            builder.append(basicTaskToString(task, tabNum));
          } catch (IOException e) {
            log.error(e);
          }
        }
        case EXTENDED -> {
          try {
            builder.append(extendedTaskToString((ExtendedTask) task, tabNum));
          } catch (IOException e) {
            log.error(e);
          }
        }
      }
    });
    return builder.toString();
  }

  private String groupListToString(List<Group> groupList, int tabNum) {
    StringBuilder builder = new StringBuilder();
    groupList.forEach(group -> {
      try {
        builder.append(groupToString(group, tabNum));
        builder.append(PropertyLoader.getProperty(Constants.NEW_LINE));
      } catch (IOException e) {
        log.error(e);
      }
    });
    return builder.toString();
  }

  private String groupToString(Group group, int tabNum) throws IOException {
    StringBuilder tabsBuilder = new StringBuilder();
    tabsBuilder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)));
    return String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_TO_STRING),
            tabsBuilder.toString(),
            group.getId(),
            tabsBuilder.toString(),
            group.getGroupType(),
            tabsBuilder.toString(),
            group.getCreated(),
            tabsBuilder.toString(),
            group.getName(),
            tabsBuilder.toString(),
            groupTasksToString(group.getTaskList(), tabNum + 1),
            tabsBuilder.toString(),
            historyListToString(group.getHistoryList(), tabNum + 1));

  }

  private String groupTasksToString(Map<Task, TaskState> taskMap, int tabNum) throws IOException {
    StringBuilder tabsBuilder = new StringBuilder();
    tabsBuilder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum + 1)));
    StringBuilder builder = new StringBuilder();
    taskMap.forEach((task, taskState) -> {
      switch (task.getTaskType()) {
        case BASIC -> {
          try {
            builder.append(String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_TASK_TO_STRING),
                    tabsBuilder.toString(),
                    taskState,
                    basicTaskToString(task, tabNum + 2)));
          } catch (IOException e) {
            log.error(e);
          }
        }
        case EXTENDED -> {
          try {
            builder.append(String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_TASK_TO_STRING),
                    tabsBuilder.toString(),
                    taskState,
                    extendedTaskToString((ExtendedTask) task, tabNum + 2)));
          } catch (IOException e) {
            log.error(e);
          }
        }
      }
    });
    return builder.toString();
  }

  @Override
  public String getUserInfo(long userId) {
    try {
      var optUser = getUser(userId);
      if (optUser.isEmpty()) {
        return PropertyLoader.getProperty(Constants.MESSAGE_USER_NOT_FOUNDED);
      }
      var user = optUser.get();
      var groupList = getUsersGroups(userId);

      return String.format(PropertyLoader.getProperty(Constants.FORMAT_USER),
              userToString(user),
              taskListToString(user.getTaskList(), 1),
              historyListToString(user.getHistoryList(), 1),
              groupListToString(groupList, 1));
    } catch (IOException e) {
      log.error(e);
      return "";
    }
  }

  //TODO
  @Override
  public String getTaskStatistic() {
    return null;
  }

  private String groupCountByTypeToString(Map<GroupTypes, Long> groupCounts, int tabNum) {
    StringBuilder builder = new StringBuilder();
    groupCounts.forEach((groupTypes, aLong) -> {
      try {
        builder
                .append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)))
                .append(groupTypes)
                .append(PropertyLoader.getProperty(Constants.MAP_DELIMITER))
                .append(aLong)
                .append(PropertyLoader.getProperty(Constants.NEW_LINE));
      } catch (IOException e) {
        log.error(e);
      }
    });
    return builder.toString();
  }

  private String averageGroupSizesByTypeToString(Map<GroupTypes, Double> groupSizes, int tabNum) {
    StringBuilder builder = new StringBuilder();
    groupSizes.forEach((groupTypes, aDouble) -> {
      try {
        builder
                .append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum)))
                .append(groupTypes)
                .append(PropertyLoader.getProperty(Constants.MAP_DELIMITER))
                .append(aDouble)
                .append(PropertyLoader.getProperty(Constants.NEW_LINE));
      } catch (IOException e) {
        log.error(e);
      }
    });
    return builder.toString();
  }

  @Override
  public String getGroupsStatistic() {
    try {
      return String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_STATISTIC),
              getFullGroupList().size(),

              groupCountByTypeToString(getGroupCountPerType(), 1),
              getAverageGroupSize().get(),
              averageGroupSizesByTypeToString(getAverageGroupSizeDividedByGroupType(), 1));
    } catch (IOException e) {
      log.error(e);
      return "";
    }
  }

  @Override
  public List<User> getFullUsersList() {
    try {
      var userList = getFromCsv(User.class);

      userList.forEach(user -> {
        user.setTaskList(getTasks(user));
        user.setHistoryList(getHistoryList(User.class, user));
      });

      return userList;
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }


  @Override
  public Optional<Double> getAverageGroupSize() {
    var groupList = getFullGroupList();
    long groupSizes = 0;
    for (Group group : groupList) {
      groupSizes += group.getMemberList().size();
    }
    return Optional.of((groupSizes * 1.0) / groupList.size());
  }

  @Override
  public Map<Owner, Long> getTaskCountPerOwner() {
    List<User> userList = getFullUsersList();
    var groupList = getFullGroupList();
    Map<Owner, Long> taskCount = new HashMap<>();

    taskCount.put(Owner.USER, (long) userList.stream().flatMapToInt(user -> IntStream.of(user.getTaskList().size())).sum());
    taskCount.put(Owner.PUBLIC_GROUP, 0L);
    taskCount.put(Owner.GROUP_WITH_CONFIRMATION, 0L);
    taskCount.put(Owner.PASSWORDED_GROUP, 0L);
    groupList.forEach(group -> {
      switch (group.getGroupType()) {
        case PUBLIC -> taskCount.replace(Owner.PUBLIC_GROUP, taskCount.get(Owner.PUBLIC_GROUP) + 1);
        case PASSWORDED -> taskCount.replace(Owner.PASSWORDED_GROUP, taskCount.get(Owner.PASSWORDED_GROUP) + 1);
        case WITH_CONFIRMATION -> taskCount.replace(Owner.GROUP_WITH_CONFIRMATION,
                taskCount.get(Owner.GROUP_WITH_CONFIRMATION) + 1);
      }
    });
    return taskCount;
  }

  //TODO
  @Override
  public Map<Owner, Double> getAverageTaskPerOwner() {
    return null;
  }

  //TODO
  @Override
  public Map<TaskTypes, Long> getTaskCountPerType() {
    return null;
  }

  @Override
  public Map<GroupTypes, Double> getAverageGroupSizeDividedByGroupType() {
    var groupList = getFullGroupList();
    Map<GroupTypes, Double> groupSizes = new HashMap<>();

    groupList.forEach(group -> {
      if (!groupSizes.containsKey(group.getGroupType())) {
        groupSizes.put(group.getGroupType(), 0.0);
      }
      groupSizes.replace(group.getGroupType(), groupSizes.get(group.getGroupType()) + group.getMemberList().size());
    });
    var groupCount = getGroupCountPerType();
    groupSizes.forEach((groupType, size) -> groupSizes.replace(groupType, size / groupCount.get(groupType)));
    return groupSizes;
  }


  @Override
  public Map<GroupTypes, Long> getGroupCountPerType() {
    var groupList = getFullGroupList();
    Map<GroupTypes, Long> groupCount = new HashMap<>();

    groupList.forEach(group -> {
      if (!groupCount.containsKey(group.getGroupType())) {
        groupCount.put(group.getGroupType(), 0L);
      }
      groupCount.replace(group.getGroupType(), groupCount.get(group.getGroupType()) + 1);
    });
    return groupCount;
  }


  @Override
  public Optional<Double> getAverageTaskPerGroup() {
    var groupList = getFullGroupList();
    long groupCount = groupList.stream().mapToLong(group -> group.getTaskList().size()).sum();
    return Optional.of((groupCount * 1.0) / groupList.size());
  }


}