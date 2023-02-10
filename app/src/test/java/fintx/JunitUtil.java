package fintx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

public class JunitUtil {

    public static void assertValidity(final Executable executable, boolean expectValid) {
        if (expectValid) {
            Assertions.assertDoesNotThrow(executable);
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, executable);
        }
    }
}
