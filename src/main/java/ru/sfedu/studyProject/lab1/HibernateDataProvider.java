package ru.sfedu.studyProject.lab1;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.sfedu.studyProject.utils.HibernateUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class HibernateDataProvider {

    public static List<String> getTableList() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List list = session
                .createSQLQuery(SqlCommands.SELECT_TABLE_NAMES)
                .list();
        session.getTransaction().commit();
        return list;
    }

    public static int getTableCount() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        BigInteger tableCount = (BigInteger) session
                .createSQLQuery(SqlCommands.SELECT_TABLE_COUNT)
                .getSingleResult();
        session.getTransaction().commit();
        return tableCount.intValue();
    }

    public static List<String> getColumnList(String tableName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List list = session
                .createSQLQuery(String.format(SqlCommands.SELECT_COLUMN_NAMES, tableName))
                .list();
        session.getTransaction().commit();
        return list;
    }

    public static Map<String, String> getTableColumns(String tableName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Object[]> list = session
                .createSQLQuery(String.format(SqlCommands.SELECT_COLUMN_NAME_TYPE,
                        tableName))
                .list();
        session.getTransaction().commit();

        Map<String, String> tableColumns = new HashMap<>();
        list.forEach(column -> tableColumns.put(column[0].toString(), column[1].toString()));
        return tableColumns;
    }


}
