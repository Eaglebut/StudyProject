package ru.sfedu.studyProject.model;


import lombok.*;

/**
 * Class PasswordedPrivateGroup
 */
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class PasswordedPrivateGroup extends PrivateGroup {

  //
  // Fields
  //

  private String password;


}
