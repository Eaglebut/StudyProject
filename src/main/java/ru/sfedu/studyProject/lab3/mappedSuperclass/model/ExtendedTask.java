package ru.sfedu.studyProject.lab3.mappedSuperclass.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.mappedSuperclass.model.enums.Importances;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
@Table(schema = "MAPPED_SUPERCLASS")
public class ExtendedTask extends Task {
  private String description;
  private Date time;
  private Importances importance;
}
