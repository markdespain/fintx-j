package fintx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

public class JunitUtil {

    public static final boolean VALID = true;
    public static final boolean INVALID = false;

    public static void assertValidity(boolean expectValid, final Executable executable) {
        if (expectValid) {
            Assertions.assertDoesNotThrow(executable);
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, executable);
        }
    }
}
