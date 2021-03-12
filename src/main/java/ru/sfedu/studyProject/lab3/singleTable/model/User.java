package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "User_SINGLE_TABLE")
@Table(schema = "SINGLE_TABLE", name = "USER")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private Date created;
  private String name;
  private String surname;
}
