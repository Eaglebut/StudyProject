package ru.sfedu.studyProject.lab3;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab3.singleTable.dataproviders.DataProvider;
import ru.sfedu.studyProject.lab3.singleTable.dataproviders.HibernateDataProvider;
import ru.sfedu.studyProject.lab3.singleTable.util.EntityGenerator;
import ru.sfedu.studyProject.utils.Statuses;

@Log4j2
public class HibernateDataProviderSingleTableTest {
  DataProvider dataProvider = new HibernateDataProvider();

  @Test
  void saveUser() {
    var userList = EntityGenerator.generateUserList(10);
    userList.forEach(user -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
      Assertions.assertTrue(user.getId() != 0);
      log.info(user);
    });
  }

  @Test
  void getUser() {
    var userList = EntityGenerator.generateUserList(10);
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
    var userList = EntityGenerator.generateUserList(10);
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
    var groupList = EntityGenerator.generateGroupList(10);
    groupList.forEach(group -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveGroup(group));
      Assertions.assertTrue(group.getId() != 0);
      log.info(group);
    });
  }


  @Test
  void getGroup() {
    var groupList = EntityGenerator.generateGroupList(10);
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
    var groupList = EntityGenerator.generateGroupList(10);
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
    var taskList = EntityGenerator.generateTaskList(30);
    taskList.forEach(task -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveTask(task));
      Assertions.assertTrue(task.getId() != 0);
    });
  }

  @Test
  void getTask() {
    var taskList = EntityGenerator.generateTaskList(30);
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
    var taskList = EntityGenerator.generateTaskList(30);
    taskList.forEach(task -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveTask(task));
      Assertions.assertTrue(task.getId() != 0);
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.deleteTask(task.getId(), task.getTaskType()));
      Assertions.assertTrue(dataProvider.getTask(task.getId(), task.getTaskType()).isEmpty());
    });
  }

}
