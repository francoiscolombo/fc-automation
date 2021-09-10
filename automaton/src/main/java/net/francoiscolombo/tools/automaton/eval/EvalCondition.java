package net.francoiscolombo.tools.automaton.eval;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalCondition {

    private final static Pattern CONDITION_PATTERN = Pattern.compile("(\\S+)\\s*(==|!=|\\<|\\>|\\<=|\\>=)\\s*(\\S+)");
    private final static Pattern FILE_CONDITION_PATTERN = Pattern.compile("(exists|readable|writable)\\s*\\((.+)\\)");

    private final String expression;

    private EvalCondition(String expression) {
        this.expression = expression;
    }

    public static EvalCondition build(String expression) {
        return new EvalCondition(expression);
    }

    public boolean eval() {
        Matcher m = CONDITION_PATTERN.matcher(expression);
        if(m.find()) {
            String operator = m.group(2);
            switch (operator) {
                case "==":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 == i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 == d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            if("true".equalsIgnoreCase(left) || "false".equalsIgnoreCase(left)) {
                                boolean b1 = Boolean.parseBoolean(m.group(1));
                                boolean b2 = Boolean.parseBoolean(m.group(3));
                                return b1 == b2;
                            } else {
                                return left.equals(right);
                            }
                        }
                    }
                case "!=":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 != i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 != d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            if("true".equalsIgnoreCase(left) || "false".equalsIgnoreCase(left)) {
                                boolean b1 = Boolean.parseBoolean(m.group(1));
                                boolean b2 = Boolean.parseBoolean(m.group(3));
                                return b1 != b2;
                            } else {
                                return !left.equals(right);
                            }
                        }
                    }
                case ">":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 > i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 > d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            return left.compareTo(right) > 0;
                        }
                    }
                case ">=":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 >= i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 >= d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            return left.compareTo(right) >= 0;
                        }
                    }
                case "<":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 < i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 < d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            return left.compareTo(right) < 0;
                        }
                    }
                case "<=":
                    try {
                        long i1 = Long.parseLong(m.group(1));
                        long i2 = Long.parseLong(m.group(3));
                        return i1 <= i2;
                    } catch(NumberFormatException e) {
                        try {
                            double d1 = Double.parseDouble(m.group(1));
                            double d2 = Double.parseDouble(m.group(3));
                            return d1 <= d2;
                        } catch(NumberFormatException e1) {
                            String left = m.group(1);
                            String right = m.group(3);
                            return left.compareTo(right) <= 0;
                        }
                    }
                default:
                    throw new RuntimeException(String.format("Operator '%s' for condition <%s> is not allowed... Operation aborted.", operator, expression));
            }
        } else {
            Matcher mf = FILE_CONDITION_PATTERN.matcher(expression);
            if(mf.find()) {
                String func = m.group(1);
                String path = m.group(2);
                File file = Paths.get(path).toFile();
                switch (func) {
                    case "exists":
                        return file.exists();
                    case "readable":
                        return file.exists() && file.canRead();
                    case "writable":
                        return file.exists() && file.canWrite();
                    default:
                        throw new RuntimeException(String.format("Condition '%s' for testing file <%s> does not exists... Operation aborted.", func, path));
                }
            }
        }
        throw new RuntimeException(String.format("Expression <%s> is not a proper expression, I can't evaluate it! Operation aborted!", expression));
    }

}
