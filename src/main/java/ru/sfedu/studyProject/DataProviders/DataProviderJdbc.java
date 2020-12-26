package ru.sfedu.studyProject.DataProviders;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

@Log4j2
public class DataProviderJdbc extends AbstractDataProvider {

  private static final DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
  private static DataProviderJdbc INSTANCE;
  private Connection connection;

  private DataProviderJdbc() {
    connect();
  }

  public static DataProviderJdbc getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataProviderJdbc();
    }
    return INSTANCE;
  }

  public void dropAll() {
    try {
      executeStatement(PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_GROUP_MEMBERSHIP)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_USER_HISTORY)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_GROUP_HISTORY)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_TASK_HISTORY)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_MODIFICATION_RECORD)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_USERS_TASKS)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_USER)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_GROUP_TASKS)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_GROUP)
              + PropertyLoader.getProperty(Constants.SQL_DROP_TABLE_TASK));
    } catch (IOException e) {
      log.error(e);
    }
  }

  private void connect() {
    try {
      //Class.forName(PropertyLoader.getProperty(Constants.JDBC_DRIVER));
      connection = DriverManager
              .getConnection(PropertyLoader.getProperty(Constants.JDBC_URL));
    } catch (SQLException | IOException e) {
      log.error(e);
    }
  }


  public Statuses executeStatement(String queryString) {
    try {
      log.debug(queryString);
      Statement statement = createStatement();
      statement.executeUpdate(queryString);
      statement.close();
      connection.commit();
      return Statuses.EXECUTED;
    } catch (SQLException exception) {
      log.error(exception);
      return Statuses.FAILED;
    }
  }

  private Statement createStatement() throws SQLException {
    if (!connection.isClosed()) {
      return connection.createStatement();
    } else {
      throw new SQLException();
    }
  }

  public Statuses createDatabase() {
    try {
      return executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_USER))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_GROUP))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_GROUP_MEMBERSHIP))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_TASK))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_MODIFICATION_RECORD))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_USER_HISTORY))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_GROUP_HISTORY))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_TASK_HISTORY))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_USERS_TASKS))
              .equals(Statuses.EXECUTED)
              && executeStatement(PropertyLoader.getProperty(Constants.SQL_CREATE_TABLE_GROUP_TASKS))
              .equals(Statuses.EXECUTED)
              ? Statuses.EXECUTED
              : Statuses.FAILED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  /**
   * Is connected boolean.
   *
   * @return the boolean
   */
  public boolean isConnected() {
    try {
      return connection.isValid(Connection.TRANSACTION_NONE);
    } catch (SQLException e) {
      log.error(e);
    }
    return false;
  }

  /**
   * Close connection.
   */
  public void closeConnection() {
    try {
      connection.close();
    } catch (SQLException e) {
      log.error(e);
    }
  }

  @Override
  protected Optional<User> getUserFromDB(long userId) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_BY_ID),
              User.class.getSimpleName().toLowerCase(), userId));
      if (!resultSet.next()) {
        return Optional.empty();
      }
      User user = setUser(resultSet);
      user.setHistoryList(getModificationRecordListFromDB(user));
      user.setTaskList(getTaskListFromDB(user));
      statement.close();
      return Optional.of(user);
    } catch (SQLException | IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected Optional<User> getUserFromDB(String email, String password) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_BY_EMAIL_PASSWORD),
              email,
              password));
      if (!resultSet.next()) {
        return Optional.empty();
      }
      User user = setUser(resultSet);
      user.setHistoryList(getModificationRecordListFromDB(user));
      user.setTaskList(getTaskListFromDB(user));
      statement.close();
      return Optional.of(user);
    } catch (SQLException | AssertionError | IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  private User setUser(ResultSet resultSet) throws SQLException {
    try {
      User user = new User();
      user.setId(resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_ID).toUpperCase()));
      user.setCreated(new Date(resultSet.getTimestamp(PropertyLoader.getProperty(Constants.FIELD_NAME_CREATED).toUpperCase())
              .getTime()));
      user.setEmail(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_EMAIL).toUpperCase()));
      user.setPassword(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD).toUpperCase()));
      user.setName(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_NAME).toUpperCase()));
      user.setSurname(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_SURNAME).toUpperCase()));
      user.setToken(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_TOKEN).toUpperCase()));
      user.setSignUpType(SignUpTypes.values()[(resultSet.getInt(PropertyLoader
              .getProperty(Constants.FIELD_NAME_SIGN_UP_TYPE).toUpperCase()))]);
      return user;
    } catch (IOException e) {
      log.error(e);
      return new User();
    }
  }

  @Override
  protected List<User> getUserListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement
              .executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT),
                      User.class.getSimpleName().toLowerCase(Locale.ROOT)));
      List<User> userList = new ArrayList<>();
      while (resultSet.next()) {
        User user = setUser(resultSet);
        user.setHistoryList(getModificationRecordListFromDB(user));
        user.setTaskList(getTaskListFromDB(user));
        userList.add(user);
      }
      statement.close();
      return userList;
    } catch (SQLException | AssertionError | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<User, UserRole> getUserListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_MEMBER_LIST),
              group.getId(),
              group.getId()));
      Map<User, UserRole> userRoleMap = new HashMap<>();
      while (resultSet.next()) {
        User user = setUser(resultSet);
        user.setHistoryList(getModificationRecordListFromDB(user));
        user.setTaskList(getTaskListFromDB(user));
        userRoleMap.put(user, UserRole.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_USER_ROLE))]);
      }
      statement.close();
      return userRoleMap;
    } catch (SQLException | AssertionError | IOException e) {
      log.error(e);
      return new HashMap<>();
    }
  }


  @Override
  protected Statuses saveUserInDB(User user) {
    try {
      Statuses status;
      if (getUserFromDB(user.getId()).isPresent()) {
        executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_UPDATE_USER),
                dateFormat.format(user.getCreated()),
                Constants.DATE_FORMAT,
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getToken(),
                user.getSignUpType().ordinal(),
                user.getId()));
        status = Statuses.UPDATED;
      } else {
        executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_USER),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getToken(),
                user.getSignUpType().ordinal()));
        status = Statuses.INSERTED;
      }
      user.getTaskList().forEach(task -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_USER_TASK),
                  user.getId(),
                  task.getId()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      user.getHistoryList().forEach(modificationRecord -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_USER_HISTORY),
                  user.getId(),
                  modificationRecord.getId()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      return status;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Group setGroup(ResultSet resultSet) throws SQLException {
    try {
      Group group = GroupTypes.values()[resultSet.getInt(PropertyLoader
              .getProperty(Constants.FIELD_NAME_GROUP_TYPE_SQL))].equals(GroupTypes.PASSWORDED)
              ? new PasswordedGroup(resultSet.getString(PropertyLoader
              .getProperty(Constants.FIELD_NAME_PASSWORD)))
              : new Group();
      group.setId(resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_ID)));
      group.setName(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_NAME)));
      group.setCreated(resultSet.getTimestamp(PropertyLoader.getProperty(Constants.FIELD_NAME_CREATED)));
      group.setGroupType(GroupTypes.values()[resultSet
              .getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_GROUP_TYPE_SQL))]);
      group.setMemberList(getUserListFromDB(group));
      group.setHistoryList(getModificationRecordListFromDB(group));
      group.setTaskList(getTaskListFromDB(group));
      return group;
    } catch (IOException e) {
      log.error(e);
      return new Group();
    }
  }

  @Override
  protected Optional<Group> getGroupFromDB(long groupId) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_BY_ID),
              PropertyLoader.getProperty(Constants.QUOTES)
                      + Group.class.getSimpleName().toUpperCase()
                      + PropertyLoader.getProperty(Constants.QUOTES), groupId));
      if (!resultSet.next()) {
        return Optional.empty();
      }
      Group group = setGroup(resultSet);
      statement.close();
      return Optional.of(group);
    } catch (SQLException | AssertionError | IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT),
              PropertyLoader.getProperty(Constants.QUOTES)
                      + Group.class.getSimpleName().toUpperCase()
                      + PropertyLoader.getProperty(Constants.QUOTES)));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_GROUP_BY_USER),
              user.getId()));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(String name) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_GROUP_BY_NAME),
              name));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Statuses saveGroupInDB(Group group) {
    try {
      Statuses status;
      if (getGroupFromDB(group.getId()).isPresent()) {
        executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_UPDATE_GROUP),
                group.getName(),
                group.getGroupType().ordinal(),
                group.getGroupType().equals(GroupTypes.PASSWORDED)
                        ? PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                        + ((PasswordedGroup) group).getPassword()
                        + PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                        : PropertyLoader.getProperty(Constants.NULL),
                group.getId()));
        status = Statuses.UPDATED;
      } else {
        executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_GROUP),
                group.getId(),
                group.getName(),
                group.getGroupType().ordinal(),
                group.getGroupType().equals(GroupTypes.PASSWORDED)
                        ? PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                        + ((PasswordedGroup) group).getPassword()
                        + PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                        : PropertyLoader.getProperty(Constants.NULL)
        ));
        status = Statuses.INSERTED;
      }
      group.getTaskList().forEach((task, state) -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_GROUP_TASK),
                  group.getId(),
                  task.getId(),
                  state.ordinal()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      group.getMemberList().forEach((user, role) -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_GROUP_MEMBERSHIP),
                  user.getId(),
                  group.getId(),
                  role.ordinal()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      group.getHistoryList().forEach(modificationRecord -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_GROUP_HISTORY),
                  group.getId(),
                  modificationRecord.getId()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      log.trace(getGroup(group.getId()));
      return status;

    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Statuses deleteGroupFromDB(long groupId) {
    try {
      return executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_DELETE_GROUP), groupId)).equals(Statuses.EXECUTED)
              ? Statuses.DELETED
              : Statuses.FAILED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Task setTask(ResultSet resultSet) throws SQLException {
    try {
      Task task = TaskTypes.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK_TYPE))].equals(TaskTypes.EXTENDED)
              ? new ExtendedTask(RepetitionTypes.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_REPETITION_TYPE_SQL))],
              RemindTypes.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_REMIND_TYPE_SQL))],
              Importances.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_IMPORTANCE))],
              resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_DESCRIPTION)),
              resultSet.getTimestamp(PropertyLoader.getProperty(Constants.FIELD_NAME_TIME)))
              : new Task();
      task.setTaskType(TaskTypes.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK_TYPE))]);
      task.setName(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_NAME)));
      task.setId(resultSet.getLong(PropertyLoader.getProperty(Constants.FIELD_NAME_ID)));
      task.setCreated(resultSet.getTimestamp(PropertyLoader.getProperty(Constants.FIELD_NAME_CREATED)));
      task.setStatus(TaskStatuses.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_NAME_STATUS))]);
      task.setHistoryList(getModificationRecordListFromDB(task));
      return task;
    } catch (IOException e) {
      log.error(e);
      return new Task();
    }
  }

  @Override
  protected Optional<Task> getTaskFromDB(long taskId) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_BY_ID),
              Task.class.getSimpleName(), taskId));
      if (!resultSet.next()) {
        return Optional.empty();
      }
      Task task = setTask(resultSet);
      statement.close();
      return Optional.of(task);
    } catch (SQLException | AssertionError | IOException e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT),
              Task.class.getSimpleName()));
      List<Task> taskList = new ArrayList<>();
      while (resultSet.next()) {
        taskList.add(setTask(resultSet));
      }
      statement.close();
      return taskList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_USERS_TASK_LIST),
              user.getId()));
      List<Task> taskList = new ArrayList<>();
      while (resultSet.next()) {
        taskList.add(setTask(resultSet));
      }
      statement.close();
      return taskList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<Task, TaskState> getTaskListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_GROUP_TASK_LIST),
              group.getId()));
      Map<Task, TaskState> taskStateMap = new HashMap<>();
      while (resultSet.next()) {
        taskStateMap.put(setTask(resultSet), TaskState.values()[resultSet.getInt(PropertyLoader.getProperty(Constants.FIELD_TASK_STATE))]);
      }
      statement.close();
      return taskStateMap;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new HashMap<>();
    }
  }

  @Override
  protected Statuses saveTaskInDB(Task task) {

      Statuses status;
      try {
        if (getTaskFromDB(task.getId()).isPresent()) {
          status = executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_UPDATE_TASK),
                  task.getName(),
                  task.getStatus().ordinal(),
                  task.getTaskType().ordinal(),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getRepetitionType().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getRemindType().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getImportance().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                          + ((ExtendedTask) task).getDescription()
                          + PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                          : PropertyLoader.getProperty(Constants.NULL),

                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? String.format(PropertyLoader.getProperty(Constants.SQL_PARSE_DATE_TIME),
                          dateFormat.format(((ExtendedTask) task).getTime()),
                          Constants.DATE_FORMAT)
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getId()
          )).equals(Statuses.EXECUTED)
                  ? Statuses.UPDATED
                  : Statuses.FAILED;
        } else {
          status = executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_TASK),
                  task.getName(),
                  task.getStatus().ordinal(),
                  task.getTaskType().ordinal(),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getRepetitionType().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getRemindType().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? ((ExtendedTask) task).getImportance().ordinal()
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                          + ((ExtendedTask) task).getDescription()
                          + PropertyLoader.getProperty(Constants.SINGLE_QUOTES)
                          : PropertyLoader.getProperty(Constants.NULL),
                  task.getTaskType().equals(TaskTypes.EXTENDED)
                          ? String.format(PropertyLoader.getProperty(Constants.SQL_PARSE_DATE_TIME),
                        dateFormat.format(((ExtendedTask) task).getTime()),
                        Constants.DATE_FORMAT)
                        : PropertyLoader.getProperty(Constants.NULL)
        )).equals(Statuses.EXECUTED)
                ? Statuses.INSERTED
                : Statuses.FAILED;
      }
      task.getHistoryList().forEach(modificationRecord -> {
        try {
          executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_TASK_HISTORY),
                  task.getId(),
                  modificationRecord.getId()));
        } catch (IOException e) {
          log.error(e);
        }
      });
      return status;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected Statuses deleteTaskFromDB(long taskId) {
    try {
      return executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_DELETE_TASK), taskId)).equals(Statuses.EXECUTED)
              ? Statuses.DELETED
              : Statuses.FAILED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_MODIFICATION_RECORD_USER),
              user.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  private ModificationRecord setModificationRecord(ResultSet resultSet) {
    try {
      ModificationRecord record = new ModificationRecord();
      record.setId(resultSet.getLong(PropertyLoader.getProperty(Constants.FIELD_NAME_ID)));
      record.setChangedDate(resultSet.getDate(PropertyLoader
              .getProperty(Constants.FIELD_NAME_CHANGED_DATE)));
      record.setChangedValueName(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_ID)));
      record.setChangedValue(resultSet.getString(PropertyLoader.getProperty(Constants.FIELD_NAME_CHANGED_VALUE)));
      record.setOperationType(OperationType.values()[resultSet.getInt(PropertyLoader
              .getProperty(Constants.FIELD_NAME_OPERATION_TYPE))]);
      return record;
    } catch (SQLException | IOException e) {
      log.error(e);
    }
    return null;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Task task) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_MODIFICATION_RECORD_TASK),
              task.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format(PropertyLoader
                      .getProperty(Constants.SQL_SELECT_MODIFICATION_RECORD_GROUP),
              group.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException | IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected void createModificationRecordInDB(ModificationRecord modificationRecord) {
    try {
      executeStatement(String.format(PropertyLoader.getProperty(Constants.SQL_INSERT_MODIFICATION_RECORD),
              modificationRecord.getChangedValueName(),
              modificationRecord.getChangedValue(),
              modificationRecord.getOperationType().ordinal()));
    } catch (IOException e) {
      log.error(e);
    }
  }

  @Override
  protected <T> long getNextId(Class<T> tClass) {
    try {
      if (tClass.equals(User.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_MAX_ID),
                User.class.getSimpleName().toUpperCase()));
        if (set.next()) {
          return set.getInt(PropertyLoader.getProperty(Constants.FIELD_MAX_ID)) + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(Task.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_MAX_ID),
                Task.class.getSimpleName().toUpperCase()));
        if (set.next()) {
          return set.getInt(PropertyLoader.getProperty(Constants.FIELD_MAX_ID)) + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(ModificationRecord.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_MAX_ID),
                PropertyLoader.getProperty(Constants.SQL_BEAN_NAME_MODIFICATION_RECORD)));
        if (set.next()) {
          return set.getInt(PropertyLoader.getProperty(Constants.FIELD_MAX_ID)) + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(Group.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery(String.format(PropertyLoader.getProperty(Constants.SQL_SELECT_MAX_ID),
                Group.class.getSimpleName().toUpperCase()));
        if (set.next()) {
          return set.getInt(PropertyLoader.getProperty(Constants.FIELD_MAX_ID)) + 1;
        } else {
          return 1;
        }
      } else {
        return -1;
      }
    } catch (SQLException | IOException e) {
      log.error(e);
      return -1;
    }
  }
}
