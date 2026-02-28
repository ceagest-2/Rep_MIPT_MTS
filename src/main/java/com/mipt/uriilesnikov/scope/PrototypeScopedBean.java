package com.mipt.uriilesnikov.scope;

import java.util.UUID;

/**
 * Bean with prototype scope.
 * Unique Identifier Generator.
 */
public class PrototypeScopedBean {
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
