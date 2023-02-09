package fintx.model;

import java.io.File;
import org.immutables.value.Value;

@Value.Immutable
public interface Err {

    static Err message(final String message) {
        return ImmutableErr.builder().message(message).build();
    }

    static Err fileNotFound(final File file) {
        return message("path not found: " + file.getAbsoluteFile());
    }

    static Err loadFileFailure(final File file, final Exception e) {
        return fileError("error loading file", file, e);
    }

    static Err fileError(final String message, final File file, final Exception e) {
        return message(
                message + ". file: " + file.getAbsoluteFile() + ", message: " + e.getMessage());
    }

    String message();
}
