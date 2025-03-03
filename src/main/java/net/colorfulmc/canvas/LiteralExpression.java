package net.colorfulmc.canvas;

/**
 * An expression that always returns a fixed value.
 */
public class LiteralExpression implements ValueExpression {
    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Context context) {
        return value;
    }
}
