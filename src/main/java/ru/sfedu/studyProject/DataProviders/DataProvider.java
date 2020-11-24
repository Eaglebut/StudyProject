package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@NonNull
public interface DataProvider {

  Statuses createUser(@NonNull String email,
                      @NonNull String password,
                      @NonNull String name,
                      @NonNull String surname,
                      @NonNull SignUpTypes signUpType);

  Statuses createTask(long userId, @NonNull String taskName, @NonNull TaskStatuses status);

  Statuses createTask(long userId,
                      @NonNull String taskName,
                      @NonNull TaskStatuses status,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  Statuses deleteTask(long userId, long taskId);

  Statuses editTask(long userId, @NonNull Task editedTask);

  Optional<User> getUser(long userId) throws NoSuchElementException;

  Optional<User> getUser(@NonNull String login, @NonNull String password) throws NoSuchElementException;

  Statuses editUser(@NonNull User editedUser);

  Statuses addUserToGroup(long userId, long groupId);

  Statuses createGroup(@NonNull String groupName,
                       long creatorId,
                       @NonNull GroupTypes groupType);

  Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType);

  List<Group> searchGroupByName(@NonNull String name);

  Group searchGroupById(long id) throws NoSuchElementException;

  List<Group> getFullGroupList();

  Optional<Group> getGroup(long groupId);

  Optional<Group> getGroup(long userId, long groupId);

  Statuses deleteUserFromGroup(long userId, long groupId);

  Statuses suggestTask(long userId, long groupId, long taskId);

  Statuses createTask(long userId, long groupId, @NonNull String name);

  Statuses createTask(long userId,
                      long groupId,
                      @NonNull String name,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  Statuses updateGroup(long userId, @NonNull Group editedGroup);


  Statuses setUserRole(long userId, long groupId, long userIdToSet, @NonNull UserRole role);

  Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state);

  Statuses deleteGroup(long userId, long groupId);



}
