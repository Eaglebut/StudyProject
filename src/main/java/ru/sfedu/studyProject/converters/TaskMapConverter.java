package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.TaskState;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class TaskMapConverter extends AbstractBeanField<Task, Integer> {


  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException {
    try {
      String mapString;
      if (s.matches(PropertyLoader.getProperty(Constants.CONVERTER_REGEXP_MAP_WITHOUT_QUOTES))) {
        mapString = s.substring(1, s.length() - 1);
      } else if (s.matches(PropertyLoader.getProperty(Constants.CONVERTER_REGEXP_MAP_WITH_QUOTES))) {
        mapString = s.substring(2, s.length() - 2);
      } else {
        throw new CsvDataTypeMismatchException();
      }
      String[] unparsedKeyValueList = mapString.split(
              PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
      Map<Task, TaskState> taskIdMap = new HashMap<>();
      for (String strKeyValue : unparsedKeyValueList) {
        if (!strKeyValue.isEmpty()) {
          var splitKeyValueString = strKeyValue.split(PropertyLoader.getProperty(Constants.MAP_DELIMITER));
          if (splitKeyValueString.length == 2) {
            Task task = new Task();
            task.setId(Long.parseLong(splitKeyValueString[0]));
            taskIdMap.put(task, TaskState.valueOf(splitKeyValueString[1]));
          }
        }
      }
      return taskIdMap;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  protected String convertToWrite(Object value) {
    try {
      Map<Task, TaskState> taskMap = (Map<Task, TaskState>) value;
      StringBuilder strTaskMap = new StringBuilder();
      strTaskMap.append(PropertyLoader.getProperty(Constants.MAP_START_SYMBOL));
      if (taskMap.size() > 0) {
        taskMap.forEach((task, state) -> {
          try {
            strTaskMap.append(task.getId());
            strTaskMap.append(PropertyLoader.getProperty(Constants.MAP_DELIMITER));
            strTaskMap.append(state);
            strTaskMap.append(PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
          } catch (IOException e) {
            log.error(e);
          }
        });
        strTaskMap.delete(strTaskMap.length() - 1, strTaskMap.length());
      }
      strTaskMap.append(PropertyLoader.getProperty(Constants.MAP_END_SYMBOL));
      return strTaskMap.toString();
    } catch (Exception e) {
      log.error(e);
      return null;
    }
  }
}
