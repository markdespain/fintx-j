package fintx.model;

import java.time.LocalDate;
import org.immutables.value.Value;

/** A financial transaction, such as those extracted from a financial statement */
@Value.Immutable
public interface FinTransaction {

    /** date of the transaction */
    LocalDate date();

    /** describes either or the product purchased */
    String placeOrProduct();

    /** The transaction amount */
    String amount();
}
