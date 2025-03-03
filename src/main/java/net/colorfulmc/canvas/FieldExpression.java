package net.colorfulmc.canvas;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Expression that retrieves a value from the context by key or path.
 */
public class FieldExpression implements ValueExpression {
    private final String path;

    public FieldExpression(String path) {
        this.path = path;
    }

    @Override
    public Object evaluate(Context context) {
        String[] parts = path.split("\\.");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }

        Object current = context.get(parts[0]);
        if (current == null || parts.length == 1) {
            return current;
        }

        // Navigate through the object graph using reflection
        for (int i = 1; i < parts.length && current != null; i++) {
            current = getProperty(current, parts[i]);
        }

        return current;
    }

    private Object getProperty(Object obj, String name) {
        try {
            // Try getting a field directly
            java.lang.reflect.Field field = obj.getClass().getField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception ignored) {
            // Then try a getter method
            try {
                Method[] methods = obj.getClass().getMethods();

                for (Method method : methods) {
                    if (method.getAnnotation(Exposed.class) != null) {
                        Exposed annotation = method.getAnnotation(Exposed.class);

                        if (annotation.name().equals(name)) {
                            method.setAccessible(true);
                            return method.invoke(obj);
                        }
                    }
                }

                Field[] fields = obj.getClass().getFields();

                for (Field field : fields) {
                    if (field.getAnnotation(Exposed.class) != null) {
                        Exposed annotation = field.getAnnotation(Exposed.class);

                        if (annotation.name().equals(name)) {
                            field.setAccessible(true);
                            return field.get(obj);
                        }
                    }
                }

                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
