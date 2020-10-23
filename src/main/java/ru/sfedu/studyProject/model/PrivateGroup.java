package ru.sfedu.studyProject.model;


import lombok.*;

import java.util.List;

/**
 * Class PrivateGroup
 */
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
abstract public class PrivateGroup extends Group {

  //
  // Fields
  //

  private List<User> banList;
  private List<Task> unacceptedTaskList;


}
