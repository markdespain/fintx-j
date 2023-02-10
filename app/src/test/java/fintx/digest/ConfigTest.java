package fintx.digest;

import fintx.JunitUtil;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static fintx.JunitUtil.INVALID;
import static fintx.JunitUtil.VALID;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ConfigTest {

    static Stream<Arguments> testValidation() {
        return Stream.of(
                arguments(VALID, fromDefault())

                // negative numbers not allowed for indexes
                , arguments(INVALID, fromDefault().numHeaderLines(-1))
                , arguments(INVALID, fromDefault().dateIndex(-1))
                , arguments(INVALID, fromDefault().amountIndex(-1))
                , arguments(INVALID, fromDefault().placeOrProductIndex(-1))
        );
    }

    private static ImmutableConfig.Builder fromDefault() {
        return ImmutableConfig.builder().from(CsvDigester.DEFAULT);
    }


    @ParameterizedTest
    @MethodSource
    void testValidation(final boolean expectValid, final ImmutableConfig.Builder builder) {
        final Executable executable = builder::build;
        JunitUtil.assertValidity(expectValid, executable);
    }
}
