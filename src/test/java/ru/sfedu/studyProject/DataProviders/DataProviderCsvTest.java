package ru.sfedu.studyProject.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.ExtendedTask;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataProviderCsvTest {

    private static final Logger log = LogManager.getLogger(DataProviderCsvTest.class);

    private static final DataProvider dataProvider = DataProviderCsv.getInstance();

    private User getCorrectTestUser() throws IOException {
        User user = new User();
        user.setId(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        user.setEmail(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_EMAIL));
        user.setPassword(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_PASSWORD));
        user.setName(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_NAME));
        user.setSurname(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SURNAME));
        user.setSignUpType(SignUpTypes.valueOf(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SIGN_UP_TYPE)));
        return user;
    }

    private Task getCorrectTestTask() throws IOException {
        Task task = new Task();
        task.setId(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_ID)));
        task.setName(new Date(System.currentTimeMillis()).toString());
        task.setStatus(TaskStatuses.valueOf(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_STATUS)));
        return task;
    }

    private ExtendedTask getCorrectExtendedTestTask() throws IOException {
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


    @Order(0)
    @Test
    void setCsvFile() throws IOException {
        DataProviderCsv dataProviderCSV = (DataProviderCsv) dataProvider;
        dataProviderCSV.deleteAll();
        User user = getCorrectTestUser();

        Assertions.assertEquals(Statuses.INSERTED, dataProviderCSV.createUser(user.getEmail(),
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
    void createTaskCorrect() throws IOException {
        Optional<User> serverUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(serverUser.isPresent());
        List<Task> taskList = serverUser.get().getTaskList();

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

    @Test
    @Order(2)
    void createExtendedTaskCorrect() throws IOException {
        for (int i = 0; i < 2; i++) {
            Optional<User> serverUser = dataProvider.getUser(getCorrectTestUser().getId());
            ExtendedTask correctExtendedTestTask = getCorrectExtendedTestTask();
            Assertions.assertTrue(serverUser.isPresent());
            Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(serverUser.get().getId(),
                    correctExtendedTestTask.getName(),
                    correctExtendedTestTask.getStatus(),
                    correctExtendedTestTask.getRepetitionType(),
                    correctExtendedTestTask.getRemindType(),
                    correctExtendedTestTask.getImportance(),
                    correctExtendedTestTask.getDescription(),
                    correctExtendedTestTask.getTime()
            ));
            Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
            Assertions.assertTrue(updatedUser.isPresent());
            Assertions.assertEquals(serverUser.get().getTaskList().size() + 1,
                    updatedUser.get().getTaskList().size());
            Assertions.assertTrue(updatedUser.get().getTaskList().stream()
                    .anyMatch(task -> task.getName().equals(correctExtendedTestTask.getName())));
        }
    }

    @Test
    @Order(3)
    void deleteTaskCorrect() throws IOException {
        Optional<User> serverUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(serverUser.isPresent());
        Assertions.assertEquals(Statuses.DELETED,
                dataProvider.deleteTask(serverUser.get().getId(), serverUser.get().getTaskList().get(1).getId()));
        Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(updatedUser.isPresent());
        Assertions.assertEquals(serverUser.get().getTaskList().size() - 1, updatedUser.get().getTaskList().size());
    }
}