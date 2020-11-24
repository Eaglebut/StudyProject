package ru.sfedu.studyProject.utils;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Metadata {

  @CsvBindByName
  private String className;
  @CsvBindByName
  private Long lastId;

}
