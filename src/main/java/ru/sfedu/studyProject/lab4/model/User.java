package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Entity(name = "User_LAB4")
@Table(schema = "LAB4")
@EqualsAndHashCode
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private Date created;
  private String name;
  private String surname;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "LAB4")
  private Set<Adress> adressSet;
}
