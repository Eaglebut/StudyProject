package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "USER_LAB5")
@Table(schema = "LAB5")
@EqualsAndHashCode
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Date created;
  private String name;
  private String surname;
  @OneToOne(cascade = CascadeType.PERSIST, mappedBy = "user")
  private Address address;
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToMany(mappedBy = "userList", fetch = FetchType.EAGER)
  private Set<Group> group = new HashSet<>();
}
