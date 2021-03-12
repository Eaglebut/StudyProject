package ru.sfedu.studyProject.lab3.tablePerClass.model;

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
@Entity(name = "_TABLE_PER_CLASS")
@Table(schema = "TABLE_PER_CLASS")
public class ExtendedTask extends Task {
  private String description;
  private Date time;
  private Importances importance;
}
