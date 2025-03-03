package net.colorfulmc.canvas;

/**
 * Expression that evaluates a mathematical operation between two values.
 */
public class MathExpression implements ValueExpression {
    private final ValueExpression left;
    private final String operator;
    private final ValueExpression right;

    public MathExpression(ValueExpression left, String operator, ValueExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object evaluate(Context context) {
        Object leftVal = left.evaluate(context);
        Object rightVal = right.evaluate(context);

        // Convert to numbers
        Number leftNum = convertToNumber(leftVal);
        Number rightNum = convertToNumber(rightVal);

        return switch (operator) {
            case "+" -> add(leftNum, rightNum);
            case "-" -> subtract(leftNum, rightNum);
            case "*" -> multiply(leftNum, rightNum);
            case "/" -> divide(leftNum, rightNum);
            case "%" -> modulo(leftNum, rightNum);
            case "^" -> power(leftNum, rightNum);
            default -> throw new IllegalArgumentException("Unsupported math operator: " + operator);
        };
    }

    private Number convertToNumber(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot perform math operations on null values");
        }

        if (value instanceof Number) {
            return (Number) value;
        }

        if (value instanceof String) {
            try {
                String str = (String) value;
                if (str.contains(".")) {
                    return Double.parseDouble(str);
                } else {
                    return Integer.parseInt(str);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert string to number: " + value);
            }
        }

        throw new IllegalArgumentException("Cannot convert to number: " + value);
    }

    private Number add(Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() + b.doubleValue();
        } else {
            return a.longValue() + b.longValue();
        }
    }

    private Number subtract(Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() - b.doubleValue();
        } else {
            return a.longValue() - b.longValue();
        }
    }

    private Number multiply(Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() * b.doubleValue();
        } else {
            return a.longValue() * b.longValue();
        }
    }

    private Number divide(Number a, Number b) {
        if (b.doubleValue() == 0) {
            throw new ArithmeticException("Division by zero");
        }

        // Always return double for division to handle fractions
        return a.doubleValue() / b.doubleValue();
    }

    private Number modulo(Number a, Number b) {
        if (b.doubleValue() == 0) {
            throw new ArithmeticException("Modulo by zero");
        }

        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() % b.doubleValue();
        } else {
            return a.longValue() % b.longValue();
        }
    }

    private Number power(Number a, Number b) {
        return Math.pow(a.doubleValue(), b.doubleValue());
    }
}
