package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sfedu.studyProject.lab3.enums.GroupTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "Group_LAB4")
@EqualsAndHashCode
@Table(schema = "LAB4", name = "\"GROUP\"")
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private Date created;
  //@ElementCollection
  //@CollectionTable(schema = "LAB4")
  //private Map<Task, TaskStates> taskMap;
  //@ElementCollection(fetch = FetchType.EAGER)
  //@CollectionTable(schema = "LAB4")
  //private Map<User, UserRoles> userMap;
  private GroupTypes groupType;
  private String password;
}
