package fintx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import fintx.digest.CsvDigester.Config;
import fintx.digest.ImmutableConfig;
import fintx.model.Result;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CsvDigesterConfigParserTest {

    public static Stream<Arguments> testParseSuccess() {
        return Stream.of(
                arguments("{}", ImmutableConfig.builder().build()),
                arguments(
                        "{ \"numHeaderLines\" : 0 }",
                        ImmutableConfig.builder().numHeaderLines(0).build()),
                arguments("{ \"dateIndex\" : 9 }", ImmutableConfig.builder().dateIndex(9).build()));
    }

    @ParameterizedTest
    @MethodSource
    void testParseSuccess(final String input, final Config expected) {
        final CsvDigesterConfigParser parser = new CsvDigesterConfigParser();
        final Result<Config> actual = parser.parse(input);
        assertEquals(Optional.empty(), actual.error());
        assertEquals(Optional.of(expected), actual.value());
    }

    public static Stream<Arguments> testParseErr() {
        return Stream.of(
                arguments(""),
                arguments(" "),
                arguments("{"),
                arguments("{ \"unknownProperty\" : 0 }"),
                arguments("{ \"numHeaderLines\" : \"not-a-number\" }"));
    }

    @ParameterizedTest
    @MethodSource
    void testParseErr(final String input) {
        final CsvDigesterConfigParser parser = new CsvDigesterConfigParser();
        final Result<Config> actual = parser.parse(input);
        assertTrue(
                actual.error().isPresent(),
                () -> "an error should have occurred for input: " + input);
    }
}
