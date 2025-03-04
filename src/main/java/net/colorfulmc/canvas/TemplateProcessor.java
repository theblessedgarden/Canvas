package net.colorfulmc.canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic interface for template processors supporting different output formats.
 *
 * @param <T> The output type of the processor
 */
public interface TemplateProcessor<T> {
    /**
     * Process the template with the given context.
     *
     * @param template The template string containing placeholders
     * @param context The context containing values to use
     * @return The processed output of type T
     */
    T process(String template, Context context);
}
