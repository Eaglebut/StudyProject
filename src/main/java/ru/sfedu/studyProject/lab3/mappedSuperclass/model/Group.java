package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.GroupTypes;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.UserRoles;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(schema = "MAPPED_SUPERCLASS", name = "\"GROUP\"")
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private Date created;
  //TODO
  //private Map<Task, TaskStates> taskMap;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "MAPPED_SUPERCLASS")
  private Map<User, UserRoles> userMap;
  private GroupTypes groupType;
  private String password;
}
