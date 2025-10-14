package com.otabi.jcodroneedu.storage;

import java.util.HashMap;

public class StorageCount
{
    private HashMap<String, Long> storageCounts = new HashMap<>();

    // Expose a simple accessor used by other components or tests. This avoids IDE
    // warnings about an unused private field and makes the class minimally useful.
    public synchronized void set(String key, long value) {
        storageCounts.put(key, value);
    }
    public synchronized Long get(String key) {
        return storageCounts.get(key);
    }
}
