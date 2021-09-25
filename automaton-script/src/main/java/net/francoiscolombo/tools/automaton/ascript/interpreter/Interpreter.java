package net.francoiscolombo.tools.automaton.ascript.interpreter;

import net.francoiscolombo.tools.automaton.ascript.exceptions.InterpreterException;

import net.francoiscolombo.tools.automaton.ascript.helpers.Utils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import ascript.AScriptLexer;
import ascript.AScriptParser;

public class Interpreter {

    final private InputStream stdin;
    final private PrintStream stdout;
    final private PrintStream stderr;
    private Memory memory;

    public Interpreter(InputStream in, PrintStream out, PrintStream err) {
        this.stdin = in;
        this.stdout = out;
        this.stderr = err;
    }

    public Value run(FileInputStream fileInputStream) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(fileInputStream);
        AScriptLexer lexer = new AScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AScriptParser parser = new AScriptParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(new ErrorListener(stderr));
        try {
            ParseTree tree = parser.prog();
            memory = new Memory();
            AScriptVisitor eval = new AScriptVisitor(memory, stdin, stdout, stderr);
            eval.visit(tree);
        } catch (InterpreterException e) {
            stderr.println(e.getMessage());
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof InputMismatchException) {
                InputMismatchException inputEx = (InputMismatchException)e.getCause();
                String msg = Utils.formatErrorMessage(
                        inputEx.getOffendingToken().getLine(),
                        inputEx.getOffendingToken().getCharPositionInLine(),
                        "Syntax error");
                stderr.println(msg);
            }
        }
        return null;
    }

    public Memory getMemory() {
        return memory;
    }

    public void clear() {
        memory.free();
    }

}
