package ru.sfedu.studyProject.lab3.tablePerClass.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.Importances;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ExtendedTask extends Task implements Serializable {
  private String description;
  private Date time;
  private Importances importance;
}
