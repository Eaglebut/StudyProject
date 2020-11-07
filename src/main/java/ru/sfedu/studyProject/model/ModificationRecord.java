package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Class ModificationRecord
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
public class ModificationRecord<T> {

  //
  // Fields
  //

  private String changedValueName;
  private Date changedDate;
  private T changedValue;

  //
  // Methods
  //


}
