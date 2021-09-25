package net.francoiscolombo.tools.automaton.ascript.interpreter;

import java.io.*;

import ascript.AScriptBaseVisitor;
import ascript.AScriptParser;
import ascript.ASExpressionParser;
import net.francoiscolombo.tools.automaton.ascript.exceptions.ContinueLoopException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.ExitLoopException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.InterpreterException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.TypeException;
import org.antlr.v4.runtime.ParserRuleContext;

public class AScriptVisitor extends AScriptBaseVisitor<Value> {

    final private InputStream stdin;
    final private PrintStream stdout;
    final private PrintStream stderr;
    final private Memory memory;

    private PrintStream printStream;
    private BufferedReader inputStream;

    public AScriptVisitor(Memory memory, InputStream stdin, PrintStream stdout, PrintStream stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
        this.memory = memory;
    }

    @Override
    public Value visitProg(AScriptParser.ProgContext ctx) {
        init();
        try {
            return super.visitProg(ctx);
        } finally {
            cleanup();
        }
    }

    private void init() {
        printStream = new PrintStream(stdout, true);
        inputStream = new BufferedReader(new InputStreamReader(stdin));
    }

    private void cleanup() {
        printStream.close();
    }

    @Override
    public Value visitString(AScriptParser.StringContext ctx) {
        String value = ctx.getText();
        return new Value(value.substring(1, value.length() - 1));
    }

    @Override
    public Value visitNumber(AScriptParser.NumberContext ctx) {
        return new Value(Long.parseLong(ctx.getText()));
    }

    @Override
    public Value visitId(AScriptParser.IdContext ctx) {
        String id = ctx.getText();
        return memory.get(id);
    }

    @Override
    public Value visitLetstmt(AScriptParser.LetstmtContext ctx) {
        String varname = ctx.vardecl().varname().ID().getText();
        Value value = visit(ctx.expression());
        memory.assign(varname, value);
        return value;
    }

    @Override
    public Value visitMulDivExpr(AScriptParser.MulDivExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        if (ctx.op.getType() == ASExpressionParser.MUL) {
            return left.mul(right);
        } else if (ctx.op.getType() == ASExpressionParser.DIV) {
            return left.div(right);
        } else {
            return left.mod(right);
        }
    }

    @Override
    public Value visitAddSubExpr(AScriptParser.AddSubExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        if (ctx.op.getType() == ASExpressionParser.ADD) {
            return left.add(right);
        } else {
            return left.sub(right);
        }
    }

    @Override
    public Value visitLenfunc(AScriptParser.LenfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            return new Value(arg.internalString().length());
        } else {
            throw new TypeException("Couldn't evaluate LEN(). Argument is not a string");
        }
    }

    @Override
    public Value visitValfunc(AScriptParser.ValfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            String str = arg.internalString();
            try {
                return new Value(Long.parseLong(str));
            } catch (NumberFormatException e) {
                return Value.NaN;
            }
        }
        return arg;
    }

    @Override
    public Value visitIsnanfunc(AScriptParser.IsnanfuncContext ctx) {
        Value arg = visit(ctx.expression());
        return arg.isNaN() ? Value.TRUE : Value.FALSE;
    }

    @Override
    public Value visitStatement(AScriptParser.StatementContext ctx) {
        try {
            return super.visitStatement(ctx);
        } catch (TypeException e) {
            addLocation(e, ctx);
            throw e;
        }
    }

    @Override
    public Value visitRelExpr(AScriptParser.RelExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        switch (ctx.op.getType()) {
            case ASExpressionParser.GT:
                return left.gt(right);
            case ASExpressionParser.GTE:
                return left.gte(right);
            case ASExpressionParser.LT:
                return left.lt(right);
            case ASExpressionParser.LTE:
                return left.lte(right);
            case ASExpressionParser.EQ:
                return left.eq(right);
            default:
                return left.neq(right);
        }
    }

    private void addLocation(InterpreterException ex, ParserRuleContext ctx) {
        ex.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }

    @Override
    public Value visitNotExpr(AScriptParser.NotExprContext ctx) {
        Value value = visit(ctx.expression());
        return value.not();
    }

    @Override
    public Value visitAndExpr(AScriptParser.AndExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        return left.and(right);
    }

    @Override
    public Value visitOrExpr(AScriptParser.OrExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        return left.or(right);
    }

    @Override
    public Value visitExpExpr(AScriptParser.ExpExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        // TODO which one is left and which is right ?
        return left.exp(right);
    }

    @Override
    public Value visitIfstmt(AScriptParser.IfstmtContext ctx) {
        Value condition = visit(ctx.expression());
        if (condition.isTrue()) {
            return visit(ctx.block());
        } else {
            for(AScriptParser.ElifstmtContext elifCtx : ctx.elifstmt()) {
                condition = visit(elifCtx.expression());
                if (condition.isTrue()) {
                    return visit(elifCtx.block());
                }
            }
            if (ctx.elsestmt() != null) {
                return visit(ctx.elsestmt().block());
            }
        }
        return condition;
    }

    @Override
    public Value visitPrintstmt(AScriptParser.PrintstmtContext ctx) {
        Value value = visit(ctx.expression());
        if (value.isNumber()) {
            printStream.println(value.internalNumber());
        } else {
            printStream.println(value.internalString());
        }
        return value;
    }

    @Override
    public Value visitInputstmt(AScriptParser.InputstmtContext ctx) {
        printStream.print(visit(ctx.string()).internalString() + " ");
        String varname = ctx.vardecl().getText();
        try {
            String line = inputStream.readLine();
            Value val = new Value(line);
            memory.assign(varname, val);
            return val;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Override
    public Value visitForstmt(AScriptParser.ForstmtContext ctx) {
        String varname = ctx.vardecl().varname().ID().getText();
        Value start = visit(ctx.expression(0));
        Value end = visit(ctx.expression(1));
        Value step = ctx.expression(2) != null ? visit(ctx.expression(2)) : new Value(1);
        for (long i = start.internalNumber(); i <= end.internalNumber(); i = i + step.internalNumber()) {
            memory.assign(varname, new Value(i));
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            }
        }
        return new Value(0);
    }

    @Override
    public Value visitWhilestmt(AScriptParser.WhilestmtContext ctx) {
        Value cond = visit(ctx.expression());
        while (cond.isTrue()) {
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            } finally {
                cond = visit(ctx.expression());
            }
        }
        return new Value(0);
    }

    @Override
    public Value visitRepeatstmt(AScriptParser.RepeatstmtContext ctx) {
        Value cond;
        do {
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            } finally {
                cond = visit(ctx.expression());
            }
        } while (cond.isFalse());
        return new Value(0);
    }

    @Override
    public Value visitContinuestmt(AScriptParser.ContinuestmtContext ctx) {
        throw new ContinueLoopException();
    }

    @Override
    public Value visitExitstmt(AScriptParser.ExitstmtContext ctx) {
        throw new ExitLoopException();
    }
    
}
