package fintx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fintx.digest.CsvDigester.Config;
import fintx.digest.ImmutableConfig;
import fintx.model.Result;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CsvDigesterConfigParserTest {

    public static Stream<Arguments> testParseSuccess() {
        return Stream.of(
                Arguments.arguments("{}", Result.value(ImmutableConfig.builder().build())),
                Arguments.arguments(
                        "{ \"numHeaderLines\" : 0 }",
                        Result.value(ImmutableConfig.builder().numHeaderLines(0).build())));
    }

    @ParameterizedTest
    @MethodSource
    void testParseSuccess(final String input, final Result<Config> expected) {
        final CsvDigesterConfigParser parser = new CsvDigesterConfigParser();
        final Result<Config> actual = parser.parse(input);
        assertEquals(expected.error(), actual.error());
        assertEquals(expected.value(), actual.value());
        assertEquals(expected, actual);
    }
}
