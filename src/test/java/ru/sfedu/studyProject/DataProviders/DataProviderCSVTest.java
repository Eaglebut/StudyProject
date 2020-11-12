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
import ru.sfedu.studyProject.enums.Statuses;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;


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

        Task task = new Task();
        task.setId(1);
        Task task1 = new Task();
        task1.setId(2);
        user.setTaskList(Arrays.asList(task, task1));

        ModificationRecord modificationRecord = new ModificationRecord();
        modificationRecord.setId(1);
        ModificationRecord modificationRecord1 = new ModificationRecord();
        modificationRecord1.setId(2);

        user.setHistoryList(Arrays.asList(modificationRecord, modificationRecord1));
        try {
            user.setCreated(new SimpleDateFormat(Constants.DATE_FORMAT).parse(ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_CREATED)));
        } catch (ParseException e) {
            log.error(e);
        }
        return user;
    }

    @Test
    @Order(1)
    void getProfileInformationByIdCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        User user = dataProvider.getProfileInformation(Integer.parseInt(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_ID)));
        Assertions.assertEquals(correctUser, user);
    }

    @Test
    @Order(2)
    void getProfileInformationByEmailAndPasswordCorrect() throws IOException {
        User correctUser = getCorrectTestUser();
        User user = dataProvider.getProfileInformation(
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_EMAIL),
                ConfigurationUtil.getConfigurationEntry(Constants.TEST_USER_CORRECT_PASSWORD));
        Assertions.assertEquals(correctUser, user);
    }


    @Test
    void insertIntoUserList() throws IOException {
        User user = getCorrectTestUser();
        DataProviderCSV dataProviderCSV = (DataProviderCSV) dataProvider;
        Assertions.assertEquals(Statuses.INSERTED, dataProviderCSV.insertIntoUserList(user));
    }


}