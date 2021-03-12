package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.LessonTypes;

import javax.persistence.Entity;
import javax.persistence.Table;
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
  private String assigment;
  private LessonTypes lessonType;
}
