package fintx.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Report {

    FileInfo file1Info();

    FileInfo file2Info();

    DateRange dateRange();

    @Value.Derived
    @Value.Redacted
    default boolean hasErrors() {
        return !file2Info().digestErrors().isEmpty() || !file1Info().digestErrors().isEmpty();
    }
}
