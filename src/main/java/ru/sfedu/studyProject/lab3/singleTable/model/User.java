package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
  private long id;
  private Date created;
  private String name;
  private String surname;
}
