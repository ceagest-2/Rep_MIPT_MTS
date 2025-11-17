package com.mipt.uriilesnikov.patterns;

import java.util.Optional;

public class LoggingDecorator implements DataService {

    private final DataService delegate;

    public LoggingDecorator(DataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<String> findByKey(String key) {
        System.out.println("Logging: findByKey called with key=" + key);
        Optional<String> result = delegate.findByKey(key);
        System.out.println("Logging: findByKey returned " + result.orElse("null"));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        System.out.println("Logging: saveData called with key=" + key + ", data=" + data);
        delegate.saveData(key, data);
        System.out.println("Logging: saveData completed");
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("Logging: deleteData called with key=" + key);
        boolean result = delegate.deleteData(key);
        System.out.println("Logging: deleteData returned " + result);
        return result;
    }
}
