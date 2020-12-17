package ru.sfedu.studyProject.DataProviders;

import lombok.extern.log4j.Log4j2;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.utils.PropertyLoader;
import ru.sfedu.studyProject.utils.XmlList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DataProviderXml extends AbstractGenericDataProvider {

  private static DataProviderXml INSTANCE;

  private DataProviderXml() {

  }

  public static DataProviderXml getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DataProviderXml();
    }
    return INSTANCE;
  }


  @Override
  protected <T> void insertIntoDB(Class<T> tClass, List<T> objectList) throws IOException {
    Persister serializer = new Persister();
    try {
      serializer.write(new XmlList<>(objectList), getFile(tClass));
    } catch (Exception e) {
      log.error(e);
      throw new IOException(e);
    }
  }

  @Override
  protected <T> List<T> getFromDB(Class<T> tClass) throws IOException {
    Persister serializer = new Persister();
    XmlList<T> xmlList = new XmlList<>();
    try {
      xmlList = serializer.read(xmlList.getClass(), getFile(tClass));
      return xmlList.getTList() == null ? new ArrayList<>() : xmlList.getTList();
    } catch (Exception e) {
      log.error(e);
      throw new IOException(e);
    }
  }


  @Override
  protected <T> void deleteFile(Class<T> tClass) {
    try {
      log.debug(new File(PropertyLoader.getProperty(Constants.XML_PATH)
              + tClass.getSimpleName().toLowerCase()
              + PropertyLoader.getProperty(Constants.XML_EXTENSION)).delete());
    } catch (IOException e) {
      log.error(e);
    }
  }

  private <T> File getFile(Class<T> tClass) throws IOException {
    File path = new File(PropertyLoader.getProperty(Constants.XML_PATH));
    File file = new File(PropertyLoader.getProperty(Constants.XML_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyLoader.getProperty(Constants.XML_EXTENSION));
    log.debug(file.getPath());
    if (!file.exists()) {
      log.debug(path.mkdirs());
      log.debug(file.createNewFile());
    }
    return file;
  }

}
