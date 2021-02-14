package ru.sfedu.studyProject.lab2;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.studyProject.lab2.model.TestEntity;

import java.util.Date;
import java.util.Optional;

@Log4j2
class HibernateDataProviderTest {

    @BeforeEach
    void setUp() {
        HibernateDataProvider.setUp();
    }


    @Test
    void getTestEntity() {
        TestEntity entity = new TestEntity();
        entity.setCheck(false);
        entity.setDateCreated(new Date(System.currentTimeMillis()));
        entity.setName("testGet");
        entity.setDescription("test");
        Assertions.assertTrue(HibernateDataProvider.setTestEntity(entity));
        Optional<TestEntity> optBaseEntity = HibernateDataProvider.getTestEntity(entity.getId());
        Assertions.assertTrue(optBaseEntity.isPresent());
        Assertions.assertEquals(entity, optBaseEntity.get());
        log.info(optBaseEntity.get());
    }

    @Test
    void setTestEntity() {
        TestEntity entity = new TestEntity();
        entity.setCheck(false);
        entity.setDateCreated(new Date(System.currentTimeMillis()));
        entity.setName("testGet");
        entity.setDescription("test");
        Assertions.assertTrue(HibernateDataProvider.setTestEntity(entity));
        Optional<TestEntity> optBaseEntity = HibernateDataProvider.getTestEntity(entity.getId());
        Assertions.assertTrue(optBaseEntity.isPresent());
        Assertions.assertEquals(entity, optBaseEntity.get());
        log.info(optBaseEntity.get());
    }

    @Test
    void deleteTestEntity() {
        TestEntity entity = new TestEntity();
        entity.setCheck(false);
        entity.setDateCreated(new Date(System.currentTimeMillis()));
        entity.setName("testGet");
        entity.setDescription("test");
        Assertions.assertTrue(HibernateDataProvider.setTestEntity(entity));
        Optional<TestEntity> optBaseEntity = HibernateDataProvider.getTestEntity(entity.getId());
        Assertions.assertTrue(optBaseEntity.isPresent());
        Assertions.assertEquals(entity, optBaseEntity.get());
        log.info(optBaseEntity.get());
        Assertions.assertTrue(HibernateDataProvider.deleteTestEntity(entity));
        optBaseEntity = HibernateDataProvider.getTestEntity(entity.getId());
        Assertions.assertFalse(optBaseEntity.isPresent());
    }
}