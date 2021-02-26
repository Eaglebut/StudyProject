package ru.sfedu.studyProject.lab3.joinedTable.model;

import lombok.Data;

@Data
public class User {
  private long id;
  private long created;
  private String name;
  private String surname;
}
