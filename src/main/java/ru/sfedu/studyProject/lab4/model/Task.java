package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sfedu.studyProject.lab3.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "Task_LAB4")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(schema = "LAB4")
@EqualsAndHashCode
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
