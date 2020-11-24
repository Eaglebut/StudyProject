package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TaskListConverter extends AbstractBeanField<Task, Integer> {

  private static final Logger log = LogManager.getLogger(TaskListConverter.class);


  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException {
    try {
      String indexString;
      if (s.matches(PropertyLoader.getProperty(Constants.CONVERTER_REGEXP_WITHOUT_QUOTES))) {
        indexString = s.substring(1, s.length() - 1);
      } else if (s.matches(PropertyLoader.getProperty(Constants.CONVERTER_REGEXP_WITH_QUOTES))) {
        indexString = s.substring(2, s.length() - 2);
      } else {
        throw new CsvDataTypeMismatchException();
      }
      String[] unparsedIndexList = indexString.split(
              PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
      List<Task> indexTaskList = new ArrayList<>();
      for (String strIndex : unparsedIndexList) {
        if (!strIndex.isEmpty()) {
          Task task = new Task();
          task.setId(Long.parseLong(strIndex));
          indexTaskList.add(task);
        }
      }
      return indexTaskList;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }


  @Override
  protected String convertToWrite(Object value) {
    try {
      List<Task> taskList = (List<Task>) value;
      StringBuilder builder = new StringBuilder(PropertyLoader.getProperty(Constants.ARRAY_START_SYMBOL));
      if (taskList.size() > 0) {
        for (Task task : taskList) {
          builder.append(task.getId());
          builder.append(PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
        }

        builder.delete(builder.length() - 1, builder.length());
      }
      builder.append(PropertyLoader.getProperty(Constants.ARRAY_END_SYMBOL));
      log.debug(builder.toString());
      return builder.toString();
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }
}
