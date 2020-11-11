package ru.sfedu.studyProject.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Task implements Serializable {

    //
    // Fields
    //

    private long id;
    private Date created;
    private String name;
    private Date lastUpdated;
    private TaskStatuses status;
  private TaskTypes taskType;
  private List<ModificationRecord> historyList;


  //
  // Methods
  //

}
