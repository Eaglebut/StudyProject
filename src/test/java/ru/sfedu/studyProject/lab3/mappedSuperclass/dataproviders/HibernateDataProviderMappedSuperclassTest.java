package ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.Group;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.User;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.GroupTypes;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.UserRoles;
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

}