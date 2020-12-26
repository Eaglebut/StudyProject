package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.LogUtil;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Log4j2
public abstract class AbstractDataProvider implements DataProvider {

  protected abstract Optional<User> getUserFromDB(long userId);

  protected abstract Optional<User> getUserFromDB(String email, String password);

  protected abstract List<User> getUserListFromDB();

  protected abstract Map<User, UserRole> getUserListFromDB(Group group);

  protected abstract Statuses saveUserInDB(User user);

  protected abstract Optional<Group> getGroupFromDB(long groupId);

  protected abstract List<Group> getGroupListFromDB();

  protected abstract List<Group> getGroupListFromDB(User user);

  protected abstract List<Group> getGroupListFromDB(String name);

  protected abstract Statuses saveGroupInDB(Group group);

  protected abstract Statuses deleteGroupFromDB(long groupId);

  protected abstract Optional<Task> getTaskFromDB(long TaskId);

  protected abstract List<Task> getTaskListFromDB();

  protected abstract List<Task> getTaskListFromDB(User user);

  protected abstract Map<Task, TaskState> getTaskListFromDB(Group group);

  protected abstract Statuses saveTaskInDB(Task task);

  protected abstract Statuses deleteTaskFromDB(long taskId);

  protected abstract List<ModificationRecord> getModificationRecordListFromDB(User user);

  protected abstract List<ModificationRecord> getModificationRecordListFromDB(Task task);

  protected abstract List<ModificationRecord> getModificationRecordListFromDB(Group group);

  protected abstract void createModificationRecordInDB(ModificationRecord modificationRecord);

  protected abstract <T> long getNextId(Class<T> tClass);


  //start user zone
  @Override
  public Optional<User> getUser(long userId) {
    log.info(LogUtil.startFunc(userId));
    var user = getUserFromDB(userId);
    log.info(LogUtil.endFunc(userId));
    return user;
  }

  @Override
  public Optional<User> getUser(@NonNull String email,
                                @NonNull String password) {
    log.info(LogUtil.startFunc(email, password));
    var user = getUserFromDB(email, password);
    log.info(LogUtil.endFunc(email, password));
    return user;
  }

  @Override
  public Statuses createUser(@NonNull String email,
                             @NonNull String password,
                             @NonNull String name,
                             @NonNull String surname,
                             @NonNull SignUpTypes signUpType) {
    log.info(LogUtil.startFunc(email, password, name, surname, signUpType));
    List<User> userList = getUserListFromDB();
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
    user.setToken(Integer.toHexString(user.hashCode()));
    var status = saveUserInDB(user);
    log.info(LogUtil.endFunc(email, password, name, surname, signUpType));
    return status;
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

  private Statuses saveUserChanges(User serverUser, User editedUser) {
    try {
      if (!serverUser.getName().equals(editedUser.getName())) {
        editedUser.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                OperationType.EDIT,
                serverUser.getName()));
      }
      if (!serverUser.getSurname().equals(editedUser.getSurname())) {
        editedUser.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_SURNAME),
                OperationType.EDIT,
                serverUser.getSurname()));
      }
      if (!serverUser.getEmail().equals(editedUser.getEmail())) {
        editedUser.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_EMAIL),
                OperationType.EDIT,
                serverUser.getEmail()));
      }
      if (!serverUser.getPassword().equals(editedUser.getPassword())) {
        editedUser.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD),
                OperationType.EDIT,
                serverUser.getPassword()));
      }
      return Statuses.SAVED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }


  private ModificationRecord createHistoryRecord(String changedValueName,
                                                 OperationType operationType,
                                                 String changedValue) {
    ModificationRecord record = new ModificationRecord();
    record.setId(getNextId(ModificationRecord.class));
    record.setChangedValueName(changedValueName);
    record.setOperationType(operationType);
    record.setChangedDate(new Date(System.currentTimeMillis()));
    record.setChangedValue(changedValue);
    createModificationRecordInDB(record);
    return record;
  }

  @Override
  public Statuses editUser(@NonNull User editedUser) {
    log.info(LogUtil.startFunc(editedUser));
    var optUser = getUserFromDB(editedUser.getId());
    if (optUser.isEmpty()) {
      return Statuses.NOT_FOUNDED;
    }
    if (!isEditValid(optUser.get(), editedUser)) {
      return Statuses.FORBIDDEN;
    }
    var status = saveUserChanges(optUser.get(), editedUser);
    if (!status.equals(Statuses.SAVED)) {
      return status;
    }
    status = saveUserInDB(editedUser);
    log.info(LogUtil.endFunc(editedUser));
    return status;
  }

  @Override
  public List<User> getFullUsersList() {
    log.info(LogUtil.startFunc());
    var userList = getUserListFromDB();
    log.info(LogUtil.endFunc());
    return userList;
  }

  @Override
  public List<Group> getUsersGroups(long userId) {
    log.info(LogUtil.startFunc(userId));
    var optUser = getUserFromDB(userId);
    if (optUser.isEmpty()) {
      return new ArrayList<>();
    }
    var groupList = getGroupListFromDB(optUser.get());
    log.info(LogUtil.endFunc());
    return groupList;
  }

  @Override
  public String getUserInfo(long userId) {
    try {
      log.info(LogUtil.startFunc(userId));
      var optUser = getUser(userId);
      if (optUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId));
        return PropertyLoader.getProperty(Constants.MESSAGE_USER_NOT_FOUNDED);
      }
      var user = optUser.get();
      var groupList = getUsersGroups(userId);
      log.info(LogUtil.endFunc(userId));
      return String.format(PropertyLoader.getProperty(Constants.FORMAT_USER),
              userToString(user),
              taskListToString(user.getTaskList(), 1),
              historyListToString(user.getHistoryList(), 1),
              groupListToString(groupList, 1));
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId));
      return "";
    }
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

  private String taskListToString(List<Task> taskList, int tabNum) {
    StringBuilder builder = new StringBuilder();
    taskList.forEach(task -> {
      switch (task.getTaskType()) {
        case BASIC:
          try {
            builder.append(basicTaskToString(task, tabNum));
            break;
          } catch (IOException e) {
            log.error(e);
        }
        case EXTENDED:
          try {
            builder.append(extendedTaskToString((ExtendedTask) task, tabNum));
            break;
          } catch (IOException e) {
            log.error(e);
        }
      }
    });
    return builder.toString();
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
        case BASIC:
          try {
            builder.append(String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_TASK_TO_STRING),
                    tabsBuilder.toString(),
                    taskState,
                    basicTaskToString(task, tabNum + 2)));
          } catch (IOException e) {
            log.error(e);
          }
          break;
        case EXTENDED:
          try {
            builder.append(String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_TASK_TO_STRING),
                    tabsBuilder.toString(),
                    taskState,
                    extendedTaskToString((ExtendedTask) task, tabNum + 2)));
          } catch (IOException e) {
            log.error(e);
          }
          break;
      }
    });
    return builder.toString();
  }

  //end user zone

  //start group zone

  @Override
  public List<Group> getFullGroupList() {
    log.info(LogUtil.startFunc());
    var groupListFromDB = getGroupListFromDB();
    log.info(LogUtil.endFunc());
    return groupListFromDB;
  }

  @Override
  public List<Group> searchGroupByName(@NonNull String name) {
    log.info(LogUtil.startFunc(name));
    var groupListFromDB = getGroupListFromDB(name);
    log.info(LogUtil.endFunc(name));
    return groupListFromDB;
  }

  @Override
  public Optional<Group> getGroup(long groupId) {
    log.info(LogUtil.startFunc(groupId));
    var groupFromDB = getGroupFromDB(groupId);
    log.info(LogUtil.endFunc(groupId));
    return groupFromDB;
  }

  @Override
  public Statuses createGroup(@NonNull String groupName, long creatorId, @NonNull GroupTypes groupType) {
    log.info(LogUtil.startFunc(groupName, creatorId, groupType));
    var user = getUserFromDB(creatorId);
    if (user.isEmpty()) {
      return Statuses.FORBIDDEN;
    }
    Group group;
    switch (groupType) {
      case PASSWORDED:
        group = new PasswordedGroup();
        break;
      case PUBLIC:
      case WITH_CONFIRMATION:
        group = new Group();
        break;
      default:
        return Statuses.FAILED;
    }
    group.setCreated(new Date(System.currentTimeMillis()));
    group.setGroupType(groupType);
    group.setHistoryList(new ArrayList<>());
    group.setId(getNextId(Group.class));
    group.setMemberList(new HashMap<>());
    group.getMemberList().put(user.get(), UserRole.CREATOR);
    group.setTaskList(new HashMap<>());
    group.setName(groupName);

    var ret_val = saveGroupInDB(group);
    log.info(LogUtil.endFunc(groupName, creatorId, groupType));
    return ret_val;
  }

  @Override
  public Statuses deleteGroup(long userId, long groupId) {
    log.info(LogUtil.startFunc(userId, groupId));
    Optional<Group> optionalGroup = getGroupFromDB(groupId);
    if (optionalGroup.isEmpty()) {
      log.info(LogUtil.endFunc(userId, groupId));
      return Statuses.NOT_FOUNDED;
    }
    Group group = optionalGroup.get();
    var optUser = group.getMemberList()
            .keySet()
            .stream()
            .filter(serverGroup -> serverGroup.getId() == userId)
            .findAny();
    if (optUser.isEmpty()) {
      log.info(LogUtil.endFunc(userId, groupId));
      return Statuses.FORBIDDEN;
    }
    if (!group.getMemberList().get(optUser.get()).equals(UserRole.CREATOR)) {
      log.info(LogUtil.endFunc(userId, groupId));
      return Statuses.FORBIDDEN;
    }
    var status = deleteGroupFromDB(groupId);
    log.info(LogUtil.endFunc(userId, groupId));
    return status;
  }

  @Override
  public Statuses addUserToGroup(long userId, long groupId) {
    try {
      var optionalUser = getUserFromDB(userId);
      var optionalGroup = getGroupFromDB(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();

      if (group.getMemberList().containsKey(user)) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.FORBIDDEN;
      }
      switch (group.getGroupType()) {
        case PUBLIC:
        case PASSWORDED:
          group.getMemberList().put(user, UserRole.MEMBER);
          group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
                  OperationType.ADD,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                          user.getId(),
                          UserRole.MEMBER.toString())));
          break;
        case WITH_CONFIRMATION:
          group.getMemberList().put(user, UserRole.REQUIRES_CONFIRMATION);
          group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
                  OperationType.ADD,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                          user.getId(),
                          UserRole.REQUIRES_CONFIRMATION.toString())));
          break;
        default:
          log.info(LogUtil.endFunc(userId, groupId));
          return Statuses.FAILED;
      }
      var status = saveGroupInDB(group);
      if (status.equals(Statuses.UPDATED)) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.INSERTED;
      }
      return status;
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, groupId));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses editGroup(long userId, @NonNull Group editedGroup) {
    try {
      log.info(LogUtil.startFunc(userId, editedGroup));
      var optionalUser = getUserFromDB(userId);
      var optionalGroup = getGroupFromDB(editedGroup.getId());
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId, editedGroup));
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var role = optionalGroup.get().getMemberList().get(optionalUser.get());
      if (role == null) {
        log.info(LogUtil.endFunc(userId, editedGroup));
        return Statuses.FORBIDDEN;
      }
      switch (role) {
        case CREATOR:
        case ADMINISTRATOR:
          if (!(group.getHistoryList().equals(editedGroup.getHistoryList())
                  && group.getTaskList().equals(editedGroup.getTaskList())
                  && group.getMemberList().equals(editedGroup.getMemberList())
                  && group.getGroupType().equals(editedGroup.getGroupType())
                  && group.getCreated().equals(editedGroup.getCreated()))) {
            log.info(LogUtil.endFunc(userId, editedGroup));
            return Statuses.FORBIDDEN;
          }
          if (!group.getName().equals(editedGroup.getName())) {
            editedGroup.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                    OperationType.EDIT,
                    group.getName()));
          }
          if (group.getGroupType() == GroupTypes.PASSWORDED
                  && ((PasswordedGroup) group).getPassword().equals(((PasswordedGroup) editedGroup).getPassword())) {
            editedGroup.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_PASSWORD),
                    OperationType.EDIT,
                    ((PasswordedGroup) group).getPassword()));
          }
          var status = saveGroupInDB(editedGroup);
          log.info(LogUtil.endFunc(userId, editedGroup));
          return status;
        case MEMBER:
        case REQUIRES_CONFIRMATION:
          log.info(LogUtil.endFunc(userId, editedGroup));
          return Statuses.FORBIDDEN;
        default:
          log.info(LogUtil.endFunc(userId, editedGroup));
          return Statuses.FAILED;
      }
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, editedGroup));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses deleteUserFromGroup(long userId, long groupId) {
    try {
      log.info(LogUtil.startFunc(userId, groupId));
      var optionalUser = getUserFromDB(userId);
      var optionalGroup = getGroupFromDB(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();
      if (!group.getMemberList().containsKey(user) || group.getMemberList().get(user).equals(UserRole.CREATOR)) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.FORBIDDEN;
      }
      group.getMemberList().remove(user);
      group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
              OperationType.DELETE,
              String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING),
                      user.getId(),
                      UserRole.MEMBER.toString())));
      var status = saveGroupInDB(group);
      if (status.equals(Statuses.UPDATED)) {
        log.info(LogUtil.endFunc(userId, groupId));
        return Statuses.DELETED;
      } else {
        log.info(LogUtil.endFunc(userId, groupId));
        return status;
      }
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, groupId));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType) {
    try {
      log.info(LogUtil.startFunc(userId, groupId, groupType));
      var optionalUser = getUserFromDB(userId);
      var optionalGroup = getGroupFromDB(groupId);
      if (optionalGroup.isEmpty() || optionalUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId, groupId, groupType));
        return Statuses.NOT_FOUNDED;
      }
      var user = optionalUser.get();
      var group = optionalGroup.get();
      if (!group.getMemberList().containsKey(user)) {
        log.info(LogUtil.endFunc(userId, groupId, groupType));
        return Statuses.FORBIDDEN;
      }
      if (!group.getMemberList().get(user).equals(UserRole.CREATOR)) {
        log.info(LogUtil.endFunc(userId, groupId, groupType));
        return Statuses.FORBIDDEN;
      }
      if (groupType.equals(group.getGroupType())) {
        log.info(LogUtil.endFunc(userId, groupId, groupType));
        return Statuses.UPDATED;
      }
      group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_GROUP_TYPE),
              OperationType.EDIT,
              group.getGroupType().name()));
      switch (group.getGroupType()) {
        case PUBLIC:
        case WITH_CONFIRMATION:
          if (groupType.equals(GroupTypes.PASSWORDED)) {
            var status = changeGroupTypeToPassworded(group);
            log.info(LogUtil.endFunc(userId, groupId, groupType));
            return status;
          }
          group.setGroupType(groupType);
          var status = saveGroupInDB(group);
          log.info(LogUtil.endFunc(userId, groupId, groupType));
          return status;
        case PASSWORDED:
          var returnStatus = changeGroupTypeFromPassworded(group, groupType);
          log.info(LogUtil.endFunc(userId, groupId, groupType));
          return returnStatus;
        default:
          log.info(LogUtil.endFunc(userId, groupId, groupType));
          return Statuses.FAILED;
      }
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, groupId, groupType));
      return Statuses.FAILED;
    }
  }

  private Statuses changeGroupTypeToPassworded(Group group) {
    PasswordedGroup passwordedGroup = new PasswordedGroup();
    passwordedGroup.setId(group.getId());
    passwordedGroup.setCreated(group.getCreated());
    passwordedGroup.setGroupType(GroupTypes.PASSWORDED);
    passwordedGroup.setHistoryList(group.getHistoryList());
    passwordedGroup.setMemberList(group.getMemberList());
    passwordedGroup.setName(group.getName());
    passwordedGroup.setTaskList(group.getTaskList());
    var status = deleteGroupFromDB(group.getId());
    if (!status.equals(Statuses.DELETED)) {
      return status;
    }
    status = saveGroupInDB(passwordedGroup);
    if (!status.equals(Statuses.INSERTED)) {
      return status;
    }
    return Statuses.UPDATED;
  }

  private Statuses changeGroupTypeFromPassworded(Group group, GroupTypes groupType) {
    Group basicGroup = new Group();
    basicGroup.setGroupType(groupType);
    basicGroup.setTaskList(group.getTaskList());
    basicGroup.setHistoryList(group.getHistoryList());
    basicGroup.setMemberList(group.getMemberList());
    basicGroup.setName(group.getName());
    basicGroup.setCreated(group.getCreated());
    basicGroup.setId(group.getId());
    var status = deleteGroupFromDB(group.getId());
    if (!status.equals(Statuses.DELETED)) {
      return status;
    }
    status = saveGroupInDB(basicGroup);
    if (!status.equals(Statuses.INSERTED)) {
      return status;
    }
    return Statuses.UPDATED;
  }

  @Override
  public String getGroupsStatistic() {
    try {
      log.info(LogUtil.startFunc());
      var avgGroupSize = getAverageGroupSize();
      if (avgGroupSize.isEmpty()) {
        log.info(LogUtil.endFunc());
        return "";
      }
      var ret = String.format(PropertyLoader.getProperty(Constants.FORMAT_GROUP_STATISTIC),
              getFullGroupList().size(),
              mapToString(getGroupCountPerType(), 1),
              avgGroupSize.get(),
              mapToString(getAverageGroupSizeDividedByGroupType(), 1));
      log.info(LogUtil.endFunc());
      return ret;
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc());
      return "";
    }
  }

  private <K, V> String mapToString(Map<K, V> kvMap, int tabNum) {
    StringBuilder builder = new StringBuilder();
    kvMap.forEach((k, v) -> {
      try {
        builder.append(PropertyLoader.getProperty(Constants.TAB).repeat(Math.max(0, tabNum + 1)))
                .append(k)
                .append(PropertyLoader.getProperty(Constants.MAP_DELIMITER))
                .append(v)
                .append(PropertyLoader.getProperty(Constants.NEW_LINE));
      } catch (IOException e) {
        log.error(e);
      }
    });
    return builder.toString();
  }

  @Override
  public Map<GroupTypes, Double> getAverageGroupSizeDividedByGroupType() {
    log.info(LogUtil.startFunc());
    var groupList = getGroupListFromDB();
    Map<GroupTypes, Double> groupSizes = new HashMap<>();

    groupList.forEach(group -> {
      if (!groupSizes.containsKey(group.getGroupType())) {
        groupSizes.put(group.getGroupType(), 0.0);
      }
      groupSizes.replace(group.getGroupType(), groupSizes.get(group.getGroupType()) + group.getMemberList().size());
    });
    var groupCount = getGroupCountPerType();
    groupSizes.forEach((groupType, size) -> groupSizes.replace(groupType, size / groupCount.get(groupType)));
    log.info(LogUtil.endFunc());
    return groupSizes;
  }

  @Override
  public Map<GroupTypes, Long> getGroupCountPerType() {
    log.info(LogUtil.startFunc());
    var groupList = getGroupListFromDB();
    Map<GroupTypes, Long> groupCount = new HashMap<>();

    groupList.forEach(group -> {
      if (!groupCount.containsKey(group.getGroupType())) {
        groupCount.put(group.getGroupType(), 0L);
      }
      groupCount.replace(group.getGroupType(), groupCount.get(group.getGroupType()) + 1);
    });
    log.info(LogUtil.endFunc());
    return groupCount;
  }

  @Override
  public Optional<Double> getAverageTaskPerGroup() {
    log.info(LogUtil.startFunc());
    var groupList = getGroupListFromDB();
    long groupCount = groupList.stream().mapToLong(group -> group.getTaskList().size()).sum();
    log.info(LogUtil.endFunc());
    return Optional.of(((double) groupCount) / groupList.size());
  }

  @Override
  public Optional<Double> getAverageGroupSize() {
    log.info(LogUtil.startFunc());
    var groupList = getGroupListFromDB();
    long groupSizes = 0;
    for (Group group : groupList) {
      groupSizes += group.getMemberList().size();
    }
    log.info(LogUtil.endFunc());
    return Optional.of(((double) groupSizes) / groupList.size());
  }

  @Override
  public Statuses setUserRole(long administratorId, long groupId, long userIdToSet, @NonNull UserRole role) {
    log.info(LogUtil.startFunc(administratorId, groupId, userIdToSet, role));
    Optional<Group> optionalGroup = getGroupFromDB(groupId);
    Optional<User> optionalAdministrator = getUserFromDB(administratorId);
    Optional<User> optionalUser = getUserFromDB(userIdToSet);
    if (optionalGroup.isEmpty() || optionalAdministrator.isEmpty() || optionalUser.isEmpty()) {
      log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();
    var administrator = optionalAdministrator.get();
    var user = optionalUser.get();
    if (!group.getMemberList().containsKey(user) || !group.getMemberList().containsKey(administrator)) {
      log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
      return Statuses.FORBIDDEN;
    }
    var userRole = group.getMemberList().get(user);
    var administratorRole = group.getMemberList().get(administrator);

    if (userRole.equals(UserRole.CREATOR)) {
      log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
      return Statuses.FORBIDDEN;
    }
    switch (role) {
      case CREATOR:
        log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
        return Statuses.FORBIDDEN;
      case MEMBER:
        switch (userRole) {
          case REQUIRES_CONFIRMATION:
            if (!administratorRole.equals(UserRole.ADMINISTRATOR) && !administratorRole.equals(UserRole.CREATOR)) {
              log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
              return Statuses.FORBIDDEN;
            }
            var status = setRole(group, user, role);
            log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
            return status;
          case ADMINISTRATOR:
            if (!administratorRole.equals(UserRole.CREATOR)) {
              log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
              return Statuses.FORBIDDEN;
            }
            status = setRole(group, user, role);
            log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
            return status;
          default:
            log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
            return Statuses.FORBIDDEN;
        }
      case ADMINISTRATOR:
        if (!administratorRole.equals(UserRole.CREATOR)) {
          log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
          return Statuses.FORBIDDEN;
        }
        var status = setRole(group, user, role);
        log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
        return status;
      default:
        log.info(LogUtil.endFunc(administratorId, groupId, userIdToSet, role));
        return Statuses.FAILED;
    }
  }

  private Statuses setRole(Group group, User user, UserRole role) {
    try {
      group.getMemberList().replace(user, role);
      group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_MEMBER),
              OperationType.EDIT,
              String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING), user.getId(), role.name())));
      return saveGroupInDB(group);
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state) {
    try {
      log.info(LogUtil.startFunc(taskId, groupId, state));
      Optional<Group> optionalGroup = getGroupFromDB(groupId);
      Optional<User> optionalAdministrator = getUserFromDB(userId);
      Optional<Task> optionalTask = getTaskFromDB(taskId);

      if (optionalGroup.isEmpty() || optionalAdministrator.isEmpty() || optionalTask.isEmpty()) {
        log.info(LogUtil.endFunc(taskId, groupId, state));
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var administrator = optionalAdministrator.get();
      var task = optionalTask.get();

      if (!group.getTaskList().containsKey(task) || !group.getMemberList().containsKey(administrator)) {
        log.info(LogUtil.endFunc(taskId, groupId, state));
        return Statuses.FORBIDDEN;
      }
      var administratorRole = group.getMemberList().get(administrator);

      switch (administratorRole) {
        case ADMINISTRATOR:
        case CREATOR:
          group.getTaskList().replace(task, state);
          group.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                  OperationType.EDIT,
                  String.format(PropertyLoader.getProperty(Constants.MAP_FORMAT_STRING), taskId, state)));
          var status = saveGroupInDB(group);
          log.info(LogUtil.endFunc(taskId, groupId, state));
          return status;
        default:
          log.info(LogUtil.endFunc(taskId, groupId, state));
          return Statuses.FORBIDDEN;
      }
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(taskId, groupId, state));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses suggestTask(long userId, long groupId, long taskId) {
    log.info(LogUtil.startFunc(taskId, groupId, taskId));
    var optionalGroup = getGroupFromDB(groupId);
    if (optionalGroup.isEmpty()) {
      log.info(LogUtil.endFunc(taskId, groupId, taskId));
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();
    var optionalUser = group.getMemberList()
            .keySet()
            .stream()
            .filter(groupUser -> groupUser.getId() == userId)
            .findAny();
    if (optionalUser.isEmpty()) {
      log.info(LogUtil.endFunc(taskId, groupId, taskId));
      return Statuses.FORBIDDEN;
    }
    User user = optionalUser.get();
    var optionalTask = user.getTaskList().stream()
            .filter(task -> task.getId() == taskId)
            .findAny();
    if (optionalTask.isEmpty()) {
      log.info(LogUtil.endFunc(taskId, groupId, taskId));
      return Statuses.FORBIDDEN;
    }
    switch (group.getGroupType()) {
      case PUBLIC:
        log.info(LogUtil.endFunc(taskId, groupId, taskId));
        return Statuses.FORBIDDEN;
      case PASSWORDED:
      case WITH_CONFIRMATION:
        var status = suggestTask(group, user, optionalTask.get());
        log.info(LogUtil.endFunc(taskId, groupId, taskId));
        return status;
      default:
        log.info(LogUtil.endFunc(taskId, groupId, taskId));
        return Statuses.FAILED;
    }
  }

  private Statuses suggestTask(Group group, User user, Task task) {
    var userRole = group.getMemberList().get(user);
    TaskState taskState;
    switch (userRole) {
      case MEMBER:
        taskState = TaskState.SUGGESTED;
        break;
      case CREATOR:
      case ADMINISTRATOR:
        taskState = TaskState.APPROVED;
        break;
      default:
        return Statuses.FORBIDDEN;
    }
    user.getTaskList().remove(task);
    group.getTaskList().put(task, taskState);
    var status = saveGroupInDB(group);
    if (!status.equals(Statuses.UPDATED)) {
      return status;
    }
    status = saveUserInDB(user);
    if (!status.equals(Statuses.UPDATED)) {
      return status;
    }
    return Statuses.SUGGESTED;
  }

  //end group zone

  //start task zone

  private void setBasicTask(Task task, String taskName, TaskStatuses status) {
    task.setId(getNextId(Task.class));
    task.setName(taskName);
    task.setCreated(new Date(System.currentTimeMillis()));
    task.setHistoryList(new ArrayList<>());
    task.setStatus(status);
  }

  private Statuses saveUserWithTask(long userId, Task task) {
    try {
      Optional<User> optionalUser = getUserFromDB(userId);
      if (optionalUser.isEmpty()) {
        return Statuses.FORBIDDEN;
      }
      User user = optionalUser.get();
      user.getTaskList().add(task);
      user.getHistoryList()
              .add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                      OperationType.ADD,
                      String.valueOf(task.getId())));
      var insertTaskStatus = saveTaskInDB(task);
      if (insertTaskStatus != Statuses.INSERTED) {
        return insertTaskStatus;
      }
      var updateUserStatus = saveUserInDB(user);
      if (updateUserStatus != Statuses.UPDATED) {
        return updateUserStatus;
      }
      return Statuses.INSERTED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  private Statuses saveGroupWithTask(Group group, Task task) {
    try {
      group.getHistoryList()
              .add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
                      OperationType.ADD,
                      String.valueOf(task.getId())));
      var insertTaskStatus = saveTaskInDB(task);
      if (insertTaskStatus != Statuses.INSERTED) {
        return insertTaskStatus;
      }
      var updateGroupStatus = saveGroupInDB(group);
      if (updateGroupStatus != Statuses.UPDATED) {
        return updateGroupStatus;
      }
      return Statuses.INSERTED;
    } catch (IOException e) {
      log.error(e);
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses createTask(long userId,
                             @NonNull String taskName,
                             @NonNull TaskStatuses status) {
    log.info(LogUtil.startFunc(userId, taskName, status));
    Task task = new Task();
    setBasicTask(task, taskName, status);
    task.setTaskType(TaskTypes.BASIC);
    var retStatus = saveUserWithTask(userId, task);
    log.info(LogUtil.endFunc(userId, taskName, status));
    return retStatus;
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
    log.info(LogUtil.startFunc(userId, taskName, status, repetitionType, remindType, importance, description, time));
    ExtendedTask task = new ExtendedTask();
    setBasicTask(task, taskName, status);
    task.setTaskType(TaskTypes.EXTENDED);
    task.setRepetitionType(repetitionType);
    task.setRemindType(remindType);
    task.setImportance(importance);
    task.setDescription(description);
    task.setTime(time);
    var retStatus = saveUserWithTask(userId, task);
    log.info(LogUtil.endFunc(userId, taskName, status, repetitionType, remindType, importance, description, time));
    return retStatus;
  }

  @Override
  public Statuses createTask(long userId, long groupId, @NonNull String taskName, @NonNull TaskStatuses taskStatus) {
    log.info(LogUtil.startFunc(userId, groupId, taskName, taskStatus));
    var optionalGroup = getGroupFromDB(groupId);
    if (optionalGroup.isEmpty()) {
      log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();
    var optionalUser = group
            .getMemberList()
            .keySet()
            .stream()
            .filter(user -> user.getId() == userId)
            .findAny();
    if (optionalUser.isEmpty()) {
      log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
      return Statuses.FORBIDDEN;
    }
    var role = optionalGroup.get().getMemberList().get(optionalUser.get());
    if (role == null) {
      log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
      return Statuses.FORBIDDEN;
    }
    switch (role) {
      case CREATOR:
      case ADMINISTRATOR:
        Task task = new Task();
        setBasicTask(task, taskName, taskStatus);
        task.setTaskType(TaskTypes.BASIC);
        group.getTaskList().put(task, TaskState.APPROVED);
        var retStatus = saveGroupWithTask(group, task);
        log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
        return retStatus;
      case MEMBER:
      case REQUIRES_CONFIRMATION:
        log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
        return Statuses.FORBIDDEN;
      default:
        log.info(LogUtil.endFunc(userId, groupId, taskName, taskStatus));
        return Statuses.FAILED;
    }
  }

  @Override
  public Statuses createTask(long userId,
                             long groupId,
                             @NonNull String taskName,
                             @NonNull TaskStatuses taskStatus,
                             @NonNull RepetitionTypes repetitionType,
                             @NonNull RemindTypes remindType,
                             @NonNull Importances importance,
                             @NonNull String description,
                             @NonNull Date time) {
    log.info(LogUtil.startFunc(
            userId,
            groupId,
            taskName,
            taskStatus,
            remindType,
            remindType,
            importance,
            description,
            time));
    var optionalGroup = getGroupFromDB(groupId);
    if (optionalGroup.isEmpty()) {
      log.info(LogUtil.endFunc(
              userId,
              groupId,
              taskName,
              taskStatus,
              remindType,
              remindType,
              importance,
              description,
              time));
      return Statuses.NOT_FOUNDED;
    }
    var group = optionalGroup.get();
    var optionalUser = group
            .getMemberList()
            .keySet()
            .stream()
            .filter(user -> user.getId() == userId)
            .findAny();
    if (optionalUser.isEmpty()) {
      log.info(LogUtil.endFunc(
              userId,
              groupId,
              taskName,
              taskStatus,
              remindType,
              remindType,
              importance,
              description,
              time));
      return Statuses.FORBIDDEN;
    }
    var role = optionalGroup.get().getMemberList().get(optionalUser.get());
    if (role == null) {
      log.info(LogUtil.endFunc(
              userId,
              groupId,
              taskName,
              taskStatus,
              remindType,
              remindType,
              importance,
              description,
              time));
      return Statuses.FORBIDDEN;
    }
    switch (role) {
      case CREATOR:
      case ADMINISTRATOR:
        ExtendedTask task = new ExtendedTask();
        setBasicTask(task, taskName, taskStatus);
        task.setTaskType(TaskTypes.BASIC);
        task.setRepetitionType(repetitionType);
        task.setRemindType(remindType);
        task.setImportance(importance);
        task.setDescription(description);
        task.setTime(time);
        group.getTaskList().put(task, TaskState.APPROVED);
        var retStatus = saveGroupWithTask(group, task);
        log.info(LogUtil.endFunc(
                userId,
                groupId,
                taskName,
                taskStatus,
                remindType,
                remindType,
                importance,
                description,
                time));
        return retStatus;
      case MEMBER:
      case REQUIRES_CONFIRMATION:
        log.info(LogUtil.endFunc(
                userId,
                groupId,
                taskName,
                taskStatus,
                remindType,
                remindType,
                importance,
                description,
                time));
        return Statuses.FORBIDDEN;
      default:
        log.info(LogUtil.endFunc(
                userId,
                groupId,
                taskName,
                taskStatus,
                remindType,
                remindType,
                importance,
                description,
                time));
        return Statuses.FAILED;
    }
  }

  @Override
  public List<Task> getFullTaskList() {
    log.info(LogUtil.startFunc());
    var taskListFromDB = getTaskListFromDB();
    log.info(LogUtil.endFunc());
    return taskListFromDB;
  }

  @Override
  public Statuses deleteTask(long userId, long taskId) {
    try {
      log.info(LogUtil.startFunc(userId, taskId));
      var optionalUser = getUserFromDB(userId);
      if (optionalUser.isEmpty()) {
        log.info(LogUtil.endFunc(userId, taskId));
        return Statuses.NOT_FOUNDED;
      }
      User user = optionalUser.get();
      Optional<Task> optionalTask = user
              .getTaskList()
              .stream()
              .filter(task -> task.getId() == taskId)
              .findAny();
      if (optionalTask.isEmpty()) {
        log.info(LogUtil.endFunc(userId, taskId));
        return Statuses.FORBIDDEN;
      }
      Task task = optionalTask.get();
      user.getTaskList().remove(task);
      var status = deleteTaskFromDB(taskId);
      if (!status.equals(Statuses.DELETED)) {
        log.info(LogUtil.endFunc(userId, taskId));
        return status;
      }
      user.getHistoryList().add(createHistoryRecord(PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
              OperationType.DELETE,
              String.valueOf(taskId)));
      var updateUserStatus = saveUserInDB(user);
      if (updateUserStatus != Statuses.UPDATED) {
        log.info(LogUtil.endFunc(userId, taskId));
        return updateUserStatus;
      }
      log.info(LogUtil.endFunc(userId, taskId));
      return Statuses.DELETED;
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, taskId));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses deleteTask(long userId, long groupId, long taskId) {
    try {
      log.info(LogUtil.startFunc(userId, groupId, taskId));
      var optionalGroup = getGroup(groupId);
      if (optionalGroup.isEmpty()) {
        log.info(LogUtil.endFunc(userId, groupId, taskId));
        return Statuses.NOT_FOUNDED;
      }
      var group = optionalGroup.get();
      var optionalUser = group.
              getMemberList()
              .keySet()
              .stream()
              .filter(user -> user.getId() == userId)
              .findAny();
      var optionalTask = group
              .getTaskList()
              .keySet()
              .stream()
              .filter(task -> task.getId() == taskId)
              .findAny();
      if (optionalUser.isEmpty() ||
              optionalTask.isEmpty() ||
              (!group.getMemberList().get(optionalUser.get()).equals(UserRole.CREATOR) &&
                      !group.getMemberList().get(optionalUser.get()).equals(UserRole.ADMINISTRATOR))) {
        log.info(LogUtil.endFunc(userId, groupId, taskId));
        return Statuses.FORBIDDEN;
      }
      var task = optionalTask.get();
      group.getTaskList().remove(task);
      group.getHistoryList().add(createHistoryRecord(
              PropertyLoader.getProperty(Constants.FIELD_NAME_TASK),
              OperationType.DELETE,
              String.valueOf(task.getId())));

      var status = saveGroupInDB(group);
      if (status.equals(Statuses.UPDATED)) {
        status = deleteTaskFromDB(task.getId());
      }
      log.info(LogUtil.endFunc(userId, groupId, taskId));
      return status;
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc(userId, groupId, taskId));
      return Statuses.FAILED;
    }
  }

  @Override
  public Statuses editTask(long userId, @NonNull Task editedTask) {
    log.info(LogUtil.startFunc(userId, editedTask));
    var optionalUser = getUserFromDB(userId);
    if (optionalUser.isEmpty()) {
      log.info(LogUtil.endFunc(userId, editedTask));
      return Statuses.FORBIDDEN;
    }
    User user = optionalUser.get();
    var optionalUserTask = user.getTaskList().stream()
            .filter(task -> task.getId() == editedTask.getId())
            .findAny();
    if (optionalUserTask.isEmpty()) {
      log.info(LogUtil.endFunc(userId, editedTask));
      return Statuses.FORBIDDEN;
    }
    if (!isEditValid(editedTask, optionalUserTask.get())) {
      log.info(LogUtil.endFunc(userId, editedTask));
      return Statuses.FORBIDDEN;
    }
    switch (optionalUserTask.get().getTaskType()) {
      case BASIC:
        Task basicUserTask = optionalUserTask.get();
        saveChangesHistory(basicUserTask, editedTask);
        var status = saveTaskInDB(basicUserTask);
        log.info(LogUtil.endFunc(userId, editedTask));
        return status;
      case EXTENDED:
        ExtendedTask extendedUserTask = (ExtendedTask) optionalUserTask.get();
        saveChangesHistory(extendedUserTask, (ExtendedTask) editedTask);
        log.info(LogUtil.endFunc(userId, editedTask));
        return Statuses.UPDATED;
      default:
        log.info(LogUtil.endFunc(userId, editedTask));
        return Statuses.FAILED;
    }
  }

  private void saveChangesHistory(ExtendedTask task, ExtendedTask editedTask) {
    try {
      saveChangesHistory((Task) task, editedTask);
      if (!task.getDescription().equals(editedTask.getDescription())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_DESCRIPTION),
                OperationType.EDIT,
                task.getDescription()));
      }
      if (!task.getImportance().equals(editedTask.getImportance())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_IMPORTANCE),
                OperationType.EDIT,
                task.getImportance().name()));
      }
      if (!task.getRemindType().equals(editedTask.getRemindType())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_REMIND_TYPE),
                OperationType.EDIT,
                task.getRemindType().name()));
      }
      if (!task.getRepetitionType().equals(editedTask.getRepetitionType())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_REPETITION_TYPE),
                OperationType.EDIT,
                task.getRepetitionType().name()));
      }
      if (!task.getTime().equals(editedTask.getTime())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_TIME),
                OperationType.EDIT,
                task.getTime().toString()));
      }
    } catch (IOException e) {
      log.error(e);
    }
  }

  private void saveChangesHistory(Task task, Task editedTask) {
    try {
      if (!task.getName().equals(editedTask.getName())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_NAME),
                OperationType.EDIT,
                task.getName()));
      }
      if (!task.getStatus().equals(editedTask.getStatus())) {
        editedTask.getHistoryList().add(createHistoryRecord(
                PropertyLoader.getProperty(Constants.FIELD_NAME_STATUS),
                OperationType.EDIT,
                task.getStatus().name()));
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
  public String getTaskStatistic() {
    try {
      log.info(LogUtil.startFunc());
      var str = String.format(PropertyLoader.getProperty(Constants.FORMAT_TASK_STATISTIC),
              getFullTaskList().size(),
              mapToString(getTaskCountPerType(), 1),
              mapToString(getTaskCountPerOwner(), 1),
              mapToString(getAverageTaskPerOwner(), 1));
      log.info(LogUtil.endFunc());
      return str;
    } catch (IOException e) {
      log.error(e);
      log.info(LogUtil.endFunc());
      return "";
    }
  }

  @Override
  public Map<Owner, Long> getTaskCountPerOwner() {
    log.info(LogUtil.startFunc());
    List<User> userList = getFullUsersList();
    var groupList = getFullGroupList();
    Map<Owner, Long> taskCount = new HashMap<>();

    Arrays.stream(Owner.values()).forEach(owner -> taskCount.put(owner, 0L));
    taskCount.replace(Owner.USER,
            (long) userList.stream().flatMapToInt(user -> IntStream.of(user.getTaskList().size())).sum());
    groupList.forEach(group -> {
      switch (group.getGroupType()) {
        case PUBLIC:
          taskCount.replace(Owner.PUBLIC_GROUP,
                  taskCount.get(Owner.PUBLIC_GROUP) + group.getTaskList().size());
          break;
        case PASSWORDED:
          taskCount.replace(Owner.PASSWORDED_GROUP,
                  taskCount.get(Owner.PASSWORDED_GROUP) + group.getTaskList().size());
          break;
        case WITH_CONFIRMATION:
          taskCount.replace(Owner.GROUP_WITH_CONFIRMATION,
                  taskCount.get(Owner.GROUP_WITH_CONFIRMATION) + group.getTaskList().size());
      }
    });
    log.info(LogUtil.endFunc());
    return taskCount;
  }

  @Override
  public Map<Owner, Double> getAverageTaskPerOwner() {
    log.info(LogUtil.startFunc());
    var taskCount = getTaskCountPerOwner();
    Map<Owner, Double> averageTask = new HashMap<>();
    averageTask.put(Owner.USER, ((double) taskCount.get(Owner.USER)) / getFullUsersList().size());
    averageTask.put(Owner.PUBLIC_GROUP,
            ((double) taskCount.get(Owner.PUBLIC_GROUP)) / getFullGroupList().stream()
                    .filter(group -> group.getGroupType().equals(GroupTypes.PUBLIC))
                    .count());
    averageTask.put(Owner.GROUP_WITH_CONFIRMATION,
            ((double) taskCount.get(Owner.GROUP_WITH_CONFIRMATION)) / getFullGroupList().stream()
                    .filter(group -> group.getGroupType().equals(GroupTypes.WITH_CONFIRMATION))
                    .count());
    averageTask.put(Owner.PASSWORDED_GROUP,
            ((double) taskCount.get(Owner.PASSWORDED_GROUP)) / getFullGroupList().stream()
                    .filter(group -> group.getGroupType().equals(GroupTypes.PASSWORDED))
                    .count());
    log.info(LogUtil.endFunc());
    return averageTask;
  }

  @Override
  public Map<TaskTypes, Long> getTaskCountPerType() {
    log.info(LogUtil.startFunc());
    Map<TaskTypes, Long> taskCount = new HashMap<>();
    var taskList = getFullTaskList();
    Arrays.stream(TaskTypes.values()).forEach(taskType -> taskCount.put(taskType, 0L));
    taskList.forEach(task -> taskCount.replace(task.getTaskType(), taskCount.get(task.getTaskType()) + 1));
    log.info(LogUtil.endFunc());
    return taskCount;
  }

  //end task zone
}
