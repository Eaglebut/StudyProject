package ru.sfedu.studyProject.lab1;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Log4j2
class HibernateDataProviderTest {

    private static final String TABLE_NAME = "TESTENTITY";
    private static final String FIRST_COLUMN_NAME = "ID";
    private static final String SECOND_COLUMN_NAME = "check";
    private static final String FIRST_COLUMN_TYPE = "BIGINT";
    private static final String SECOND_COLUMN_TYPE = "BOOLEAN";

    @BeforeAll
    static void setUp() {
        HibernateDataProvider.setUp();
    }

    @Test
    void getTableList() {
        Assertions.assertEquals(1, HibernateDataProvider.getTableList().size());
        Assertions.assertTrue(HibernateDataProvider.getTableList().contains(TABLE_NAME));
        HibernateDataProvider.getTableList().forEach(log::info);
    }

    @Test
    void getTableCount() {
        Assertions.assertEquals(1, HibernateDataProvider.getTableCount());
        log.info(HibernateDataProvider.getTableCount());
    }

    @Test
    void getColumnList() {
        Assertions.assertEquals(6, HibernateDataProvider.getColumnList(TABLE_NAME).size());
        Assertions.assertEquals(FIRST_COLUMN_NAME, HibernateDataProvider.getColumnList(TABLE_NAME).get(0));
        Assertions.assertEquals(SECOND_COLUMN_NAME, HibernateDataProvider.getColumnList(TABLE_NAME).get(1));
        HibernateDataProvider.getColumnList(TABLE_NAME).forEach(log::info);
    }

    @Test
    void getTableColumns() {
        Assertions.assertEquals(6, HibernateDataProvider.getTableColumns(TABLE_NAME).size());
        Assertions.assertEquals(FIRST_COLUMN_TYPE, HibernateDataProvider.getTableColumns(TABLE_NAME)
                .get(FIRST_COLUMN_NAME));
        Assertions.assertEquals(SECOND_COLUMN_TYPE, HibernateDataProvider.getTableColumns(TABLE_NAME)
                .get(SECOND_COLUMN_NAME));
        HibernateDataProvider.getTableColumns(TABLE_NAME).forEach((column, type) -> log.info(column + " " + type));
    }
}