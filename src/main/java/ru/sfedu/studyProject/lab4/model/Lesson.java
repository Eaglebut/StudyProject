package ru.sfedu.studyProject.lab4.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sfedu.studyProject.lab3.enums.LessonTypes;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity(name = "Lesson_LAB4")
@Table(schema = "LAB4")
public class Lesson extends Task {
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "LAB4")
  @OrderColumn
  private List<String> teachers;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "LAB4")
  private Map<String, Date> breakPoints;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(schema = "LAB4")
  @MapKeyColumn
  private Map<Assigment, Date> assigment;
  private LessonTypes lessonType;
}
