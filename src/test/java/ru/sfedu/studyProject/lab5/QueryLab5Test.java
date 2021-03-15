package ru.sfedu.studyProject.lab5;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.lab5.dataproviders.*;
import ru.sfedu.studyProject.lab5.model.Group;
import ru.sfedu.studyProject.lab5.util.EntityGenerator;

@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QueryLab5Test {

  static DataProvider dataProvider = new HibernateDataProvider();
  static Group group;


  @BeforeAll
  static void setUp() {
    group = EntityGenerator.generateGroupList(1).get(0);
    dataProvider.saveGroup(group);
    var taskList = EntityGenerator.generateTaskList(2);
    var userList = EntityGenerator.generateUserList(3);
    taskList.forEach(task -> {
      task.setGroup(group);
      dataProvider.saveTask(task);
      group.getTaskList().add(task);
    });
    userList.forEach(user -> {
      user.getGroup().add(group);
      dataProvider.saveUser(user);
      group.getUserList().add(user);
    });
    dataProvider.saveGroup(group);

  }

  @Test
  @Order(0)
  void test() {
    QueryLab5 queryLab5 = new SqlQueryLab5();
    var optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());

    queryLab5 = new HqlQueryLab5();
    optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());

    queryLab5 = new CriteriaQueryLab5();
    optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());

    setUp();
  }

  @Test
  void NativeTest() {
    QueryLab5 queryLab5 = new SqlQueryLab5();
    var optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());
  }

  @Test
  void HQLTest() {
    QueryLab5 queryLab5 = new HqlQueryLab5();
    var optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());
  }

  @Test
  void CriteriaTest() {
    QueryLab5 queryLab5 = new CriteriaQueryLab5();
    var optGroup = queryLab5.getGroup(group.getId());
    Assertions.assertTrue(optGroup.isPresent());
    Assertions.assertEquals(group, optGroup.get());
    log.info(optGroup.get());
  }
}
