package fintx.model;

import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

@Value.Immutable
public interface FileInfo {

    /** The name of the file for which information is being provided */
    String name();

    /**
     * Errors encountered when trying to digest the file into transactions.
     *
     * @see fintx.digest.CsvDigester
     */
    ImmutableList<Err> digestErrors();

    /**
     * The number of successfully digested transactions
     *
     * @see fintx.digest.CsvDigester
     */
    int numTransactions();

    /**
     * The number of successfully digested transactions that matched date range for the report.
     *
     * @see #numTransactions()
     */
    int numTransactionsInDateRange();

    /**
     * The number of transactions from this file that were missing from the other file.
     *
     * @see fintx.report.Reconciler
     */
    ImmutableList<FinTransaction> missingFromOther();
}
