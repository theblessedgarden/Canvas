# Canvas
A powerful Java expression evaluation and templating library with a clean, flexible API.

> [!WARNING]  
> This library is (mostly) made by various LLMs; while I was playing around.
> It is stable, but there may be bugs.

## Overview
Canvas provides a lightweight framework for parsing, evaluating, and working with expressions in Java applications. It supports both mathematical and boolean logic operations, field access with dot notation, and string templating with expression-based substitutions.

## Features

### Expression Evaluation
Canvas can parse and evaluate various types of expressions:

- **Mathematical Expressions**: Supports standard operators (+, -, *, /, %, ^) with proper precedence
  ```java
  Expression expr = ExpressionParser.parse("{score * 2 + bonus}");
  boolean result = expr.evaluate(context); // Converts non-boolean results appropriately
  ```

- **Comparison Expressions**: Compare values using operators (==, !=, >, <, >=, <=, is)
  ```java
  Expression expr = ExpressionParser.parse("{user.age >= 18}");
  boolean isAdult = expr.evaluate(context);
  ```

- **Field Access**: Access nested properties using dot notation
  ```java
  Expression expr = ExpressionParser.parse("{player.stats.highScore > 1000}");
  boolean isHighScorer = expr.evaluate(context);
  ```

### Context System
Canvas uses a flexible, immutable context system for passing data to expressions:

- **Immutable Context**: Thread-safe with copy-on-write semantics
  ```java
  Context context = new MapContext()
      .with("user", userObject)
      .with("settings", settingsObject);
  ```

- **Mutable Context**: When you need to update values
  ```java
  MutableContext context = new MutableContext();
  context.set("counter", 0);
  context.set("counter", 1); // Updates the value
  ```

- **Context Merging**: Combine multiple contexts
  ```java
  Context merged = Context.merge(globalContext, sessionContext, requestContext);
  ```

### Templating
Process string templates with expression-based substitutions:

```java
String template = "Hello, ${user.name}! Your score is ${score}.";
String result = Template.process(template, context);
```

### Reflection-Based Property Access

Access object properties via reflection with proper annotation support:

```java
public class User {
    @Exposed(name="fullName")
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

// Then in expressions:
Expression expr = ExpressionParser.parse("{user.fullName == 'John Doe'}");
```

## Example Usage

### Basic Expression Evaluation

```java
// Create a context with some values
Context context = new MapContext()
    .with("x", 10)
    .with("y", 5);

// Parse and evaluate expressions
Expression mathExpr = ExpressionParser.parse("{x + y * 2}");
// Result: true (10 + 5*2 = 20, non-zero is true)
boolean mathResult = mathExpr.evaluate(context);

Expression comparisonExpr = ExpressionParser.parse("{x > y}");
// Result: true (10 > 5)
boolean comparisonResult = comparisonExpr.evaluate(context);
```

### Working with Objects

```java
Player player = new Player("Alice", 95);
Context context = new MapContext().with("player", player);

Expression expr = ExpressionParser.parse("{player.name == 'Alice' && player.score > 90}");
boolean result = expr.evaluate(context); // true
```

### String Templating

```java
Context context = new MapContext()
    .with("user", new User("John", "Doe"))
    .with("items", 5)
    .with("total", 125.50);

String template = "Dear ${user.fullName},\nYour order of ${items} items totaling $${total} has been processed.";
String message = Template.process(template, context);
```

## Design Philosophy
Canvas is designed with these principles in mind:

1. **Immutability**: Core classes are immutable for thread safety
2. **Composability**: Expressions can be nested and combined
3. **Flexibility**: Expression evaluation works with various data types
4. **Simplicity**: Clean, intuitive API with minimal dependencies

## Implementation Details
The library uses a recursive descent parser to handle expressions with proper operator precedence. It implements a comprehensive type conversion system for mathematical operations and comparisons between different data types.

## License
This project is released into the public domain.

For more information, see the [UNLICENSE](UNLICENSE) file and visit [unlicense.org](https://unlicense.org).