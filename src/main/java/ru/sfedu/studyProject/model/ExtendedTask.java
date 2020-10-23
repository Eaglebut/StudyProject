package ru.sfedu.studyProject.model;


import lombok.*;
import ru.sfedu.studyProject.enums.Importances;
import ru.sfedu.studyProject.enums.RemindTypes;
import ru.sfedu.studyProject.enums.RepetitionTypes;

import java.util.Date;

/**
 * Class ExtendedTask
 **/

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class ExtendedTask extends Task {

  //
  // Fields
  //

  private RepetitionTypes repetitionType;
  private RemindTypes remindType;
  private Importances importance;
  private String description;
  private Date time;


}
