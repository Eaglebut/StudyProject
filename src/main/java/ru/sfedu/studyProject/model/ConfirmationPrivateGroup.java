package ru.sfedu.studyProject.model;


import lombok.*;

import java.util.List;

/**
 * Class ConfirmationPrivateGroup
 */
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class ConfirmationPrivateGroup extends PrivateGroup {

  //
  // Fields
  //

  private List<User> unacceptedUserList;
  


}
