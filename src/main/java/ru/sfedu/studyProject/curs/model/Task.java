package ru.sfedu.studyProject.curs.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import ru.sfedu.studyProject.curs.Constants;
import ru.sfedu.studyProject.curs.converters.ModificationRecordConverter;
import ru.sfedu.studyProject.curs.enums.TaskStatuses;
import ru.sfedu.studyProject.curs.enums.TaskTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Class Task
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class Task implements Serializable {

  //
  // Fields
  //
  @Attribute
  @CsvBindByName
  private long id;
  @Attribute
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date created;
  @Attribute
  @CsvBindByName
  private String name;
  @Attribute
  @CsvBindByName
  private TaskStatuses status;
  @Attribute
  @CsvBindByName
  private TaskTypes taskType;
  @ElementList
  @CsvCustomBindByName(converter = ModificationRecordConverter.class)
  private List<ModificationRecord> historyList;


  //
  // Methods
  //

}
