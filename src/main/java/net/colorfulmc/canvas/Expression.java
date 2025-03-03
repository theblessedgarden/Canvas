package net.colorfulmc.canvas;

/**
 * Base interface for all expressions that evaluate to a boolean result.
 */
public interface Expression {
    /**
     * Evaluate the expression against the provided context.
     *
     * @param context The context containing values to evaluate against
     * @return The boolean result of the expression
     */
    boolean evaluate(Context context);
}

