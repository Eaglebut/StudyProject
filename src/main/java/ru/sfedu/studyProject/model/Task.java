package ru.sfedu.studyProject.model;


import lombok.*;
import ru.sfedu.studyProject.enums.EventStatuses;
import ru.sfedu.studyProject.enums.TaskTypes;

import java.util.Date;

/**
 * Class Task
 */
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class Task {

  //
  // Fields
  //

  private Long id;
  private Date created;
  private String name;
  private Date lastUpdated;
  private EventStatuses status;
  private TaskTypes taskType;

}
