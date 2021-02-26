package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.RemindTypes;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.TaskStatuses;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.TaskTypes;

import java.io.Serializable;
import java.util.Date;

@Data
public class Task implements Serializable {
  private long id;
  private Date created;
  private String name;
  private Date lastUpdated;
  private TaskStatuses taskStatus;
  private TaskTypes taskType;
  private RemindTypes remindType;
}
