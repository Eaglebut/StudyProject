package ru.sfedu.studyProject.DataProviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.*;

@Log4j2
abstract class AbstractDataProviderTest {

  private static final List<User> userList = new ArrayList<>();
  private static final Random random = new Random();
  static DataProvider dataProvider;
  private static User user;
  private static int num = 0;
  long userId;

  static void setUp(DataProvider dataProvider) {
    for (int k = 0; k < 1; k++) {
      String userEmail = new Date(System.currentTimeMillis()).toString() + "@" + random.nextInt();
      Assertions.assertEquals(Statuses.INSERTED, dataProvider.createUser(userEmail,
              "test",
              "Test" + num,
              "Surname",
              SignUpTypes.SIMPLE));

      var optUser = dataProvider.getUser(userEmail, "test");
      Assertions.assertTrue(optUser.isPresent());
      user = optUser.get();

      for (int i = 0; i < 30; i++) {
        userEmail = new Date(System.currentTimeMillis()).toString() + "@" + random.nextInt();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createUser(userEmail,
                "test",
                "Test" + num,
                "Surname",
                SignUpTypes.SIMPLE));

        optUser = dataProvider.getUser(userEmail, "test");
        Assertions.assertTrue(optUser.isPresent());
        userList.add(optUser.get());
      }

      for (int i = 0; i < 10; i++) {
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createTask(user.getId(), "test", TaskStatuses.TEST_TASK_STATUS));
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createTask(user.getId(),
                        "test",
                        TaskStatuses.TEST_TASK_STATUS,
                        RepetitionTypes.DONT_REPEAT,
                        RemindTypes.DONT_REMIND,
                        Importances.ORDINAL,
                        "test description",
                        new Date(System.currentTimeMillis() + random.nextLong())));
      }

      for (int i = 0; i < 3; i++) {
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createGroup("test group " + num + " public",
                        user.getId(),
                        GroupTypes.PUBLIC));
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createGroup("test group " + num + " passworded",
                        user.getId(),
                        GroupTypes.PASSWORDED));
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createGroup("test group " + num + " with confirmation",
                        user.getId(),
                        GroupTypes.WITH_CONFIRMATION));
      }
      List<Group> groupList = dataProvider.getUsersGroups(user.getId());

      groupList.forEach(group -> {
        for (int i = 0; i < random.nextInt() % 5 + 2; i++) {
          Assertions.assertEquals(Statuses.INSERTED,
                  dataProvider.createTask(user.getId(),
                          group.getId(),
                          "test " + random.nextInt(),
                          TaskStatuses.TEST_TASK_STATUS));
        }
        for (int i = 0; i < random.nextInt() % 5 + 2; i++) {
          Assertions.assertEquals(Statuses.INSERTED,
                  dataProvider.createTask(user.getId(),
                          group.getId(),
                          "test " + random.nextInt(),
                          TaskStatuses.TEST_TASK_STATUS,
                          RepetitionTypes.DONT_REPEAT,
                          RemindTypes.DONT_REMIND,
                          Importances.ORDINAL,
                          "test description",
                          new Date(System.currentTimeMillis() + random.nextLong())));
        }
        userList.forEach(user -> {
          if (random.nextInt() % 3 == 0) {
            Assertions.assertEquals(Statuses.INSERTED, dataProvider.addUserToGroup(user.getId(), group.getId()));
          }
        });
      });


      optUser = dataProvider.getUser(userEmail, "test");
      Assertions.assertTrue(optUser.isPresent());
      user = optUser.get();

      num++;
    }
  }

  void setUser() {
    var optUser = dataProvider.getUser(userId);
    Assertions.assertTrue(optUser.isPresent());
    user = optUser.get();
  }

  void createUserCorrect() {
    Assertions.assertEquals(Statuses.INSERTED,
            dataProvider.createUser(user.getEmail() + new Date(System.currentTimeMillis()).toString(),
                    user.getPassword(),
                    user.getName(),
                    user.getSurname(),
                    user.getSignUpType()));
    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createUser(new Date(System.currentTimeMillis()).toString(),
            user.getPassword(),
            user.getName(),
            user.getSurname(),
            user.getSignUpType()));
  }

  void createUserIncorrect() {
    Assertions.assertThrows(NullPointerException.class, () -> dataProvider.createUser(null,
            user.getPassword(),
            user.getName(),
            user.getSurname(),
            user.getSignUpType()));
    Assertions.assertThrows(NullPointerException.class, () -> dataProvider.createUser(user.getEmail(),
            null,
            user.getName(),
            user.getSurname(),
            user.getSignUpType()));
    Assertions.assertThrows(NullPointerException.class, () -> dataProvider.createUser(user.getEmail(),
            user.getPassword(),
            null,
            user.getSurname(),
            user.getSignUpType()));
    Assertions.assertThrows(NullPointerException.class, () -> dataProvider.createUser(user.getEmail(),
            user.getPassword(),
            user.getName(),
            null,
            user.getSignUpType()));
    Assertions.assertThrows(NullPointerException.class, () -> dataProvider.createUser(user.getEmail(),
            user.getPassword(),
            user.getName(),
            user.getSurname(),
            null));

  }

  void getUserByIdCorrect() {
    Optional<User> optUser = dataProvider.getUser(user.getId());
    Assertions.assertTrue(optUser.isPresent());
    Assertions.assertEquals(user, optUser.get());
  }


  void getUserByIdIncorrect() {
    var user = dataProvider.getUser(134113);
    Assertions.assertFalse(user.isPresent());
  }


  void getUserByEmailAndPasswordCorrect() {
    Optional<User> optUser = dataProvider.getUser(user.getEmail(), user.getPassword());
    Assertions.assertTrue(optUser.isPresent());
    Assertions.assertEquals(user, optUser.get());
  }

  void getUserByEmailAndPasswordIncorrect() throws IOException {
    var user = dataProvider.getUser(PropertyLoader.getProperty(
            Constants.TEST_USER_INCORRECT_EMAIL),
            PropertyLoader.getProperty(Constants.TEST_USER_INCORRECT_PASSWORD)
    );
    Assertions.assertFalse(user.isPresent());
  }

  void editUserCorrect() {
    log.debug(user);
    user.setName(new Date(System.currentTimeMillis()).toString() + "edit");
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.editUser(user));
    var editedUser = dataProvider.getUser(user.getId());
    Assertions.assertTrue(editedUser.isPresent());
    Assertions.assertEquals(user.getName(), editedUser.get().getName());
    log.debug(editedUser);
  }

  void editUserIncorrect() {
    long userId = user.getId();
    log.debug(user);
    user.setId(3123123);
    log.debug(user);
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.editUser(user));

    user.setId(userId);
    var userName = user.getName();
    user.setName(null);
    log.debug(user);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editUser(user));

    user.setName(userName);
    var created = user.getCreated();
    user.setCreated(new Date(System.currentTimeMillis()));
    log.debug(user);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editUser(user));

    user.setCreated(created);
    var taskList = user.getTaskList();
    user.setTaskList(new ArrayList<>());
    log.debug(user);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editUser(user));


    user.setTaskList(taskList);
    user.getHistoryList().add(new ModificationRecord());
    log.debug(user);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editUser(user));
  }

  void createTaskCorrect() {
    Assertions.assertEquals(Statuses.INSERTED,
            dataProvider.createTask(user.getId(),
                    "test c",
                    TaskStatuses.TEST_TASK_STATUS));
    Optional<User> updatedUser = dataProvider.getUser(user.getId());
    Assertions.assertTrue(updatedUser.isPresent());
    Assertions.assertEquals(user.getTaskList().size() + 1,
            updatedUser.get().getTaskList().size());
    Assertions.assertTrue(updatedUser.get().getTaskList().stream()
            .anyMatch(task -> task.getName().equals("test c")));
  }

  void createTaskIncorrect() {
    Assertions.assertEquals(Statuses.FORBIDDEN,
            dataProvider.createTask(35616516,
                    "test",
                    TaskStatuses.TEST_TASK_STATUS));

  }


  void createExtendedTaskCorrect() {
    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(user.getId(),
            "test extended",
            TaskStatuses.TEST_TASK_STATUS,
            RepetitionTypes.DONT_REPEAT,
            RemindTypes.DONT_REMIND,
            Importances.ORDINAL,
            "test description",
            new Date(System.currentTimeMillis())
    ));
    var optUser = dataProvider.getUser(user.getId());
    Assertions.assertTrue(optUser.isPresent());
    var updatedUser = optUser.get();
    Assertions.assertEquals(user.getTaskList().size() + 1,
            updatedUser.getTaskList().size());
    Assertions.assertTrue(updatedUser.getTaskList().stream()
            .anyMatch(task -> task.getName().equals("test extended")));

  }

  void createExtendedTaskIncorrect() {
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.createTask(516516,
            "test extended",
            TaskStatuses.TEST_TASK_STATUS,
            RepetitionTypes.DONT_REPEAT,
            RemindTypes.DONT_REMIND,
            Importances.ORDINAL,
            "test description",
            new Date(System.currentTimeMillis())
    ));
  }

  void deleteTaskCorrect() {
    for (int i = 0; i < 2; i++) {
      var serverUser = user;
      log.debug(serverUser.getTaskList().get(i));
      Assertions.assertEquals(Statuses.DELETED,
              dataProvider.deleteTask(serverUser.getId(), serverUser.getTaskList().get(i).getId()));
      Optional<User> updatedUser = dataProvider.getUser(user.getId());
      Assertions.assertTrue(updatedUser.isPresent());
      Assertions.assertEquals(serverUser.getTaskList().size() - (i + 1),
              updatedUser.get().getTaskList().size());
    }
  }

  void deleteTaskIncorrect() {
    var serverUser = user;
    log.debug(serverUser.getTaskList().get(0));
    Assertions.assertEquals(Statuses.NOT_FOUNDED,
            dataProvider.deleteTask(58465,
                    serverUser.getTaskList().get(0).getId()));
    Assertions.assertEquals(Statuses.FORBIDDEN,
            dataProvider.deleteTask(userId, 46810));
    Assertions.assertEquals(Statuses.NOT_FOUNDED,
            dataProvider.deleteTask(16480, 10468));

  }


  void editTaskCorrect() {
    var task = user.getTaskList().get(0);
    task.setName(new Date(System.currentTimeMillis()).toString() + " edit");
    log.debug(task);
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.editTask(user.getId(), task));

    var extendedTask = user.getTaskList().get(1);
    extendedTask.setName(new Date(System.currentTimeMillis()).toString() + " edit");
    log.debug(task);
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.editTask(user.getId(), extendedTask));
  }

  void editTaskIncorrect() {
    var task = user.getTaskList().get(0);
    log.debug(task);

    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(10, task));

    task = user.getTaskList().get(0);
    task.setName(null);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));

    task = user.getTaskList().get(0);
    task.setCreated(new Date(System.currentTimeMillis()));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));

    task = user.getTaskList().get(0);
    task.setCreated(null);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));

    task = user.getTaskList().get(0);
    task.setId(204214124);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));

    task = user.getTaskList().get(0);
    task.setTaskType(TaskTypes.EXTENDED);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));

    task = user.getTaskList().get(0);
    task.getHistoryList().add(new ModificationRecord());
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.editTask(user.getId(), task));
  }

  void createGroupCorrect() {
    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup("test group",
            user.getId(),
            GroupTypes.PUBLIC));

    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup("test pass",
            user.getId(),
            GroupTypes.PASSWORDED));

    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup("test conf",
            user.getId(),
            GroupTypes.WITH_CONFIRMATION));
  }


  void getFullGroupList() {
    var groupList = dataProvider.getFullGroupList();
    Assertions.assertTrue(groupList.size() > 0);
    log.debug(groupList.get(0));
  }

  void getGroupCorrect() {
    var groupList = dataProvider.getFullGroupList();
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();

    var optionalGroup = dataProvider.getGroup(group.getId());
    Assertions.assertTrue(optionalGroup.isPresent());
    log.debug(optionalGroup.get());
    Assertions.assertEquals(group.getId(), optionalGroup.get().getId());
    Assertions.assertEquals(group.getName(), optionalGroup.get().getName());
    Assertions.assertEquals(group.getGroupType(), optionalGroup.get().getGroupType());
    Assertions.assertEquals(group.getMemberList(), optionalGroup.get().getMemberList());
  }

  void addUserToGroupCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();

    for (User user1 : userList) {
      if (!group.getMemberList().containsKey(user1)) {
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.addUserToGroup(user1.getId(), group.getId()));
        return;
      }
    }
  }

  void removeUserFromGroupCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    userList.forEach(user -> {
      if (group.getMemberList().containsKey(user)) {
        if (!group.getMemberList().get(user).equals(UserRole.CREATOR)) {
          Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteUserFromGroup(user.getId(), group.getId()));
        }
      }
    });
  }

  void changeGroupTypeCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();

    Assertions.assertEquals(Statuses.UPDATED,
            dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.PASSWORDED));
    var serverGroup = dataProvider.getGroup(group.getId());
    Assertions.assertTrue(serverGroup.isPresent());
    Assertions.assertEquals(GroupTypes.PASSWORDED, serverGroup.get().getGroupType());

    Assertions.assertEquals(Statuses.UPDATED,
            dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.WITH_CONFIRMATION));
    serverGroup = dataProvider.getGroup(group.getId());
    Assertions.assertTrue(serverGroup.isPresent());
    Assertions.assertEquals(GroupTypes.WITH_CONFIRMATION, serverGroup.get().getGroupType());

    Assertions.assertEquals(Statuses.UPDATED,
            dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.PUBLIC));
    serverGroup = dataProvider.getGroup(group.getId());
    Assertions.assertTrue(serverGroup.isPresent());
    Assertions.assertEquals(GroupTypes.PUBLIC, serverGroup.get().getGroupType());
  }

  void changeGroupTypeIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();

    Assertions.assertEquals(Statuses.NOT_FOUNDED,
            dataProvider.changeGroupType(10631, group.getId(), GroupTypes.PASSWORDED));
    Assertions.assertEquals(Statuses.NOT_FOUNDED,
            dataProvider.changeGroupType(user.getId(), 16516, GroupTypes.PASSWORDED));
    Assertions.assertEquals(Statuses.FORBIDDEN,
            dataProvider.changeGroupType(3, group.getId(), GroupTypes.PASSWORDED));
  }


  void searchGroupByNameCorrect() {
    Assertions.assertTrue(dataProvider.searchGroupByName("test").size() > 0);
  }

  void suggestTaskToGroupCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream()
            .filter(group -> !group.getGroupType().equals(GroupTypes.PUBLIC))
            .findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    var optTask = user.getTaskList().stream().findAny();
    Assertions.assertTrue(optTask.isPresent());
    Assertions.assertEquals(Statuses.SUGGESTED,
            dataProvider.suggestTask(user.getId(),
                    group.getId(),
                    optTask.get().getId()));
  }

  void suggestTaskToGroupIncorrect() {
    Assertions.assertEquals(Statuses.FORBIDDEN,
            dataProvider.suggestTask(userId,
                    1,
                    123));
  }

  void createBasicGroupTaskCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(user.getId(),
            group.getId(),
            "group basic task test",
            TaskStatuses.TEST_TASK_STATUS));
  }

  void createBasicGroupTaskIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.createTask(1,
            group.getId(),
            "group basic task test",
            TaskStatuses.TEST_TASK_STATUS));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.createTask(112365,
            group.getId(),
            "group basic task test",
            TaskStatuses.TEST_TASK_STATUS));
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.createTask(userId,
            1546,
            "group basic task test",
            TaskStatuses.TEST_TASK_STATUS));
  }

  void createExtendedGroupTaskCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(user.getId(),
            group.getId(),
            "test",
            TaskStatuses.TEST_TASK_STATUS,
            RepetitionTypes.DONT_REPEAT,
            RemindTypes.DONT_REMIND,
            Importances.ORDINAL,
            "test description",
            new Date(System.currentTimeMillis() + random.nextLong())));
  }

  void createExtendedGroupTaskIncorrect() {

    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.createTask(163,
            group.getId(),
            "test",
            TaskStatuses.TEST_TASK_STATUS,
            RepetitionTypes.DONT_REPEAT,
            RemindTypes.DONT_REMIND,
            Importances.ORDINAL,
            "test description",
            new Date(System.currentTimeMillis() + random.nextLong())));

    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.createTask(userId,
            13426,
            "test",
            TaskStatuses.TEST_TASK_STATUS,
            RepetitionTypes.DONT_REPEAT,
            RemindTypes.DONT_REMIND,
            Importances.ORDINAL,
            "test description",
            new Date(System.currentTimeMillis() + random.nextLong())));
  }

  void updateGroupCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    group.setName("updated group");
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.updateGroup(user.getId(), group));
  }

  void updateGroupIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    group.setCreated(null);
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.updateGroup(user.getId(), group));


    groupList = dataProvider.getUsersGroups(user.getId());
    optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    group = optGroup.get();
    group.setCreated(new Date(System.currentTimeMillis()));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.updateGroup(user.getId(), group));

    groupList = dataProvider.getUsersGroups(user.getId());
    optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    group = optGroup.get();
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.updateGroup(45, group));

    groupList = dataProvider.getUsersGroups(user.getId());
    optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    group = optGroup.get();
    group.setId(156);
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.updateGroup(user.getId(), group));
  }

  void setUserRoleCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream()
            .filter(group -> group.getGroupType().equals(GroupTypes.WITH_CONFIRMATION)).findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    var confUser = userList.stream().filter(user1 -> !group.getMemberList().containsKey(user1))
            .findAny();
    Assertions.assertTrue(confUser.isPresent());

    Assertions.assertEquals(Statuses.INSERTED, dataProvider.addUserToGroup(confUser.get().getId(), group.getId()));

    Assertions.assertEquals(Statuses.UPDATED,
            dataProvider.setUserRole(user.getId(),
                    group.getId(),
                    confUser.get().getId(),
                    UserRole.MEMBER));
    Assertions.assertEquals(Statuses.UPDATED,
            dataProvider.setUserRole(user.getId(),
                    group.getId(),
                    confUser.get().getId(),
                    UserRole.ADMINISTRATOR));
  }

  void setUserRoleIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream()
            .filter(group -> group.getGroupType().equals(GroupTypes.WITH_CONFIRMATION)).findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var group = optGroup.get();
    var confUser = userList.stream()
            .filter(user1 ->
                    !group.getMemberList().containsKey(user1))
            .findAny();
    Assertions.assertTrue(confUser.isPresent());

    Assertions.assertEquals(Statuses.INSERTED, dataProvider.addUserToGroup(confUser.get().getId(), group.getId()));

    if (!group.getMemberList().get(user).equals(UserRole.REQUIRES_CONFIRMATION)) {
      Assertions.assertEquals(Statuses.FORBIDDEN,
              dataProvider.setUserRole(confUser.get().getId(),
                      group.getId(),
                      user.getId(),
                      UserRole.REQUIRES_CONFIRMATION));
    }
    Assertions.assertEquals(Statuses.FORBIDDEN,
            dataProvider.setUserRole(user.getId(),
                    group.getId(),
                    confUser.get().getId(),
                    UserRole.CREATOR));
    Assertions.assertEquals(Statuses.NOT_FOUNDED,
            dataProvider.setUserRole(user.getId(),
                    52134,
                    confUser.get().getId(),
                    UserRole.ADMINISTRATOR));
  }

  void deleteGroupTaskCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().filter(group -> !group.getTaskList().isEmpty()).findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var optTask = optGroup.get().getTaskList().keySet().stream().findAny();
    log.debug(optGroup.get());
    Assertions.assertTrue(optTask.isPresent());
    Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteTask(user.getId(),
            optGroup.get().getId(),
            optTask.get().getId()));
    optGroup = dataProvider.getGroup(optGroup.get().getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertFalse(optGroup.get().getTaskList().containsKey(optTask.get()));
  }

  void deleteGroupTaskIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().filter(group -> !group.getTaskList().isEmpty()).findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var optTask = optGroup.get().getTaskList().keySet().stream().findAny();
    log.debug(optGroup.get());
    Assertions.assertTrue(optTask.isPresent());
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.deleteTask(2,
            optGroup.get().getId(),
            optTask.get().getId()));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.deleteTask(userId,
            optGroup.get().getId(),
            5468));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.deleteTask(5425,
            optGroup.get().getId(),
            optTask.get().getId()));
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.deleteTask(userId,
            12253,
            optTask.get().getId()));
  }

  void changeTaskStateCorrect() {
    List<Group> groupList = dataProvider.getUsersGroups(user.getId());
    Optional<Group> optGroup = groupList.stream().filter(group -> group.getTaskList().size() > 0).findAny();

    Assertions.assertTrue(optGroup.isPresent());
    Optional<Task> optTask = optGroup.get().getTaskList().keySet().stream().findAny();
    log.debug(optGroup.get());
    Assertions.assertTrue(optTask.isPresent());
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.changeTaskState(user.getId(),
            optGroup.get().getId(),
            optTask.get().getId(),
            TaskState.SUGGESTED));
  }

  void changeTaskStateIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    Optional<Group> optGroup = groupList.stream().filter(group -> group.getTaskList().size() > 0).findAny();
    Assertions.assertTrue(optGroup.isPresent());
    var optTask = optGroup.get().getTaskList().keySet().stream().findAny();
    log.debug(optGroup.get());
    Assertions.assertTrue(optTask.isPresent());
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.changeTaskState(user.getId(),
            532138,
            optTask.get().getId(),
            TaskState.SUGGESTED));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.changeTaskState(2,
            optGroup.get().getId(),
            optTask.get().getId(),
            TaskState.SUGGESTED));
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.changeTaskState(userId,
            optGroup.get().getId(),
            56468,
            TaskState.SUGGESTED));
  }


  void getUserGroupsCorrect() {

    var usersGroupList = dataProvider.getUsersGroups(userId);
    Assertions.assertTrue(usersGroupList.size() > 0);
  }

  void getUserGroupsIncorrect() {
    var usersGroupList = dataProvider.getUsersGroups(5458);
    Assertions.assertTrue(usersGroupList.isEmpty());
  }

  void deleteGroupCorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    log.debug(optGroup.get());
    Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteGroup(user.getId(), optGroup.get().getId()));
  }

  void deleteGroupIncorrect() {
    var groupList = dataProvider.getUsersGroups(user.getId());
    var optGroup = groupList.stream().findAny();
    Assertions.assertTrue(optGroup.isPresent());
    log.debug(optGroup.get());
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.deleteGroup(1, optGroup.get().getId()));
    Assertions.assertEquals(Statuses.FORBIDDEN, dataProvider.deleteGroup(1165, optGroup.get().getId()));
    Assertions.assertEquals(Statuses.NOT_FOUNDED, dataProvider.deleteGroup(user.getId(), 165165));
  }

  void getUserInfoCorrect() {
    log.info(dataProvider.getUserInfo(userId));
  }

  void getFullUserList() {
    Assertions.assertTrue(dataProvider.getFullUsersList().size() > 0);
    log.debug(dataProvider.getFullUsersList());
  }

  void getAverageGroupSize() {
    var optSize = dataProvider.getAverageGroupSize();
    Assertions.assertTrue(optSize.isPresent());
    log.info(optSize.get());
  }

  void getGroupCountPerType() {
    var groupCount = dataProvider.getGroupCountPerType();
    Assertions.assertFalse(groupCount.isEmpty());
    StringBuilder builder = new StringBuilder();
    groupCount.forEach((groupTypes, aLong) ->
            builder.append(groupTypes)
                    .append(" : ")
                    .append(aLong)
                    .append("\n"));
    log.info(builder.toString());
  }

  void getAverageGroupSizeDividedByGroupType() {
    var groupSizes = dataProvider.getGroupCountPerType();
    Assertions.assertFalse(groupSizes.isEmpty());
    StringBuilder builder = new StringBuilder();
    groupSizes.forEach((groupTypes, aLong) ->
            builder.append(groupTypes)
                    .append(" : ")
                    .append(aLong)
                    .append("\n"));
    log.info(builder.toString());
  }

  void getAverageTaskPerGroup() {
    var optSize = dataProvider.getAverageTaskPerGroup();
    Assertions.assertTrue(optSize.isPresent());
    log.info(optSize.get());
  }

  void getGroupStatistic() {
    log.info(dataProvider.getGroupsStatistic());
  }

  void getTaskStatistic() {
    log.info(dataProvider.getTaskStatistic());
  }

}