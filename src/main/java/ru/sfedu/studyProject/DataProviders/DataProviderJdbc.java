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
    executeStatement("drop table if exists GROUP_MEMBERSHIP cascade;\n" +
            "drop table if exists USER_HISTORY cascade;\n" +
            "drop table if exists GROUP_HISTORY cascade;\n" +
            "drop table if exists TASK_HISTORY cascade;\n" +
            "drop table if exists MODIFICATION_RECORD cascade;\n" +
            "drop table if exists USER_TASKS cascade;\n" +
            "drop table if exists USER cascade;\n" +
            "drop table if exists GROUP_TASKS cascade;\n" +
            "drop table if exists \"GROUP\" cascade;\n" +
            "drop table if exists TASK cascade;");
  }

  private void connect() {
    try {
      Class.forName(PropertyLoader.getProperty(Constants.JDBC_DRIVER));
      connection = DriverManager
              .getConnection(PropertyLoader.getProperty(Constants.JDBC_URL));
    } catch (ClassNotFoundException | SQLException | IOException e) {
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
    return executeStatement("create table if not exists user (id integer not null auto_increment, created timestamp not null default(systimestamp), email varchar(128) not null, password varchar(32) not null, name varchar(64) not null, surname varchar(64) not null, token varchar(128) not null, signUpType integer not null, primary key (id));").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists \"GROUP\"( ID LONG auto_increment, NAME VARCHAR(128) not null, CREATED TIMESTAMP default CURRENT_TIMESTAMP not null, GROUP_TYPE INT not null, PASSWORD VARCHAR(64), constraint GROUP_PK primary key (ID));").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists GROUP_MEMBERSHIP( USER_ID LONG not null, GROUP_ID LONG not null, ROLE INT not null, constraint GROUP_MEMBERSHIP_PK primary key (USER_ID, GROUP_ID), constraint GROUP_MEMBERSHIP_GROUP_ID_FK foreign key (GROUP_ID) references \"GROUP\" (ID) on delete cascade, constraint GROUP_MEMBERSHIP_USER_ID_FK foreign key (USER_ID) references USER (ID) on delete cascade);").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists TASK( ID LONG auto_increment, CREATED TIMESTAMP default CURRENT_TIMESTAMP not null, NAME VARCHAR(128) not null, STATUS INT not null, TASK_TYPE INT not null, REPETITION_TYPE INT, REMIND_TYPE INT, IMPORTANCE INT, DESCRIPTION VARCHAR(512), TIME TIMESTAMP, constraint TASK_PK primary key (ID));").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists MODIFICATION_RECORD( ID LONG auto_increment, CHANGED_VALUE_NAME VARCHAR(128) not null, CHANGED_DATE TIMESTAMP default CURRENT_TIMESTAMP not null, CHANGED_VALUE VARCHAR(128) not null, OPERATION_TYPE INT not null, constraint MODIFICATION_RECORD_PK primary key (ID));").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists USER_HISTORY( USER_ID LONG not null, MODIFICATION_RECORD_ID LONG not null, constraint USER_HISTORY_PK primary key (USER_ID, MODIFICATION_RECORD_ID), constraint USER_HISTORY_MODIFICATION_RECORD_ID_FK foreign key (USER_ID) references MODIFICATION_RECORD (ID) on delete cascade, constraint USER_HISTORY_USER_ID_FK foreign key (USER_ID) references USER (ID) on delete cascade);").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists GROUP_HISTORY( GROUP_ID LONG not null, MODIFICATION_RECORD_ID LONG not null, constraint GROUP_HISTORY_PK primary key (GROUP_ID, MODIFICATION_RECORD_ID), constraint GROUP_HISTORY_GROUP_ID_FK foreign key (GROUP_ID) references \"GROUP\" (ID) on delete cascade, constraint GROUP_HISTORY_MODIFICATION_RECORD_ID_FK foreign key (MODIFICATION_RECORD_ID) references MODIFICATION_RECORD (ID) on delete cascade);").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists TASK_HISTORY( TASK_ID LONG not null, MODIFICATION_RECORD_ID LONG not null, constraint TASK_HISTORY_PK primary key (TASK_ID, MODIFICATION_RECORD_ID), constraint TASK_HISTORY_MODIFICATION_RECORD_ID_FK foreign key (MODIFICATION_RECORD_ID) references MODIFICATION_RECORD (ID) on delete cascade, constraint TASK_HISTORY_TASK_ID_FK foreign key (TASK_ID) references TASK (ID) on delete cascade);").equals(Statuses.EXECUTED)
            && executeStatement("create table USER_TASKS\n(\n\tUSER_ID LONG not null,\n\tTASK_ID LONG not null,\n\tconstraint USER_TASKS_PK\n\t\tprimary key (USER_ID, TASK_ID),\n\tconstraint USER_TASKS_TASK_ID_FK\n\t\tforeign key (TASK_ID) references TASK (ID)\n\t\t\ton delete cascade,\n\tconstraint USER_TASKS_USER_ID_FK\n\t\tforeign key (USER_ID) references USER (ID)\n\t\t\ton delete cascade\n);\n\ncreate unique index USER_TASKS_TASK_ID_UINDEX\n\ton USER_TASKS (TASK_ID);\n\n").equals(Statuses.EXECUTED)
            && executeStatement("create table if not exists GROUP_TASKS( GROUP_ID LONG not null, TASK_ID LONG not null, TASK_STATE INT not null, constraint GROUP_TASKS_PK primary key (GROUP_ID, TASK_ID), constraint GROUP_TASKS_GROUP_ID_FK foreign key (GROUP_ID) references \"GROUP\" (ID) on delete cascade, constraint GROUP_TASKS_TASK_ID_FK foreign key (TASK_ID) references TASK (ID) on delete cascade); create unique index GROUP_TASKS_TASK_ID_UINDEX on GROUP_TASKS (TASK_ID);").equals(Statuses.EXECUTED)
            ? Statuses.EXECUTED
            : Statuses.FAILED;
  }


  public boolean isConnected() {
    try {
      return connection.isValid(Connection.TRANSACTION_NONE);
    } catch (SQLException e) {
      log.error(e);
    }
    return false;
  }

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
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s where id = %d;",
              "user", userId));
      assert resultSet.next();
      User user = setUser(resultSet);
      user.setHistoryList(getModificationRecordListFromDB(user));
      user.setTaskList(getTaskListFromDB(user));
      statement.close();
      return Optional.of(user);
    } catch (SQLException e) {
      log.error(e);
      return Optional.empty();
    } catch (AssertionError e) {
      return Optional.empty();
    }
  }

  @Override
  protected Optional<User> getUserFromDB(String email, String password) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s where email = '%s' and password = '%s';",
              "user", email, password));
      assert resultSet.next();
      User user = setUser(resultSet);
      user.setHistoryList(getModificationRecordListFromDB(user));
      user.setTaskList(getTaskListFromDB(user));
      statement.close();
      return Optional.of(user);
    } catch (SQLException | AssertionError e) {
      log.error(e);
      return Optional.empty();
    }
  }

  private User setUser(ResultSet resultSet) throws SQLException {

    User user = new User();
    user.setId(resultSet.getInt("ID"));
    user.setCreated(new Date(resultSet.getTimestamp("CREATED").getTime()));
    user.setEmail(resultSet.getString("EMAIL"));
    user.setPassword(resultSet.getString("PASSWORD"));
    user.setName(resultSet.getString("NAME"));
    user.setSurname(resultSet.getString("SURNAME"));
    user.setToken(resultSet.getString("TOKEN"));
    user.setSignUpType(SignUpTypes.values()[(resultSet.getInt("SIGNUPTYPE"))]);
    return user;
  }

  @Override
  protected List<User> getUserListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s;",
              "user"));
      List<User> userList = new ArrayList<>();
      while (resultSet.next()) {
        User user = setUser(resultSet);
        user.setHistoryList(getModificationRecordListFromDB(user));
        user.setTaskList(getTaskListFromDB(user));
        userList.add(user);
      }
      statement.close();
      return userList;
    } catch (SQLException | AssertionError e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<User, UserRole> getUserListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select A.*, B.ROLE from (select * from USER where ID in (select USER_ID from GROUP_MEMBERSHIP where GROUP_ID = %d)) as A inner join (select * from GROUP_MEMBERSHIP where GROUP_ID = %d) as B on A.ID = USER_ID;",
              group.getId(),
              group.getId()));
      Map<User, UserRole> userRoleMap = new HashMap<>();
      while (resultSet.next()) {
        User user = setUser(resultSet);
        user.setHistoryList(getModificationRecordListFromDB(user));
        user.setTaskList(getTaskListFromDB(user));
        userRoleMap.put(user, UserRole.values()[resultSet.getInt("ROLE")]);
      }
      statement.close();
      return userRoleMap;
    } catch (SQLException | AssertionError e) {
      log.error(e);
      return new HashMap<>();
    }
  }


  @Override
  protected Statuses saveUserInDB(User user) {
    Statuses status;
    try {
      assert getUserFromDB(user.getId()).isPresent();
      executeStatement(String.format("UPDATE USER set CREATED = parsedatetime ('%s', '%s'), EMAIL = '%s', PASSWORD = '%s', NAME = '%s', SURNAME = '%s',TOKEN = '%s', SIGNUPTYPE = '%d' where ID = '%d';",
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
    } catch (AssertionError e) {
      executeStatement(String.format("insert into USER values (default, default , '%s', '%s', '%s', '%s', '%s', %d);",
              user.getEmail(),
              user.getPassword(),
              user.getName(),
              user.getSurname(),
              user.getToken(),
              user.getSignUpType().ordinal()));
      status = Statuses.INSERTED;
    }
    user.getTaskList().forEach(task -> executeStatement(String.format("insert into USER_TASKS values (%d, %d)",
            user.getId(),
            task.getId())));
    user.getHistoryList().forEach(modificationRecord -> executeStatement(String.format("insert into USER_HISTORY values (%d, %d)",
            user.getId(),
            modificationRecord.getId())));
    return status;
  }

  private Group setGroup(ResultSet resultSet) throws SQLException {
    Group group = GroupTypes.values()[resultSet.getInt("group_type")].equals(GroupTypes.PASSWORDED)
            ? new PasswordedGroup(resultSet.getString("PASSWORD"))
            : new Group();
    group.setId(resultSet.getInt("ID"));
    group.setName(resultSet.getString("name"));
    group.setCreated(resultSet.getTimestamp("created"));
    group.setGroupType(GroupTypes.values()[resultSet.getInt("group_type")]);
    group.setMemberList(getUserListFromDB(group));
    group.setHistoryList(getModificationRecordListFromDB(group));
    group.setTaskList(getTaskListFromDB(group));
    return group;
  }

  @Override
  protected Optional<Group> getGroupFromDB(long groupId) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s where id = %d;",
              "\"GROUP\"", groupId));
      assert resultSet.next();
      Group group = setGroup(resultSet);
      statement.close();
      return Optional.of(group);
    } catch (SQLException | AssertionError e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s;",
              "\"GROUP\""));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from \"GROUP\" where ID in (select GROUP_ID from GROUP_MEMBERSHIP where USER_ID = %d);",
              user.getId()));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Group> getGroupListFromDB(String name) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from \"GROUP\" where name like '%%%s%%';",
              name));
      List<Group> groupList = new ArrayList<>();
      while (resultSet.next()) {
        groupList.add(setGroup(resultSet));
      }
      statement.close();
      return groupList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Statuses saveGroupInDB(Group group) {
    Statuses status;
    try {
      assert getGroupFromDB(group.getId()).isPresent();
      executeStatement(String.format("UPDATE \"GROUP\" set NAME = '%s', GROUP_TYPE = %d, PASSWORD = %s where ID = %d",
              group.getName(),
              group.getGroupType().ordinal(),
              group.getGroupType().equals(GroupTypes.PASSWORDED)
                      ? "'" + ((PasswordedGroup) group).getPassword() + "'"
                      : "null",
              group.getId()));
      status = Statuses.UPDATED;
    } catch (AssertionError e) {
      executeStatement(String.format("INSERT INTO PUBLIC.\"GROUP\" (ID ,NAME, CREATED, GROUP_TYPE, PASSWORD) VALUES (%d ,'%s', DEFAULT, %d, %s);",
              group.getId(),
              group.getName(),
              group.getGroupType().ordinal(),
              group.getGroupType().equals(GroupTypes.PASSWORDED)
                      ? "'" + ((PasswordedGroup) group).getPassword() + "'"
                      : "null"
      ));
      status = Statuses.INSERTED;
    }
    group.getTaskList().forEach((task, state) -> executeStatement(String.format("insert into GROUP_TASKS values (%d, %d, %d)",
            group.getId(),
            task.getId(),
            state.ordinal())));
    group.getMemberList().forEach((user, role) -> executeStatement(String.format("insert into GROUP_MEMBERSHIP values (%d, %d, %d)",
            user.getId(),
            group.getId(),
            role.ordinal())));
    group.getHistoryList().forEach(modificationRecord -> executeStatement(String.format("insert into GROUP_HISTORY values (%d, %d)",
            group.getId(),
            modificationRecord.getId())));
    log.trace(getGroup(group.getId()));
    return status;
  }

  @Override
  protected Statuses deleteGroupFromDB(long groupId) {
    return executeStatement(String.format("DELETE FROM PUBLIC.\"GROUP\" WHERE ID = %d", groupId)).equals(Statuses.EXECUTED)
            ? Statuses.DELETED
            : Statuses.FAILED;
  }

  private Task setTask(ResultSet resultSet) throws SQLException {
    Task task = TaskTypes.values()[resultSet.getInt("task_type")].equals(TaskTypes.EXTENDED)
            ? new ExtendedTask(RepetitionTypes.values()[resultSet.getInt("repetition_type")],
            RemindTypes.values()[resultSet.getInt("remind_type")],
            Importances.values()[resultSet.getInt("importance")],
            resultSet.getString("description"),
            resultSet.getTimestamp("time"))
            : new Task();
    task.setTaskType(TaskTypes.values()[resultSet.getInt("task_type")]);
    task.setName(resultSet.getString("name"));
    task.setId(resultSet.getLong("id"));
    task.setCreated(resultSet.getTimestamp("created"));
    task.setStatus(TaskStatuses.values()[resultSet.getInt("status")]);
    task.setHistoryList(getModificationRecordListFromDB(task));
    return task;
  }

  @Override
  protected Optional<Task> getTaskFromDB(long taskId) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s where id = %d;",
              "task", taskId));
      assert resultSet.next();
      Task task = setTask(resultSet);
      statement.close();
      return Optional.of(task);
    } catch (SQLException | AssertionError e) {
      log.error(e);
      return Optional.empty();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB() {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from %s;",
              "task"));
      List<Task> taskList = new ArrayList<>();
      while (resultSet.next()) {
        taskList.add(setTask(resultSet));
      }
      statement.close();
      return taskList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<Task> getTaskListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from TASK where ID in (SELECT TASK_ID from USER_TASKS where USER_ID = %d);",
              user.getId()));
      List<Task> taskList = new ArrayList<>();
      while (resultSet.next()) {
        taskList.add(setTask(resultSet));
      }
      statement.close();
      return taskList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected Map<Task, TaskState> getTaskListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select TASK.*, GT.TASK_STATE from TASK inner join GROUP_TASKS GT on TASK.ID = GT.TASK_ID WHERE GROUP_ID = %d",
              group.getId()));
      Map<Task, TaskState> taskStateMap = new HashMap<>();
      while (resultSet.next()) {
        taskStateMap.put(setTask(resultSet), TaskState.values()[resultSet.getInt("TASK_STATE")]);
      }
      statement.close();
      return taskStateMap;
    } catch (SQLException e) {
      log.error(e);
      return new HashMap<>();
    }
  }

  @Override
  protected Statuses saveTaskInDB(Task task) {
    Statuses status;
    try {
      assert getTaskFromDB(task.getId()).isPresent();
      status = executeStatement(String.format("UPDATE TASK set NAME = '%s', STATUS = %d, TASK_TYPE = %d, REPETITION_TYPE = %s, REMIND_TYPE = %s, IMPORTANCE = %s, DESCRIPTION = '%s', TIME = %s where ID = %d",
              task.getName(),
              task.getStatus().ordinal(),
              task.getTaskType().ordinal(),
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getRepetitionType().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getRemindType().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getImportance().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? "'" + ((ExtendedTask) task).getDescription() + "'"
                      : "null",

              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? String.format("parsedatetime ('%s', '%s')",
                      dateFormat.format(((ExtendedTask) task).getTime()),
                      Constants.DATE_FORMAT)
                      : "null",
              task.getId()
      )).equals(Statuses.EXECUTED)
              ? Statuses.UPDATED
              : Statuses.FAILED;
    } catch (AssertionError e) {
      status = executeStatement(String.format("INSERT INTO PUBLIC.TASK " +
                      "(NAME, STATUS, TASK_TYPE, REPETITION_TYPE, REMIND_TYPE, IMPORTANCE, DESCRIPTION, TIME)" +
                      " VALUES ('%s', %d, %d, %s, %s, %s, %s, %s);",
              task.getName(),
              task.getStatus().ordinal(),
              task.getTaskType().ordinal(),
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getRepetitionType().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getRemindType().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? ((ExtendedTask) task).getImportance().ordinal()
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? "'" + ((ExtendedTask) task).getDescription() + "'"
                      : "null",
              task.getTaskType().equals(TaskTypes.EXTENDED)
                      ? String.format("parsedatetime ('%s', '%s')",
                      dateFormat.format(((ExtendedTask) task).getTime()),
                      Constants.DATE_FORMAT)
                      : "null"
      )).equals(Statuses.EXECUTED)
              ? Statuses.INSERTED
              : Statuses.FAILED;
    }
    task.getHistoryList().forEach(modificationRecord -> executeStatement(String.format("insert into TASK_HISTORY values (%d, %d)",
            task.getId(),
            modificationRecord.getId())));
    return status;
  }

  @Override
  protected Statuses deleteTaskFromDB(long taskId) {
    return executeStatement(String.format("DELETE FROM PUBLIC.\"TASK\" WHERE ID = %d", taskId)).equals(Statuses.EXECUTED)
            ? Statuses.DELETED
            : Statuses.FAILED;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(User user) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from MODIFICATION_RECORD where ID in (select MODIFICATION_RECORD_ID from USER_HISTORY where USER_ID = %d);",
              user.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  private ModificationRecord setModificationRecord(ResultSet resultSet) {
    try {
      ModificationRecord record = new ModificationRecord();
      record.setId(resultSet.getLong("id"));
      record.setChangedDate(resultSet.getDate("CHANGED_DATE"));
      record.setChangedValueName(resultSet.getString("CHANGED_VALUE_NAME"));
      record.setChangedValue(resultSet.getString("CHANGED_VALUE"));
      record.setOperationType(OperationType.values()[resultSet.getInt("OPERATION_TYPE")]);
      return record;
    } catch (SQLException e) {
      log.error(e);
    }
    return null;
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Task task) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from MODIFICATION_RECORD where ID in (select MODIFICATION_RECORD_ID from TASK_HISTORY where TASK_ID = %d);",
              task.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected List<ModificationRecord> getModificationRecordListFromDB(Group group) {
    try {
      Statement statement = createStatement();
      ResultSet resultSet = statement.executeQuery(String.format("select * from MODIFICATION_RECORD where ID in (select MODIFICATION_RECORD_ID from GROUP_HISTORY where GROUP_ID = %d);",
              group.getId()));
      List<ModificationRecord> historyList = new ArrayList<>();
      while (resultSet.next()) {
        historyList.add(setModificationRecord(resultSet));
      }
      statement.close();
      return historyList;
    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  protected void createModificationRecordInDB(ModificationRecord modificationRecord) {
    executeStatement(String.format("INSERT INTO PUBLIC.MODIFICATION_RECORD (CHANGED_VALUE_NAME, CHANGED_VALUE, OPERATION_TYPE) VALUES ('%s', '%s', %d)",
            modificationRecord.getChangedValueName(),
            modificationRecord.getChangedValue(),
            modificationRecord.getOperationType().ordinal()));
  }

  @Override
  protected <T> long getNextId(Class<T> tClass) {
    try {

      if (tClass.equals(User.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery("select max(ID) as MAX_ID  from USER");
        if (set.next()) {
          return set.getInt("MAX_ID") + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(Task.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery("select max(ID) as MAX_ID  from TASK");
        if (set.next()) {
          return set.getInt("MAX_ID") + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(ModificationRecord.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery("select max(ID) as MAX_ID  from MODIFICATION_RECORD");
        if (set.next()) {
          return set.getInt("MAX_ID") + 1;
        } else {
          return 1;
        }
      }
      if (tClass.equals(Group.class)) {
        Statement statement = createStatement();
        var set = statement.executeQuery("select max(ID) as MAX_ID  from \"GROUP\"");
        if (set.next()) {
          return set.getInt("MAX_ID") + 1;
        } else {
          return 1;
        }
      } else {
        return -1;
      }
    } catch (SQLException e) {
      log.error(e);
      return -1;
    }
  }
}
