package fintx.model;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DateRangeTest {

    private static final LocalDate START = LocalDate.of(2023, 2, 13);
    private static final LocalDate END = LocalDate.of(2023, 2, 15);

    public static Stream<Arguments> testToString() {
        return Stream.of(
                arguments("[2023-02-13, 2023-02-15)", START, END),
                arguments("[2023-02-13, ∞)", START, null),
                arguments("[-∞, 2023-02-15)", null, END),
                arguments("[-∞, ∞)", null, null));
    }

    @ParameterizedTest
    @MethodSource
    public void testToString(
            final String expected,
            final @CheckForNull LocalDate start,
            final @CheckForNull LocalDate end) {
        final String range =
                ImmutableDateRange.builder()
                        .startInclusive(Optional.ofNullable(start))
                        .endExclusive(Optional.ofNullable(end))
                        .build()
                        .toString();
        Assertions.assertEquals(expected, range);
    }

    public static Stream<Arguments> testIsUnbounded() {
        return Stream.of(
                arguments(false, START, END),
                arguments(false, START, null),
                arguments(false, null, END),
                arguments(true, null, null));
    }

    @ParameterizedTest
    @MethodSource
    public void testIsUnbounded(
            final boolean expected,
            final @CheckForNull LocalDate start,
            final @CheckForNull LocalDate end) {
        final boolean unbounded =
                ImmutableDateRange.of(Optional.ofNullable(start), Optional.ofNullable(end))
                        .isUnbounded();
        Assertions.assertEquals(expected, unbounded);
    }

    public static Stream<Arguments> testContains() {
        return Stream.of(
                arguments(false, START, END, START.minusDays(1)),
                arguments(true, START, END, START),
                arguments(true, START, END, START.plusDays(1)),
                arguments(false, START, END, END),
                arguments(true, START, null, START.plusDays(2)),
                arguments(true, START, null, START.plusYears(1)),
                arguments(true, null, END, END.minusDays(1)),
                arguments(true, null, END, END.minusYears(1)),
                arguments(true, null, null, START),
                arguments(true, null, null, END));
    }

    @ParameterizedTest
    @MethodSource
    public void testContains(
            final boolean expected,
            final @CheckForNull LocalDate start,
            final @CheckForNull LocalDate end,
            final LocalDate toCheck) {
        final boolean contains =
                ImmutableDateRange.of(Optional.ofNullable(start), Optional.ofNullable(end))
                        .contains(toCheck);
        Assertions.assertEquals(expected, contains);
    }
}
