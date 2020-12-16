package ru.sfedu.studyProject.utils;

import lombok.Data;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Data
@Root
public class XmlList<T> {
  @ElementList
  List<T> tList;
}
