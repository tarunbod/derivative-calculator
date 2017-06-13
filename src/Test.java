public class Test {
    public static void main(String[] args) {
        String[] inputs = {
                "5x^4 + 6x^3 - x^2 + 25x",
                "5x^4 + 6x^2 -& 42x * sin(5x / 6)",
                "4x^3 + 6(2x + 7)"
        };

//        List<Token> tokens = Tokenizer.processInput(inputs[0]);
//        for (Token t : tokens) {
//            if (t.type() != TokenType.WHITESPACE) {
//                System.out.println(t);
//            }
//        }

        Parser p = new Parser(inputs[0]);
        System.out.println(p.getExpression());
        System.out.println(p.getExpression().getDerivative());
    }
}