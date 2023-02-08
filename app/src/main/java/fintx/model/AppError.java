package fintx.model;

import java.io.File;
import org.immutables.value.Value;

@Value.Immutable
public interface AppError {

    static AppError message(final String message) {
        return ImmutableAppError.builder().message(message).build();
    }

    static AppError fileNotFound(final File file) {
        return message("path not found: " + file.getAbsoluteFile());
    }

    static AppError notAFile(final File file) {
        return message("path is not a file: " + file.getAbsoluteFile());
    }

    static AppError loadFileFailure(final File file, final Exception e) {
        return fileError("error loading file", file, e);
    }

    static AppError fileError(final String message, final File file, final Exception e) {
        return message(
                message + ". file: " + file.getAbsoluteFile() + ", message: " + e.getMessage());
    }

    String message();
}
