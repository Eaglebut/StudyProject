package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.WorkTaskType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
@Table(schema = "LAB4")
public class WorkTask extends Task implements Serializable {
  private Date time;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "LAB4")
  private Set<String> addresses;
  private WorkTaskType type;
  private String description;
}
