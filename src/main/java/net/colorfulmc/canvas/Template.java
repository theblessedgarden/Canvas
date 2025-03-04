package net.colorfulmc.canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced template system that supports different output types.
 */
public class Template {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.*?)}");
    private final String template;
    private static final Map<Class<?>, TemplateProcessor<?>> PROCESSORS = new HashMap<>();

    // Register default string processor
    static {
        registerProcessor(String.class, new StringTemplateProcessor());
    }

    /**
     * Create a new template from a string.
     *
     * @param template The template string containing placeholders
     */
    public Template(String template) {
        this.template = template;
    }

    /**
     * Process the template using the default string processor.
     *
     * @param context The context containing values to use
     * @return The processed string with placeholders replaced
     */
    public String process(Context context) {
        return processAs(String.class, context);
    }

    /**
     * Process the template using a specific processor type.
     *
     * @param <T> The output type
     * @param type The class of the output type
     * @param context The context containing values to use
     * @return The processed output of type T
     * @throws IllegalArgumentException if no processor is registered for the type
     */
    @SuppressWarnings("unchecked")
    public <T> T processAs(Class<T> type, Context context) {
        TemplateProcessor<T> processor = (TemplateProcessor<T>) PROCESSORS.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("No template processor registered for type: " + type.getName());
        }
        return processor.process(template, context);
    }

    /**
     * Register a new template processor for a specific output type.
     *
     * @param <T> The output type
     * @param type The class of the output type
     * @param processor The processor to register
     */
    public static <T> void registerProcessor(Class<T> type, TemplateProcessor<T> processor) {
        PROCESSORS.put(type, processor);
    }

    /**
     * Convenience method to process a template string directly using the default processor.
     *
     * @param template The template string
     * @param context The context to use
     * @return The processed string
     */
    public static String process(String template, Context context) {
        return new Template(template).process(context);
    }

    /**
     * Convenience method to process a template string directly using a specific processor type.
     *
     * @param <T> The output type
     * @param type The class of the output type
     * @param template The template string
     * @param context The context to use
     * @return The processed output of type T
     */
    public static <T> T processAs(Class<T> type, String template, Context context) {
        return new Template(template).processAs(type, context);
    }

    /**
     * Utility method to resolve a path expression from the context.
     */
    public static Object resolvePath(String path, Context context) {
        if (path.contains(".")) {
            return new FieldExpression(path).evaluate(context);
        } else {
            return context.get(path);
        }
    }
}