package net.colorfulmc.canvas;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard implementation of Context backed by a Map.
 */
public class MapContext implements Context {
    private final Map<String, Object> data;

    /**
     * Create a new MapContext with the provided data.
     */
    public MapContext(Map<String, Object> data) {
        // Defensive copy to ensure immutability
        this.data = Collections.unmodifiableMap(new HashMap<>(data));
    }

    /**
     * Create an empty MapContext.
     */
    public MapContext() {
        this(Collections.emptyMap());
    }

    @Override
    public Object get(String key) {
        return data.get(key);
    }

    @Override
    public Context with(String key, Object value) {
        Map<String, Object> newData = new HashMap<>(data);
        newData.put(key, value);
        return new MapContext(newData);
    }

    /**
     * Get the underlying data map.
     * Used internally for merging.
     */
    Map<String, Object> getDataMap() {
        return data;
    }
}
