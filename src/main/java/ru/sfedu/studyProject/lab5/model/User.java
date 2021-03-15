package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "USER_LAB5")
@Table(schema = "LAB5")
@EqualsAndHashCode
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;
  @EqualsAndHashCode.Exclude
  private Date created;
  private String name;
  private String surname;
  @OneToOne(cascade = CascadeType.PERSIST, mappedBy = "user")
  private Address address;
}
