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
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public <T> void insertIntoCsv(T object, boolean overwrite) throws IOException {
        insertIntoCsv(Collections.singletonList(object), overwrite);
    }

    public <T> void insertIntoCsv(List<T> objectList, boolean overwrite) throws IOException {
        Optional<T> tOptional = objectList.stream().findAny();
        Class<?> tClass;
        if (tOptional.isPresent()) {
            tClass = tOptional.get().getClass();
        } else {
            return;
        }
        List<T> tList;
        if (!overwrite) {
            tList = (List<T>) getFromCsv(tClass);
            tList.addAll(objectList);
        } else {
            tList = objectList;
        }
        CSVWriter csvWriter = getCsvWriter(tClass);
        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                .withApplyQuotesToAll(false)
                .build();
        try {
            beanToCsv.write(tList);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(e);
        }
        csvWriter.close();
    }

    private <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
        FileWriter writer;
        String filename = ConfigurationUtil.getConfigurationEntry(Constants.CSV_PATH) +
                tClass.getSimpleName().toLowerCase() +
                ConfigurationUtil.getConfigurationEntry(Constants.CSV_EXTENSION);
        log.debug(filename);
        writer = new FileWriter(filename);
        return new CSVWriter(writer);
    }


    private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
        File file = new File(ConfigurationUtil.getConfigurationEntry(Constants.CSV_PATH) +
                tClass.getSimpleName().toLowerCase() +
                ConfigurationUtil.getConfigurationEntry(Constants.CSV_EXTENSION));

        if (!file.exists()) {
            if (!file.createNewFile())
                throw new IOException(
                        String.format(
                                ConfigurationUtil.getConfigurationEntry(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                                file.getName()));
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return new CSVReader(bufferedReader);
    }

    private <T> List<T> getFromCsv(Class<T> tClass) throws IOException {
        List<T> tList;
        try {
            CSVReader csvReader = getCsvReader(tClass);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                    .withType(tClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            tList = csvToBean.parse();
            csvReader.close();
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
        return tList;
    }


    private Optional<User> getUserProfile(long userId) {
        try {
            List<User> userList = getFromCsv(User.class);
            return userList
                    .stream()
                    .findFirst()
                    .filter(user -> user.getId() == userId);

        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private Optional<User> getUserProfile(String email,
                                          String password) {
        try {
            List<User> userList = getFromCsv(User.class);
            return userList
                    .stream()
                    .findFirst()
                    .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password));
        } catch (IOException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    //TODO add parsing template
    private List<ModificationRecord> getHistoryList(User user) {
        try {
            List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
            return historyList
                    .stream()
                    .filter(modificationRecord -> user
                            .getHistoryList()
                            .stream()
                            .findFirst()
                            .filter(userModificationRecord ->
                                    userModificationRecord.getId() == modificationRecord.getId())
                            .isPresent())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    //TODO add parsing template
    private List<ModificationRecord> getHistoryList(Task task) {
        try {
            List<ModificationRecord> historyList = getFromCsv(ModificationRecord.class);
            return historyList
                    .stream()
                    .filter(modificationRecord -> task
                            .getHistoryList()
                            .stream()
                            .findFirst()
                            .filter(taskModificationRecord ->
                                    taskModificationRecord.getId() == modificationRecord.getId())
                            .isPresent())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }


    @Override
    public Optional<User> getUser(@NonNull long userId) {
        Optional<User> optionalUser = getUserProfile(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setTaskList(getTasks(user));
            user.setHistoryList(getHistoryList(user));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUser(@NonNull String email,
                                  @NonNull String password) {
        Optional<User> optionalUser = getUserProfile(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setTaskList(getTasks(user));
            user.setHistoryList(getHistoryList(user));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> getTasks(@NonNull User user) throws NoSuchElementException {
        try {
            List<Task> taskList = getFromCsv(Task.class);
            List<Task> usersTaskList = user.getTaskList();
            List<Task> usersFilledTaskList = new ArrayList<>();
            usersTaskList.forEach(usersTask -> taskList.forEach(task -> {
                if (usersTask.getId() == task.getId()) {
                    task.setHistoryList(getHistoryList(task));
                    usersFilledTaskList.add(task);
                }
            }));
            return usersFilledTaskList;
        } catch (IOException e) {
            log.error(e);
            return null;
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
