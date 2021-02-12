package ru.sfedu.studyProject.curs.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import ru.sfedu.studyProject.curs.Constants;
import ru.sfedu.studyProject.curs.converters.ModificationRecordConverter;
import ru.sfedu.studyProject.curs.converters.TaskListConverter;
import ru.sfedu.studyProject.curs.enums.SignUpTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Class User
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class User implements Serializable {

  //
  // Fields
  //
  @CsvBindByName
  @Attribute
  private long id;
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  @Attribute
  private Date created;
  @CsvBindByName
  @Attribute
  private String email;
  @CsvBindByName
  @Attribute
  private String password;
  @CsvBindByName
  @Attribute
  private String name;
  @CsvBindByName
  @Attribute
  private String surname;
  @CsvBindByName
  @Attribute
  private String token;
  @CsvBindByName
  @Attribute
  private SignUpTypes signUpType;
  @CsvCustomBindByName(converter = TaskListConverter.class)
  @ElementList
  private List<Task> taskList;
  @CsvCustomBindByName(converter = ModificationRecordConverter.class)
  @ElementList
  private List<ModificationRecord> historyList;

  //
  // Methods
  //


}
