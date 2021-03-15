package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.LessonTypes;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity(name = "Lesson_LAB5")
@Table(schema = "LAB5")
public class Lesson extends Task {
  private String teacherName;
  private Date beginDate;
  private Date endDate;
  @OneToOne(fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  private Assigment assigment;
  private LessonTypes lessonType;
}
