package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

  private long id;
  private String name;
  private Date created;
  private Map<Task, TaskState> taskList;
  private Map<User, UserRole> memberList;
  private GroupTypes groupType;
  private List<ModificationRecord> historyList;

  //
  // Methods
  //


}
