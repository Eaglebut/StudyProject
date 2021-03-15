package ru.sfedu.studyProject.lab5.generatedPrimaryKey.util;

import ru.sfedu.studyProject.lab3.enums.*;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.dataproviders.HibernateDataProvider;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    userList.forEach(dataProvider::saveUser);
    List<Task> taskList = generateTaskList(2);
    taskList.forEach(dataProvider::saveTask);

    for (int i = 0; i < count; i++) {
      group = new Group();
      group.setGroupType(GroupTypes.PUBLIC);
      group.setCreated(new Date(System.currentTimeMillis()));
      group.setName("testGroup" + random.nextInt());
      //group.setUserList(new HashSet<>(userList));
      //group.setTaskList(new HashSet<>(taskList));
      groupList.add(group);
    }
    return groupList;
  }

  private static void setBasicTask(Task task) {
    task.setCreated(new Date(System.currentTimeMillis()));
    task.setLastUpdated(new Date(System.currentTimeMillis() + 100000));
    task.setTaskStatus(TaskStatuses.STANDARD);
    task.setRemindType(RemindTypes.DONT_REMIND);
  }

  public static List<Task> generateTaskList(int count) {
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

}
