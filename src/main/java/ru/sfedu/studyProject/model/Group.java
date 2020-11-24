package ru.sfedu.studyProject.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.*;
import ru.sfedu.studyProject.converters.ModificationRecordConverter;
import ru.sfedu.studyProject.converters.TaskMapConverter;
import ru.sfedu.studyProject.converters.UserMapConverter;
import ru.sfedu.studyProject.enums.GroupTypes;
import ru.sfedu.studyProject.enums.TaskState;
import ru.sfedu.studyProject.enums.UserRole;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Class Group
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class Group implements Serializable {

  //
  // Fields
  //
  @CsvBindByName
  private long id;
  @CsvBindByName
  private String name;
  @CsvBindByName
  private Date created;
  @CsvCustomBindByName(converter = TaskMapConverter.class)
  private Map<Task, TaskState> taskList;
  @CsvCustomBindByName(converter = UserMapConverter.class)
  private Map<User, UserRole> memberList;
  @CsvBindByName
  private GroupTypes groupType;
  @CsvCustomBindByName(converter = ModificationRecordConverter.class)
  private List<ModificationRecord> historyList;

  //
  // Methods
  //


}
