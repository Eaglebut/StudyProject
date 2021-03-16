package ru.sfedu.studyProject.lab5.util;

import ru.sfedu.studyProject.lab3.enums.*;
import ru.sfedu.studyProject.lab5.dataproviders.HibernateDataProvider;
import ru.sfedu.studyProject.lab5.model.*;

import java.util.*;

public class EntityGenerator {

  private static final HibernateDataProvider dataProvider = new HibernateDataProvider();

  public static List<User> generateUserList(int count) {
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

  public static List<Address> generateAddress(int count) {
    var addressList = new ArrayList<Address>();
    Address address = new Address();
    Random random = new Random();
    var userList = generateUserList(count);
    userList.forEach(user -> {
      dataProvider.saveUser(user);
      address.setCity("Rostov_on_don");
      address.setStreet("Zorge");
      address.setHouse(21);
      address.setUser(user);
      addressList.add(address);
    });
    return addressList;
  }

  public static List<Group> generateGroupList(int count) {
    var groupList = new ArrayList<Group>();
    Group group;
    Random random = new Random();

    var userList = generateUserList(2);
    userList.forEach(user -> {
      dataProvider.saveUser(user);
      Address address = new Address();
      address.setCity("Rostov_on_don");
      address.setStreet("Zorge");
      address.setHouse(21);
      address.setUser(user);
      dataProvider.saveAddress(address);
      user.setAddress(address);
      dataProvider.saveUser(user);
    });

    for (int i = 0; i < count; i++) {
      group = new Group();
      group.setGroupType(GroupTypes.PUBLIC);
      group.setCreated(new Date(System.currentTimeMillis()));
      group.setName("testGroup" + random.nextInt());
      group.setUserList(new HashSet<>(userList));
      List<Task> taskList = generateTaskList(5);
      taskList.forEach(dataProvider::saveTask);
      group.setTaskList(new HashSet<>(taskList));

      groupList.add(group);

    }
    return groupList;
  }

  private static void setBasicTask(Task task) {
    task.setTaskStatus(TaskStatuses.STANDARD);
    task.setRemindType(RemindTypes.DONT_REMIND);
    Metadata metadata = generateMetadata(1).get(0);
    dataProvider.saveMetadata(metadata);
    task.setMetadata(metadata);
  }

  public static List<Task> generateTaskList(int count) {
    Random random = new Random();
    var taskList = new ArrayList<Task>();
    for (int i = 0; i < count; i++) {
      switch (random.nextInt() % 3) {
        case 0:
          taskList.add(generateExtendedTask());
          break;
        case 1:
          taskList.add(generateLesson());
          break;
        case 2:
          taskList.add(generateWorkTask());
      }
    }
    return taskList;
  }

  private static ExtendedTask generateExtendedTask(){
    Random random = new Random();
    ExtendedTask extendedTask = new ExtendedTask();
    setBasicTask(extendedTask);
    extendedTask.setTaskType(TaskTypes.EXTENDED);
    extendedTask.setName("extended task " + random.nextInt());
    extendedTask.setDescription("task description");
    extendedTask.setTime(new Date(System.currentTimeMillis() + random.nextInt()));
    extendedTask.setImportance(Importances.STANDARD);
    return extendedTask;
  }

  private static Lesson generateLesson(){
    Random random = new Random();
    Lesson lesson = new Lesson();
    setBasicTask(lesson);
    lesson.setTaskType(TaskTypes.LESSON);
    lesson.setName("lesson " + random.nextInt());
    lesson.setTeacherName("teacher " + random.nextInt());
    lesson.setBeginDate(new Date(System.currentTimeMillis() + random.nextInt()));
    lesson.setEndDate(new Date(System.currentTimeMillis() + random.nextInt()));
    //lesson.setAssigment("Assigment");
    lesson.setLessonType(LessonTypes.LECTURE);
    return lesson;
  }

  private static WorkTask generateWorkTask(){
    Random random = new Random();
    WorkTask workTask = new WorkTask();
    setBasicTask(workTask);
    workTask.setTaskType(TaskTypes.WORK_TASK);
    workTask.setName("work task " + random.nextInt());
    workTask.setTime(new Date(System.currentTimeMillis() + random.nextInt()));
    workTask.setType(WorkTaskType.MEETING);
    workTask.setDescription("description");
    return workTask;
  }


  public static List<Assigment> generateAssigment(int count){
    var assigmentList = new ArrayList<Assigment>();
    Assigment assigment;
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      Lesson lesson = generateLesson();
      dataProvider.saveTask(lesson);
      assigment = new Assigment();
      assigment.setTask("Task");
      assigment.setTeacherName("Teacher");
      assigment.setLesson(lesson);
      assigmentList.add(assigment);
    }
    return assigmentList;
  }

  public static List<Metadata> generateMetadata(int count){
    var metadataList = new ArrayList<Metadata>();
    Metadata metadata;
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      metadata = new Metadata();
      metadata.setLastUpdated(new Date(random.nextLong()));
      metadata.setCreated(new Date(random.nextLong()));
      metadataList.add(metadata);
    }
    return metadataList;
  }
}
