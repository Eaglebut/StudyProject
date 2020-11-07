package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.Date;
import java.util.List;


/**
 * The interface Data provider.
 */
@NonNull
public interface DataProvider {

  /**
   * Create users task.
   *
   * @param user     the user
   * @param taskName the task name
   * @param status   the status
   * @return the statuses
   */
  Statuses createTask(@NonNull User user, @NonNull String taskName, @NonNull TaskStatuses status);

  /**
   * Create users extended task.
   *
   * @param user           the user
   * @param taskName       the task name
   * @param status         the status
   * @param repetitionType the repetition type
   * @param remindType     the remind type
   * @param importance     the importance
   * @param description    the description
   * @param time           the time
   * @return the statuses
   */
  Statuses createTask(@NonNull User user,
                      @NonNull String taskName,
                      @NonNull TaskStatuses status,
                      @NonNull RepetitionTypes repetitionType,
                      @NonNull RemindTypes remindType,
                      @NonNull Importances importance,
                      @NonNull String description,
                      @NonNull Date time);

  /**
   * Gets own tasks.
   *
   * @param user the user
   * @return the own tasks
   */
  List<Task> getOwnTasks(@NonNull User user);

  /**
   * Delete users task.
   *
   * @param user the user
   * @param task the task
   * @return the statuses
   */
  Statuses deleteTask(@NonNull User user, @NonNull Task task);

  /**
   * Edit users task.
   *
   * @param user       the user
   * @param editedTask the edited task
   * @return the statuses
   */
  Statuses editTask(@NonNull User user, @NonNull Task editedTask);

  /**
   * Gets users profile information.
   *
   * @param userId the user id
   * @return the profile information
   */
  User getProfileInformation(@NonNull Long userId);

  /**
   * Gets users profile information.
   *
   * @param login    the login
   * @param password the password
   * @return the profile information
   */
  User getProfileInformation(@NonNull String login, @NonNull String password);

  /**
   * Change users profile information.
   *
   * @param editedUser the edited user
   * @return the statuses
   */
  Statuses changeProfileInformation(@NonNull User editedUser);

  /**
   * Join the group.
   *
   * @param user  the user
   * @param group the group
   * @return the statuses
   */
  Statuses joinTheGroup(@NonNull User user, @NonNull Group group);

  /**
   * Create group.
   *
   * @param groupName the group name
   * @param creator   the creator
   * @param groupType the group type
   * @return the statuses
   */
  Statuses createGroup(@NonNull String groupName,
                       @NonNull User creator,
                       @NonNull GroupTypes groupType);

  /**
   * Sets group private.
   *
   * @param user  the user
   * @param group the group
   * @return the group private
   */
  Statuses setGroupPrivate(@NonNull User user, @NonNull Group group);

  /**
   * Sets group passworded with confirmation.
   *
   * @param user  the user
   * @param group the group
   * @return the group passworded with confirmation
   */
  Statuses setGroupPasswordedWithConfirmation(@NonNull User user, @NonNull Group group);

  /**
   * Search group by name.
   *
   * @param name the name
   * @return public group list
   */
  List<Group> searchGroupByName(@NonNull String name);

  /**
   * Search group by id.
   *
   * @param id the id
   * @return the group
   */
  Group searchGroupById(@NonNull Long id);

  /**
   * Gets full public group list.
   *
   * @return the full public group list
   */
  List<Group> getFullGroupList();

  /**
   * Gets public group tasks.
   *
   * @param group the group
   * @return the public group tasks
   */
  List<Task> getGroupTasks(@NonNull Group group);

  /**
   * Gets group profile.
   *
   * @param groupId the group id
   * @return the group profile
   */
  Group getGroupProfile(@NonNull Long groupId);

  /**
   * Gets group and own tasks.
   *
   * @param user the user
   * @return the group and own tasks
   */
  List<Task> getGroupAndOwnTasks(@NonNull User user);

  /**
   * Leave group.
   *
   * @param user  the user
   * @param group the group
   * @return the statuses
   */
  Statuses leaveGroup(@NonNull User user, @NonNull Group group);

  /**
   * Gets private group tasks.
   *
   * @param user  the user
   * @param group the group
   * @return the private group tasks
   */
  List<Task> getGroupTasks(@NonNull User user, @NonNull Group group);

  /**
   * Gets private group profile.
   *
   * @param user    the user
   * @param groupId the group id
   * @return the private group profile
   */
  Group getGroupProfile(@NonNull User user, @NonNull Long groupId);

  /**
   * Gets private group member list.
   *
   * @param user  the user
   * @param group the group
   * @return the private group member list
   */
  List<User> getGroupMemberList(@NonNull User user, @NonNull Group group);

  /**
   * Offer own task to group .
   *
   * @param user  the user
   * @param group the group
   * @param task  the task
   * @return the statuses
   */
  Statuses offerTaskToGroup(@NonNull User user, @NonNull Group group, @NonNull Task task);

  /**
   * Offer new basic task to group.
   *
   * @param user  the user
   * @param group the group
   * @param name  the name
   * @return the statuses
   */
  Statuses offerTaskToGroup(@NonNull User user, @NonNull Group group, @NonNull String name);

  /**
   * Offer extended task to group.
   *
   * @param user           the user
   * @param group          the group
   * @param name           the name
   * @param repetitionType the repetition type
   * @param remindType     the remind type
   * @param importance     the importance
   * @param description    the description
   * @param time           the time
   * @return the statuses
   */
  Statuses offerTaskToGroup(@NonNull User user,
                            @NonNull Group group,
                            @NonNull String name,
                            @NonNull RepetitionTypes repetitionType,
                            @NonNull RemindTypes remindType,
                            @NonNull Importances importance,
                            @NonNull String description,
                            @NonNull Date time);

  /**
   * Change group profile.
   *
   * @param user  the user
   * @param group the group
   * @return the statuses
   */
  Statuses changeGroupProfile(@NonNull User user, @NonNull Group group);

  /**
   * Create group task.
   *
   * @param user     the user
   * @param group    the group
   * @param taskName the task name
   * @param status   the status
   * @return the statuses
   */
  Statuses createGroupTask(@NonNull User user,
                           @NonNull Group group,
                           @NonNull String taskName,
                           @NonNull TaskStatuses status);

  /**
   * Create extended group task.
   *
   * @param user           the user
   * @param group          the group
   * @param taskName       the task name
   * @param status         the status
   * @param repetitionType the repetition type
   * @param remindType     the remind type
   * @param importance     the importance
   * @param description    the description
   * @param time           the time
   * @return the statuses
   */
  Statuses createGroupTask(@NonNull User user,
                           @NonNull Group group,
                           @NonNull String taskName,
                           @NonNull TaskStatuses status,
                           @NonNull RepetitionTypes repetitionType,
                           @NonNull RemindTypes remindType,
                           @NonNull Importances importance,
                           @NonNull String description,
                           @NonNull Date time);

  /**
   * Edit group task.
   *
   * @param user  the user
   * @param group the group
   * @param task  the task
   * @return the statuses
   */
  Statuses editGroupTask(@NonNull User user, @NonNull Group group, @NonNull Task task);

  /**
   * Delete group task.
   *
   * @param user  the user
   * @param group the group
   * @param task  the task
   * @return the statuses
   */
  Statuses deleteGroupTask(@NonNull User user, @NonNull Group group, @NonNull Task task);

  /**
   * Ban user.
   *
   * @param user      the user
   * @param group     the group
   * @param userToBan the user to ban
   * @return the statuses
   */
  Statuses banUser(@NonNull User user, @NonNull Group group, @NonNull User userToBan);

  /**
   * Unban user.
   *
   * @param user       the user
   * @param group      the group
   * @param bannedUser the banned user
   * @return the statuses
   */
  Statuses unbanUser(@NonNull User user, @NonNull Group group, @NonNull User bannedUser);

  /**
   * Accept task.
   *
   * @param user  the user
   * @param group the group
   * @param task  the task
   * @return the statuses
   */
  Statuses acceptTask(@NonNull User user, @NonNull Group group, @NonNull Task task);

  /**
   * Decline task.
   *
   * @param user  the user
   * @param group the group
   * @param task  the task
   * @return the statuses
   */
  Statuses declineTask(@NonNull User user, @NonNull Group group, @NonNull Task task);

  /**
   * Accept user.
   *
   * @param user         the user
   * @param group        the group
   * @param userToAccept the user to accept
   * @return the statuses
   */
  Statuses acceptUser(@NonNull User user, @NonNull Group group, @NonNull User userToAccept);

  /**
   * Decline user.
   *
   * @param user          the user
   * @param group         the group
   * @param userToDecline the user to decline
   * @return the statuses
   */
  Statuses declineUser(@NonNull User user, @NonNull Group group, @NonNull User userToDecline);

  /**
   * Give administrator status.
   *
   * @param user             the user
   * @param group            the group
   * @param newAdministrator the new administrator
   * @return the statuses
   */
  Statuses giveAdministratorStatus(@NonNull User user, @NonNull Group group, @NonNull User newAdministrator);

  /**
   * Take away administrator status.
   *
   * @param user          the user
   * @param group         the group
   * @param administrator the administrator
   * @return the statuses
   */
  Statuses takeAwayAdministratorStatus(@NonNull User user, @NonNull Group group, @NonNull User administrator);

  /**
   * Delete group.
   *
   * @param user  the user
   * @param group the group
   * @return the statuses
   */
  Statuses deleteGroup(@NonNull User user, @NonNull Group group);
}
