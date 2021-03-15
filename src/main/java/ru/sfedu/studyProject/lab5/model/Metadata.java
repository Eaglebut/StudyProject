package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@ToString
@Entity(name = "Metadata_LAB5")
@Table(schema = "LAB5")
@EqualsAndHashCode
public class Metadata {
    @Id
    @GeneratedValue
    private long id;
    private Date created;
    private Date lastUpdated;
}
