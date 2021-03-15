package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.GroupTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "Group_LAB5")
@Table(schema = "LAB5")
@EqualsAndHashCode(doNotUseGetters = true)
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  private String name;
  private Date created;
  @OneToMany(fetch = FetchType.EAGER,mappedBy = "id")
  private Set<Task> taskList = new HashSet<>();
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(schema = "LAB5")
  private Set<User> userList;
  private GroupTypes groupType;
  private String password;

}
