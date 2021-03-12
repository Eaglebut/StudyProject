package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "User_MAPPED_SUPERCLASS")
@Table(schema = "mapped_superclass")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private Date created;
  private String name;
  private String surname;
}
