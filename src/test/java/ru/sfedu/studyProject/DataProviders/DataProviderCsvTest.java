package ru.sfedu.studyProject.DataProviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.*;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.*;

@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataProviderCsvTest {

    private static final DataProvider dataProvider = DataProviderCsv.getInstance();

    private static User getCorrectTestUser() throws IOException {
        User user = new User();
        user.setId(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        user.setEmail(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_EMAIL));
        user.setPassword(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_PASSWORD));
        user.setName(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_NAME));
        user.setSurname(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SURNAME));
        user.setSignUpType(SignUpTypes.valueOf(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SIGN_UP_TYPE)));
        return user;
    }

    private static Task getCorrectTestTask() throws IOException {
        Task task = new Task();
        task.setId(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_ID)));
        task.setName(new Date(System.currentTimeMillis()).toString());
        task.setStatus(TaskStatuses.valueOf(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_STATUS)));
        return task;
    }

    private static ExtendedTask getCorrectExtendedTestTask() throws IOException {
        ExtendedTask task = new ExtendedTask();
        task.setId(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_ID)));
        task.setName(new Date(System.currentTimeMillis()).toString());
        task.setStatus(TaskStatuses.valueOf(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_STATUS)));
        task.setRepetitionType(RepetitionTypes.DONT_REPEAT);
        task.setRemindType(RemindTypes.DONT_REMIND);
        task.setImportance(Importances.ORDINAL);
        task.setDescription("test");
        task.setTime(new Date(System.currentTimeMillis()));
        return task;
    }

    private User getUser(long id) throws IOException {
        Optional<User> serverUser = dataProvider.getUser(id);
        Assertions.assertTrue(serverUser.isPresent());
        log.debug(serverUser.get());
        return serverUser.get();
    }

    @BeforeAll
    static void setCsvEnv() {
        DataProviderCsv dataProviderCSV = (DataProviderCsv) dataProvider;
        dataProviderCSV.deleteAll();
    }

    @Test
    @Order(0)
    void createUserTest() throws IOException {
        User user = getCorrectTestUser();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createUser(user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getSignUpType()));
    }

    @Test
    @Order(1)
    void getUserByIdCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        if (user.isEmpty()) {
            Assertions.fail(PropertyLoader.getProperty(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser.getEmail(), user.get().getEmail());
        Assertions.assertEquals(correctUser.getPassword(), user.get().getPassword());
        Assertions.assertEquals(correctUser.getName(), user.get().getName());
        Assertions.assertEquals(correctUser.getSurname(), user.get().getSurname());
    }


    @Test
    @Order(1)
    void getUserByIdIncorrect() throws IOException {
        var user = dataProvider.getUser(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_USER_INCORRECT_ID)));
        Assertions.assertFalse(user.isPresent());
    }


    @Test
    @Order(1)
    void getUserByEmailAndPasswordCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_EMAIL),
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_PASSWORD));
        if (user.isEmpty()) {
            Assertions.fail(PropertyLoader.getProperty(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser.getEmail(), user.get().getEmail());
        Assertions.assertEquals(correctUser.getPassword(), user.get().getPassword());
        Assertions.assertEquals(correctUser.getName(), user.get().getName());
        Assertions.assertEquals(correctUser.getSurname(), user.get().getSurname());
    }

    @Test
    @Order(1)
    void getUserByEmailAndPasswordIncorrect() throws IOException {
        var user = dataProvider.getUser(PropertyLoader.getProperty(
                Constants.TEST_USER_INCORRECT_EMAIL),
                PropertyLoader.getProperty(Constants.TEST_USER_INCORRECT_PASSWORD)
        );
        Assertions.assertFalse(user.isPresent());
    }

    @Test
    @Order(2)
    void editUserCorrect() throws IOException {
        Optional<User> user = dataProvider.getUser(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        if (user.isEmpty()) {
            Assertions.fail(PropertyLoader.getProperty(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());

        user.get().setName(new Date(System.currentTimeMillis()).toString());

        Assertions.assertEquals(Statuses.UPDATED, dataProvider.editUser(user.get()));
        var editedUser = dataProvider.getUser((Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID))));

        Assertions.assertTrue(editedUser.isPresent());
        log.debug(editedUser.get());


    }

    @Test
    @Order(2)
    void createTaskCorrect() throws IOException {
        for (int i = 0; i < 2; i++) {
            var serverUser = getUser(0);
            List<Task> taskList = serverUser.getTaskList();

            Task correctTask = getCorrectTestTask();
            Assertions.assertEquals(Statuses.INSERTED,
                    dataProvider.createTask(getCorrectTestUser().getId(),
                            correctTask.getName(),
                            correctTask.getStatus()));
            Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
            Assertions.assertTrue(updatedUser.isPresent());
            Assertions.assertEquals(taskList.size() + 1,
                    updatedUser.get().getTaskList().size());
            Assertions.assertTrue(updatedUser.get().getTaskList().stream()
                    .anyMatch(task -> task.getName().equals(correctTask.getName())));
        }
    }


    @Test
    @Order(2)
    void createExtendedTaskCorrect() throws IOException {
        for (int i = 0; i < 2; i++) {
            var serverUser = getUser(0);
            ExtendedTask correctExtendedTestTask = getCorrectExtendedTestTask();
            Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(serverUser.getId(),
                    correctExtendedTestTask.getName(),
                    correctExtendedTestTask.getStatus(),
                    correctExtendedTestTask.getRepetitionType(),
                    correctExtendedTestTask.getRemindType(),
                    correctExtendedTestTask.getImportance(),
                    correctExtendedTestTask.getDescription(),
                    correctExtendedTestTask.getTime()
            ));
            User updatedUser = getUser(0);
            Assertions.assertEquals(serverUser.getTaskList().size() + 1,
                    updatedUser.getTaskList().size());
            Assertions.assertTrue(updatedUser.getTaskList().stream()
                    .anyMatch(task -> task.getName().equals(correctExtendedTestTask.getName())));
        }
    }

    @Test
    @Order(3)
    void deleteTaskCorrect() throws IOException {
        for (int i = 0; i < 2; i++) {
            var serverUser = getUser(0);
            log.debug(serverUser.getTaskList().get(1));
            Assertions.assertEquals(Statuses.DELETED,
                    dataProvider.deleteTask(serverUser.getId(), serverUser.getTaskList().get(1).getId()));
            Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
            Assertions.assertTrue(updatedUser.isPresent());
            Assertions.assertEquals(serverUser.getTaskList().size() - 1, updatedUser.get().getTaskList().size());
        }
    }


    @Test
    @Order(3)
    void editTaskCorrect() throws IOException {
        User user = getUser(0);
        var task = user.getTaskList().get(0);
        task.setName(new Date(System.currentTimeMillis()).toString() + " edit");
        log.debug(task);
        Assertions.assertEquals(Statuses.UPDATED, dataProvider.editTask(user.getId(), task));

        var extendedTask = user.getTaskList().get(1);
        extendedTask.setName(new Date(System.currentTimeMillis()).toString() + " edit");
        log.debug(task);
        Assertions.assertEquals(Statuses.UPDATED, dataProvider.editTask(user.getId(), extendedTask));
    }

    @Test
    @Order(4)
    void createGroupCorrect() throws IOException {
        User user = getUser(0);
        Group group = getCorrectPublicGroup();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup(group.getName(),
                user.getId(),
                group.getGroupType()));

        group = getCorrectPasswordedGroup();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup(group.getName(),
                user.getId(),
                group.getGroupType()));

        group = getCorrectConfirmationGroup();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createGroup(group.getName(),
                user.getId(),
                group.getGroupType()));
    }

    private Group getCorrectPublicGroup() throws IOException {
        Group group = new Group();
        group.setId(0);
        group.setName("test public Group");
        group.setGroupType(GroupTypes.PUBLIC);
        Map<User, UserRole> userMap = new HashMap<>();
        userMap.put((getUser(0)), UserRole.CREATOR);
        group.setMemberList(userMap);
        return group;
    }

    private Group getCorrectConfirmationGroup() throws IOException {
        Group group = new Group();
        group.setId(2);
        group.setName("test Group with confirmation");
        group.setGroupType(GroupTypes.WITH_CONFIRMATION);
        Map<User, UserRole> userMap = new HashMap<>();
        userMap.put((getUser(0)), UserRole.CREATOR);
        group.setMemberList(userMap);
        return group;
    }

    private PasswordedGroup getCorrectPasswordedGroup() throws IOException {
        PasswordedGroup group = new PasswordedGroup();
        group.setId(1);
        group.setName("test passworded Group");
        group.setGroupType(GroupTypes.PASSWORDED);
        Map<User, UserRole> userMap = new HashMap<>();
        userMap.put((getUser(0)), UserRole.CREATOR);
        group.setMemberList(userMap);
        return group;
    }

    @Test
    @Order(5)
    void getFullGroupList() throws IOException {
        var groupList = dataProvider.getFullGroupList();
        Assertions.assertEquals(3, groupList.size());
        Group group = getCorrectPublicGroup();
        log.debug(groupList.get(0));
        Assertions.assertEquals(group.getId(), groupList.get(0).getId());
        Assertions.assertEquals(group.getName(), groupList.get(0).getName());
        Assertions.assertEquals(group.getGroupType(), groupList.get(0).getGroupType());
        Assertions.assertEquals(group.getMemberList(), groupList.get(0).getMemberList());
    }

    @Test
    @Order(5)
    void getGroupCorrect() throws IOException {
        Group group = getCorrectPublicGroup();
        var optionalGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(optionalGroup.isPresent());
        log.debug(optionalGroup.get());
        Assertions.assertEquals(group.getId(), optionalGroup.get().getId());
        Assertions.assertEquals(group.getName(), optionalGroup.get().getName());
        Assertions.assertEquals(group.getGroupType(), optionalGroup.get().getGroupType());
        Assertions.assertEquals(group.getMemberList(), optionalGroup.get().getMemberList());
    }

    @Test
    @Order(6)
    void addUserToGroupCorrect() throws IOException {
        Group group = getCorrectPublicGroup();
        User user = new User();
        user.setEmail("test2");
        var correctUser = getCorrectTestUser();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createUser(user.getEmail(),
                correctUser.getPassword(),
                correctUser.getName(),
                correctUser.getSurname(),
                correctUser.getSignUpType()));
        var serverUser = dataProvider.getUser(user.getEmail(), correctUser.getPassword());
        Assertions.assertTrue(serverUser.isPresent());
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.addUserToGroup(serverUser.get().getId(), group.getId()));
        var serverGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(serverGroup.isPresent());
        Assertions.assertTrue(serverGroup.get().getMemberList().containsKey(serverUser.get()));
        Assertions.assertEquals(UserRole.MEMBER, serverGroup.get().getMemberList().get(serverUser.get()));
    }

    @Test
    @Order(7)
    void removeUserFromGroupCorrect() throws IOException {
        Group group = getCorrectPublicGroup();
        User user = new User();
        user.setEmail("test2");
        var correctUser = getCorrectTestUser();
        var serverUser = dataProvider.getUser(user.getEmail(), correctUser.getPassword());
        Assertions.assertTrue(serverUser.isPresent());
        Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteUserFromGroup(serverUser.get().getId(), group.getId()));
        var serverGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(serverGroup.isPresent());
        Assertions.assertFalse(serverGroup.get().getMemberList().containsKey(serverUser.get()));
    }

    @Test
    @Order(8)
    void changeGroupTypeCorrect() throws IOException {
        Group group = getCorrectPublicGroup();
        User user = getUser(0);

        Assertions.assertEquals(Statuses.UPDATED, dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.PASSWORDED));
        var serverGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(serverGroup.isPresent());
        Assertions.assertEquals(GroupTypes.PASSWORDED, serverGroup.get().getGroupType());

        Assertions.assertEquals(Statuses.UPDATED, dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.WITH_CONFIRMATION));
        serverGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(serverGroup.isPresent());
        Assertions.assertEquals(GroupTypes.WITH_CONFIRMATION, serverGroup.get().getGroupType());

        Assertions.assertEquals(Statuses.UPDATED, dataProvider.changeGroupType(user.getId(), group.getId(), GroupTypes.PUBLIC));
        serverGroup = dataProvider.getGroup(group.getId());
        Assertions.assertTrue(serverGroup.isPresent());
        Assertions.assertEquals(GroupTypes.PUBLIC, serverGroup.get().getGroupType());
    }

    @Test
    @Order(8)
    void searchGroupByNameCorrect() {
        Assertions.assertEquals(3, dataProvider.searchGroupByName("test").size());
        Assertions.assertEquals(2, dataProvider.searchGroupByName("test p").size());
        Assertions.assertEquals(1, dataProvider.searchGroupByName("test public").size());
    }

    @Test
    @Order(9)
    void suggestTaskToGroupCorrect() throws IOException {
        Group group = getCorrectConfirmationGroup();
        User user = getUser(0);

        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.suggestTask(user.getId(),
                        group.getId(),
                        user.getTaskList().stream().findAny().get().getId()));
    }

    @Test
    @Order(9)
    void createBasicGroupTaskCorrect() throws IOException {
        User user = getUser(0);
        var groupList = dataProvider.getFullGroupList();
        var group = groupList.stream().findAny();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(user.getId(),
                group.get().getId(),
                "group basic task test",
                TaskStatuses.TEST_TASK_STATUS));
    }

    @Test
    @Order(9)
    void createExtendedGroupTaskCorrect() throws IOException {
        User user = getUser(0);
        ExtendedTask task = getCorrectExtendedTestTask();
        var groupList = dataProvider.getFullGroupList();
        var group = groupList.stream().findAny();
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(user.getId(),
                group.get().getId(),
                "group extended task test",
                task.getStatus(),
                task.getRepetitionType(),
                task.getRemindType(),
                task.getImportance(),
                task.getDescription(),
                task.getTime()));
    }

    @Test
    @Order(9)
    void updateGroupCorrect() throws IOException {
        User user = getUser(0);
        var groupList = dataProvider.getFullGroupList();
        var group = groupList.stream().findAny();
        group.get().setName("updated group");
        Assertions.assertEquals(Statuses.UPDATED, dataProvider.updateGroup(user.getId(), group.get()));
    }

    @Test
    @Order(9)
    void setUserRoleCorrect() throws IOException {
        long adminId = 0;
        long userId = 1;
        User user = getUser(userId);
        Optional<Group> group = dataProvider.getFullGroupList().stream().findAny();
        Assertions.assertTrue(group.isPresent());
        if (!group.get().getGroupType().equals(GroupTypes.WITH_CONFIRMATION)) {
            Assertions.assertEquals(Statuses.UPDATED,
                    dataProvider.changeGroupType(adminId, group.get().getId(), GroupTypes.WITH_CONFIRMATION));
        }
        if (!group.get().getMemberList().containsKey(user)) {
            Assertions.assertEquals(Statuses.INSERTED,
                    dataProvider.addUserToGroup(userId, group.get().getId()));
        }
        group = dataProvider.getGroup(group.get().getId());
        Assertions.assertTrue(group.isPresent());
        if (!group.get().getMemberList().get(user).equals(UserRole.REQUIRES_CONFIRMATION)) {
            Assertions.assertEquals(Statuses.UPDATED,
                    dataProvider.setUserRole(adminId,
                            group.get().getId(),
                            user.getId(),
                            UserRole.REQUIRES_CONFIRMATION));
        }
        Assertions.assertEquals(Statuses.UPDATED,
                dataProvider.setUserRole(adminId,
                        group.get().getId(),
                        user.getId(),
                        UserRole.MEMBER));
        Assertions.assertEquals(Statuses.UPDATED,
                dataProvider.setUserRole(adminId,
                        group.get().getId(),
                        user.getId(),
                        UserRole.ADMINISTRATOR));
    }

    @Order(10)
    @Test
    void deleteGroupTaskCorrect() {
        var optGroup = dataProvider.getFullGroupList().stream().findAny();
        Assertions.assertTrue(optGroup.isPresent());
        var optTask = optGroup.get().getTaskList().keySet().stream().findAny();
        log.debug(optGroup.get());
        Assertions.assertTrue(optTask.isPresent());
        Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteTask(0,
                optGroup.get().getId(),
                optTask.get().getId()));
        optGroup = dataProvider.getGroup(optGroup.get().getId());
        Assertions.assertTrue(optGroup.isPresent());
        Assertions.assertFalse(optGroup.get().getTaskList().containsKey(optTask.get()));
    }

    @Order(10)
    @Test
    void changeTaskStateCorrect() {
        var optGroup = dataProvider.getFullGroupList().stream().findAny();
        Assertions.assertTrue(optGroup.isPresent());
        var optTask = optGroup.get().getTaskList().keySet().stream().findAny();
        log.debug(optGroup.get());
        Assertions.assertTrue(optTask.isPresent());
        Assertions.assertEquals(Statuses.UPDATED, dataProvider.changeTaskState(0,
                optGroup.get().getId(),
                optTask.get().getId(),
                TaskState.SUGGESTED));
    }


    @Order(10)
    @Test
    void getUserGroupsCorrect() {
        var groupList = dataProvider.getFullGroupList();
        var usersGroupList = dataProvider.getUsersGroups(0);
        Assertions.assertEquals(groupList, usersGroupList);
    }

    @Order(11)
    @Test
    void deleteGroupCorrect() {
        var optGroup = dataProvider.getFullGroupList().stream().findAny();
        Assertions.assertTrue(optGroup.isPresent());
        log.debug(optGroup.get());
        Assertions.assertEquals(Statuses.DELETED, dataProvider.deleteGroup(0, optGroup.get().getId()));
    }
}