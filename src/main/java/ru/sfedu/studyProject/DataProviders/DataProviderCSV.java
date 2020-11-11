package ru.sfedu.studyProject.DataProviders;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.GroupTypes;
import ru.sfedu.studyProject.enums.Importances;
import ru.sfedu.studyProject.enums.RemindTypes;
import ru.sfedu.studyProject.enums.RepetitionTypes;
import ru.sfedu.studyProject.enums.Statuses;
import ru.sfedu.studyProject.enums.TaskStatuses;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class DataProviderCSV implements DataProvider {

    private static final Logger log = LogManager.getLogger(DataProviderCSV.class);
    private static DataProvider INSTANCE = null;


    private DataProviderCSV() {
    }

    public static DataProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataProviderCSV();
        }
        return INSTANCE;
    }

    public Statuses insertIntoUserList(User user) throws IOException {
        FileWriter writer;
        writer = new FileWriter(ConfigurationUtil.getConfigurationEntry(Constants.CSV_PATH) +
                User.class.getSimpleName().toLowerCase() +
                ConfigurationUtil.getConfigurationEntry(Constants.CSV_EXTENSION));
        CSVWriter csvWriter = new CSVWriter(writer);
        StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(csvWriter)
                .withApplyQuotesToAll(false)
                .build();
        try {
            beanToCsv.write(user);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(e);
            csvWriter.close();
            writer.close();
            return Statuses.FAILED;
        }
        csvWriter.close();
        writer.close();
        return Statuses.INSERTED;
    }

    private <T> CsvToBean<T> getCsvToBean(Class<T> tClass) throws IOException {
        File file = new File(ConfigurationUtil.getConfigurationEntry(Constants.CSV_PATH) +
                User.class.getSimpleName().toLowerCase() +
                ConfigurationUtil.getConfigurationEntry(Constants.CSV_EXTENSION));

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        CSVReader csvReader = new CSVReader(bufferedReader);
        return new CsvToBeanBuilder<T>(csvReader)
                .withType(tClass)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
    }

    private <T> List<T> getFromCsv(Class<T> tClass) throws IOException {
        List<T> tList;
        try {
            CsvToBean<T> csvToBean = getCsvToBean(tClass);
            tList = csvToBean.parse();
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
        return tList;
    }



    //TODO Add parsing task list and history list
    @Override
    public User getProfileInformation(long userId) throws NoSuchElementException, IOException {
        try {
            List<User> userList = getFromCsv(User.class);
            Optional<User> matchUser = userList
                    .stream()
                    .findFirst()
                    .filter(user -> user.getId() == userId);
            if (matchUser.isPresent()) {
                return matchUser.get();
            } else {
                throw new NoSuchElementException(
                        String.format(
                                ConfigurationUtil.getConfigurationEntry(
                                        Constants.EXCEPTION_NOT_FOUNDED_BY_ID_MESSAGE),
                                User.class.getSimpleName(),
                                userId
                        ));
            }
        } catch (IOException e) {
            log.error(e);
            throw new IOException(e);
        }
    }

    //TODO Add parsing task list and history list
    @Override
    public User getProfileInformation(@NonNull String email,
                                      @NonNull String password) throws NoSuchElementException, IOException {
        try {
            List<User> userList = getFromCsv(User.class);
            Optional<User> matchUser = userList
                    .stream()
                    .findFirst()
                    .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password));
            if (matchUser.isPresent()) {
                return matchUser.get();
            } else {
                throw new NoSuchElementException(
                        String.format(
                                ConfigurationUtil.getConfigurationEntry(
                                        Constants.EXCEPTION_NOT_FOUNDED_BY_EMAIL_AND_PASSWORD_MESSAGE),
                                User.class.getSimpleName(),
                                email,
                                password
                        ));
            }
        } catch (IOException e) {
            log.error(e);
            throw new IOException(e);
        }
    }

    //TODO
    @Override
    public Statuses createTask(@NonNull User user,
                               @NonNull String taskName,
                               @NonNull TaskStatuses status) {
        return null;
    }

    //TODO
    @Override
    public Statuses createTask(@NonNull User user,
                               @NonNull String taskName,
                               @NonNull TaskStatuses status,
                               @NonNull RepetitionTypes repetitionType,
                               @NonNull RemindTypes remindType,
                               @NonNull Importances importance,
                               @NonNull String description,
                               @NonNull Date time) {
        return null;
    }

    //TODO
    @Override
    public List<Task> getOwnTasks(@NonNull User user) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public Statuses deleteTask(@NonNull User user,
                               @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses editTask(@NonNull User user,
                             @NonNull Task editedTask) {
        return null;
    }


    //TODO
    @Override
    public Statuses changeProfileInformation(@NonNull User editedUser) {
        return null;
    }

    //TODO
    @Override
    public Statuses joinTheGroup(@NonNull User user,
                                 @NonNull Group group) {
        return null;
    }

    //TODO
    @Override
    public Statuses createGroup(@NonNull String groupName,
                                @NonNull User creator,
                                @NonNull GroupTypes groupType) {
        return null;
    }

    //TODO
    @Override
    public Statuses setGroupPrivate(@NonNull User user,
                                    @NonNull Group group) {
        return null;
    }

    //TODO
    @Override
    public Statuses setGroupPasswordedWithConfirmation(@NonNull User user,
                                                       @NonNull Group group) {
        return null;
    }

    //TODO
    @Override
    public List<Group> searchGroupByName(@NonNull String name) {
        return null;
    }

    //TODO
    @Override
    public Group searchGroupById(long id) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public List<Group> getFullGroupList() {
        return null;
    }

    //TODO
    @Override
    public List<Task> getGroupTasks(@NonNull Group group) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public Group getGroupProfile(long groupId) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public List<Task> getGroupAndOwnTasks(@NonNull User user) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public Statuses leaveGroup(@NonNull User user,
                               @NonNull Group group) {
        return null;
    }

    //TODO
    @Override
    public List<Task> getGroupTasks(@NonNull User user,
                                    @NonNull Group group) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public Group getGroupProfile(@NonNull User user,
                                 long groupId) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public List<User> getGroupMemberList(@NonNull User user,
                                         @NonNull Group group) throws NoSuchElementException {
        return null;
    }

    //TODO
    @Override
    public Statuses offerTaskToGroup(@NonNull User user,
                                     @NonNull Group group,
                                     @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses offerTaskToGroup(@NonNull User user,
                                     @NonNull Group group,
                                     @NonNull String name) {
        return null;
    }

    //TODO
    @Override
    public Statuses offerTaskToGroup(@NonNull User user,
                                     @NonNull Group group,
                                     @NonNull String name,
                                     @NonNull RepetitionTypes repetitionType,
                                     @NonNull RemindTypes remindType,
                                     @NonNull Importances importance,
                                     @NonNull String description,
                                     @NonNull Date time) {
        return null;
    }

    //TODO
    @Override
    public Statuses changeGroupProfile(@NonNull User user,
                                       @NonNull Group group) {
        return null;
    }

    //TODO
    @Override
    public Statuses createGroupTask(@NonNull User user,
                                    @NonNull Group group,
                                    @NonNull String taskName,
                                    @NonNull TaskStatuses status) {
        return null;
    }

    //TODO
    @Override
    public Statuses createGroupTask(@NonNull User user,
                                    @NonNull Group group,
                                    @NonNull String taskName,
                                    @NonNull TaskStatuses status,
                                    @NonNull RepetitionTypes repetitionType,
                                    @NonNull RemindTypes remindType,
                                    @NonNull Importances importance,
                                    @NonNull String description,
                                    @NonNull Date time) {
        return null;
    }

    //TODO
    @Override
    public Statuses editGroupTask(@NonNull User user,
                                  @NonNull Group group,
                                  @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses deleteGroupTask(@NonNull User user,
                                    @NonNull Group group,
                                    @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses banUser(@NonNull User user,
                            @NonNull Group group,
                            @NonNull User userToBan) {
        return null;
    }

    //TODO
    @Override
    public Statuses unbanUser(@NonNull User user,
                              @NonNull Group group,
                              @NonNull User bannedUser) {
        return null;
    }

    //TODO
    @Override
    public Statuses acceptTask(@NonNull User user,
                               @NonNull Group group,
                               @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses declineTask(@NonNull User user,
                                @NonNull Group group,
                                @NonNull Task task) {
        return null;
    }

    //TODO
    @Override
    public Statuses acceptUser(@NonNull User user,
                               @NonNull Group group,
                               @NonNull User userToAccept) {
        return null;
    }

    //TODO
    @Override
    public Statuses declineUser(@NonNull User user,
                                @NonNull Group group,
                                @NonNull User userToDecline) {
        return null;
    }

    //TODO
    @Override
    public Statuses giveAdministratorStatus(@NonNull User user,
                                            @NonNull Group group,
                                            @NonNull User newAdministrator) {
        return null;
    }

    //TODO
    @Override
    public Statuses takeAwayAdministratorStatus(@NonNull User user,
                                                @NonNull Group group,
                                                @NonNull User administrator) {
        return null;
    }

    //TODO
    @Override
    public Statuses deleteGroup(@NonNull User user,
                                @NonNull Group group) {
        return null;
    }
}
