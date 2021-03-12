package ru.sfedu.studyProject.lab3.joinedTable.model;

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
@Entity
@Table(schema = "JOINED_TABLE")
public class Lesson extends Task {
  private String teacherName;
  private Date beginDate;
  private Date endDate;
  private String assigment;
  private LessonTypes lessonType;
}
