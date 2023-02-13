package fintx.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Report {

    FileInfo rakutenFileInfo();

    FileInfo genericFileInfo();

    DateRange dateRange();

    @Value.Derived
    @Value.Redacted
    default boolean hasErrors() {
        return !genericFileInfo().digestErrors().isEmpty()
                || !rakutenFileInfo().digestErrors().isEmpty();
    }
}
