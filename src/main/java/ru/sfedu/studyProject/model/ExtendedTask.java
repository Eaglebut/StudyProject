package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.sfedu.studyProject.enums.Importances;
import ru.sfedu.studyProject.enums.RemindTypes;
import ru.sfedu.studyProject.enums.RepetitionTypes;

import java.util.Date;


/**
 * Class ExtendedTask
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@ToString
public class ExtendedTask extends Task {

  //
  // Fields
  //

  private RepetitionTypes repetitionType;
  private RemindTypes remindType;
  private Importances importance;
  private String description;
  private Date time;


  //
  // Methods
  //
}
