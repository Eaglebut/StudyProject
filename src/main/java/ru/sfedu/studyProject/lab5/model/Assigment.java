package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.simpleframework.xml.Element;

import javax.persistence.*;

@Data
@Element
@Table(schema = "LAB5")
@ToString
@EqualsAndHashCode
@Entity(name = "Assigment_LAB5")
public class Assigment {

    @Id
    private long id;
    private String task;
    private String teacherName;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private Lesson lesson;


    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        this.id = lesson.getId();
    }
}
