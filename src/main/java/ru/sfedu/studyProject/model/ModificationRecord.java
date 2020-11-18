package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.sfedu.studyProject.Constants;

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

  //
  // Methods
  //


}
