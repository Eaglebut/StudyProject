package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Embeddable;

@Data
@ToString
@EqualsAndHashCode
@Embeddable
public class Adress {

  private String city;
  private String adress;
}
