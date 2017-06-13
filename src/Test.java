import java.util.List;

public class Test {
    public static void main(String[] args) {
        String complexInput = "5x^4 + 6x^2 -& 42x * sin(5x / 6)";
        String simpleInput = "5x^4 + 6x^3 - x^2 + 25x";
        List<Token> tokens = Tokenizer.processInput(complexInput);
        for (Token t : tokens) {
            if (t.type() != TokenType.WHITESPACE) {
                System.out.println(t);
            }
        }
    }
}