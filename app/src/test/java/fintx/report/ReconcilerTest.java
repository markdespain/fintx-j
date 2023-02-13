package fintx.report;

import static fintx.Fixture.UNBOUNDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import fintx.JunitUtil;
import fintx.model.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReconcilerTest {

    public static Stream<Arguments> testDigest() {
        return Stream.of(
                arguments(
                        "ReconcilerTest/exactMatch/",
                        UNBOUNDED,
                        2,
                        Collections.emptyList(),
                        Collections.emptyList()),
                arguments(
                        "ReconcilerTest/matchWithDifferentNames/",
                        UNBOUNDED,
                        2,
                        Collections.emptyList(),
                        Collections.emptyList()),
                arguments(
                        "ReconcilerTest/txWithSameAmount/",
                        UNBOUNDED,
                        2,
                        Collections.emptyList(),
                        Collections.emptyList()),
                arguments(
                        "ReconcilerTest/noOverlap/",
                        UNBOUNDED,
                        2,
                        List.of(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(1)
                                        .date(LocalDate.of(2022, 12, 31))
                                        .placeOrProduct("item 0")
                                        .amount("2147")
                                        .build(),
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2022, 12, 30))
                                        .placeOrProduct("item 1")
                                        .amount("7580")
                                        .build()),
                        List.of(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(1)
                                        .date(LocalDate.of(2023, 2, 9))
                                        .placeOrProduct("Nishimuta")
                                        .amount("1173")
                                        .build(),
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2023, 2, 3))
                                        .placeOrProduct("eon")
                                        .amount("1784")
                                        .build())),
                arguments(
                        "ReconcilerTest/partialOverlap/",
                        UNBOUNDED,
                        2,
                        List.of(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2022, 12, 30))
                                        .placeOrProduct("item 1")
                                        .amount("7580")
                                        .build()),
                        List.of(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2023, 2, 3))
                                        .placeOrProduct("eon")
                                        .amount("1784")
                                        .build())),
                arguments(
                        "ReconcilerTest/partialOverlap/",
                        ImmutableDateRange.of(
                                Optional.of(LocalDate.of(2021, 01, 01)),
                                Optional.of(LocalDate.of(2022, 01, 01))),
                        0,
                        List.of(),
                        List.of()));
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource
    public void testDigest(
            final String testFolder,
            final DateRange dateRange,
            final int expectedNumTransactionsInDateRange,
            final List<FinTransaction> rakutenMissingFromGeneric,
            final List<FinTransaction> genericMissingFromRakuten) {
        final File rakutenFile = JunitUtil.fileForTestResource(testFolder + "rakuten.csv");
        final File genericFile = JunitUtil.fileForTestResource(testFolder + "generic.csv");
        final Reconciler reconciler = new Reconciler();
        final Report report = reconciler.reconcile(rakutenFile, genericFile, dateRange);
        final Report expectedReport =
                ImmutableReport.builder()
                        .dateRange(dateRange)
                        .rakutenFileInfo(
                                ImmutableFileInfo.builder()
                                        .name(rakutenFile.getPath())
                                        .numTransactions(2)
                                        .numTransactionsInDateRange(
                                                expectedNumTransactionsInDateRange)
                                        .missingFromOther(rakutenMissingFromGeneric)
                                        .build())
                        .genericFileInfo(
                                ImmutableFileInfo.builder()
                                        .name(genericFile.getPath())
                                        .numTransactions(2)
                                        .numTransactionsInDateRange(
                                                expectedNumTransactionsInDateRange)
                                        .missingFromOther(genericMissingFromRakuten)
                                        .build())
                        .build();
        assertEquals(expectedReport, report);
    }
}
