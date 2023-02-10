package fintx.digest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import fintx.JunitUtil;
import fintx.model.FinTransaction;
import fintx.model.ImmutableFinTransaction;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CsvDigesterTest {

    private static final int NO_ERRORS = 0;
    private static final int ONE_ERROR = 1;

    public static Stream<Arguments> testDigest() {
        return Stream.of(
                arguments(
                        "CsvDigesterTest/rakuten-no-errors.csv",
                        CsvDigester.RAKUTEN_CC,
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
                        NO_ERRORS),
                arguments(
                        "CsvDigesterTest/generic-no-errors.csv",
                        CsvDigester.DEFAULT,
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
                                        .build()),
                        NO_ERRORS),
                arguments(
                        "CsvDigesterTest/rakuten-bad-date-format.csv",
                        CsvDigester.RAKUTEN_CC,
                        Arrays.asList(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2022, 12, 30))
                                        .placeOrProduct("item 1")
                                        .amount("7580")
                                        .build()),
                        ONE_ERROR),
                arguments(
                        "CsvDigesterTest/generic-bad-date-format.csv",
                        CsvDigester.DEFAULT,
                        Arrays.asList(
                                ImmutableFinTransaction.builder()
                                        .lineNumber(2)
                                        .date(LocalDate.of(2023, 2, 3))
                                        .placeOrProduct("eon")
                                        .amount("1784")
                                        .build()),
                        ONE_ERROR),
                arguments(
                        "CsvDigesterTest/no-such-file.csv",
                        CsvDigester.DEFAULT,
                        Collections.emptyList(),
                        ONE_ERROR)

                /* */ );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    public void testDigest(
            final String testResource,
            final CsvDigester.Config config,
            final List<FinTransaction> expectedTxns,
            final int expectedNumErrors) {
        final CsvDigester digester = new CsvDigester(config);
        final File file = JunitUtil.fileForTestResource(testResource);
        final DigestResult digestResult = digester.digest(file);
        assertEquals(expectedTxns, digestResult.transactions());
        assertEquals(expectedNumErrors, digestResult.errors().size());
    }
}
