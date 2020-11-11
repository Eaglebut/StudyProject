package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.sfedu.studyProject.Constants;
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
public class User implements Serializable {

  //
  // Fields
  //

  @CsvBindByName
  private long id;
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date created;
  @CsvBindByName
  private String email;
  @CsvBindByName
  private String password;
  @CsvBindByName
  private String name;
  @CsvBindByName
  private String surname;
  @CsvBindByName
  private String token;
  @CsvBindByName(column = "SIGN_UP_TYPE")
  private SignUpTypes signUpType;
  private List<Task> taskList;
  private List<ModificationRecord> historyList;


  //
  // Methods
  //


}
