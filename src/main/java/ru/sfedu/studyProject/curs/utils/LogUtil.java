package ru.sfedu.studyProject.curs.utils;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.curs.Constants;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.Arrays;

@Log4j2
public class LogUtil {
  public static String startFunc(Object... args) {
    try {
      return String.format(PropertyLoader.getProperty(Constants.START_STR),
              Thread.currentThread().getStackTrace()[2].getMethodName(),
              prepareArgs(args));
    } catch (IOException e) {
      log.error(e);
      return "";
    }
  }

  public static String endFunc(Object... args) {
    try {
      return String.format(PropertyLoader.getProperty(Constants.END_STR),
              Thread.currentThread().getStackTrace()[2].getMethodName(),
              prepareArgs(args));
    } catch (IOException e) {
      log.error(e);
      return "";
    }
  }


  private static String prepareArgs(Object[] args) throws IOException {
    StringBuilder argsStringBuilder = new StringBuilder();
    if (args.length > 0) {
      argsStringBuilder.append(PropertyLoader.getProperty(Constants.WITH_ARGUMENTS));
      Arrays.stream(args).forEach(arg -> {
        try {
          argsStringBuilder.append(arg);
          argsStringBuilder.append(PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
        } catch (IOException e) {
          log.error(e);
        }
      });
      argsStringBuilder.delete(argsStringBuilder.length() - 1, argsStringBuilder.length());
    }
    return argsStringBuilder.toString();
  }
}
