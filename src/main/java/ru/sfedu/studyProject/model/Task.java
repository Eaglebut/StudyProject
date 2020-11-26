package ru.sfedu.studyProject.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.converters.ModificationRecordConverter;
import ru.sfedu.studyProject.enums.TaskStatuses;
import ru.sfedu.studyProject.enums.TaskTypes;

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
  @CsvBindByName
  private long id;
  @CsvBindByName
  @CsvDate(value = Constants.DATE_FORMAT)
  private Date created;
  @CsvBindByName
  private String name;
  @CsvBindByName
  private TaskStatuses status;
  @CsvBindByName
  private TaskTypes taskType;
  @CsvCustomBindByName(converter = ModificationRecordConverter.class)
  private List<ModificationRecord> historyList;


  //
  // Methods
  //

}
