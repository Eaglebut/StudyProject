package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;


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

  Statuses createTask(long userId, long groupId, @NonNull String name, @NonNull TaskStatuses taskStatus);

  Statuses createTask(long userId,
                      long groupId,
                      @NonNull String name,
                      @NonNull TaskStatuses taskStatus,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  Statuses deleteTask(long userId, long taskId);

  Statuses deleteTask(long userId, long groupId, long taskId);

  Statuses editTask(long userId, @NonNull Task editedTask);

  Optional<User> getUser(long userId);

  Optional<User> getUser(@NonNull String login, @NonNull String password);

  Statuses editUser(@NonNull User editedUser);

  Statuses addUserToGroup(long userId, long groupId);

  Statuses createGroup(@NonNull String groupName,
                       long creatorId,
                       @NonNull GroupTypes groupType);

  Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType);

  List<Group> searchGroupByName(@NonNull String name);

  List<Group> getFullGroupList();

  Optional<Group> getGroup(long groupId);

  Statuses deleteUserFromGroup(long userId, long groupId);

  Statuses suggestTask(long userId, long groupId, long taskId);


  Statuses updateGroup(long userId, @NonNull Group editedGroup);

  Statuses setUserRole(long userId, long groupId, long userIdToSet, @NonNull UserRole role);

  Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state);

  Statuses deleteGroup(long userId, long groupId);

  List<Group> getUsersGroups(long userId);

}
