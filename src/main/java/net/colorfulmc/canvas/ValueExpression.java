package net.colorfulmc.canvas;

/**
 * Base interface for expressions that return arbitrary values.
 */
public interface ValueExpression {
    /**
     * Evaluate the expression and return its value.
     *
     * @param context The context to evaluate against
     * @return The result of evaluating the expression
     */
    Object evaluate(Context context);
}
