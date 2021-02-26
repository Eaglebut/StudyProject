package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.GroupTypes;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.TaskStates;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.UserRoles;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
//@Entity
public class Group implements Serializable {
  //@Id
  //@GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private Date created;
  private Map<Task, TaskStates> taskMap;
  private Map<User, UserRoles> userMap;
  private GroupTypes groupType;
  private String password;
}
