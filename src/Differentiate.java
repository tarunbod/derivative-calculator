import java.util.Arrays;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

// Enumeration of all possible tokens in an input
enum TokenType {
    WHITESPACE("\\s+"),
    OPEN_PARENTHESIS("\\("),
    CLOSE_PARENTHESIS("\\)"),
    PLUS("\\+"),
    MINUS("\\-"),
    MULTIPLY("\\*"),
    DIVIDE("\\/"),
    POWER("\\^"),
    CONSTANT("\\d+(\\.\\d+)?"),
    FUNCTION("[a-z]{2,}"),
    VARIABLE("[a-z]{1}");

    private Pattern pattern;

    TokenType(String value) {
        this.pattern = Pattern.compile(value);
    }

    Pattern getPattern() {
        return pattern;
    }

}

// Represents a single Token, which can be a single character or a function name
class Token {
    private String chars;
    private TokenType type;
    private int start, end;

    Token(String chars, TokenType type, int start, int end) {
        this.chars = chars;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    String chars() {
        return chars;
    }

    TokenType type() {
        return type;
    }

    boolean isDouble() {
        try {
            Double.parseDouble(chars);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    double asDouble() {
        return Double.parseDouble(chars);
    }

    int start() {
        return start;
    }

    int end() {
        return end;
    }

    int compareTo(Token other) {
        return this.start - other.start;
    }

    public String toString() {
        return String.format("%17s | %5s | [%d-%d]", type.name(), chars, start, end);
    }
}

// Processes input and returns a list of tokens
class Tokenizer {
    static List<Token> processInput(String input) {
        input += " + 0"; // i hate myself for this
        List<Token> tokens = new ArrayList<>();
        Matcher matcher;
        for (TokenType type : TokenType.values()) {
            matcher = type.getPattern().matcher(input);
            while (matcher.find()) {
                int start = matcher.start(), end = matcher.end();
                tokens.add(new Token(input.substring(start, end), type, start, end));
            }
        }
        tokens.sort(Token::compareTo);
        // tokens = tokens.stream().sort(Token::compareTo).filter(t -> t.type() != TokenType.WHITESPACE).collect(Collectors::toList);
        List<Token> functions = new ArrayList<>();
        for (Token t : tokens) {
            if (t.type() == TokenType.FUNCTION) {
                functions.add(t);
            }
        }

        List<Token> toRemove = new ArrayList<>();
        for (Token f : functions) {
            for (Token t : tokens) {
                if (t.type() == TokenType.FUNCTION) continue;
                if (t.start() >= f.start() && t.end() <= f.end()) {
                    toRemove.add(t);
                }
            }
        }

        tokens.removeAll(toRemove);
        return tokens;
    }
}

// Takes input, converts into tokens, takes the tokens and creates an expression tree
class Parser {

    private List<Token> tokens;
    private Expression expression;

    Parser(String input) {
        this.tokens = Tokenizer.processInput(input);
        this.expression = processTokens();
        // TODO TODO TODO
        // - do parentheses first (shit)
    }

    public Expression getExpression() {
        return expression;
    }

    // NOTE:
    // this method doesn't work with sums inside parentheses, lol gotta fix that
    private Expression processTokens() {
        Expression root = new Expression(ExpressionType.SUM);
        List<Token> currentTerm = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            if (current.type() == TokenType.WHITESPACE) {
                continue;
            }
            if (current.type() == TokenType.PLUS || current.type() == TokenType.MINUS) {
                root.getArguments().add(processTerm(currentTerm, current.type() == TokenType.MINUS));
                currentTerm.clear();
                continue;
            }
            currentTerm.add(current);
        }
//        root.getArguments().add(processTerm(currentTerm));
        return root;
    }

    private Expression processTerm(List<Token> term, boolean negative) {
//        List<Token> currentFactor = new ArrayList<>();
//        for (int i = 0; i < term.size(); i++) {
//            Token current = term.get(i);
//            if (current.type() == TokenType.MULTIPLY || current.type() == TokenType.DIVIDE) {
//                processFactor(currentFactor);
//                currentFactor.clear();
//                continue;
//            }
//            currentFactor.add(current);
//        }
        boolean hasCoeff = term.get(0).isDouble();
        double coeff = hasCoeff ? term.get(0).asDouble() : 1;
        coeff = negative ? coeff * -1 : coeff;
        char var = term.get(hasCoeff ? 1 : 0).chars().charAt(0);
        double power = 1;
        if ((hasCoeff && term.size() > 2) || (!hasCoeff && term.size() > 1)) {
            power = term.get(hasCoeff ? 3 : 2).asDouble();
        }
        return new Monomial(coeff, var, power);
    }

//    private void processFactor(List<Token> factor) {
    // if (factor.size() == 1) {
    //     Token f = factor.get(0);
    //     if (f.type() == TokenType.CONSTANT) {

    //     }
    //     return;
    // }
    // if (factor.get(0).type() == TokenType.NAME)  {
    //     // TODO
    //     return;
    // }
    // for (Token part : factor) {
    //     if (part.type() == TokenType.NAME {
    //         // TODO
    //     } else if (part.type() == )
    // }
//    }

}

// Expression Tree
// Nodes:
// - types: addition, multiplication, 

enum ExpressionType {
    CONSTANT(false),
    MONOMIAL(false),
    SUM,
    PRODUCT,
    QUOTIENT,
    FUNCTION;

    boolean arguments;

    ExpressionType() {
        this(true);
    }

    ExpressionType(boolean arguments) {
        this.arguments = arguments;
    }

    boolean acceptsArguments() {
        return arguments;
    }
}

class Expression {
    private ExpressionType type;

    private List<Expression> arguments;

    Expression(ExpressionType type, List<Expression> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    Expression(ExpressionType type) {
        this(type, new ArrayList<>());
    }

    ExpressionType getType() {
        return type;
    }

    List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Expression: ").append(type.name()).append("\n");
        if (arguments != null) {
            for (Expression e : arguments) {
                str.append("\t").append(e.toString()).append("\n");
            }
        }
        return str.toString();
    }

    Expression getDerivative() {
        if (type == ExpressionType.SUM) {
            Expression derivative = new Expression(ExpressionType.SUM);
            for (Expression arg : arguments) {
                Expression argDerivative = arg.getDerivative();
                if (argDerivative != null) {
                    derivative.getArguments().add(argDerivative);
                }
            }
            return derivative;
        }
        return null;
    }
}

class Monomial extends Expression {

    private double coefficient;
    private char variable;
    private double power;

    public Monomial(double coefficient, char variable, double power) {
        super(ExpressionType.MONOMIAL, null);
        this.coefficient = coefficient;
        this.variable = variable;
        this.power = power;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public char getVariable() {
        return variable;
    }

    public double getPower() {
        return power;
    }

    @Override
    public String toString() {
        return super.toString() + coefficient + "*" + variable + "^" + power;
    }

    // beautiful representation of the power rule for derivatives
    // d/dx (ax^n) = (a*n*x^(n-1))
    @Override
    Expression getDerivative() {
        if (power == 1) {
            return new Constant(coefficient * power);
        }
        return new Monomial(coefficient * power, variable, power - 1);
    }
}

class Constant extends Expression {
    private double value;

    Constant(double value) {
        super(ExpressionType.CONSTANT, null);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    Expression getDerivative() {
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + value;
    }
}

// class Traverser {

// }

// class Transformer {

// }
