package com.mipt.uriilesnikov.patterns;

import java.util.Optional;

public interface DataService {
    Optional<String> findByKey(String key);

    void saveData(String key, String data);

    boolean deleteData(String key);
}
