package ru.sfedu.studyProject.lab5;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab5.dataproviders.DataProvider;
import ru.sfedu.studyProject.lab5.dataproviders.HibernateDataProvider;
import ru.sfedu.studyProject.lab5.util.EntityGenerator;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.HashSet;

@Log4j2
public class HibernateDataProviderTest {

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
      var taskList = EntityGenerator.generateTaskList(5);
      taskList.forEach(task -> {
        task.setGroup(group);
        dataProvider.saveTask(task);
      });
      var optGroup = dataProvider.getGroup(group.getId());
      Assertions.assertTrue(optGroup.isPresent());
      Assertions.assertEquals(group.getUserList() ,optGroup.get().getUserList());
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

  @Test
  void saveAddress() {
    var addressList = EntityGenerator.generateAddress(10);
    addressList.forEach(address -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAddress(address));
      Assertions.assertTrue(address.getId() != 0);
      log.info(address);
    });
  }

  @Test
  void getAddress() {
    var addressList = EntityGenerator.generateAddress(10);
    addressList.forEach(address -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAddress(address));
      Assertions.assertTrue(address.getId() != 0);
      var optAddress = dataProvider.getAddress(address.getId());
      Assertions.assertTrue(optAddress.isPresent());
      Assertions.assertEquals(address, optAddress.get());
      log.info(optAddress);
    });
  }

  @Test
  void deleteAddress() {
    var addressList = EntityGenerator.generateAddress(10);
    addressList.forEach(address -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAddress(address));
      Assertions.assertTrue(address.getId() != 0);
      log.info(address);
      var status = dataProvider.deleteAddress(address.getId());
      Assertions.assertEquals(Statuses.SUCCESSFUL, status);
    });
  }

  @Test
  void saveAssigment() {
    var assigmentList = EntityGenerator.generateAssigment(10);
    assigmentList.forEach(assigment -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAssigment(assigment));
      Assertions.assertTrue(assigment.getId() != 0);
      log.info(assigment);
    });
  }

  @Test
  void getAssigment() {
    var assigmentList = EntityGenerator.generateAssigment(10);
    assigmentList.forEach(assigment -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAssigment(assigment));
      Assertions.assertTrue(assigment.getId() != 0);
      var optAddress = dataProvider.getAssigment(assigment.getId());
      Assertions.assertTrue(optAddress.isPresent());
      Assertions.assertEquals(assigment, optAddress.get());
      log.info(optAddress);
    });
  }

  @Test
  void deleteAssigment() {
    var assigmentList = EntityGenerator.generateAssigment(10);
    assigmentList.forEach(assigment -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveAssigment(assigment));
      Assertions.assertTrue(assigment.getId() != 0);
      log.info(assigment);
      var status = dataProvider.deleteAssigment(assigment.getId());
      Assertions.assertEquals(Statuses.SUCCESSFUL, status);
    });
  }

  @Test
  void saveMetadata() {
    var metadataList = EntityGenerator.generateMetadata(10);
    metadataList.forEach(assigment -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveMetadata(assigment));
      Assertions.assertTrue(assigment.getId() != 0);
      log.info(assigment);
    });
  }

  @Test
  void getMetadata() {
    var metadataList = EntityGenerator.generateMetadata(10);
    metadataList.forEach(metadata -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveMetadata(metadata));
      Assertions.assertTrue(metadata.getId() != 0);
      var optAddress = dataProvider.getMetadata(metadata.getId());
      Assertions.assertTrue(optAddress.isPresent());
      Assertions.assertEquals(metadata, optAddress.get());
      log.info(optAddress);
    });
  }

  @Test
  void deleteMetadata() {
    var metadataList = EntityGenerator.generateMetadata(10);
    metadataList.forEach(metadata -> {
      Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveMetadata(metadata));
      Assertions.assertTrue(metadata.getId() != 0);
      log.info(metadata);
      var status = dataProvider.deleteMetadata(metadata.getId());
      Assertions.assertEquals(Statuses.SUCCESSFUL, status);
    });
  }
}
