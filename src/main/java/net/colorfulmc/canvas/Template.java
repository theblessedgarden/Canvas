package net.colorfulmc.canvas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template processor that replaces placeholders in strings.
 */
public class Template {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.*?)}");
    private final String template;

    /**
     * Create a new template from a string.
     *
     * @param template The template string containing placeholders
     */
    public Template(String template) {
        this.template = template;
    }

    /**
     * Process the template by replacing placeholders with values from the context.
     *
     * @param context The context containing values to use
     * @return The processed string with placeholders replaced
     */
    public String process(Context context) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String path = matcher.group(1).trim();
            Object value = resolvePath(path, context);

            if (value == null) {
                throw new IllegalArgumentException("No value found for placeholder: " + path);
            }

            String replacement = value.toString();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private Object resolvePath(String path, Context context) {
        if (path.contains(".")) {
            return new FieldExpression(path).evaluate(context);
        } else {
            return context.get(path);
        }
    }

    /**
     * Convenience method to process a template string directly.
     *
     * @param template The template string
     * @param context The context to use
     * @return The processed string
     */
    public static String process(String template, Context context) {
        return new Template(template).process(context);
    }
}
