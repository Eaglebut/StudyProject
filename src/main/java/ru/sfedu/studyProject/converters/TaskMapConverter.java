package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.sfedu.studyProject.model.Task;

//TODO
public class TaskMapConverter extends AbstractBeanField<Task, Integer> {
  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException {
    return null;
  }

  @Override
  protected String convertToWrite(Object value) {


    return "";
  }
}
