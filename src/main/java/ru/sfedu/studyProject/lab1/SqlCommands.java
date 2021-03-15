package ru.sfedu.studyProject.lab1;

public class SqlCommands {
    public static final String SELECT_TABLE_NAMES = "select TABLE_NAME from information_schema.tables where table_schema = 'LAB2'";
    public static final String SELECT_TABLE_COUNT = "select count(TABLE_NAME) from information_schema.tables where table_schema = 'LAB2'";
    public static final String SELECT_COLUMN_NAME_TYPE = "select column_name, type_name from information_schema.columns where table_name = '%s'";
    public static final String SELECT_COLUMN_NAMES = "select column_name from information_schema.columns where table_name = '%s'";
}
