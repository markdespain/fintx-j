package fintx;

import static fintx.Fixture.UNBOUNDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static picocli.CommandLine.ExitCode;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import fintx.format.ReportFormatter;
import fintx.model.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

public class AppTest {

    static Stream<Arguments> testMain() {
        final String rakutenFile = "src/test/resources/AppTest/exactMatch/rakuten.csv";
        final String genericFile = "src/test/resources/AppTest/exactMatch/generic.csv";
        final String noSuchFile = "src/test/resources/AppTest/doesNotExist";

        return Stream.of(
                arguments(
                        ExitCode.OK,
                        ImmutableReport.builder()
                                .dateRange(UNBOUNDED)
                                .rakutenFileInfo(
                                        ImmutableFileInfo.builder()
                                                .name(rakutenFile)
                                                .numTransactions(2)
                                                .numTransactionsInDateRange(2)
                                                .build())
                                .genericFileInfo(
                                        ImmutableFileInfo.builder()
                                                .name(genericFile)
                                                .numTransactions(2)
                                                .numTransactionsInDateRange(2)
                                                .build())
                                .build(),
                        rakutenFile,
                        genericFile),
                arguments(
                        CommandLine.ExitCode.SOFTWARE,
                        ImmutableReport.builder()
                                .dateRange(UNBOUNDED)
                                .rakutenFileInfo(
                                        ImmutableFileInfo.builder()
                                                .name(noSuchFile)
                                                .numTransactions(0)
                                                .numTransactionsInDateRange(0)
                                                .digestErrors(
                                                        List.of(
                                                                Err.fileNotFound(
                                                                        new File(noSuchFile))))
                                                .build())
                                .genericFileInfo(
                                        ImmutableFileInfo.builder()
                                                .name(genericFile)
                                                .numTransactions(2)
                                                .numTransactionsInDateRange(2)
                                                .missingFromOther(
                                                        List.of(
                                                                ImmutableFinTransaction.builder()
                                                                        .lineNumber(1)
                                                                        .date(
                                                                                LocalDate.of(
                                                                                        2022, 12,
                                                                                        31))
                                                                        .amount("2147")
                                                                        .placeOrProduct("item 0")
                                                                        .build(),
                                                                ImmutableFinTransaction.builder()
                                                                        .lineNumber(2)
                                                                        .date(
                                                                                LocalDate.of(
                                                                                        2022, 12,
                                                                                        30))
                                                                        .amount("7580")
                                                                        .placeOrProduct("item 1")
                                                                        .build()))
                                                .build())
                                .build(),
                        noSuchFile,
                        genericFile));
    }

    @ParameterizedTest
    @MethodSource
    void testMain(
            final int expectedCode,
            final Report expectedReport,
            final String rakutenFile,
            final String genericFile)
            throws Exception {
        final String output =
                SystemLambda.tapSystemOut(
                        () -> {
                            final int statusCode =
                                    SystemLambda.catchSystemExit(
                                            () -> {
                                                App.main(
                                                        new String[] {
                                                            "-r", rakutenFile, "-g", genericFile
                                                        });
                                            });
                            assertEquals(expectedCode, statusCode);
                        });
        final String expectedOutput = new ReportFormatter().format(expectedReport) + "\n";
        assertEquals(expectedOutput, output);
    }
}
