package ru.sfedu.studyProject.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataProviderCsvTest {

    private static final Logger log = LogManager.getLogger(DataProviderCsvTest.class);

    private static final DataProvider dataProvider = DataProviderCsv.getInstance();



    private User getCorrectTestUser() throws IOException {
        User user = new User();
        user.setId(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        user.setName(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_NAME));
        user.setSurname(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SURNAME));
        user.setEmail(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_EMAIL));
        user.setPassword(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_PASSWORD));
        user.setSignUpType(SignUpTypes.valueOf(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_SIGN_UP_TYPE)));
        user.setToken(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_TOKEN));
        user.setTaskList(getCorrectTestTaskList());
        user.setHistoryList(new ArrayList<>());
        try {
            user.setCreated(new SimpleDateFormat(Constants.DATE_FORMAT)
                    .parse(PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        return user;
    }


    private List<Task> getCorrectTestTaskList() throws IOException {
        List<Task> taskList = new ArrayList<>();
        Task correctTask = new Task();
        correctTask.setId(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_ID)));
        correctTask.setName(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_NAME));
        correctTask.setStatus(TaskStatuses
                .valueOf(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_STATUS)));
        correctTask.setTaskType(TaskTypes
                .valueOf(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_TASK_TYPE)));
        correctTask.setHistoryList(getCorrectTestHistoryList());
        try {
            correctTask.setCreated(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(PropertyLoader.getProperty(Constants.TEST_TASK1_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        taskList.add(correctTask);

        Task correctTask1 = new Task();
        correctTask1.setId(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_TASK2_CORRECT_ID)));
        correctTask1.setName(PropertyLoader.getProperty(Constants.TEST_TASK2_CORRECT_NAME));
        correctTask1.setStatus(TaskStatuses
                .valueOf(PropertyLoader.getProperty(Constants.TEST_TASK2_CORRECT_STATUS)));
        correctTask1.setTaskType(TaskTypes
                .valueOf(PropertyLoader.getProperty(Constants.TEST_TASK2_CORRECT_TASK_TYPE)));
        correctTask1.setHistoryList(new ArrayList<>());
        try {
            correctTask1.setCreated(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(PropertyLoader.getProperty(Constants.TEST_TASK2_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        taskList.add(correctTask1);
        return taskList;
    }


    private List<ModificationRecord> getCorrectTestHistoryList() throws IOException {
        List<ModificationRecord> historyList = new ArrayList<>();

        ModificationRecord modificationRecord = new ModificationRecord();
        modificationRecord.setId(Long.parseLong(
                PropertyLoader.getProperty(Constants.TEST_MODIFICATION_RECORD_CORRECT_ID)));
        try {
            modificationRecord.setChangedDate(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(PropertyLoader
                                    .getProperty(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_DATE)));
        } catch (ParseException e) {
            log.error(e);
        }
        modificationRecord.setChangedValueName(PropertyLoader.
                getProperty(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_VALUE_NAME));
        modificationRecord.setChangedValue(
                PropertyLoader.getProperty(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_VALUE));
        modificationRecord.setOperationType(OperationType.ADD);
        historyList.add(modificationRecord);
        return historyList;
    }


    @Order(0)
    @Test
    void setCsvFile() throws IOException {
        boolean overwrite = true;
        User user = getCorrectTestUser();
        DataProviderCsv dataProviderCSV = (DataProviderCsv) dataProvider;
        dataProviderCSV.deleteAll();
        dataProviderCSV.insertIntoCsv(Collections.singletonList(user), overwrite);
        dataProviderCSV.insertIntoCsv(user.getTaskList(), overwrite);
        dataProviderCSV.insertIntoCsv(user.getHistoryList(), overwrite);
        user.getTaskList().forEach(task -> {
            try {
                dataProviderCSV.insertIntoCsv(task.getHistoryList(), overwrite);
            } catch (IOException e) {
                log.error(e);
            }
        });

        user.setTaskList(new ArrayList<>());
        user.setHistoryList(new ArrayList<>());
        user.setEmail("test");
        Assertions.assertEquals(dataProviderCSV.createUser(user), Statuses.INSERTED);
    }


    @Test
    @Order(1)
    void getProfileInformationByIdCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        if (user.isEmpty()) {
            Assertions.fail(PropertyLoader.getProperty(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser, user.get());
    }

    @Test
    @Order(1)
    void getProfileInformationByIdIncorrect() throws IOException {
        var user = dataProvider.getUser(Long.parseLong(PropertyLoader.getProperty(Constants.TEST_USER_INCORRECT_ID)));
        Assertions.assertFalse(user.isPresent());
    }


    @Test
    @Order(1)
    void getProfileInformationByEmailAndPasswordCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_EMAIL),
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_PASSWORD));
        if (user.isEmpty()) {
            Assertions.fail(PropertyLoader.getProperty(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser, user.get());
    }

    @Test
    @Order(1)
    void getProfileInformationByEmailAndPasswordIncorrect() throws IOException {
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
        Date taskName = new Date(System.currentTimeMillis());
        Task correctTask = getCorrectTestTaskList().get(0);
        Assertions.assertEquals(Statuses.INSERTED,
                dataProvider.createTask(getCorrectTestUser(),
                        taskName.toString(),
                        correctTask.getStatus()));
        Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(updatedUser.isPresent());
        Assertions.assertEquals(taskList.size() + 1,
                updatedUser.get().getTaskList().size());
        Assertions.assertTrue(updatedUser.get().getTaskList().stream()
                .anyMatch(task -> task.getName().equals(taskName.toString())));
    }

    @Test
    void createExtendedTaskCorrect() throws IOException {
        Optional<User> serverUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(serverUser.isPresent());
        Assertions.assertEquals(Statuses.INSERTED, dataProvider.createTask(serverUser.get(),
                "test",
                TaskStatuses.TEST_TASK_STATUS,
                RepetitionTypes.DONT_REPEAT,
                RemindTypes.DONT_REMIND,
                Importances.ORDINAL,
                "test",
                new Date(System.currentTimeMillis())
        ));
        Optional<User> updatedUser = dataProvider.getUser(getCorrectTestUser().getId());
        Assertions.assertTrue(updatedUser.isPresent());
        Assertions.assertEquals(serverUser.get().getTaskList().size(),
                updatedUser.get().getTaskList().size());
        Assertions.assertTrue(updatedUser.get().getTaskList().stream()
                .anyMatch(task -> task.getName().equals("test")));
    }

}