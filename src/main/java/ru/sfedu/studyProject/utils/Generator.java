package ru.sfedu.studyProject.utils;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.DataProviders.DataProvider;
import ru.sfedu.studyProject.enums.*;
import ru.sfedu.studyProject.model.Group;
import ru.sfedu.studyProject.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Log4j2
public class Generator {
  private static final Random random = new Random();


  public static User setUp(DataProvider dataProvider, int num) {
    try {

      User user = new User();
      List<User> userList = new ArrayList<>();
      for (int k = 0; k < 1; k++) {
        String userEmail = new Date(System.currentTimeMillis()).toString() + Constants.AT + random.nextInt();
        log.debug(dataProvider.createUser(userEmail,
                Constants.TEST,
                Constants.TEST + num,
                Constants.TEST + PropertyLoader.getProperty(Constants.FIELD_NAME_SURNAME),
                SignUpTypes.SIMPLE));

        var optUser = dataProvider.getUser(userEmail, Constants.TEST);
        user = optUser.get();

        for (int i = 0; i < 5; i++) {
          userEmail = new Date(System.currentTimeMillis()).toString() + Constants.AT + random.nextInt();
          log.debug(dataProvider.createUser(userEmail,
                  Constants.TEST,
                  Constants.TEST + num,
                  Constants.TEST + PropertyLoader.getProperty(Constants.FIELD_NAME_SURNAME),
                  SignUpTypes.SIMPLE));

          optUser = dataProvider.getUser(userEmail, Constants.TEST);
          userList.add(optUser.get());
        }

        for (int i = 0; i < 5; i++) {
          log.debug(dataProvider.createTask(user.getId(), Constants.TEST, TaskStatuses.TEST_TASK_STATUS));
          log.debug(dataProvider.createTask(user.getId(),
                  Constants.TEST,
                  TaskStatuses.TEST_TASK_STATUS,
                  RepetitionTypes.DONT_REPEAT,
                  RemindTypes.DONT_REMIND,
                  Importances.ORDINAL,
                  Constants.TEST + PropertyLoader.getProperty(Constants.FIELD_NAME_DESCRIPTION),
                  new Date(System.currentTimeMillis() + random.nextLong())));
        }

        for (int i = 0; i < 3; i++) {
          log.debug(dataProvider.createGroup(Constants.TEST + num + GroupTypes.PUBLIC.toString(),
                  user.getId(),
                  GroupTypes.PUBLIC));
          log.debug(dataProvider.createGroup(Constants.TEST + num + GroupTypes.PASSWORDED.toString(),
                  user.getId(),
                  GroupTypes.PASSWORDED));
          log.debug(dataProvider.createGroup(Constants.TEST + num + GroupTypes.WITH_CONFIRMATION.toString(),
                  user.getId(),
                  GroupTypes.WITH_CONFIRMATION));
        }
        List<Group> groupList = dataProvider.getUsersGroups(user.getId());

        User finalUser = user;
        groupList.forEach(group -> {
          for (int i = 0; i < random.nextInt() % 5 + 2; i++) {
            log.debug(dataProvider.createTask(finalUser.getId(),
                    group.getId(),
                    Constants.TEST + random.nextInt(),
                    TaskStatuses.TEST_TASK_STATUS));
          }
          dataProvider.getFullGroupList().forEach(log::info);
          for (int i = 0; i < random.nextInt() % 5 + 2; i++) {
            try {
              log.debug(dataProvider.createTask(finalUser.getId(),
                      group.getId(),
                      Constants.TEST + random.nextInt(),
                      TaskStatuses.TEST_TASK_STATUS,
                      RepetitionTypes.DONT_REPEAT,
                      RemindTypes.DONT_REMIND,
                      Importances.ORDINAL,
                      Constants.TEST + PropertyLoader.getProperty(Constants.FIELD_NAME_DESCRIPTION),
                      new Date(System.currentTimeMillis() + random.nextLong())));
            } catch (IOException e) {
              log.error(e);
            }
          }
          userList.forEach(listUser -> {
            if (random.nextInt() % 3 == 0) {
              log.debug(listUser);
              log.debug(group);
              log.debug(dataProvider.addUserToGroup(listUser.getId(), group.getId()));
            }
          });
        });


        optUser = dataProvider.getUser(userEmail, Constants.TEST);
        user = optUser.get();

        num++;
      }
      return user;

    } catch (IOException e) {
      log.error(e);
      return new User();
    }
  }

}
