package net.francoiscolombo.tools.automaton.ascript.interpreter;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import ascript.AScriptBaseVisitor;
import ascript.AScriptParser;
import ascript.ASExpressionParser;
import net.francoiscolombo.tools.automaton.ascript.exceptions.ContinueLoopException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.ExitLoopException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.InterpreterException;
import net.francoiscolombo.tools.automaton.ascript.exceptions.TypeException;
import net.francoiscolombo.tools.automaton.ascript.statements.*;
import net.francoiscolombo.tools.automaton.ascript.statements.File;
import org.antlr.v4.runtime.ParserRuleContext;

public class AScriptVisitor extends AScriptBaseVisitor<Value> {

    // global logger
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    final private InputStream stdin;
    final private PrintStream stdout;
    final private PrintStream stderr;
    final private Memory memory;

    private PrintStream printStream;
    private PrintStream errorStream;
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
        errorStream = new PrintStream(stderr, true);
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
    public Value visitLowerfunc(AScriptParser.LowerfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            return new Value(arg.internalString().toLowerCase());
        } else {
            throw new TypeException("Couldn't evaluate LOWER(). Argument is not a string");
        }
    }

    @Override
    public Value visitUpperfunc(AScriptParser.UpperfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            return new Value(arg.internalString().toUpperCase());
        } else {
            throw new TypeException("Couldn't evaluate UPPER(). Argument is not a string");
        }
    }

    @Override
    public Value visitContainsfunc(AScriptParser.ContainsfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isString()) {
                return arg1.internalString().contains(arg2.internalString()) ? Value.TRUE : Value.FALSE;
            } else {
                throw new TypeException("Couldn't evaluate CONTAINS(). Second argument is not a string");
            }
        } else {
            throw new TypeException("Couldn't evaluate CONTAINS(). First argument is not a string");
        }
    }

    @Override
    public Value visitMatchesfunc(AScriptParser.MatchesfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isString()) {
                return arg1.internalString().matches(arg2.internalString()) ? Value.TRUE : Value.FALSE;
            } else {
                throw new TypeException("Couldn't evaluate MATCHES(). Second argument is not a string");
            }
        } else {
            throw new TypeException("Couldn't evaluate MATCHES(). First argument is not a string");
        }
    }

    @Override
    public Value visitLeftfunc(AScriptParser.LeftfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isNumber()) {
                return new Value(arg1.internalString().substring(0, (int)arg2.internalNumber()));
            } else {
                throw new TypeException("Couldn't evaluate LEFT(). Second argument is not a number");
            }
        } else {
            throw new TypeException("Couldn't evaluate LEFT(). First argument is not a string");
        }
    }

    @Override
    public Value visitRightfunc(AScriptParser.RightfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isNumber()) {
                int start = (int)(arg1.internalString().length() - arg2.internalNumber());
                int end = arg1.internalString().length();
                return new Value(arg1.internalString().substring(start, end));
            } else {
                throw new TypeException("Couldn't evaluate RIGHT(). Second argument is not a number");
            }
        } else {
            throw new TypeException("Couldn't evaluate RIGHT(). First argument is not a string");
        }
    }

    @Override
    public Value visitSubstrfunc(AScriptParser.SubstrfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        Value arg3 = visit(ctx.expression(2));
        if (arg1.isString()) {
            if (arg2.isNumber()) {
                if (arg3.isNumber()) {
                    int start = (int)arg2.internalNumber();
                    int end = (int)arg3.internalNumber();
                    if(end<start) {
                        throw new TypeException("Couldn't evaluate SUBSTR(), third argument is lower than the second one.");
                    }
                    return new Value(arg1.internalString().substring(start, end));
                } else {
                    throw new TypeException("Couldn't evaluate SUBSTR(). Third argument is not a number");
                }
            } else {
                throw new TypeException("Couldn't evaluate SUBSTR(). Second argument is not a number");
            }
        } else {
            throw new TypeException("Couldn't evaluate SUBSTR(). First argument is not a string");
        }
    }

    @Override
    public Value visitStartswithfunc(AScriptParser.StartswithfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isString()) {
                return arg1.internalString().startsWith(arg2.internalString()) ? Value.TRUE : Value.FALSE;
            } else {
                throw new TypeException("Couldn't evaluate STARTSWITH(). Second argument is not a string");
            }
        } else {
            throw new TypeException("Couldn't evaluate STARTSWITH(). First argument is not a string");
        }
    }

    @Override
    public Value visitEndswithfunc(AScriptParser.EndswithfuncContext ctx) {
        Value arg1 = visit(ctx.expression(0));
        Value arg2 = visit(ctx.expression(1));
        if (arg1.isString()) {
            if (arg2.isString()) {
                return arg1.internalString().startsWith(arg2.internalString()) ? Value.TRUE : Value.FALSE;
            } else {
                throw new TypeException("Couldn't evaluate ENDSWITH(). Second argument is not a string");
            }
        } else {
            throw new TypeException("Couldn't evaluate STARTSWITH(). First argument is not a string");
        }
    }
/*
    | endswithfunc
    | replacewithfunc
    | concatfunc
 */


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
    public Value visitPrintstmt(AScriptParser.PrintstmtContext ctx) {
        Print print = new Print(printStream, errorStream);
        return print.visitStatement(visit(ctx.expression()));
    }

    @Override
    public Value visitCompressstmt(AScriptParser.CompressstmtContext ctx) {
        Compress compress = new Compress(printStream, errorStream);
        return compress.visitStatement(visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Value visitExtractstmt(AScriptParser.ExtractstmtContext ctx) {
        Extract extract = new Extract(printStream, errorStream);
        return extract.visitStatement(visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Value visitCopystmt(AScriptParser.CopystmtContext ctx) {
        Copy copy = new Copy(printStream, errorStream);
        return copy.visitStatement(visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Value visitShowstmt(AScriptParser.ShowstmtContext ctx) {
        Show show = new Show(printStream, errorStream);
        return show.visitStatement(visit(ctx.expression()));
    }

    @Override
    public Value visitDownloadstmt(AScriptParser.DownloadstmtContext ctx) {
        Download download = new Download(printStream, errorStream);
        return download.visitStatement(visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Value visitExecutestmt(AScriptParser.ExecutestmtContext ctx) {
        String varname = ctx.vardecl().getText();
        Execute execute = new Execute(printStream, errorStream);
        Value value = execute.visitStatement(visit(ctx.expression(0)), visit(ctx.expression(1)));
        Value val = new Value(execute.getOutput());
        memory.assign(varname, val);
        return value;
    }

    @Override
    public Value visitFilestmt(AScriptParser.FilestmtContext ctx) {
        File file = new File(printStream, errorStream);
        Value value = new Value(-1);
        if(ctx.expression(2) != null) {
            if(ctx.expression(3) != null) {
                value = file.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)),visit(ctx.expression(3)));
            } else {
                value = file.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)));
            }
        } else {
            value = file.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)));
        }
        return value;
    }

    @Override
    public Value visitLinesstmt1(AScriptParser.Linesstmt1Context ctx) {
        LinesBefore linesBefore = new LinesBefore(printStream, errorStream);
        return linesBefore.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)));
    }

    @Override
    public Value visitLinesstmt2(AScriptParser.Linesstmt2Context ctx) {
        LinesAfter linesAfter = new LinesAfter(printStream, errorStream);
        return linesAfter.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)));
    }

    @Override
    public Value visitLinesstmt3(AScriptParser.Linesstmt3Context ctx) {
        LinesReplace linesReplace = new LinesReplace(printStream, errorStream);
        return linesReplace.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)));
    }

    @Override
    public Value visitPackagestmt(AScriptParser.PackagestmtContext ctx) {
        Packages packages = new Packages(printStream, errorStream);
        final List<Value> valueList = new LinkedList<>();
        ctx.exprlist().expression().forEach(v -> {
            valueList.add(visit(v));
        });
        return packages.visitStatement(valueList.toArray(Value[]::new));
    }

    @Override
    public Value visitPingstmt(AScriptParser.PingstmtContext ctx) {
        Ping ping = new Ping(printStream, errorStream);
        return ping.visitStatement(visit(ctx.expression()));
    }

    @Override
    public Value visitScanstmt(AScriptParser.ScanstmtContext ctx) {
        ScanNetwork scanNetwork = new ScanNetwork(printStream, errorStream);
        return scanNetwork.visitStatement(visit(ctx.expression()));
    }

    @Override
    public Value visitScriptstmt(AScriptParser.ScriptstmtContext ctx) {
        Script script = new Script(printStream, errorStream);
        String varname = ctx.vardecl().getText();
        Value val = script.visitStatement(visit(ctx.expression()));
        memory.assign(varname, val);
        Memory mem = script.getMemory();
        for(String name : mem.getVariables()) {
            memory.assign(name, mem.get(name));
        }
        mem.free();
        return val;
    }

    @Override
    public Value visitSendstmt(AScriptParser.SendstmtContext ctx) {
        SendFile sendFile = new SendFile(printStream, errorStream);
        return sendFile.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)),visit(ctx.expression(2)),visit(ctx.expression(3)));
    }

    @Override
    public Value visitTemplatestmt1(AScriptParser.Templatestmt1Context ctx) {
        TemplateFromFile template = new TemplateFromFile(printStream, errorStream, memory);
        return template.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)));
    }

    @Override
    public Value visitTemplatestmt2(AScriptParser.Templatestmt2Context ctx) {
        TemplateFromContent template = new TemplateFromContent(printStream, errorStream, memory);
        return template.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)));
    }

    @Override
    public Value visitUnzipstmt(AScriptParser.UnzipstmtContext ctx) {
        Zip zip = new Zip(printStream, errorStream);
        return zip.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)));
    }

    @Override
    public Value visitZipstmt(AScriptParser.ZipstmtContext ctx) {
        Unzip unzip = new Unzip(printStream, errorStream);
        return unzip.visitStatement(visit(ctx.expression(0)),visit(ctx.expression(1)));
    }

}
