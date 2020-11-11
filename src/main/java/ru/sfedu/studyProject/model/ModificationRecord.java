package ru.sfedu.studyProject.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Class ModificationRecord
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class ModificationRecord<T> implements Serializable {

    //
    // Fields
    //
    private long id;
    private String changedValueName;
    private Date changedDate;
    private T changedValue;

    //
  // Methods
  //


}
