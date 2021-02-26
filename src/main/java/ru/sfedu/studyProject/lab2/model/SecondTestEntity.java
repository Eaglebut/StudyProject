package ru.sfedu.studyProject.lab2.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class SecondTestEntity implements Serializable {
  private String someText;
}
