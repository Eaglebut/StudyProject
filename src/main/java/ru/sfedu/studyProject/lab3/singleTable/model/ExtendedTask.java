package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.singleTable.model.enums.Importances;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ExtendedTask extends Task {
  private String description;
  private Date time;
  private Importances importance;
}
