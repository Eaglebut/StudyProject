package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.Importances;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
@Table(schema = "SINGLE_TABLE")
public class ExtendedTask extends Task {
  private String description;
  private Date time;
  private Importances importance;
}
