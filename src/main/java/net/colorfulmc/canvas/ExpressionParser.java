package net.colorfulmc.canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Parser for both comparison and mathematical expressions.
 */
public class ExpressionParser {
    // Comparison operators
    private static final List<String> COMPARISON_OPERATORS = Arrays.asList("==", "!=", ">=", "<=", ">", "<", "is");

    // Math operators in order of precedence (lower index = higher precedence)
    private static final List<String> MATH_OPERATORS = Arrays.asList("^", "*", "/", "%", "+", "-");

    /**
     * Parse an expression string into an Expression object.
     *
     * @param expr The expression string to parse
     * @return The parsed Expression
     */
    public static Expression parse(String expr) {
        expr = expr.trim();

        if (!expr.startsWith("{") || !expr.endsWith("}")) {
            throw new IllegalArgumentException("Expression must be enclosed in curly braces: " + expr);
        }

        // Remove the braces
        expr = expr.substring(1, expr.length() - 1).trim();

        // First check if it's a comparison expression
        String[] parts = splitByOperators(expr, COMPARISON_OPERATORS);
        if (parts.length == 3) {
            String fieldPath = parts[0].trim();
            String operator = parts[1].trim();
            String valueStr = parts[2].trim();

            ValueExpression left = parseValueExpression(fieldPath);
            ValueExpression right = parseValueExpression(valueStr);

            return new ComparisonExpression(left, operator, right);
        } else {
            // It's not a comparison, so return a MathExpression wrapped in a BooleanExpression
            ValueExpression mathExpr = parseValueExpression(expr);
            return new Expression() {
                @Override
                public boolean evaluate(Context context) {
                    Object result = mathExpr.evaluate(context);
                    if (result instanceof Boolean) {
                        return (Boolean) result;
                    } else if (result instanceof Number) {
                        // Non-zero is true, zero is false
                        return ((Number) result).doubleValue() != 0;
                    } else if (result == null) {
                        return false;
                    } else {
                        return true; // Non-null objects are true
                    }
                }
            };
        }
    }

    /**
     * Parse a string into a ValueExpression (which could be a MathExpression, FieldExpression, or LiteralExpression).
     */
    public static ValueExpression parseValueExpression(String expr) {
        expr = expr.trim();

        // Check for math expressions
        for (String op : MATH_OPERATORS) {
            int index = findOperatorIndex(expr, op);
            if (index > 0) {
                String leftStr = expr.substring(0, index).trim();
                String rightStr = expr.substring(index + op.length()).trim();

                ValueExpression left = parseValueExpression(leftStr);
                ValueExpression right = parseValueExpression(rightStr);

                return new MathExpression(left, op, right);
            }
        }

        // Not a math expression, so it's either a literal or field reference
        return parseValue(expr);
    }

    private static String[] splitByOperators(String expr, List<String> operators) {
        for (String op : operators) {
            int index = findOperatorIndex(expr, op);
            if (index > 0) {
                return new String[] {
                        expr.substring(0, index),
                        op,
                        expr.substring(index + op.length())
                };
            }
        }

        // No operator found
        return new String[] { expr };
    }

    /**
     * Find the index of an operator, but only if it's not inside parentheses
     * and is not part of another operator.
     */
    private static int findOperatorIndex(String expr, String op) {
        int parenthesesLevel = 0;

        for (int i = 0; i <= expr.length() - op.length(); i++) {
            if (expr.charAt(i) == '(') {
                parenthesesLevel++;
            } else if (expr.charAt(i) == ')') {
                parenthesesLevel--;
            } else if (parenthesesLevel == 0) {
                boolean match = true;
                for (int j = 0; j < op.length(); j++) {
                    if (expr.charAt(i + j) != op.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    // Make sure we're not inside another operator (e.g. finding "=" in ">=")
                    boolean operatorBoundary = true;
                    if (i > 0 && i + op.length() < expr.length()) {
                        char before = expr.charAt(i - 1);
                        char after = expr.charAt(i + op.length());
                        if ((before == '=' || before == '!' || before == '<' || before == '>' || before == '+' ||
                                before == '-' || before == '*' || before == '/' || before == '%' || before == '^') ||
                                (after == '=' || after == '!' || after == '<' || after == '>' || after == '+' ||
                                        after == '-' || after == '*' || after == '/' || after == '%' || after == '^')) {
                            operatorBoundary = false;
                        }
                    }

                    if (operatorBoundary) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    private static ValueExpression parseValue(String value) {
        value = value.trim();

        // Check if it's parenthesized
        if (value.startsWith("(") && value.endsWith(")")) {
            return parseValueExpression(value.substring(1, value.length() - 1));
        }

        // Check if it's a string literal
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
            return new LiteralExpression(value.substring(1, value.length() - 1));
        }

        // Check if it's a boolean
        if (value.equalsIgnoreCase("true")) {
            return new LiteralExpression(Boolean.TRUE);
        }
        if (value.equalsIgnoreCase("false")) {
            return new LiteralExpression(Boolean.FALSE);
        }

        // Check if it's a number
        try {
            if (value.contains(".")) {
                return new LiteralExpression(Double.parseDouble(value));
            } else {
                return new LiteralExpression(Integer.parseInt(value));
            }
        } catch (NumberFormatException ignored) {
            // Not a number, treat as a field reference
            return new FieldExpression(value);
        }
    }
}