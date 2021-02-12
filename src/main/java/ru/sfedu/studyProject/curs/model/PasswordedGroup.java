package ru.sfedu.studyProject.curs.model;


import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.simpleframework.xml.Attribute;

/**
 * Class PasswordedGroup
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class PasswordedGroup extends Group {

  //
  // Fields
  //
  @Attribute(required = false)
  @CsvBindByName
  private String password;

  //
  // Methods
  //


}
