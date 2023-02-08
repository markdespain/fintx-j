package fintx.model;

import java.io.File;
import java.io.IOException;
import org.immutables.value.Value;

@Value.Immutable
public interface AppError {

    static AppError fileNotFound(final File file) {
        return ImmutableAppError.builder()
                .message("path not found: " + file.getAbsoluteFile())
                .build();
    }

    static AppError notAFile(final File file) {
        return ImmutableAppError.builder()
                .message("path is not a file: " + file.getAbsoluteFile())
                .build();
    }

    static AppError loadFileFailure(final File file, final IOException e) {
        return ImmutableAppError.builder()
                .message(
                        "error loading file.  file: "
                                + file.getAbsoluteFile()
                                + ", message: "
                                + e.getMessage())
                .build();
    }

    String message();
}
