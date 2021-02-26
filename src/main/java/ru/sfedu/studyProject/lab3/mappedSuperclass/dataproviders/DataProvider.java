package ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders;

import ru.sfedu.studyProject.lab3.mappedSuperclass.model.Group;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.Task;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.User;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Optional;

public interface DataProvider {

  Optional<Task> getTask();

  Optional<Group> getGroup();

  Optional<User> getUser();

  Statuses saveTask(Task task);

  Statuses saveGroup(Group group);

  Statuses saveUser(User user);

  Statuses deleteTask(long taskId);

  Statuses deleteGroup(long groupId);

  Statuses deleteUser(long userId);

}
