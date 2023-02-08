package fintx.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface Result {

    Optional<AppError> error();

    Optional<String> output();

    @Value.Check
    default void check() {
        final String baseMessage = "exactly one of 'error' and 'output' should be set, but ";
        if (error().isPresent() && output().isPresent()) {
            throw new IllegalArgumentException(baseMessage + "but both were set.");
        } else if (error().isEmpty() && output().isEmpty()) {
            throw new IllegalArgumentException(baseMessage + "neither were set.");
        }
    }
}
