package ru.sfedu.studyProject.lab5.primaryKeyJoinColumn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name = "Address_LAB5_PKJC")
@Data
@EqualsAndHashCode
@Table(schema = "LAB5_primaryKeyJoinColumn")
public class Address {
  @Id
  private long id;
  private String city;
  private String street;
  private int house;
  @OneToOne
  @PrimaryKeyJoinColumn
  private User user;
}
