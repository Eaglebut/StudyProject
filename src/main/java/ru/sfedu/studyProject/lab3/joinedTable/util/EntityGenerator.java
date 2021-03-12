package ru.sfedu.studyProject.lab3.joinedTable.util;


import ru.sfedu.studyProject.lab3.enums.*;
import ru.sfedu.studyProject.lab3.joinedTable.dataproviders.HibernateDataProvider;
import ru.sfedu.studyProject.lab3.joinedTable.model.*;

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

  public static List<Group> generateGroupList(int count) {
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
      switch (random.nextInt() % 4) {
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
        case 3:
          Task task = new Task();
          setBasicTask(task);
          task.setTaskType(TaskTypes.BASIC);
          task.setName("task " + random.nextInt());
          taskList.add(task);
      }
    }
    return taskList;
  }

}
