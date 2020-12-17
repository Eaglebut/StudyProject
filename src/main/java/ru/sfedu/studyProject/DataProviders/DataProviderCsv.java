package ru.sfedu.studyProject.DataProviders;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.*;
import java.util.List;

@Log4j2
public class DataProviderCsv extends AbstractGenericDataProvider {
  private static DataProviderCsv INSTANCE = null;

  private DataProviderCsv() {

  }

  public static DataProviderCsv getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataProviderCsv();
    }
    return INSTANCE;
  }


  private <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
    FileWriter writer;
    File path = new File(PropertyLoader.getProperty(Constants.CSV_PATH));
    File file = new File(PropertyLoader.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.CSV_EXTENSION));
    log.debug(file.getPath());
    if (!file.exists() && path.mkdirs() && !file.createNewFile()) {
      throw new IOException(
              String.format(PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                      file.getName()));
    }
    writer = new FileWriter(file);
    return new CSVWriter(writer);
  }

  private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
    File path = new File(PropertyLoader.getProperty(Constants.CSV_PATH));
    File file = new File(PropertyLoader.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.CSV_EXTENSION));
    if (!file.exists() && path.mkdirs() && !file.createNewFile()) {
      throw new IOException(
              String.format(
                      PropertyLoader.getProperty(Constants.EXCEPTION_CANNOT_CREATE_FILE),
                      file.getName()));

    }
    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    return new CSVReader(bufferedReader);
  }

  @Override
  protected <T> void deleteFile(Class<T> tClass) {
    try {
      log.debug(new File(PropertyLoader.getProperty(Constants.CSV_PATH)
              + tClass.getSimpleName().toLowerCase()
              + PropertyLoader.getProperty(Constants.CSV_EXTENSION)).delete());
    } catch (IOException e) {
      log.error(e);
    }
  }

  @Override
  protected <T> void insertIntoDB(Class<T> tClass, List<T> objectList) throws IOException {
    List<T> tList;
    tList = objectList;
    if (tList.isEmpty()) {
      deleteFile(tClass);
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

  @Override
  protected <T> List<T> getFromDB(Class<T> tClass) throws IOException {
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
}
