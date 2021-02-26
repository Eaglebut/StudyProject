package ru.sfedu.studyProject.lab3.tablePerClass.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.tablePerClass.model.enums.WorkTaskType;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class WorkTask extends Task {
  private Date time;
  private String address;
  private WorkTaskType type;
  private String description;
}
