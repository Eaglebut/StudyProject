package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.converters.ModificationRecordConverter;
import ru.sfedu.studyProject.converters.TaskListConverter;
import ru.sfedu.studyProject.enums.SignUpTypes;

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
@Root
public class User implements Serializable {

  //
  // Fields
  //
  @Attribute
  @CsvBindByName
  private long id;
  @CsvBindByName
  @Attribute
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date created;
  @Attribute
  @CsvBindByName
  private String email;
  @Attribute
  @CsvBindByName
  private String password;
  @Attribute
  @CsvBindByName
  private String name;
  @Attribute
  @CsvBindByName
  private String surname;
  @Attribute
  @CsvBindByName
  private String token;
  @Attribute
  @CsvBindByName()
  private SignUpTypes signUpType;
  @ElementList
  @CsvCustomBindByName(converter = TaskListConverter.class)
  private List<Task> taskList;
  @ElementList
  @CsvCustomBindByName(converter = ModificationRecordConverter.class)
  private List<ModificationRecord> historyList;

  //
  // Methods
  //


}
