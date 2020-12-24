package ru.sfedu.studyProject;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.DataProviders.DataProvider;
import ru.sfedu.studyProject.DataProviders.DataProviderCsv;

import java.io.File;

/**
 * The type Main.
 */
@Log4j2
public class Client {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {

    System.out.println(new File("").getAbsolutePath());

    DataProvider dataProvider;
    if (args[0].equals("DataProviderCsv")) {
      dataProvider = DataProviderCsv.getInstance();
    } else if (args[0].equals("DataProviderXml")) {
      dataProvider = DataProviderCsv.getInstance();
    } else if (args[0].equals("DataProviderJdbc")) {
      dataProvider = DataProviderCsv.getInstance();
    }


  }
}
