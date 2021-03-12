package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(schema = "SINGLE_TABLE")
public class Task implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private Date created;
  private String name;
  private Date lastUpdated;
  private TaskStatuses taskStatus;
  private TaskTypes taskType;
  private RemindTypes remindType;
}
