package ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.*;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.*;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.*;

@Log4j2
class HibernateDataProviderMappedSuperclassTest {

  DataProvider dataProvider = new HibernateDataProvider();


  private List<User> generateUserList(int count) {
    var userList = new ArrayList<User>();
    User user;
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      user = new User();
      user.setName("testName" + random.nextInt());
      user.setSurname("testSurname" + random.nextInt());
      user.setCreated(new Date(System.currentTimeMillis()));
      userList.add(user);
    }
    return userList;
  }

  private List<Group> generateGroupList(int count) {
    var groupList = new ArrayList<Group>();
    Group group;
    Random random = new Random();

    var userList = generateUserList(count * 10);
    userList.forEach(dataProvider::saveUser);

    for (int i = 0; i < count; i++) {
      group = new Group();
      group.setGroupType(GroupTypes.PUBLIC);
      group.setCreated(new Date(System.currentTimeMillis()));
      group.setName("testGroup" + random.nextInt());
      var userMap = new HashMap<User, UserRoles>();
      userMap.put(userList.stream().findAny().get(), UserRoles.CREATOR);
      userList.forEach(user -> {
        if (random.nextBoolean() && !userMap.containsKey(user)) {
          userMap.put(user, UserRoles.MEMBER);
        }
      });
      group.setUserMap(userMap);
      groupList.add(group);
    }
    return groupList;
  }

  private void setBasicTask(Task task) {
    task.setCreated(new Date(System.currentTimeMillis()));
    task.setLastUpdated(new Date(System.currentTimeMillis() + 100000));
    task.setTaskStatus(TaskStatuses.STANDARD);
    task.setRemindType(RemindTypes.DONT_REMIND);
  }

  private List<Task> generateTaskList(int count) {
    Random random = new Random();
    var taskList = new ArrayList<Task>();
    for (int i = 0; i < count; i++) {
      switch (random.nextInt() % 3) {
        case 0:
          ExtendedTask extendedTask = new ExtendedTask();
          setBasicTask(extendedTask);
          extendedTask.setTaskType(TaskTypes.EXTENDED);
          extendedTask.setName("extended task " + random.nextInt());
          extendedTask.setDescription("task description");
          extendedTask.setTime(new Date(System.currentTimeMillis() + random.nextInt()));
          extendedTask.setImportance(Importances.STANDARD);
          taskList.add(extendedTask);
          break;
        case 1:
          Lesson lesson = new Lesson();
          setBasicTask(lesson);
          lesson.setTaskType(TaskTypes.LESSON);
          lesson.setName("lesson " + random.nextInt());
          lesson.setTeacherName("teacher " + random.nextInt());
          lesson.setBeginDate(new Date(System.currentTimeMillis() + random.nextInt()));
          lesson.setEndDate(new Date(System.currentTimeMillis() + random.nextInt()));
          lesson.setAssigment("Assigment");
          lesson.setLessonType(LessonTypes.LECTURE);
          taskList.add(lesson);
          break;
        case 2:
          WorkTask workTask = new WorkTask();
          setBasicTask(workTask);
          workTask.setTaskType(TaskTypes.WORK_TASK);
          workTask.setName("work task " + random.nextInt());
          workTask.setTime(new Date(System.currentTimeMillis() + random.nextInt()));
          workTask.setType(WorkTaskType.MEETING);
          workTask.setDescription("description");
          taskList.add(workTask);
      }
    }
    return taskList;
  }


  @Test
  void saveUser() {
    var userList = generateUserList(10);
    userList.forEach(user -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
      Assertions.assertTrue(user.getId() != 0);
      log.info(user);
    });
  }

  @Test
  void getUser() {
    var userList = generateUserList(10);
    userList.forEach(user -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
      Assertions.assertTrue(user.getId() != 0);
      var optUser = dataProvider.getUser(user.getId());
      Assertions.assertTrue(optUser.isPresent());
      Assertions.assertEquals(user, optUser.get());
      log.info(optUser);
    });
  }


  @Test
  void deleteUser() {
    var userList = generateUserList(10);
    userList.forEach(user -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
      Assertions.assertTrue(user.getId() != 0);
      log.info(user);
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.deleteUser(user.getId()));
      Assertions.assertTrue(dataProvider.getUser(user.getId()).isEmpty());
    });
  }

  @Test
  void createGroup() {
    var groupList = generateGroupList(10);
    groupList.forEach(group -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveGroup(group));
      Assertions.assertTrue(group.getId() != 0);
      log.info(group);
    });
  }


  @Test
  void getGroup() {
    var groupList = generateGroupList(10);
    groupList.forEach(group -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveGroup(group));
      Assertions.assertTrue(group.getId() != 0);
      var optGroup = dataProvider.getGroup(group.getId());
      Assertions.assertTrue(optGroup.isPresent());
      Assertions.assertEquals(group, optGroup.get());
      log.info(optGroup);
    });
  }

  @Test
  void deleteGroup() {
    var groupList = generateGroupList(10);
    groupList.forEach(group -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveGroup(group));
      Assertions.assertTrue(group.getId() != 0);
      log.info(group);
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.deleteGroup(group.getId()));
      Assertions.assertTrue(dataProvider.getGroup(group.getId()).isEmpty());
    });
  }

  @Test
  void saveTask() {
    var taskList = generateTaskList(30);
    taskList.forEach(task -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveTask(task));
      Assertions.assertTrue(task.getId() != 0);
    });
  }

  @Test
  void getTask() {
    var taskList = generateTaskList(30);
    taskList.forEach(task -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveTask(task));
      Assertions.assertTrue(task.getId() != 0);
      var optTask = dataProvider.getTask(task.getId(), task.getTaskType());
      Assertions.assertTrue(optTask.isPresent());
      Assertions.assertEquals(task, optTask.get());
      log.info(optTask.get());
    });
  }

  @Test
  void deleteTask() {
    var taskList = generateTaskList(30);
    taskList.forEach(task -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveTask(task));
      Assertions.assertTrue(task.getId() != 0);
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.deleteTask(task.getId(), task.getTaskType()));
      Assertions.assertTrue(dataProvider.getTask(task.getId(), task.getTaskType()).isEmpty());
    });
  }

}