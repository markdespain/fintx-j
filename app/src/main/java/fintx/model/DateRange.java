package fintx.model;

import static fintx.format.Formats.DATE_FORMAT;

import java.time.LocalDate;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public abstract class DateRange {

    private static final String INFINITY = "∞";
    private static final String MINUS_INFINITY = "-∞";

    @Value.Parameter
    public abstract Optional<LocalDate> startInclusive();

    @Value.Parameter
    public abstract Optional<LocalDate> endExclusive();

    public boolean contains(final LocalDate date) {
        return startContains(date) && endContains(date);
    }

    public boolean isUnbounded() {
        return startInclusive().isEmpty() && endExclusive().isEmpty();
    }

    private boolean startContains(final LocalDate date) {
        return startInclusive()
                .map(start -> start.isBefore(date) || start.isEqual(date))
                .orElse(true);
    }

    public String toString() {
        return String.format(
                "[%s, %s)",
                toString(startInclusive(), MINUS_INFINITY), toString(endExclusive(), INFINITY));
    }

    private static String toString(final Optional<LocalDate> date, final String defaultValue) {
        return date.map(d -> d.format(DATE_FORMAT)).orElse(defaultValue);
    }

    private boolean endContains(final LocalDate date) {
        return endExclusive().map(date::isBefore).orElse(true);
    }
}
