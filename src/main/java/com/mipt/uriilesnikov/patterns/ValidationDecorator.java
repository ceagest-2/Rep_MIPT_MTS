package com.mipt.uriilesnikov.patterns;

import java.util.Optional;

public class ValidationDecorator implements DataService {

    private final DataService delegate;

    public ValidationDecorator(DataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<String> findByKey(String key) {
        validateKey(key);
        return delegate.findByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        validateKey(key);
        validateData(data);
        delegate.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        validateKey(key);
        return delegate.deleteData(key);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    private void validateData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
    }
}
