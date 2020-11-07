package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.studyProject.enums.SignUpTypes;

import java.util.Date;
import java.util.List;

/**
 * Class User
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
public class User {

  //
  // Fields
  //

  private Long id;
  private Date created;
  private String email;
  private String password;
  private String name;
  private String surname;
  private String token;
  private SignUpTypes signUpType;
  private List<Task> taskList;
  private List<ModificationRecord> historyList;


  //
  // Methods
  //


}
