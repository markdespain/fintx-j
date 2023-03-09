package fintx.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Result<V> {

    public static <T> Result<T> error(final Exception ex) {
        return error(Err.from(ex));
    }

    public static <T> Result<T> error(final Err error) {
        return ImmutableResult.<T>builder().error(error).build();
    }

    public static <T> Result<T> value(final T value) {
        return ImmutableResult.<T>builder().value(value).build();
    }

    @Value.Parameter
    public abstract Optional<Err> error();

    @Value.Parameter
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
