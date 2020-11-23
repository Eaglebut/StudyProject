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
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
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

  //TODO make private
  public <T> void insertIntoCsv(T object) throws IOException {
    insertIntoCsv(object, false);
  }

  //TODO make private
  public <T> void insertIntoCsv(T object, boolean overwrite) throws IOException {
    insertIntoCsv(Collections.singletonList(object), overwrite);
  }

  //TODO make private
  public <T> void insertIntoCsv(List<T> objectList, boolean overwrite) throws IOException {
    Optional<T> tOptional = objectList.stream().findAny();
    Class<?> tClass;
    if (tOptional.isPresent()) {
      tClass = tOptional.get().getClass();
    } else {
      return;
    }
    List<T> tList;
    if (!overwrite) {
      tList = (List<T>) getFromCsv(tClass);
      tList.addAll(objectList);
    } else {
      tList = objectList;
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
      List<Task> usersTaskList = user.getTaskList();
      List<Task> usersFilledTaskList = new ArrayList<>();
      usersTaskList.forEach(usersTask -> taskList.forEach(task -> {
        if (usersTask.getId() == task.getId()) {
          task.setHistoryList(getHistoryList(task));
          usersFilledTaskList.add(task);
        }
      }));
      return usersFilledTaskList;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  //TODO need metadata
  private <T> long getNextId(Class<T> tClass) throws IOException {
      return getFromCsv(tClass).size() + 1;
  }

  @Override
  public Statuses createTask(@NonNull User user,
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
      insertIntoCsv(createdTask, false);
      user.getTaskList().add(createdTask);
      updateUser(user);
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
    return Statuses.INSERTED;
  }

  //TODO
  @Override
  public Statuses createTask(@NonNull User user, @NonNull String taskName, @NonNull TaskStatuses status, @NonNull RepetitionTypes repetitionType, @NonNull RemindTypes remindType, @NonNull Importances importance, @NonNull String description, @NonNull Date time) {
    return null;
  }

  //TODO
  @Override
  public Statuses deleteTask(@NonNull User user, @NonNull Task task) {
    return null;
  }

  //TODO
  @Override
  public Statuses editTask(@NonNull User user, @NonNull Task editedTask) {
    return null;
  }

  public Statuses createUser(User user) {
    try {
      List<User> userList = getFromCsv(User.class);
      if (userList.stream().anyMatch(listUser -> listUser.getEmail().equals(user.getEmail()))) {
        return Statuses.FORBIDDEN;
      }
      user.setId(getNextId(User.class));
      insertIntoCsv(user);
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
      insertIntoCsv(userList, true);
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

      if (!optionalUser.get().getTaskList().equals(editedUser.getTaskList())){
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
  public Statuses addUserToGroup(@NonNull User user, @NonNull Group group) {
    return null;
  }

  //TODO
  @Override
  public Statuses createGroup(@NonNull String groupName, @NonNull User creator, @NonNull GroupTypes groupType) {
    return null;
  }

  //TODO
  @Override
  public Statuses changeGroupType(@NonNull User user, @NonNull Group group, @NonNull GroupTypes groupType) {
    return null;
  }

  //TODO
  @Override
  public List<Group> searchGroupByName(@NonNull String name) {
    return null;
  }

  //TODO
  @Override
  public Group searchGroupById(@NonNull long id) throws NoSuchElementException {
    return null;
  }

  //TODO
  @Override
  public List<Group> getFullGroupList() {
    return null;
  }

  //TODO
  @Override
  public Optional<Group> getGroup(@NonNull long groupId) {
    return Optional.empty();
  }

  //TODO
  @Override
  public Optional<Group> getGroup(@NonNull User user, @NonNull long groupId) {
    return Optional.empty();
  }

  //TODO
  @Override
  public Statuses deleteUserFromGroup(@NonNull User user, @NonNull Group group) {
    return null;
  }

  //TODO
  @Override
  public Statuses createTask(@NonNull User user, @NonNull Group group, @NonNull Task task) {
    return null;
  }

  //TODO
  @Override
  public Statuses createTask(@NonNull User user, @NonNull Group group, @NonNull String name) {
    return null;
  }

  //TODO
  @Override
  public Statuses createTask(@NonNull User user,
                             @NonNull Group group,
                             @NonNull String name,
                             @NonNull RepetitionTypes repetitionType,
                             @NonNull RemindTypes remindType,
                             @NonNull Importances importance,
                             @NonNull String description,
                             @NonNull Date time) {
    return null;
  }

  //TODO
  @Override
  public Statuses updateGroup(@NonNull User user, @NonNull Group group) {
    return null;
  }

  //TODO
  @Override
  public Statuses setUserRole(@NonNull User user, @NonNull Group group, @NonNull User userToSet, @NonNull UserRole role) {
    return null;
  }

  //TODO
  @Override
  public Statuses changeTaskState(@NonNull User user, @NonNull Group group, @NonNull Task task, @NonNull TaskState state) {
    return null;
  }

  //TODO
  @Override
  public Statuses deleteGroup(@NonNull User user, @NonNull Group group) {
    return null;
  }
}
