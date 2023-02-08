package fintx.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface Result<V> {

    static <T> Result<T> error(final AppError error) {
        return (Result<T>) ImmutableResult.builder().error(error).build();
    }

    static <T> Result<T> value(final T value) {
        return (Result<T>) ImmutableResult.builder().value(value).build();
    }

    Optional<AppError> error();

    Optional<V> value();

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
