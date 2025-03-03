package net.colorfulmc.canvas;

import java.util.HashMap;
import java.util.Map;

/**
 * A mutable context implementation for when values need to be updated.
 */
public class MutableContext implements Context {
    private final Map<String, Object> data = new HashMap<>();

    @Override
    public Object get(String key) {
        return data.get(key);
    }

    @Override
    public Context with(String key, Object value) {
        // Return a new immutable context with the combined data
        Map<String, Object> newData = new HashMap<>(data);
        newData.put(key, value);
        return new MapContext(newData);
    }

    /**
     * Add or update a value in this context.
     *
     * @param key The key to update
     * @param value The new value
     */
    public void set(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get the underlying data map.
     */
    Map<String, Object> getDataMap() {
        return new HashMap<>(data);
    }
}
