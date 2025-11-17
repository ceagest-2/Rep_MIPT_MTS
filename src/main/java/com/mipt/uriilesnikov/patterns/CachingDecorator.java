package com.mipt.uriilesnikov.patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachingDecorator implements DataService {

    private final DataService delegate;
    private final Map<String, String> cache = new HashMap<>();

    public CachingDecorator(DataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<String> findByKey(String key) {
        if (cache.containsKey(key)) {
            return Optional.of(cache.get(key));
        }
        Optional<String> result = delegate.findByKey(key);
        result.ifPresent(data -> cache.put(key, data));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        delegate.saveData(key, data);
        cache.put(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        boolean deleted = delegate.deleteData(key);
        if (deleted) {
            cache.remove(key);
        }
        return deleted;
    }
}
