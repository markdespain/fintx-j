package fintx.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import fintx.JunitUtil;
import fintx.model.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReconcilerTest {

    public static Stream<Arguments> testDigest() {
        return Stream.of(
                arguments(
                        "ReconcilerTest/exactMatch/",
                        Collections.emptyList(),
                        Collections.emptyList()),
                arguments(
                        "ReconcilerTest/noOverlap/",
                        Arrays.asList(
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
                        Arrays.asList(
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
                        Arrays.asList(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2022, 12, 30))
                                        .placeOrProduct("item 1")
                                        .amount("7580")
                                        .build()),
                        Arrays.asList(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2023, 2, 3))
                                        .placeOrProduct("eon")
                                        .amount("1784")
                                        .build())));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    public void testDigest(
            final String testFolder,
            final List<FinTransaction> rakutenMissingFromGeneric,
            final List<FinTransaction> genericMissingFromRakuten) {
        final File rakutenFile = JunitUtil.fileForTestResource(testFolder + "rakuten.csv");
        final File genericFile = JunitUtil.fileForTestResource(testFolder + "generic.csv");
        final Reconciler reconciler = new Reconciler();
        final Report report = reconciler.reconcile(rakutenFile, genericFile);

        final Report expectedReport =
                ImmutableReport.builder()
                        .rakutenFileInfo(
                                ImmutableFileInfo.builder()
                                        .name(rakutenFile.getPath())
                                        .numTransactions(2)
                                        .missingFromOther(rakutenMissingFromGeneric)
                                        .build())
                        .genericFileInfo(
                                ImmutableFileInfo.builder()
                                        .name(genericFile.getPath())
                                        .numTransactions(2)
                                        .missingFromOther(genericMissingFromRakuten)
                                        .build())
                        .build();
        assertEquals(expectedReport, report);
    }
}
