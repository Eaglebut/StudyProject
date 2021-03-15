package ru.sfedu.studyProject.lab5.generatedPrimaryKey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sfedu.studyProject.lab3.enums.GroupTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "Group_LAB5_GPK")
@Table(schema = "LAB5_generatedPrimaryKey")
@EqualsAndHashCode(doNotUseGetters = true)
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  private String name;
  private Date created;
  //@OneToMany(fetch = FetchType.EAGER,mappedBy = "id")
  //@ToString.Exclude
  //private Set<Task> taskList;
  //@OneToMany(fetch = FetchType.EAGER)
  //private Set<User> userList;
  private GroupTypes groupType;
  private String password;

}
