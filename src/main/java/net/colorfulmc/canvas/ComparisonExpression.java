package net.colorfulmc.canvas;

/**
 * Expression that compares two values.
 */
public class ComparisonExpression implements Expression {
    private final ValueExpression left;
    private final String operator;
    private final ValueExpression right;

    public ComparisonExpression(ValueExpression left, String operator, ValueExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean evaluate(Context context) {
        Object leftVal = left.evaluate(context);
        Object rightVal = right.evaluate(context);

        return switch (operator) {
            case "==", "is" -> equals(leftVal, rightVal);
            case "!=" -> !equals(leftVal, rightVal);
            case ">" -> compare(leftVal, rightVal) > 0;
            case "<" -> compare(leftVal, rightVal) < 0;
            case ">=" -> compare(leftVal, rightVal) >= 0;
            case "<=" -> compare(leftVal, rightVal) <= 0;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    private boolean equals(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compare(Object a, Object b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Cannot compare null values");
        }

        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }

        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }

        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable) a).compareTo(b);
        }

        throw new IllegalArgumentException(
                "Cannot compare values of types " + a.getClass() + " and " + b.getClass());
    }
}
