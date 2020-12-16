package ru.sfedu.studyProject;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.ResultSet;

@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientTest {
  ResultSet set;

}