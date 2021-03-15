package ru.sfedu.studyProject.lab5.generatedPrimaryKey.dataproviders;

import ru.sfedu.studyProject.lab3.enums.TaskTypes;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.model.Address;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.model.Group;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.model.Task;
import ru.sfedu.studyProject.lab5.generatedPrimaryKey.model.User;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Optional;

public interface DataProvider {

  Optional<? extends Task> getTask(long id, TaskTypes taskType);

  Optional<Group> getGroup(long id);

  Optional<User> getUser(long id);

  Optional<User> getUser(String email, String password);

  Statuses saveTask(Task task);

  Statuses saveGroup(Group group);

  Statuses saveUser(User user);

  Statuses deleteTask(long taskId, TaskTypes taskType);

  Statuses deleteGroup(long groupId);

  Statuses deleteUser(long userId);

  Statuses saveAddress(Address address);

  Statuses deleteAddress(long addressId);

  Optional<Address> getAddress(long id);

}
