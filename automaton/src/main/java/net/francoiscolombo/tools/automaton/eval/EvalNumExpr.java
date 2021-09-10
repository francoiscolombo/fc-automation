package net.francoiscolombo.tools.automaton.eval;

import java.math.BigDecimal;
import java.math.MathContext;

// Grammar:
// expression = term | expression `+` term | expression `-` term
// term = factor | term `*` factor | term `/` factor
// factor = `+` factor | `-` factor | `(` expression `)`
//        | number | functionName factor | factor `^` factor
public class EvalNumExpr {

    // precision : 4 digits
    private final static int PRECISION = 4;

    private final String expression;
    private int pos = -1, ch;

    private EvalNumExpr(String expression) {
        this.expression = expression;
    }

    public static EvalNumExpr build(String expression) {
        return new EvalNumExpr(expression);
    }

    public BigDecimal parse() {
        nextChar();
        BigDecimal exp = parseExpression();
        if (pos < expression.length()) {
            throw new RuntimeException("Unexpected: " + (char)ch);
        }
        return exp;
    }

    private void nextChar() {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') {
            nextChar();
        }
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    BigDecimal parseExpression() {
        BigDecimal term = parseTerm();
        for (;;) {
            if (eat('+')) {
                term = term.add(parseTerm()); // addition
            }
            else if (eat('-')) {
                term = term.subtract(parseTerm()); // subtraction
            }
            else {
                return term;
            }
        }
    }

    BigDecimal parseTerm() {
        BigDecimal factor = parseFactor();
        for (;;) {
            if (eat('*')) {
                factor = factor.multiply(parseFactor()); // multiplication
            }
            else if (eat('/')) {
                factor = factor.divide(parseFactor()); // division
            }
            else {
                return factor;
            }
        }
    }

    BigDecimal parseFactor() {
        if (eat('+')) {
            return parseFactor().plus(); // unary plus
        }
        if (eat('-')) {
            return parseFactor().negate(); // unary minus
        }
        BigDecimal x;
        int startPos = this.pos;
        if (eat('(')) { // parentheses
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                nextChar();
            }
            x = new BigDecimal(expression.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // functions
            while (ch >= 'a' && ch <= 'z') {
                nextChar();
            }
            String func = expression.substring(startPos, this.pos);
            MathContext mc = new MathContext(PRECISION);
            x = parseFactor();
            switch (func) {
                case "sqrt":
                    x = x.sqrt(mc);
                    break;
                case "sin":
                    x = BigDecimal.valueOf(Math.sin(Math.toRadians(x.doubleValue())));
                    break;
                case "cos":
                    x = BigDecimal.valueOf(Math.cos(Math.toRadians(x.doubleValue())));
                    break;
                case "tan":
                    x = BigDecimal.valueOf(Math.tan(Math.toRadians(x.doubleValue())));
                    break;
                default:
                    throw new RuntimeException("Unknown function: " + func);
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char)ch);
        }
        if (eat('^')) {
            x = x.pow(parseFactor().intValue()); // exponentiation
        }
        return x;
    }

}
