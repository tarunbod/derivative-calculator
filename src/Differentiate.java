import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    private TokenType(String value) {
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

    Parser(String input) {
        this.tokens = Tokenizer.processInput(input);
        processTokens();
        // TODO
        // - put them in a tree of sorts
        // - distinguish between variable / function
        // Procedure:
        // - split tokens by addition/subtraction
        // - then split multiplication
        // - can do funtions later
        //
        // TODO TODO
        // - do parentheses first (shit)
    }

    private void processTokens() {
        List<Token> currentTerm = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token current = tokens.get(i);
            if (current.type() == TokenType.WHITESPACE) { continue; }
            if (current.type() == TokenType.PLUS || current.type() == TokenType.MINUS) {
                processTerm(currentTerm);
                currentTerm.clear();
                continue;
            }
            currentTerm.add(current);
        }
    }

    private void processTerm(List<Token> term) {
        List<Token> currentFactor = new ArrayList<>();
        for (int i = 0; i < term.size(); i++) {
            Token current = term.get(i);
            if (current.type() == TokenType.MULTIPLY || current.type() == TokenType.DIVIDE) {
                processFactor(currentFactor);
                currentFactor.clear();
                continue;
            }
            currentFactor.add(current);
        }
    }

    private void processFactor(List<Token> factor) {
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
    }

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

    boolean complex;

    private ExpressionType() {
        this(true);
    }

    private ExpressionType(boolean complex) {
        this.complex = complex;
    }

    boolean isComplex() {
        return complex;
    }
}

class Expression {
    private ExpressionType type;
    private List<Expression> arguments;

    Expression(ExpressionType type, List<Expression> arguments) {
        this.type = type;
        this.arguments = arguments;
    }
}

//class Monomial extends Expression {
//    public Monomial(int coefficient, int power)
//}

// class Traverser {

// }

// class Transformer {

// }
