package ru.sfedu.studyProject.lab3.singleTable.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.singleTable.model.enums.LessonTypes;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class Lesson extends Task implements Serializable {
  private String teacherName;
  private Date beginDate;
  private Date endDate;
  private String assigment;
  private LessonTypes lessonType;
}
