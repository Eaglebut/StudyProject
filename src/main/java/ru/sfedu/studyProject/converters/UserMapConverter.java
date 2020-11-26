package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.UserRole;
import ru.sfedu.studyProject.model.User;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserMapConverter extends AbstractBeanField<User, Integer> {

  private static final Logger log = LogManager.getLogger(UserMapConverter.class);

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
      Map<User, UserRole> userIdMap = new HashMap<>();
      for (String strKeyValue : unparsedKeyValueList) {
        if (!strKeyValue.isEmpty()) {
          var splitKeyValueString = strKeyValue.split(PropertyLoader.getProperty(Constants.MAP_DELIMITER));
          if (splitKeyValueString.length == 2) {
            User user = new User();
            user.setId(Long.parseLong(splitKeyValueString[0]));
            userIdMap.put(user, UserRole.valueOf(splitKeyValueString[1]));
          }
        }
      }
      return userIdMap;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  protected String convertToWrite(Object value) {
    try {
      Map<User, UserRole> userMap = (Map<User, UserRole>) value;
      StringBuilder strUserMap = new StringBuilder();
      strUserMap.append(PropertyLoader.getProperty(Constants.MAP_START_SYMBOL));
      if (userMap.size() > 0) {
        userMap.forEach((user, role) -> {
          try {
            strUserMap.append(user.getId());
            strUserMap.append(PropertyLoader.getProperty(Constants.MAP_DELIMITER));
            strUserMap.append(role);
            strUserMap.append(PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
          } catch (IOException e) {
            log.error(e);
          }
        });
        strUserMap.delete(strUserMap.length() - 1, strUserMap.length());
      }
      strUserMap.append(PropertyLoader.getProperty(Constants.MAP_END_SYMBOL));
      return strUserMap.toString();
    } catch (Exception e) {
      log.error(e);
      return null;
    }
  }
}
