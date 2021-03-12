package ru.sfedu.studyProject.lab3.tablePerClass.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.enums.GroupTypes;
import ru.sfedu.studyProject.lab3.enums.UserRoles;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(schema = "TABLE_PER_CLASS", name = "\"GROUP\"")
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private Date created;
  //TODO
  //private Map<Task, TaskStates> taskMap;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "TABLE_PER_CLASS")
  private Map<User, UserRoles> userMap;
  private GroupTypes groupType;
  private String password;
}
