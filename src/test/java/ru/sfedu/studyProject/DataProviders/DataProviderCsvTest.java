package ru.sfedu.studyProject.DataProviders;

import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.SignUpTypes;
import ru.sfedu.studyProject.enums.TaskStatuses;
import ru.sfedu.studyProject.enums.TaskTypes;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        historyList.add(modificationRecord);
        return historyList;
    }


    @Order(0)
    @Test
    void setCsvFile() throws IOException {
        boolean overwrite = true;
        User user = getCorrectTestUser();
        DataProviderCsv dataProviderCSV = (DataProviderCsv) dataProvider;
        dataProviderCSV.insertIntoCsv(user, overwrite);
        dataProviderCSV.insertIntoCsv(user.getTaskList(), overwrite);
        dataProviderCSV.insertIntoCsv(user.getHistoryList(), overwrite);
        user.getTaskList().forEach(task -> {
            try {
                dataProviderCSV.insertIntoCsv(task.getHistoryList(), overwrite);
            } catch (IOException e) {
                    log.error(e);
                }
            });

    }


    @Test
    @Order(1)
    void getProfileInformationByIdCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(Integer.parseInt(
                PropertyLoader.getProperty(Constants.TEST_USER_CORRECT_ID)));
        if (!user.isPresent()) {
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
        if (!user.isPresent()) {
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


}