package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Interpreter;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Memory;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class Script extends AbstractStatement {

    public Script(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    private Memory memory = new Memory();

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values)) {
            String scriptFilename = values[0].internalString();
            try {
                FileInputStream scriptInputStream = new FileInputStream(scriptFilename);
                Interpreter interpreter = new Interpreter(System.in, printStream, errorStream);
                interpreter.clear();
                interpreter.run(scriptInputStream);
                this.memory = interpreter.getMemory();
            } catch (FileNotFoundException e) {
                exitCode = 2;
                errorStream.printf("File <%s> not found (%s), please check the path.\n", scriptFilename, e.getMessage());
            } catch (IOException e) {
                exitCode = 3;
                errorStream.printf("An exception happened during execution of script <%s>, error is '%s'\n", scriptFilename, e.getMessage());
            }
        }
        return new Value(exitCode);
    }

    public Memory getMemory() {
        return memory;
    }

}
