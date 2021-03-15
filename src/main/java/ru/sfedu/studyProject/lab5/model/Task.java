package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.enums.TaskTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "Task_LAB5")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(schema = "LAB5")
@EqualsAndHashCode
public abstract class Task implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  private String name;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(unique = true)
  private Metadata metadata;
  private TaskStatuses taskStatus;
  private TaskTypes taskType;
  private RemindTypes remindType;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Group group;
}
