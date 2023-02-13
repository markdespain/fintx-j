package fintx.model;

import java.io.File;
import org.immutables.value.Value;

@Value.Immutable
public interface Err {

    static Err message(final String message) {
        return ImmutableErr.of(message);
    }

    static Err fileNotFound(final File file) {
        return message("path not found: " + file.getAbsoluteFile());
    }

    static Err fileError(final String message, final File file, final Exception e) {
        final String errorMessage =
                String.format(
                        "%s, file: %s, errorType: %s, detailError: %s",
                        message,
                        file.getAbsoluteFile(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
        return message(errorMessage);
    }

    @Value.Parameter
    String message();
}
