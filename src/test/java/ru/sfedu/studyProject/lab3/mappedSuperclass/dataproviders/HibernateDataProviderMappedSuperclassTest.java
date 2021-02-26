package ru.sfedu.studyProject.lab3.mappedSuperclass.dataproviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.User;
import ru.sfedu.studyProject.utils.Statuses;

import java.util.Date;

@Log4j2
class HibernateDataProviderMappedSuperclassTest {

  DataProvider dataProvider = new HibernateDataProvider();

  @Test
  void saveUser() {
    User user = new User();
    user.setName("testName");
    user.setSurname("testSurname");
    user.setCreated(new Date(System.currentTimeMillis()));
    Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
    Assertions.assertTrue(user.getId() != 0);
    log.info(user);
  }

  @Test
  void getUser() {
    User user = new User();
    user.setName("testName");
    user.setSurname("testSurname");
    user.setCreated(new Date(System.currentTimeMillis()));
    Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
    Assertions.assertTrue(user.getId() != 0);
    var optUser = dataProvider.getUser(user.getId());
    Assertions.assertTrue(optUser.isPresent());
    Assertions.assertEquals(user, optUser.get());
    log.info(optUser);
  }


  @Test
  void deleteUser() {
    User user = new User();
    user.setName("testName");
    user.setSurname("testSurname");
    user.setCreated(new Date(System.currentTimeMillis()));
    Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.saveUser(user));
    Assertions.assertTrue(user.getId() != 0);
    log.info(user);
    Assertions.assertEquals(Statuses.SUCCESSFUL, dataProvider.deleteUser(user.getId()));
    Assertions.assertTrue(dataProvider.getUser(user.getId()).isEmpty());
  }
}