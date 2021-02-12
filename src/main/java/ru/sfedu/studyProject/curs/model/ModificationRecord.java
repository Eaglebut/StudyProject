package ru.sfedu.studyProject.curs.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.simpleframework.xml.Attribute;
import ru.sfedu.studyProject.curs.Constants;
import ru.sfedu.studyProject.curs.enums.OperationType;

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

  @Attribute
  @CsvBindByName
  private long id;
  @Attribute
  @CsvBindByName
  private String changedValueName;
  @Attribute
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date changedDate;
  @Attribute
  @CsvBindByName
  private String changedValue;
  @Attribute
  @CsvBindByName
  private OperationType operationType;

  //
  // Methods
  //


}
