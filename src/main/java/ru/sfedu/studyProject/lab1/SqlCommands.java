package ru.sfedu.studyProject.lab1;

public class SqlCommands {
    public static final String SELECT_TABLE_NAMES = "select TABLE_NAME from information_schema.tables where table_schema = 'public'";
    public static final String SELECT_TABLE_COUNT = "select count(TABLE_NAME) from information_schema.tables where table_schema = 'public'";
    public static final String SELECT_COLUMN_NAME_TYPE = "select column_name, data_type from information_schema.columns where table_name = '%s'";
    public static final String SELECT_COLUMN_NAMES = "select column_name from information_schema.columns where table_name = '%s'";
}
