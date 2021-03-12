package ru.sfedu.studyProject.lab3.joinedTable.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "JOINED_TABLE")
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
