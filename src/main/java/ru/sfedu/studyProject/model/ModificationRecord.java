package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.enums.OperationType;

import java.io.Serializable;
import java.util.Date;

/**
 * Class ModificationRecord
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class ModificationRecord implements Serializable {

  //
  // Fields
  //
  @CsvBindByName
  private long id;
  @CsvBindByName
  private String changedValueName;
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date changedDate;
  @CsvBindByName
  private String changedValue;
  @CsvBindByName
  private OperationType operationType;

  //
  // Methods
  //


}
