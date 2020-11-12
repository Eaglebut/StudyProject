package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

//TODO
public class TemplateConverter extends AbstractBeanField {
    @Override
    protected Object convert(String s) {
        return null;
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        return super.convertToWrite(value);
    }
}
