package ru.sfedu.studyProject.lab2.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class SecondTestEntity {
  private String someText;
}
