package ru.sfedu.studyProject.model;

import com.opencsv.bean.CsvBindByName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.sfedu.studyProject.enums.TaskStatuses;
import ru.sfedu.studyProject.enums.TaskTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Class Task
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
@ToString
public class Task implements Serializable {

    //
    // Fields
    //
    @CsvBindByName
    private long id;
    private Date created;
    @CsvBindByName
    private String name;
    private Date lastUpdated;
    private TaskStatuses status;
    private TaskTypes taskType;
    private List<ModificationRecord> historyList;


    //
    // Methods
    //

}
