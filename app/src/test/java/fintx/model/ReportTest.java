package fintx.model;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReportTest {

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
                        ImmutableReport.builder()
                                .rakutenFileInfo(noErrors)
                                .genericFileInfo(noErrors)
                                .build(),
                        false),
                arguments(
                        ImmutableReport.builder()
                                .rakutenFileInfo(hasErrors)
                                .genericFileInfo(noErrors)
                                .build(),
                        true),
                arguments(
                        ImmutableReport.builder()
                                .rakutenFileInfo(noErrors)
                                .genericFileInfo(hasErrors)
                                .build(),
                        true),
                arguments(
                        ImmutableReport.builder()
                                .rakutenFileInfo(hasErrors)
                                .genericFileInfo(hasErrors)
                                .build(),
                        true));
    }

    @ParameterizedTest
    @MethodSource
    void testHasErrors(final Report report, final boolean expected) {}
}
