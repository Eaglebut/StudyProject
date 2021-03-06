package ru.sfedu.studyProject.lab5.model;

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
@Entity(name = "ExtendedTask_LAB5")
@Table(schema = "LAB5")
public class ExtendedTask extends Task {
  private String description;
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Date time;
  private Importances importance;
}
