package ru.sfedu.studyProject.lab3.tablePerClass.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.WorkTaskType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity(name = "WorkTask_TABLE_PER_CLASS")
@Table(schema = "TABLE_PER_CLASS")
public class WorkTask extends Task implements Serializable {
  private Date time;
  private String address;
  private WorkTaskType type;
  private String description;
}
