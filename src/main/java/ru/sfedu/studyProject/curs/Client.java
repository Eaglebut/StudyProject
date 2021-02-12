package ru.sfedu.studyProject.curs;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.curs.DataProviders.DataProvider;
import ru.sfedu.studyProject.curs.DataProviders.DataProviderCsv;
import ru.sfedu.studyProject.curs.DataProviders.DataProviderJdbc;
import ru.sfedu.studyProject.curs.DataProviders.DataProviderXml;
import ru.sfedu.studyProject.curs.utils.Generator;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;

/**
 * The type Main.
 */
@Log4j2
public class Client {


  private static DataProvider getDataProvider(String msg) throws IOException {
    switch (msg) {
      case Constants.DATA_PROVIDER_CSV:
        DataProviderCsv.getInstance().deleteAll();
        DataProviderCsv.getInstance().createFiles();
        return DataProviderCsv.getInstance();
      case Constants.DATA_PROVIDER_XML:
        DataProviderXml.getInstance().deleteAll();
        DataProviderXml.getInstance().createFiles();
        return DataProviderXml.getInstance();
      case Constants.DATA_PROVIDER_JDBC:
        DataProviderJdbc.getInstance().dropAll();
        DataProviderJdbc.getInstance().createDatabase();
        return DataProviderJdbc.getInstance();
    }
    throw new NullPointerException(PropertyLoader.getProperty(Constants.EXCEPTION_WRONG_DATA));
  }


  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      if (args.length < 2) {
        log.error(PropertyLoader.getProperty(Constants.EXCEPTION_NOT_ENOUGH));
        return;
      }
      DataProvider dataProvider = getDataProvider(args[0]);
      Generator.setUp(dataProvider, 1);
      switch (args[1]) {
        case Constants.GET_TASK_STATISTIC:
          System.out.println(dataProvider.getTaskStatistic());
          return;
        case Constants.GET_GROUP_STATISTIC:
          System.out.println(dataProvider.getGroupsStatistic());
          return;
        case Constants.GET_USER_INFO:
          if (args.length < 3) {
            log.error(PropertyLoader.getProperty(Constants.EXCEPTION_NOT_ENOUGH));
            return;
          }
          System.out.println(dataProvider.getUserInfo(Long.parseLong(args[2])));
          return;
        default:
          log.error(PropertyLoader.getProperty(Constants.EXCEPTION_WRONG_DATA));
      }
    } catch (IOException | NullPointerException | NumberFormatException e) {
      log.error(e);
    }
  }
}
