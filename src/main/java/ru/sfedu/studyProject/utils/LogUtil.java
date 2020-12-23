package ru.sfedu.studyProject.utils;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.studyProject.Constants;

import java.io.IOException;
import java.util.Arrays;

@Log4j2
public class LogUtil {
  public static String startFunc(Object... args) {
    return String.format("Start %s %s",
            Thread.currentThread().getStackTrace()[2].getMethodName(),
            prepareArgs(args));
  }

  public static String endFunc(Object... args) {
    return String.format("End %s %s",
            Thread.currentThread().getStackTrace()[2].getMethodName(),
            prepareArgs(args));
  }


  private static String prepareArgs(Object[] args) {
    StringBuilder argsStringBuilder = new StringBuilder();
    if (args.length > 0) {
      argsStringBuilder.append("with arguments: ");
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
