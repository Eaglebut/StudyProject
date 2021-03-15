package ru.sfedu.studyProject.lab5.primaryKeyJoinColumn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sfedu.studyProject.lab3.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "Task_LAB5_PKJC")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(schema = "LAB5_primaryKeyJoinColumn")
@EqualsAndHashCode
public abstract class Task implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  private Date created;
  private String name;
  private Date lastUpdated;
  private TaskStatuses taskStatus;
  private TaskTypes taskType;
  private RemindTypes remindType;
  @ManyToOne
  private Group group;
}
