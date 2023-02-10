package fintx.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Result<V> {

    public static <T> Result<T> error(final Err error) {
        return (Result<T>) ImmutableResult.builder().error(error).build();
    }

    public static <T> Result<T> value(final T value) {
        return (Result<T>) ImmutableResult.builder().value(value).build();
    }

    public abstract Optional<Err> error();

    public abstract Optional<V> value();

    @Value.Check
    protected void check() {
        final String baseMessage = "exactly one of 'error' and 'value' should be set, but ";
        if (error().isPresent() && value().isPresent()) {
            throw new IllegalArgumentException(baseMessage + "but both were set.");
        } else if (error().isEmpty() && value().isEmpty()) {
            throw new IllegalArgumentException(baseMessage + "neither were set.");
        }
    }
}
