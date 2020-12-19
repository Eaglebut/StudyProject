package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.simpleframework.xml.Attribute;
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
@ToString(callSuper = true)
@AllArgsConstructor
public class ExtendedTask extends Task {

  //
  // Fields
  //

  @Attribute
  @CsvBindByName
  private RepetitionTypes repetitionType;
  @Attribute
  @CsvBindByName
  private RemindTypes remindType;
  @Attribute
  @CsvBindByName
  private Importances importance;
  @Attribute
  @CsvBindByName
  private String description;
  @Attribute
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date time;


  //
  // Methods
  //
}
