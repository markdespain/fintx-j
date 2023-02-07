package fintx;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface Result {
    Optional<AppError> error();

    Optional<String> output();
}
