package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import lombok.*;

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
  @CsvBindByName
  private String password;

  //
  // Methods
  //


}
