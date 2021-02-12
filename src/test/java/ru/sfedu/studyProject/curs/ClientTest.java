package ru.sfedu.studyProject.curs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class ClientTest {


    @Test
    void getTaskStatistic() {
        Client.main(new String[]{"DataProviderCsv", "getTaskStatistic"});
        Client.main(new String[]{"DataProviderXML", "getTaskStatistic"});
        Client.main(new String[]{"DataProviderJdbc", "getTaskStatistic"});
    }

    @Test
    void getTaskStatisticIncorrect() {
        Client.main(new String[]{"dawwa", "getTaskStatistic"});
        Client.main(new String[]{"", "getTaskStatistic"});
        Client.main(new String[]{"DataProviderXML", "awdawd"});
        Client.main(new String[]{"DataProviderJdbc", ""});
        Client.main(new String[]{"", ""});
    }

    @Test
    void getGroupStatistic() {
        Client.main(new String[]{"DataProviderCsv", "getGroupStatistic"});
        Client.main(new String[]{"DataProviderXML", "getGroupStatistic"});
        Client.main(new String[]{"DataProviderJdbc", "getGroupStatistic"});
    }

    @Test
    void getUserInfo() {
        Client.main(new String[]{"DataProviderCsv", "getUserInfo", "0"});
        Client.main(new String[]{"DataProviderXML", "getUserInfo", "0"});
        Client.main(new String[]{"DataProviderJdbc", "getUserInfo", "1"});
    }

}