package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * The interface Data provider.
 */
public interface DataProvider {

  /**
   * Create user.
   *
   * @param email      the email
   * @param password   the password
   * @param name       the name
   * @param surname    the surname
   * @param signUpType the sign up type
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createUser(@NonNull String email,
                      @NonNull String password,
                      @NonNull String name,
                      @NonNull String surname,
                      @NonNull SignUpTypes signUpType);

  /**
   * Create basic task.
   *
   * @param userId   the user id
   * @param taskName the task name
   * @param status   the status
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createTask(long userId, @NonNull String taskName, @NonNull TaskStatuses status);

  /**
   * Create  extended task
   *
   * @param userId         the user id
   * @param taskName       the task name
   * @param status         the status
   * @param repetitionType the repetition type
   * @param remindType     the remind type
   * @param importance     the importance
   * @param description    the description
   * @param time           the time
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createTask(long userId,
                      @NonNull String taskName,
                      @NonNull TaskStatuses status,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  /**
   * Create basic group task.
   *
   * @param userId     the user id
   * @param groupId    the group id
   * @param name       the name
   * @param taskStatus the task status
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createTask(long userId, long groupId, @NonNull String name, @NonNull TaskStatuses taskStatus);

  /**
   * Create extended group task.
   *
   * @param userId         the user id
   * @param groupId        the group id
   * @param name           the name
   * @param taskStatus     the task status
   * @param repetitionType the repetition type
   * @param remindType     the remind type
   * @param importance     the importance
   * @param description    the description
   * @param time           the time
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createTask(long userId,
                      long groupId,
                      @NonNull String name,
                      @NonNull TaskStatuses taskStatus,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  /**
   * Delete user's task.
   *
   * @param userId the user id
   * @param taskId the task id
   * @return the status
   */
  Statuses deleteTask(long userId, long taskId);

  /**
   * Delete group task.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @param taskId  the task id
   * @return the status
   */
  Statuses deleteTask(long userId, long groupId, long taskId);

  /**
   * Edit user's task.
   *
   * @param userId     the user id
   * @param editedTask the edited task
   * @return the status
   */
  Statuses editTask(long userId, @NonNull Task editedTask);

  /**
   * Gets user.
   *
   * @param userId the user id
   * @return the user
   */
  Optional<User> getUser(long userId);

  /**
   * Gets user.
   *
   * @param login    the login
   * @param password the password
   * @return the user
   * @throws NullPointerException when input variables are null
   */
  Optional<User> getUser(@NonNull String login, @NonNull String password);

  /**
   * Edit user.
   *
   * @param editedUser the edited user
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses editUser(@NonNull User editedUser);

  /**
   * Add user to group.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @return the status
   */
  Statuses addUserToGroup(long userId, long groupId);

  /**
   * Create group.
   *
   * @param groupName the group name
   * @param creatorId the creator id
   * @param groupType the group type
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses createGroup(@NonNull String groupName,
                       long creatorId,
                       @NonNull GroupTypes groupType);

  /**
   * Change group type.
   *
   * @param userId    the user id
   * @param groupId   the group id
   * @param groupType the group type
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses changeGroupType(long userId, long groupId, @NonNull GroupTypes groupType);

  /**
   * Search group by name.
   *
   * @param name the name
   * @return the list
   * @throws NullPointerException when input variables are null
   */
  List<Group> searchGroupByName(@NonNull String name);

  /**
   * Gets full group list.
   *
   * @return the full group list
   */
  List<Group> getFullGroupList();

  /**
   * Gets group.
   *
   * @param groupId the group id
   * @return the group
   */
  Optional<Group> getGroup(long groupId);

  /**
   * Delete user from group.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @return the status
   */
  Statuses deleteUserFromGroup(long userId, long groupId);

  /**
   * Suggest user's task.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @param taskId  the task id
   * @return the status
   */
  Statuses suggestTask(long userId, long groupId, long taskId);


  /**
   * Update group.
   *
   * @param userId      the user id
   * @param editedGroup the edited group
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses updateGroup(long userId, @NonNull Group editedGroup);

  /**
   * Sets user role.
   *
   * @param administratorId the administrator id
   * @param groupId         the group id
   * @param userIdToSet     the user id to set
   * @param role            the role
   * @return the user role
   * @throws NullPointerException when input variables are null
   */
  Statuses setUserRole(long administratorId, long groupId, long userIdToSet, @NonNull UserRole role);

  /**
   * Change task state.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @param taskId  the task id
   * @param state   the state
   * @return the status
   * @throws NullPointerException when input variables are null
   */
  Statuses changeTaskState(long userId, long groupId, long taskId, @NonNull TaskState state);

  /**
   * Delete group.
   *
   * @param userId  the user id
   * @param groupId the group id
   * @return the status
   */
  Statuses deleteGroup(long userId, long groupId);

  /**
   * Gets users groups.
   *
   * @param userId the user id
   * @return the users groups
   */
  List<Group> getUsersGroups(long userId);

  /**
   * Gets user info.
   *
   * @param userId the user id
   * @return the user info
   */
  String getUserInfo(long userId);

  /**
   * Gets task statistic.
   *
   * @return the task statistic
   */
  String getTaskStatistic();

  /**
   * Gets groups statistic.
   *
   * @return the groups statistic
   */
  String getGroupsStatistic();

  /**
   * Gets full users list.
   *
   * @return the full users list
   */
  List<User> getFullUsersList();

  /**
   * Gets average group size.
   *
   * @return the average group size
   */
  Optional<Double> getAverageGroupSize();

  /**
   * Gets task count per owner.
   *
   * @return the task count per owner
   */
  Map<Owner, Long> getTaskCountPerOwner();

  /**
   * Gets average task per owner.
   *
   * @return the average task per owner
   */
  Map<Owner, Double> getAverageTaskPerOwner();

  /**
   * Gets task count per type.
   *
   * @return the task count per type
   */
  Map<TaskTypes, Long> getTaskCountPerType();

  /**
   * Gets full task list.
   *
   * @return the full task list
   */
  List<Task> getFullTaskList();

  /**
   * Gets average group size divided by group type.
   *
   * @return the average group size divided by group type
   */
  Map<GroupTypes, Double> getAverageGroupSizeDividedByGroupType();

  /**
   * Gets group count per type.
   *
   * @return the group count per type
   */
  Map<GroupTypes, Long> getGroupCountPerType();

  /**
   * Gets average task per group.
   *
   * @return the average task per group
   */
  Optional<Double> getAverageTaskPerGroup();

}
