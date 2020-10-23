package ru.sfedu.studyProject.model;


import lombok.*;
import ru.sfedu.studyProject.enums.SignUpTypes;

import java.util.Date;
import java.util.List;

/**
 * Class User
 */
@NoArgsConstructor
@Setter
@Getter
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

}
