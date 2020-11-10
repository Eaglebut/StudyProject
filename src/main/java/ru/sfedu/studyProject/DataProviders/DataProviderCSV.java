package ru.sfedu.studyProject.DataProviders;

import lombok.NonNull;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class DataProviderCSV implements DataProvider {
    @Override
    public Statuses createTask(@NonNull User user, @NonNull String taskName, @NonNull TaskStatuses status) {
        return null;
    }

    @Override
    public Statuses createTask(@NonNull User user, @NonNull String taskName, @NonNull TaskStatuses status, @NonNull RepetitionTypes repetitionType, @NonNull RemindTypes remindType, @NonNull Importances importance, @NonNull String description, @NonNull Date time) {
        return null;
    }

    @Override
    public List<Task> getOwnTasks(@NonNull User user) throws NoSuchElementException {
        return null;
    }

    @Override
    public Statuses deleteTask(@NonNull User user, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses editTask(@NonNull User user, @NonNull Task editedTask) {
        return null;
    }

    @Override
    public User getProfileInformation(@NonNull Long userId) throws NoSuchElementException {
        return null;
    }

    @Override
    public User getProfileInformation(@NonNull String login, @NonNull String password) throws NoSuchElementException {
        return null;
    }

    @Override
    public Statuses changeProfileInformation(@NonNull User editedUser) {
        return null;
    }

    @Override
    public Statuses joinTheGroup(@NonNull User user, @NonNull Group group) {
        return null;
    }

    @Override
    public Statuses createGroup(@NonNull String groupName, @NonNull User creator, @NonNull GroupTypes groupType) {
        return null;
    }

    @Override
    public Statuses setGroupPrivate(@NonNull User user, @NonNull Group group) {
        return null;
    }

    @Override
    public Statuses setGroupPasswordedWithConfirmation(@NonNull User user, @NonNull Group group) {
        return null;
    }

    @Override
    public List<Group> searchGroupByName(@NonNull String name) {
        return null;
    }

    @Override
    public Group searchGroupById(@NonNull Long id) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<Group> getFullGroupList() {
        return null;
    }

    @Override
    public List<Task> getGroupTasks(@NonNull Group group) throws NoSuchElementException {
        return null;
    }

    @Override
    public Group getGroupProfile(@NonNull Long groupId) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<Task> getGroupAndOwnTasks(@NonNull User user) throws NoSuchElementException {
        return null;
    }

    @Override
    public Statuses leaveGroup(@NonNull User user, @NonNull Group group) {
        return null;
    }

    @Override
    public List<Task> getGroupTasks(@NonNull User user, @NonNull Group group) throws NoSuchElementException {
        return null;
    }

    @Override
    public Group getGroupProfile(@NonNull User user, @NonNull Long groupId) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<User> getGroupMemberList(@NonNull User user, @NonNull Group group) throws NoSuchElementException {
        return null;
    }

    @Override
    public Statuses offerTaskToGroup(@NonNull User user, @NonNull Group group, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses offerTaskToGroup(@NonNull User user, @NonNull Group group, @NonNull String name) {
        return null;
    }

    @Override
    public Statuses offerTaskToGroup(@NonNull User user, @NonNull Group group, @NonNull String name, @NonNull RepetitionTypes repetitionType, @NonNull RemindTypes remindType, @NonNull Importances importance, @NonNull String description, @NonNull Date time) {
        return null;
    }

    @Override
    public Statuses changeGroupProfile(@NonNull User user, @NonNull Group group) {
        return null;
    }

    @Override
    public Statuses createGroupTask(@NonNull User user, @NonNull Group group, @NonNull String taskName, @NonNull TaskStatuses status) {
        return null;
    }

    @Override
    public Statuses createGroupTask(@NonNull User user, @NonNull Group group, @NonNull String taskName, @NonNull TaskStatuses status, @NonNull RepetitionTypes repetitionType, @NonNull RemindTypes remindType, @NonNull Importances importance, @NonNull String description, @NonNull Date time) {
        return null;
    }

    @Override
    public Statuses editGroupTask(@NonNull User user, @NonNull Group group, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses deleteGroupTask(@NonNull User user, @NonNull Group group, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses banUser(@NonNull User user, @NonNull Group group, @NonNull User userToBan) {
        return null;
    }

    @Override
    public Statuses unbanUser(@NonNull User user, @NonNull Group group, @NonNull User bannedUser) {
        return null;
    }

    @Override
    public Statuses acceptTask(@NonNull User user, @NonNull Group group, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses declineTask(@NonNull User user, @NonNull Group group, @NonNull Task task) {
        return null;
    }

    @Override
    public Statuses acceptUser(@NonNull User user, @NonNull Group group, @NonNull User userToAccept) {
        return null;
    }

    @Override
    public Statuses declineUser(@NonNull User user, @NonNull Group group, @NonNull User userToDecline) {
        return null;
    }

    @Override
    public Statuses giveAdministratorStatus(@NonNull User user, @NonNull Group group, @NonNull User newAdministrator) {
        return null;
    }

    @Override
    public Statuses takeAwayAdministratorStatus(@NonNull User user, @NonNull Group group, @NonNull User administrator) {
        return null;
    }

    @Override
    public Statuses deleteGroup(@NonNull User user, @NonNull Group group) {
        return null;
    }
}
