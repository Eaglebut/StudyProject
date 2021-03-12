package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

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
@Entity(name = "WorkTask_MAPPED_SUPERCLASS")
@Table(schema = "MAPPED_SUPERCLASS")
public class WorkTask extends Task implements Serializable {
  private Date time;
  private String address;
  private WorkTaskType type;
  private String description;
}
