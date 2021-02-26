package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.singleTable.model.enums.WorkTaskType;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class WorkTask extends Task implements Serializable {
  private Date time;
  private String address;
  private WorkTaskType type;
  private String description;
}
