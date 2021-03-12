package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import ru.sfedu.studyProject.lab3.enums.GroupTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity(name = "Group_LAB5")
@Table(schema = "LAB5", name = "\"GROUP\"")
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private Date created;
  @OneToMany(fetch = FetchType.EAGER)
  private List<Task> taskList;
  @OneToMany(fetch = FetchType.EAGER)
  private Set<User> userList;
  private GroupTypes groupType;
  private String password;
}
