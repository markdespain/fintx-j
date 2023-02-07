/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fintx;

import java.io.File;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command
public class App implements Callable<Integer> {

    @CommandLine.Parameters(description = "file to digest")
    private File file;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("received file: " + file.getAbsolutePath());
        return 0;
    }
}
