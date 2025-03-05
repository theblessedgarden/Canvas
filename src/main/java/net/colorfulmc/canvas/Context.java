package net.colorfulmc.canvas;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Core interface for accessing contextual data by key.
 */
public interface Context {
    /**
     * Get a value from the context by key.
     *
     * @param key The key to look up
     * @return The value, or null if not found
     */
    Object get(String key);

    /**
     * Create a new context with an additional key-value pair.
     *
     * @param key The key to add
     * @param value The value to associate with the key
     * @return A new context with the additional entry
     */
    Context with(String key, Object value);

    /**
     * Create a new context that merges multiple contexts.
     * Later contexts override values from earlier ones.
     *
     * @param contexts The contexts to merge
     * @return A new context containing all values
     */
    static Context merge(Context... contexts) {
        Map<String, Object> merged = new HashMap<>();

        for (Context context : contexts) {
            if (context instanceof MapContext mapContext) {
                merged.putAll((mapContext.getDataMap()));
            } else if (context instanceof MutableContext mutableContext) {
                merged.putAll(mutableContext.getDataMap());
            }
        }

        return new MapContext(merged);
    }
}
