package fintx.model;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReportTest {
    private static final boolean NO_ERRORS = false;
    private static final boolean HAS_ERRORS = true;

    public static Stream<Arguments> testHasErrors() {

        final FileInfo noErrors =
                ImmutableFileInfo.builder()
                        .name("no errors")
                        .digestErrors(Collections.emptyList())
                        .missingFromOther(Collections.emptyList())
                        .numTransactions(1)
                        .build();

        final FileInfo hasErrors =
                ImmutableFileInfo.builder()
                        .from(noErrors)
                        .name("has errors")
                        .digestErrors(Collections.singletonList(Err.message("some error")))
                        .build();

        return Stream.of(
                arguments(
                        NO_ERRORS,
                        ImmutableReport.builder()
                                .rakutenFileInfo(noErrors)
                                .genericFileInfo(noErrors)
                                .build()),
                arguments(
                        HAS_ERRORS,
                        ImmutableReport.builder()
                                .rakutenFileInfo(hasErrors)
                                .genericFileInfo(noErrors)
                                .build()),
                arguments(
                        HAS_ERRORS,
                        ImmutableReport.builder()
                                .rakutenFileInfo(noErrors)
                                .genericFileInfo(hasErrors)
                                .build()),
                arguments(
                        HAS_ERRORS,
                        ImmutableReport.builder()
                                .rakutenFileInfo(hasErrors)
                                .genericFileInfo(hasErrors)
                                .build()));
    }

    @ParameterizedTest
    @MethodSource
    void testHasErrors(final boolean expected, final Report report) {
        Assertions.assertEquals(expected, report.hasErrors());
    }
}
