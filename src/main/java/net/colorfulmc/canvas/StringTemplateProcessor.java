package net.colorfulmc.canvas;

import java.util.regex.Matcher;

/**
 * Default implementation of TemplateProcessor for String output.
 */
public class StringTemplateProcessor implements TemplateProcessor<String> {
    @Override
    public String process(String template, Context context) {
        Matcher matcher = Template.PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String path = matcher.group(1).trim();
            Object value = Template.resolvePath(path, context);

            if (value == null) {
                throw new IllegalArgumentException("No value found for placeholder: " + path);
            }

            String replacement = value.toString();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
