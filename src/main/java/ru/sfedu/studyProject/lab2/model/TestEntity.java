package ru.sfedu.studyProject.lab2.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "TEST_ENTITY")
public class TestEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "date_created")
    private Date dateCreated;
    @Column(name = "\"check\"")
    private Boolean check;
}
