package ru.sfedu.studyProject.curs.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Data
@Root
@AllArgsConstructor
@NoArgsConstructor
public class XmlList<T> {
  @ElementList
  private List<T> tList;
}
