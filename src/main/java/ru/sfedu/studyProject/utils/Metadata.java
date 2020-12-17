package ru.sfedu.studyProject.utils;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.simpleframework.xml.Attribute;

@Data
@EqualsAndHashCode
@ToString
public class Metadata {

  @CsvBindByName
  @Attribute
  private String className;
  @CsvBindByName
  @Attribute
  private Long lastId;

}
