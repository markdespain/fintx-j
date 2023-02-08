package fintx.model;

import static java.util.Optional.empty;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ResultTest {

    static Stream<Arguments> testValidation() {
        final Optional<AppError> error = Optional.of(AppError.fileNotFound(new File(".")));
        final Optional<String> output = Optional.of("output");
        return Stream.of(
                arguments(empty(), empty(), false),
                arguments(empty(), output, true),
                arguments(error, empty(), true),
                arguments(error, output, false));
    }

    @ParameterizedTest
    @MethodSource
    void testValidation(
            final Optional<AppError> error,
            final Optional<String> output,
            final boolean expectValid) {
        final Executable executable =
                () -> ImmutableResult.builder().error(error).output(output).build();
        if (expectValid) {
            Assertions.assertDoesNotThrow(executable);
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, executable);
        }
    }
}
