package fintx.digest;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import fintx.JunitUtil;
import fintx.model.ImmutableFinTransaction;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CsvDigesterTest {

    public static Stream<Arguments> testDigest() {
        return Stream.of(
                arguments(
                        "CsvDigesterTest/rakuten-no-errors.csv",
                        CsvDigester.RAKUTEN_CC,
                        ImmutableDigestResult.builder()
                                .transactions(
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
                                                        .build()))
                                .build()),
                arguments(
                        "CsvDigesterTest/generic-no-errors.csv",
                        CsvDigester.DEFAULT,
                        ImmutableDigestResult.builder()
                                .transactions(
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
                                                        .build()))
                                .build()));
    }

    @ParameterizedTest
    @MethodSource
    public void testDigest(
            final String testResource,
            final CsvDigester.Config config,
            final DigestResult expected) {
        final CsvDigester digester = new CsvDigester(config);
        final File file = JunitUtil.fileForTestResource(testResource);
        final DigestResult digestResult = digester.digest(file);
        Assertions.assertEquals(expected, digestResult);
    }
}
