package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import ru.sfedu.studyProject.Constants;
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


  @CsvBindByName
  private RepetitionTypes repetitionType;
  @CsvBindByName
  private RemindTypes remindType;
  @CsvBindByName
  private Importances importance;
  @CsvBindByName
  private String description;
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date time;


  //
  // Methods
  //
}
