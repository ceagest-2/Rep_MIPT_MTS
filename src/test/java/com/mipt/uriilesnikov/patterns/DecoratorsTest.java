package com.mipt.uriilesnikov.patterns;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class DecoratorsTest {

    private DataService service;

    @BeforeEach
    void setUp() {
        service = new ValidationDecorator(
                new MetricableDecorator(
                        new LoggingDecorator(
                                new CachingDecorator(
                                        new SimpleDataService()
                                )
                        )
                )
        );
    }

    @Test
    void testSaveAndFind() {
        service.saveData("testKey", "testData");
        Optional<String> found = service.findByKey("testKey");
        assertTrue(found.isPresent());
        assertEquals("testData", found.get());
    }

    @Test
    void testDelete() {
        service.saveData("deleteKey", "toDelete");
        assertTrue(service.deleteData("deleteKey"));
        assertFalse(service.deleteData("deleteKey"));
    }

    @Test
    void testFindByKeyNotFound() {
        Optional<String> found = service.findByKey("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testCacheWorks() {
        service.saveData("cachedKey", "cachedValue");
        service.findByKey("cachedKey");
        service.findByKey("cachedKey");
    }

    @Test
    void testValidationThrowsOnNullKey() {
        assertThrows(IllegalArgumentException.class, () -> service.saveData(null, "data"));
    }

    @Test
    void testValidationThrowsOnEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> service.saveData("", "data"));
    }

    @Test
    void testValidationThrowsOnNullData() {
        assertThrows(IllegalArgumentException.class, () -> service.saveData("key", null));
    }

    @Test
    void testLoggingOutput() {
        service.saveData("logTest", "logData");
        service.findByKey("logTest");
        service.deleteData("logTest");
    }

    @Test
    void testMetricsSent() {
        service.saveData("metricTest", "metricData");
        service.findByKey("metricTest");
        service.deleteData("metricTest");
    }
}
