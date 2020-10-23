package ru.sfedu.studyProject.model;

import lombok.*;
import ru.sfedu.studyProject.enums.GroupTypes;

import java.util.Date;
import java.util.List;


/**
 * Class Group
 */
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class Group {

  //
  // Fields
  //

  private Long id;
  private String name;
  private Date created;
  private List<Task> taskList;
  private List<User> memberList;
  private List<User> administratorList;
  private User creator;
  private GroupTypes groupType;


}
