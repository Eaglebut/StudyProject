package ru.sfedu.studyProject;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.DataProviders.DataProvider;
import ru.sfedu.studyProject.DataProviders.DataProviderJdbc;
import ru.sfedu.studyProject.enums.SignUpTypes;
import ru.sfedu.studyProject.enums.Statuses;

@Log4j2
class ClientTest {

  DataProvider dataProvider = DataProviderJdbc.getInstance();

  @BeforeAll
  static void setUp() {
    DataProviderJdbc dataProviderJdbc = DataProviderJdbc.getInstance();
    dataProviderJdbc.createDatabase();
  }

  @Test
  void createUserCorrect() {
    Assertions.assertEquals(Statuses.INSERTED,
            dataProvider.createUser("testUser",
                    "testPassword",
                    "testName",
                    "testSurname",
                    SignUpTypes.SIMPLE));
    var optUser = dataProvider.getUser("testUser", "testPassword");
    Assertions.assertTrue(optUser.isPresent());
    var user = optUser.get();
    log.debug(optUser.get());
    log.debug(dataProvider.getUser(optUser.get().getId()));
    user.setName("edited");
    Assertions.assertEquals(Statuses.UPDATED, dataProvider.editUser(user));
    log.debug(dataProvider.getUser(optUser.get().getId()));
  }


}