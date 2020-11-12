package ru.sfedu.studyProject.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.studyProject.Constants;
import ru.sfedu.studyProject.model.Task;
import ru.sfedu.studyProject.utils.ConfigurationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TaskConverter extends AbstractBeanField<Task, Integer> {

    private static final Logger log = LogManager.getLogger(TaskConverter.class);

    @Override
    protected Object convert(String s) {
        try {
            String indexString = s.substring(1, s.length() - 1);
            String[] unparsedIndexList = indexString.split(
                    ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_DELIMITER));
            List<Task> indexTaskList = new ArrayList<>();
            for (String sIndex : unparsedIndexList) {
                Task task = new Task();
                task.setId(Long.parseLong(sIndex));
                indexTaskList.add(task);
            }
            return indexTaskList;
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    protected String convertToWrite(Object value) {
        try {
            List<Task> taskList = (List<Task>) value;
            StringBuilder builder = new StringBuilder(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_START_SYMBOL));
            if (taskList.size() > 0) {
                for (Task task : taskList) {
                    builder.append(task.getId());
                    builder.append(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_DELIMITER));
                }

                builder.delete(builder.length() - 1, builder.length());
            }
            builder.append(ConfigurationUtil.getConfigurationEntry(Constants.ARRAY_END_SYMBOL));
            log.debug(builder.toString());
            return builder.toString();
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
