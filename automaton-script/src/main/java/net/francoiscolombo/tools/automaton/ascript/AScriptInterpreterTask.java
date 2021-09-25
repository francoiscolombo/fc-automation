package net.francoiscolombo.tools.automaton.ascript;

import net.francoiscolombo.tools.automaton.ascript.exceptions.AScriptException;
import net.francoiscolombo.tools.automaton.ascript.interpreter.Interpreter;

import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class AScriptInterpreterTask implements Callable<Integer> {

    final private String scriptFilePath;
    final private InputStream inputStream;
    final private PrintStream outputStream;
    final private PrintStream errorStream;

    public AScriptInterpreterTask(String scriptFilePath, InputStream in, PrintStream out, PrintStream err) {
        this.scriptFilePath = scriptFilePath;
        this.inputStream = in;
        this.outputStream = out;
        this.errorStream = err;
    }

    @Override
    public Integer call() throws AScriptException {
        int exitCode = -1;
        Interpreter interpreter = null;
        try(FileInputStream fileInputStream = new FileInputStream(Paths.get(scriptFilePath).toFile())) {
            interpreter = new Interpreter(inputStream, outputStream, errorStream);
            interpreter.run(fileInputStream);
            exitCode = 0;
        } catch(IOException e) {
            AScriptException exception = new AScriptException(e.getMessage());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        } finally {
            if (interpreter != null) {
                interpreter.clear();
            }
        }
        return exitCode;
    }

}
