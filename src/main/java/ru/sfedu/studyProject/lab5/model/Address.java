package ru.sfedu.studyProject.lab5.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "Address_LAB5")
@Data
@EqualsAndHashCode(doNotUseGetters = true)
@Table(schema = "LAB5")
public class Address {
  @Id
  @GeneratedValue(generator = "addressKeyGenerator")
  @GenericGenerator(
          name = "addressKeyGenerator",
          strategy = "foreign",
          parameters = @org.hibernate.annotations.Parameter(
                  name = "property", value = "user"
          )
  )
  private long id;
  private String city;
  private String street;
  private int house;
  @OneToOne(optional = false)
  @PrimaryKeyJoinColumn
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private User user;
}

