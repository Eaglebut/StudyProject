package ru.sfedu.studyProject.curs.DataProviders;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import ru.sfedu.studyProject.curs.enums.Statuses;
import ru.sfedu.studyProject.curs.utils.Generator;

import java.io.IOException;

@Log4j2
class DataProviderJdbcTest extends AbstractDataProviderTest {

  DataProviderJdbcTest() {
    dataProvider = DataProviderJdbc.getInstance();
    userId = 1;
  }

  @BeforeAll
  @Test
  static void test() {
    DataProviderJdbc dataProviderJdbc = DataProviderJdbc.getInstance();
    dataProviderJdbc.dropAll();
    Assertions.assertEquals(Statuses.EXECUTED, dataProviderJdbc.createDatabase());
    user = Generator.setUp(dataProviderJdbc, 1);
  }


  @BeforeEach
  @Override
  void setUser() {
    super.setUser();
  }

  @AfterEach
  void logAll() {
    dataProvider.getFullGroupList().forEach(log::info);
  }


  @Test
  @Override
  void createUserCorrect() {
    super.createUserCorrect();
  }

  @Test
  @Override
  void createUserIncorrect() {
    super.createUserIncorrect();
  }

  @Test
  @Override
  void getUserByIdCorrect() {
    super.getUserByIdCorrect();
  }

  @Test
  @Override
  void getUserByIdIncorrect() {
    super.getUserByIdIncorrect();
  }

  @Test
  @Override
  void getUserByEmailAndPasswordCorrect() {
    super.getUserByEmailAndPasswordCorrect();
  }

  @Test
  @Override
  void getUserByEmailAndPasswordIncorrect() throws IOException {
    super.getUserByEmailAndPasswordIncorrect();
  }

  @Test
  @Override
  void editUserCorrect() {
    super.editUserCorrect();
  }

  @Test
  @Override
  void editUserIncorrect() {
    super.editUserIncorrect();
  }

  @Test
  @Override
  void createTaskCorrect() {
    super.createTaskCorrect();
  }

  @Test
  @Override
  void createTaskIncorrect() {
    super.createTaskIncorrect();
  }

  @Test
  @Override
  void createExtendedTaskCorrect() {
    super.createExtendedTaskCorrect();
  }

  @Test
  @Override
  void createExtendedTaskIncorrect() {
    super.createExtendedTaskIncorrect();
  }

  @Test
  @Override
  void deleteTaskCorrect() {
    super.deleteTaskCorrect();
  }

  @Test
  @Override
  void deleteTaskIncorrect() {
    super.deleteTaskIncorrect();
  }

  @Test
  @Override
  void editTaskCorrect() {
    super.editTaskCorrect();
  }

  @Test
  @Override
  void editTaskIncorrect() {
    super.editTaskIncorrect();
  }

  @Test
  @Override
  void createGroupCorrect() {
    super.createGroupCorrect();
  }

  @Test
  @Override
  void createGroupIncorrect() {
    super.createGroupIncorrect();
  }

  @Test
  @Override
  void getFullGroupList() {
    super.getFullGroupList();
  }

  @Test
  @Override
  void getGroupCorrect() {
    super.getGroupCorrect();
  }

  @Test
  @Override
  void getGroupIncorrect() {
    super.getGroupIncorrect();
  }

  @Test
  @Override
  void addUserToGroupCorrect() {
    super.addUserToGroupCorrect();
  }

  @Test
  @Override
  void addUserToGroupIncorrect() {
    super.addUserToGroupIncorrect();
  }

  @Test
  @Override
  void removeUserFromGroupCorrect() {
    super.removeUserFromGroupCorrect();
  }

  @Test
  @Override
  void removeUserFromGroupIncorrect() {
    super.removeUserFromGroupIncorrect();
  }

  @Test
  @Override
  void changeGroupTypeCorrect() {
    super.changeGroupTypeCorrect();
  }

  @Test
  @Override
  void changeGroupTypeIncorrect() {
    super.changeGroupTypeIncorrect();
  }

  @Test
  @Override
  void searchGroupByNameCorrect() {
    super.searchGroupByNameCorrect();
  }

  @Test
  @Override
  void suggestTaskToGroupCorrect() {
    super.suggestTaskToGroupCorrect();
  }

  @Test
  @Override
  void suggestTaskToGroupIncorrect() {
    super.suggestTaskToGroupIncorrect();
  }

  @Test
  @Override
  void createBasicGroupTaskCorrect() {
    super.createBasicGroupTaskCorrect();
  }

  @Test
  @Override
  void createBasicGroupTaskIncorrect() {
    super.createBasicGroupTaskIncorrect();
  }

  @Test
  @Override
  void createExtendedGroupTaskCorrect() {
    super.createExtendedGroupTaskCorrect();
  }

  @Test
  @Override
  void createExtendedGroupTaskIncorrect() {
    super.createExtendedGroupTaskIncorrect();
  }

  @Test
  @Override
  void updateGroupCorrect() {
    super.updateGroupCorrect();
  }

  @Test
  @Override
  void updateGroupIncorrect() {
    super.updateGroupIncorrect();
  }

  @Test
  @Override
  void setUserRoleCorrect() {
    super.setUserRoleCorrect();
  }

  @Test
  @Override
  void setUserRoleIncorrect() {
    super.setUserRoleIncorrect();
  }

  @Test
  @Override
  void deleteGroupTaskCorrect() {
    super.deleteGroupTaskCorrect();
  }

  @Test
  @Override
  void deleteGroupTaskIncorrect() {
    super.deleteGroupTaskIncorrect();
  }

  @Test
  @Override
  void changeTaskStateCorrect() {
    super.changeTaskStateCorrect();
  }

  @Test
  @Override
  void changeTaskStateIncorrect() {
    super.changeTaskStateIncorrect();
  }

  @Test
  @Override
  void getUserGroupsCorrect() {
    super.getUserGroupsCorrect();
  }

  @Test
  @Override
  void getUserGroupsIncorrect() {
    super.getUserGroupsIncorrect();
  }

  @Test
  @Override
  void deleteGroupCorrect() {
    super.deleteGroupCorrect();
  }

  @Test
  @Override
  void deleteGroupIncorrect() {
    super.deleteGroupIncorrect();
  }

  @Test
  @Override
  void getUserInfoCorrect() throws IOException {
    super.getUserInfoCorrect();
  }

  @Test
  @Override
  void getUserInfoIncorrect() throws IOException {
    super.getUserInfoIncorrect();
  }

  @Test
  @Override
  void getFullUserList() {
    super.getFullUserList();
  }

  @Test
  @Override
  void getAverageGroupSize() {
    super.getAverageGroupSize();
  }

  @Test
  @Override
  void getGroupCountPerType() {
    super.getGroupCountPerType();
  }

  @Test
  @Override
  void getAverageGroupSizeDividedByGroupType() {
    super.getAverageGroupSizeDividedByGroupType();
  }

  @Test
  @Override
  void getAverageTaskPerGroup() {
    super.getAverageTaskPerGroup();
  }

  @Test
  @Override
  void getGroupStatistic() {
    super.getGroupStatistic();
  }

  @Test
  @Override
  void getTaskStatistic() {
    super.getTaskStatistic();
  }
}