package net.colorfulmc.canvas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTests {

    // --- ComparisonExpression Tests ---

    @Test
    public void testComparisonExpressionEquality() {
        // Test equality operator (== and "is")
        Context ctx = new MapContext();
        Expression expr1 = new ComparisonExpression(new LiteralExpression(5), "==", new LiteralExpression(5));
        Expression expr2 = new ComparisonExpression(new LiteralExpression("hello"), "is", new LiteralExpression("hello"));
        assertTrue(expr1.evaluate(ctx));
        assertTrue(expr2.evaluate(ctx));
    }

    @Test
    public void testComparisonExpressionInequality() {
        Context ctx = new MapContext();
        Expression expr = new ComparisonExpression(new LiteralExpression(5), "!=", new LiteralExpression(6));
        assertTrue(expr.evaluate(ctx));
    }

    @Test
    public void testComparisonExpressionGreaterThan() {
        Context ctx = new MapContext();
        Expression expr = new ComparisonExpression(new LiteralExpression(10), ">", new LiteralExpression(5));
        assertTrue(expr.evaluate(ctx));
    }

    @Test
    public void testComparisonExpressionLessThan() {
        Context ctx = new MapContext();
        Expression expr = new ComparisonExpression(new LiteralExpression(3), "<", new LiteralExpression(7));
        assertTrue(expr.evaluate(ctx));
    }

    @Test
    public void testComparisonExpressionUnsupportedOperator() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Expression expr = new ComparisonExpression(new LiteralExpression(5), "??", new LiteralExpression(5));
            Context ctx = new MapContext();
            expr.evaluate(ctx);
        });
        assertTrue(exception.getMessage().contains("Unsupported operator"));
    }

    // --- MathExpression Tests ---

    @Test
    public void testMathExpressionAddition() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(5), "+", new LiteralExpression(3));
        Object result = expr.evaluate(ctx);
        assertEquals(8, ((Number) result).intValue());
    }

    @Test
    public void testMathExpressionSubtraction() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(10), "-", new LiteralExpression(4));
        Object result = expr.evaluate(ctx);
        assertEquals(6, ((Number) result).intValue());
    }

    @Test
    public void testMathExpressionMultiplication() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(4), "*", new LiteralExpression(3));
        Object result = expr.evaluate(ctx);
        assertEquals(12, ((Number) result).intValue());
    }

    @Test
    public void testMathExpressionDivision() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(20), "/", new LiteralExpression(4));
        Object result = expr.evaluate(ctx);
        // Division returns a double
        assertEquals(5.0, ((Number) result).doubleValue());
    }

    @Test
    public void testMathExpressionModulo() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(10), "%", new LiteralExpression(3));
        Object result = expr.evaluate(ctx);
        assertEquals(1, ((Number) result).longValue());
    }

    @Test
    public void testMathExpressionPower() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(2), "^", new LiteralExpression(3));
        Object result = expr.evaluate(ctx);
        assertEquals(8.0, ((Number) result).doubleValue());
    }

    @Test
    public void testMathExpressionDivisionByZero() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(10), "/", new LiteralExpression(0));
        assertThrows(ArithmeticException.class, () -> expr.evaluate(ctx));
    }

    @Test
    public void testMathExpressionModuloByZero() {
        Context ctx = new MapContext();
        ValueExpression expr = new MathExpression(new LiteralExpression(10), "%", new LiteralExpression(0));
        assertThrows(ArithmeticException.class, () -> expr.evaluate(ctx));
    }

    // --- ExpressionParser Tests ---

    @Test
    public void testExpressionParserComparison() {
        // Set up a context with a value for "a"
        Context ctx = new MapContext().with("a", 10);
        Expression expr = ExpressionParser.parse("{a > 5}");
        assertTrue(expr.evaluate(ctx));
    }

    @Test
    public void testExpressionParserMath() {
        // Math expressions are wrapped in an Expression that returns a boolean (non-zero => true)
        Expression expr = ExpressionParser.parse("{5 + 3}");
        Context ctx = new MapContext();
        assertTrue(expr.evaluate(ctx));
    }

    @Test
    public void testExpressionParserMissingBraces() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ExpressionParser.parse("5 + 3");
        });
        assertTrue(exception.getMessage().contains("enclosed in curly braces"));
    }

    // --- FieldExpression Tests ---

    @Test
    public void testFieldExpressionSimple() {
        // Create a dummy object with a public field
        class Dummy {
            public int value = 42;
        }
        Dummy dummy = new Dummy();
        Context ctx = new MapContext().with("dummy", dummy);
        ValueExpression expr = new FieldExpression("dummy.value");
        Object result = expr.evaluate(ctx);
        assertEquals(42, result);
    }

    // --- LiteralExpression Test ---

    @Test
    public void testLiteralExpression() {
        Context ctx = new MapContext();
        LiteralExpression expr = new LiteralExpression("hello");
        assertEquals("hello", expr.evaluate(ctx));
    }

    // --- MapContext Tests ---

    @Test
    public void testMapContextImmutability() {
        MapContext ctx = new MapContext();
        Context newCtx = ctx.with("key", "value");
        // Original context remains unchanged
        assertNull(ctx.get("key"));
        assertEquals("value", newCtx.get("key"));
    }

    // --- MutableContext Tests ---

    @Test
    public void testMutableContextSet() {
        MutableContext ctx = new MutableContext();
        ctx.set("key", 123);
        assertEquals(123, ctx.get("key"));
    }

    @Test
    public void testMutableContextWith() {
        MutableContext ctx = new MutableContext();
        ctx.set("key", "initial");
        Context newCtx = ctx.with("key", "updated");
        // New context should have the updated value while the mutable context remains unchanged
        assertEquals("initial", ctx.get("key"));
        assertEquals("updated", newCtx.get("key"));
    }

    // --- Template Tests ---

    @Test
    public void testTemplateProcessing() {
        // Test a simple template with a single placeholder
        Template template = new Template("Hello, ${name}!");
        Context ctx = new MapContext().with("name", "World");
        String result = template.process(ctx);
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testTemplateNestedFieldProcessing() {
        // Test template with nested field access: ${dummy.value}
        class Dummy {
            public int value = 99;
        }
        Dummy dummy = new Dummy();
        Context ctx = new MapContext().with("dummy", dummy);
        Template template = new Template("Value: ${dummy.value}");
        String result = template.process(ctx);
        assertEquals("Value: 99", result);
    }

    @Test
    public void testTemplateMissingPlaceholder() {
        Template template = new Template("Hello, ${name}!");
        Context ctx = new MapContext();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            template.process(ctx);
        });
        assertTrue(exception.getMessage().contains("No value found for placeholder"));
    }
}

