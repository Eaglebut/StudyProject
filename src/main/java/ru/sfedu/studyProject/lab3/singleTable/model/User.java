package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
  private long id;
  private long created;
  private String name;
  private String surname;
}
