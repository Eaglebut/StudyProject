package ru.sfedu.studyProject.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.SignUpTypes;
import ru.sfedu.studyProject.enums.TaskStatuses;
import ru.sfedu.studyProject.enums.TaskTypes;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataProviderCSVTest {

    private static final Logger log = LogManager.getLogger(DataProviderCSVTest.class);

    DataProvider dataProvider = DataProviderCSV.getInstance();


    @BeforeEach
    void init() {

    }

    private User getCorrectTestUser() throws IOException {
        User user = new User();
        user.setId(Integer.parseInt(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_ID)));
        user.setName(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_NAME));
        user.setSurname(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_SURNAME));
        user.setEmail(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_EMAIL));
        user.setPassword(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_PASSWORD));
        user.setSignUpType(SignUpTypes.valueOf(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_SIGN_UP_TYPE)));
        user.setToken(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_TOKEN));
        user.setTaskList(getCorrectTestTaskList());
        user.setHistoryList(new ArrayList<>());
        try {
            user.setCreated(new SimpleDateFormat(Constants.DATE_FORMAT).parse(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        return user;
    }

    private List<Task> getCorrectTestTaskList() throws IOException {
        List<Task> taskList = new ArrayList<>();
        Task correctTask = new Task();
        correctTask.setId(Integer.parseInt(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK1_CORRECT_ID)));
        correctTask.setName(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK1_CORRECT_NAME));
        correctTask.setStatus(TaskStatuses
                .valueOf(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK1_CORRECT_STATUS)));
        correctTask.setTaskType(TaskTypes
                .valueOf(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK1_CORRECT_TASK_TYPE)));
        correctTask.setHistoryList(getCorrectTestHistoryList());
        try {
            correctTask.setCreated(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK1_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        taskList.add(correctTask);

        Task correctTask1 = new Task();
        correctTask1.setId(Integer.parseInt(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK2_CORRECT_ID)));
        correctTask1.setName(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK2_CORRECT_NAME));
        correctTask1.setStatus(TaskStatuses
                .valueOf(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK2_CORRECT_STATUS)));
        correctTask1.setTaskType(TaskTypes
                .valueOf(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK2_CORRECT_TASK_TYPE)));
        correctTask1.setHistoryList(new ArrayList<>());
        try {
            correctTask1.setCreated(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(ConfigurationUtil.getConfigurationEntry(Constants.TEST_TASK2_CORRECT_CREATED)));
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
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_MODIFICATION_RECORD_CORRECT_ID)));
        try {
            modificationRecord.setChangedDate(
                    new SimpleDateFormat(Constants.DATE_FORMAT)
                            .parse(ConfigurationUtil
                                    .getConfigurationEntry(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_DATE)));
        } catch (ParseException e) {
            log.error(e);
        }
        modificationRecord.setChangedValueName(ConfigurationUtil.
                getConfigurationEntry(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_VALUE_NAME));
        modificationRecord.setChangedValue(ConfigurationUtil.getConfigurationEntry(Constants.TEST_MODIFICATION_RECORD_CORRECT_CHANGED_VALUE));
        historyList.add(modificationRecord);
        return historyList;
    }


    @Order(0)
    @Test
    void setCsvFile() {
        try {
            boolean overwrite = true;

            User user = getCorrectTestUser();
            DataProviderCSV dataProviderCSV = (DataProviderCSV) dataProvider;
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
        } catch (IOException e) {
            log.error(e);
        }
    }


    @Test
    @Order(1)
    void getProfileInformationByIdCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(Integer.parseInt(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_ID)));
        if (!user.isPresent()) {
            Assertions.fail(ConfigurationUtil.getConfigurationEntry(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser, user.get());
    }


    @Test
    @Order(2)
    void getProfileInformationByEmailAndPasswordCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        Optional<User> user = dataProvider.getUser(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_EMAIL),
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_PASSWORD));
        if (!user.isPresent()) {
            Assertions.fail(ConfigurationUtil.getConfigurationEntry(Constants.MESSAGE_NULL_METHOD));
        }
        log.debug(user.get());
        Assertions.assertEquals(correctUser, user.get());
    }

    @Test
    @Order(3)
    void testGenericGetHistory() throws IOException {
        List<Task> taskList = getCorrectTestTaskList();
        Task task = taskList.get(0);
        DataProviderCSV dataProviderCSV = (DataProviderCSV) dataProvider;
        dataProviderCSV.getHistoryList(task);
    }

}