package fintx.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface Result {

    Optional<AppError> error();

    Optional<String> value();

    @Value.Check
    default void check() {
        final String baseMessage = "exactly one of 'error' and 'value' should be set, but ";
        if (error().isPresent() && value().isPresent()) {
            throw new IllegalArgumentException(baseMessage + "but both were set.");
        } else if (error().isEmpty() && value().isEmpty()) {
            throw new IllegalArgumentException(baseMessage + "neither were set.");
        }
    }
}
