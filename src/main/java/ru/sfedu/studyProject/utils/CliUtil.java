package ru.sfedu.studyProject.utils;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.lab1.HibernateDataProvider;
import ru.sfedu.studyProject.lab2.model.SecondTestEntity;
import ru.sfedu.studyProject.lab2.model.TestEntity;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;
import ru.sfedu.studyProject.lab3.joinedTable.util.EntityGenerator;

import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
public class CliUtil {

  public static void startFunc(String[] args) {
    switch (args[0].toLowerCase()) {
      case "lab1":
        lab1(args);
        break;
      case "lab2":
        lab2(args);
        break;
      case "lab3":
        lab3(args);
        break;
      case "lab4":
        lab4(args);
        break;
      case "lab5":
        lab5(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void lab1(String[] args) {
    switch (args[1]) {
      case "getTableList":
        getTableList();
        break;
      case "getTableColumns":
        getTableColumns(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void getTableList() {
    HibernateDataProvider.getTableList().forEach(System.out::println);
  }

  private static void getTableColumns(String[] args) {
    HibernateDataProvider.getTableColumns(args[2]).forEach((s, s2) -> System.out.println(s + " : " + s2));
  }


  private static void lab2(String[] args) {
    switch (args[1]) {
      case "createTestEntity":
        createTestEntity(args);
        break;
      case "getTestEntity":
        getTestEntity(args);
        break;
      case "deleteTestEntity":
        deleteTestEntity(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void createTestEntity(String[] args) {
    TestEntity entity = new TestEntity();
    entity.setName(args[2]);
    entity.setDescription(args[3]);
    entity.setDateCreated(new Date(System.currentTimeMillis()));
    entity.setCheck(Boolean.parseBoolean(args[4]));
    SecondTestEntity secondTestEntity = new SecondTestEntity();
    secondTestEntity.setSomeText(args[5]);
    entity.setSecondTestEntity(secondTestEntity);
    ru.sfedu.studyProject.lab2.HibernateDataProvider.setTestEntity(entity);
    System.out.print(entity);
  }

  private static void getTestEntity(String[] args) {
    var optEntity = ru.sfedu.studyProject.lab2.HibernateDataProvider.getTestEntity(Long.parseLong(args[2]));
    optEntity.ifPresentOrElse(System.out::println, () -> System.out.print("Объект не найден"));
  }

  private static void deleteTestEntity(String[] args) {
    var optEntity = ru.sfedu.studyProject.lab2.HibernateDataProvider.getTestEntity(Long.parseLong(args[2]));
    optEntity.ifPresentOrElse(ru.sfedu.studyProject.lab2.HibernateDataProvider::deleteTestEntity, () -> System.out.print("Объект не найден"));
  }

  private static void lab3(String[] args) {
    switch (args[1]) {
      case "joinedTable":
        joinedTable(args);
        break;
      case "mappedSuperclass":
        mappedSuperclass(args);
        break;
      case "singleTable":
        singleTable(args);
        break;
      case "tablePerClass":
        tablePerClass(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void joinedTable(String[] args) {
    switch (args[2]) {
      case "generateTask":
        generateTaskJoinedTable();
        break;
      case "getTask":
        getTaskJoinedTable(args);
      default:
        System.out.println("Ошибка");
    }
  }

  private static void generateTaskJoinedTable() {
    var taskList = EntityGenerator.generateTaskList(10);
    ru.sfedu.studyProject.lab3.joinedTable.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.joinedTable.dataproviders.HibernateDataProvider();
    taskList.forEach(dataProvider::saveTask);
    taskList.forEach(System.out::println);
  }

  private static void getTaskJoinedTable(String[] args) {
    ru.sfedu.studyProject.lab3.joinedTable.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.joinedTable.dataproviders.HibernateDataProvider();
    var optEntity = dataProvider.getTask(Long.parseLong(args[3]), TaskTypes.valueOf(args[4]));
    optEntity.ifPresentOrElse(System.out::println, () -> System.out.print("Объект не найден"));
  }

  private static void mappedSuperclass(String[] args) {
    switch (args[2]) {
      case "generateTask":
        generateTaskMappedSuperclass();
        break;
      case "getTask":
        getTaskMappedSuperclass(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void generateTaskMappedSuperclass() {
    var taskList = ru.sfedu.studyProject.lab3.mappedSuperclass.util.EntityGenerator.generateTaskList(10);
    ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders.HibernateDataProvider();
    taskList.forEach(dataProvider::saveTask);
    taskList.forEach(System.out::println);
  }

  private static void getTaskMappedSuperclass(String[] args) {
    ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders.HibernateDataProvider();
    var optEntity = dataProvider.getTask(Long.parseLong(args[3]), TaskTypes.valueOf(args[4]));
    optEntity.ifPresentOrElse(System.out::println, () -> System.out.print("Объект не найден"));
  }

  private static void singleTable(String[] args) {
    switch (args[2]) {
      case "generateTask":
        generateTaskSingleTable();
        break;
      case "getTask":
        getTaskSingleTable(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void generateTaskSingleTable() {
    var taskList = ru.sfedu.studyProject.lab3.singleTable.util.EntityGenerator.generateTaskList(10);
    ru.sfedu.studyProject.lab3.singleTable.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.singleTable.dataproviders.HibernateDataProvider();
    taskList.forEach(dataProvider::saveTask);
    taskList.forEach(System.out::println);
  }

  private static void getTaskSingleTable(String[] args) {
    ru.sfedu.studyProject.lab3.singleTable.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.singleTable.dataproviders.HibernateDataProvider();
    var optEntity = dataProvider.getTask(Long.parseLong(args[3]), TaskTypes.valueOf(args[4]));
    optEntity.ifPresentOrElse(System.out::println, () -> System.out.print("Объект не найден"));
  }

  private static void tablePerClass(String[] args) {
    switch (args[2]) {
      case "generateTask":
        generateTaskTablePerClass();
        break;
      case "getTask":
        getTaskTablePerClass(args);
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void generateTaskTablePerClass() {
    var taskList = ru.sfedu.studyProject.lab3.tablePerClass.util.EntityGenerator.generateTaskList(10);
    ru.sfedu.studyProject.lab3.tablePerClass.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.tablePerClass.dataproviders.HibernateDataProvider();
    taskList.forEach(dataProvider::saveTask);
    taskList.forEach(System.out::println);
  }

  private static void getTaskTablePerClass(String[] args) {
    ru.sfedu.studyProject.lab3.tablePerClass.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab3.tablePerClass.dataproviders.HibernateDataProvider();
    var optEntity = dataProvider.getTask(Long.parseLong(args[3]), TaskTypes.valueOf(args[4]));
    optEntity.ifPresentOrElse(System.out::println, () -> System.out.print("Объект не найден"));
  }

  private static void lab4(String[] args) {
    switch (args[1]) {
      case "generateLesson":
        generateLessons();
        break;
      case "generateUser":
        generateUsers();
        break;
      case "generateWorkTask":
        generateWorkTasks();
        break;
      default:
        System.out.println("Ошибка");
    }
  }

  private static void generateLessons() {
    var lessonList = ru.sfedu.studyProject.lab4.util.EntityGenerator.generateTaskList(10).stream()
            .filter(task -> task.getTaskType().equals(TaskTypes.LESSON))
            .collect(Collectors.toList());
    ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider();
    lessonList.forEach(lesson -> {
      dataProvider.saveTask(lesson);
      System.out.println(lesson);
    });
  }

  private static void generateUsers() {
    var userList = ru.sfedu.studyProject.lab4.util.EntityGenerator.generateUserList(3);
    ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider();
    userList.forEach(user -> {
      dataProvider.saveUser(user);
      System.out.println(user);
    });
  }

  private static void generateWorkTasks() {
    var lessonList = ru.sfedu.studyProject.lab4.util.EntityGenerator.generateTaskList(10).stream()
            .filter(task -> task.getTaskType().equals(TaskTypes.WORK_TASK))
            .collect(Collectors.toList());
    ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab4.dataproviders.HibernateDataProvider();
    lessonList.forEach(lesson -> {
      dataProvider.saveTask(lesson);
      System.out.println(lesson);
    });
  }

  private static void lab5(String[] args) {
    if ("generateGroup".equals(args[1])) {
      generateGroup();
    }
  }

  private static void generateGroup() {
    var groupList = ru.sfedu.studyProject.lab5.util.EntityGenerator.generateGroupList(1);
    ru.sfedu.studyProject.lab5.dataproviders.HibernateDataProvider dataProvider =
            new ru.sfedu.studyProject.lab5.dataproviders.HibernateDataProvider();
    groupList.forEach(group -> {
      dataProvider.saveGroup(group);
      group.getTaskList().forEach(task -> {
        task.setGroup(group);
        dataProvider.saveTask(task);
      });
      dataProvider.saveGroup(group);
      System.out.println(group);
    });
  }


}
