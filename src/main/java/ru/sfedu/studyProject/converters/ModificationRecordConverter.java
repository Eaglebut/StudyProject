package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.utils.PropertyLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModificationRecordConverter extends AbstractBeanField<ModificationRecord, Integer> {

  private static final Logger log = LogManager.getLogger(ModificationRecordConverter.class);

  @Override
  protected Object convert(String s) {
    if (s.isEmpty()) {
      return null;
    }
    try {
      String indexString = s.substring(1, s.length() - 1);
      String[] unparsedIndexList = indexString.split(
              PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
      List<ModificationRecord> indexModificationRecordList = new ArrayList<>();
      for (String strIndex : unparsedIndexList) {
        if (!strIndex.isEmpty()) {
          ModificationRecord modificationRecord = new ModificationRecord();
          modificationRecord.setId(Long.parseLong(strIndex));
          indexModificationRecordList.add(modificationRecord);
        }
      }
      return indexModificationRecordList;
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  protected String convertToWrite(Object value) {
    try {
      List<ModificationRecord> modificationRecordList = (List<ModificationRecord>) value;
      StringBuilder builder = new StringBuilder(PropertyLoader.getProperty(Constants.ARRAY_START_SYMBOL));
      if (modificationRecordList.size() != 0) {
        for (ModificationRecord modificationRecord : modificationRecordList) {
          builder.append(modificationRecord.getId());
          builder.append(PropertyLoader.getProperty(Constants.ARRAY_DELIMITER));
        }
        builder.delete(builder.length() - 1, builder.length());
      }
      builder.append(PropertyLoader.getProperty(Constants.ARRAY_END_SYMBOL));
      log.debug(builder.toString());
      return builder.toString();
    } catch (IOException e) {
      log.error(e);
      return null;
    }
  }
}
