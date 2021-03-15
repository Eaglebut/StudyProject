package ru.sfedu.studyProject.lab5.dataproviders;

import ru.sfedu.studyProject.lab5.model.Group;

import java.util.Optional;

public interface QueryLab5 {

  Optional<Group> getGroup(long id);

}
