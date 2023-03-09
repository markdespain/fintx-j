/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fintx;

import fintx.digest.CsvDigester;
import fintx.digest.CsvDigester.Config;
import fintx.format.ReportFormatter;
import fintx.model.DateRange;
import fintx.model.ImmutableDateRange;
import fintx.model.Report;
import fintx.model.Result;
import fintx.report.Reconciler;
import java.io.File;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

@CommandLine.Command
public class App implements Callable<Integer> {

    private static final String RAKUTEN_FORMAT = "rakuten";
    private static final String DEFAULT_FORMAT = "default";

    private static final String CSV_FORMAT_OPTIONS = "default, rakuten, or a specification expressed in JSON)";

    @CommandLine.Option(
            names = {"-f1", "-file1"},
            description = "transactions file 1",
            required = true)
    private File file1;

    @CommandLine.Option(
            names = {"-f1f", "-file1Format"},
            description = "CSV format for file 1: " + CSV_FORMAT_OPTIONS,
            defaultValue = DEFAULT_FORMAT)
    private String file1Format;

    @CommandLine.Option(
            names = {"-f2", "-file2"},
            description = "transactions file 2",
            required = true)
    private File file2;

    @CommandLine.Option(
            names = {"-f2f", "-file2Format"},
            description = "CSV format for file 2: " + CSV_FORMAT_OPTIONS,
            defaultValue = DEFAULT_FORMAT)
    private String file2Format;

    @CommandLine.Option(
            names = {"-s", "--start", "--startInclusive"},
            description =
                    "Filter identifying the start date of the range records to match (inclusive)")
    private LocalDate startInclusive;

    @CommandLine.Option(
            names = {"-e", "--end", "--endExclusive"},
            description =
                    "Filter identifying the end date of the range records to match (exclusive)")
    private LocalDate endExclusive;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    private static void print(final Report report) {
        String formattedReport = new ReportFormatter().format(report);
        System.out.println(formattedReport);
    }

    static Result<Config> parseConfig(final String config) {
        final String trimmedConfig = config.trim();
        if (config.isEmpty() || trimmedConfig.toLowerCase(Locale.ENGLISH).equals(DEFAULT_FORMAT)) {
            return Result.value(CsvDigester.DEFAULT);
        }
        if (trimmedConfig.toLowerCase(Locale.ENGLISH).equals(RAKUTEN_FORMAT)) {
            return Result.value(CsvDigester.RAKUTEN_CC);
        }
        return new CsvDigesterConfigParser().parse(config);
    }

    @Override
    public Integer call() {
        final Result<Config> file1Config = parseConfig(file1Format);
        if (file1Config.error().isPresent()) {
            System.err.println(
                    "file2: invalid format. error: " + file1Config.error().get().message());
            return ExitCode.USAGE;
        }
        final Result<Config> file2Config = parseConfig(file2Format);
        if (file2Config.error().isPresent()) {
            System.err.println(
                    "file12 invalid format. error: " + file2Config.error().get().message());
            return ExitCode.USAGE;
        }
        final DateRange dateRange =
                ImmutableDateRange.of(
                        Optional.ofNullable(startInclusive), Optional.ofNullable(endExclusive));
        final Report report =
                new Reconciler(file1Config.value().get(), file2Config.value().get())
                        .reconcile(file1, file2, dateRange);
        print(report);
        return report.hasErrors() ? ExitCode.SOFTWARE : ExitCode.OK;
    }
}
