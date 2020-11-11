package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class PasswordedGroup
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@ToString
public class PasswordedGroup extends Group {

  //
  // Fields
  //

  private String password;

  //
  // Methods
  //


}
