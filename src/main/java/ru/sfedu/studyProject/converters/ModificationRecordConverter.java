package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.DataProviders.DataProviderCSV;
import ru.sfedu.studyProject.model.ModificationRecord;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModificationRecordConverter extends AbstractBeanField<ModificationRecord, Integer> {

    private static final Logger log = LogManager.getLogger(DataProviderCSV.class);

    @Override
    protected Object convert(String s) {
        try {
            String indexString = s.substring(1, s.length() - 1);
            String[] unparsedIndexList = indexString.split(
                    ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_DELIMITER));
            List<ModificationRecord> indexModificationRecordList = new ArrayList<>();
            for (String sIndex : unparsedIndexList) {
                ModificationRecord modificationRecord = new ModificationRecord();
                modificationRecord.setId(Long.parseLong(sIndex));
                indexModificationRecordList.add(modificationRecord);
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
            StringBuilder builder = new StringBuilder(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_START_SYMBOL));
            for (ModificationRecord modificationRecord : modificationRecordList) {
                builder.append(modificationRecord.getId());
                builder.append(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_DELIMITER));
            }
            builder.delete(builder.length() - 1, builder.length());
            builder.append(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_END_SYMBOL));
            return builder.toString();
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}