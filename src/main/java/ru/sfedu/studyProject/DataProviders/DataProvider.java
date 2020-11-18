package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@NonNull
public interface DataProvider {

  Statuses createTask(@NonNull User user, @NonNull String taskName, @NonNull TaskStatuses status);

  Statuses createTask(@NonNull User user,
                      @NonNull String taskName,
                      @NonNull TaskStatuses status,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  Statuses deleteTask(@NonNull User user, @NonNull Task task);

  Statuses editTask(@NonNull User user, @NonNull Task editedTask);

  Optional<User> getUser(@NonNull long userId) throws NoSuchElementException;

  Optional<User> getUser(@NonNull String login, @NonNull String password) throws NoSuchElementException;

  Statuses editUser(@NonNull User editedUser);

  Statuses addUserToGroup(@NonNull User user, @NonNull Group group);

  Statuses createGroup(@NonNull String groupName,
                       @NonNull User creator,
                       @NonNull GroupTypes groupType);

  Statuses changeGroupType(@NonNull User user, @NonNull Group group, @NonNull GroupTypes groupType);

  List<Group> searchGroupByName(@NonNull String name);

  Group searchGroupById(@NonNull long id) throws NoSuchElementException;

  List<Group> getFullGroupList();

  Optional<Group> getGroup(@NonNull long groupId);

  Optional<Group> getGroup(@NonNull User user, @NonNull long groupId);

  Statuses deleteUserFromGroup(@NonNull User user, @NonNull Group group);

  Statuses createTask(@NonNull User user, @NonNull Group group, @NonNull Task task);

  Statuses createTask(@NonNull User user, @NonNull Group group, @NonNull String name);

  Statuses createTask(@NonNull User user,
                      @NonNull Group group,
                      @NonNull String name,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  Statuses updateGroup(@NonNull User user, @NonNull Group group);


  Statuses setUserRole(@NonNull User user, @NonNull Group group, @NonNull User userToSet, @NonNull UserRole role);

  Statuses changeTaskState(@NonNull User user, @NonNull Group group, @NonNull Task task, @NonNull TaskState state);

  Statuses deleteGroup(@NonNull User user, @NonNull Group group);



}
