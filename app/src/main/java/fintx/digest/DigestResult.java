package fintx.digest;

import com.google.common.collect.ImmutableList;
import fintx.model.AppError;
import fintx.model.FinTransaction;
import java.io.File;
import java.util.Collections;
import org.immutables.value.Value;

/**
 * The result of digesting a file into transactions. Note that this can contain both digested lines
 * and errors.
 *
 * @see CsvDigester#digest(File)
 */
@Value.Immutable
public interface DigestResult {

    static DigestResult error(final AppError error) {
        return ImmutableDigestResult.builder()
                .errors(Collections.singletonList(error))
                .transactions(Collections.emptyList())
                .build();
    }

    ImmutableList<AppError> errors();

    ImmutableList<FinTransaction> transactions();
}
