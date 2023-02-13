package fintx.report;

import com.google.common.collect.ImmutableList;
import fintx.digest.CsvDigester;
import fintx.digest.DigestResult;
import fintx.model.*;
import java.io.File;
import java.util.*;

/** Produces a report that reconciles transactions contained within two files. */
public class Reconciler {

    private static final Comparator<FinTransaction> PLACE_OR_PRODUCT =
            Comparator.comparing(FinTransaction::placeOrProduct);

    /**
     * Produces a report that reconciles transactions contained within two files.
     *
     * @param rakutenFile file that is in the Rakuten CSV file format
     * @param genericFile file that is in the default generic CSV file format
     * @see fintx.digest.CsvDigester#RAKUTEN_CC
     * @see fintx.digest.CsvDigester#DEFAULT
     */
    public Report reconcile(
            final File rakutenFile, final File genericFile, final DateRange dateRange) {
        final DigestResult rakutenDigest =
                new CsvDigester(CsvDigester.RAKUTEN_CC).digest(rakutenFile);
        final DigestResult genericDigest = new CsvDigester(CsvDigester.DEFAULT).digest(genericFile);

        // 0. Create filtered view of the each file's transactions
        final ImmutableList<FinTransaction> rakutenTxnsInDateRange =
                filterByDateRange(rakutenDigest.transactions(), dateRange);
        final ImmutableList<FinTransaction> genericTxnsInDateRange =
                filterByDateRange(genericDigest.transactions(), dateRange);

        // 1.  Find Rakuten transactions not within generic file
        final ImmutableList<FinTransaction> rakutenMissing =
                findMissingFromSecond(rakutenTxnsInDateRange, genericTxnsInDateRange);

        // 2.  Find generic file transactions not within Rakuten file
        final ImmutableList<FinTransaction> genericMissing =
                findMissingFromSecond(genericTxnsInDateRange, rakutenTxnsInDateRange);

        // 3.  build report
        return ImmutableReport.builder()
                .rakutenFileInfo(
                        buildFileInfo(
                                rakutenFile, rakutenDigest, rakutenTxnsInDateRange, rakutenMissing))
                .genericFileInfo(
                        buildFileInfo(
                                genericFile, genericDigest, genericTxnsInDateRange, genericMissing))
                .dateRange(dateRange)
                .build();
    }

    /**
     * Returns the FinTransactions first list that could not be mapped to corresponding transaction
     * in the second list.
     */
    private static ImmutableList<FinTransaction> findMissingFromSecond(
            final ImmutableList<FinTransaction> first, final ImmutableList<FinTransaction> second) {
        final Map<String, List<FinTransaction>> amountToTxns =
                groupByAmountSortByPlaceOrProduct(second);
        final ImmutableList.Builder<FinTransaction> missing = ImmutableList.builder();
        for (FinTransaction tx : first) {
            final List<FinTransaction> txnsForAmount =
                    amountToTxns.getOrDefault(tx.amount(), Collections.emptyList());
            if (txnsForAmount.isEmpty()) {
                missing.add(tx);
                continue;
            }
            // remove the transaction based on if a match ws found.  If no match just remove the
            // first item in the list.
            // note that this approach is flawed in the case of
            // multiple transactions with the same amount, since a later transaction could
            // have had an exact match.
            // However, for the moment I'll consider it as good enough.
            final int indexToRemove =
                    // note: perhaps matching by date is more likely...
                    Math.max(0, Collections.binarySearch(txnsForAmount, tx, PLACE_OR_PRODUCT));
            txnsForAmount.remove(indexToRemove);
        }
        return missing.build();
    }

    /**
     * Returns a mutable Map of transactions, grouped by their amount. The list of transactions for
     * each amount is also mutable, as well as sorted by {@link #PLACE_OR_PRODUCT}.
     *
     * <p>This returned value is intended to be used by {@link #findMissingFromSecond(ImmutableList,
     * ImmutableList)} (List, Map)} which will modify the returned value.
     */
    private static Map<String, List<FinTransaction>> groupByAmountSortByPlaceOrProduct(
            final ImmutableList<FinTransaction> transactions) {
        // build a mutable Map with mutable lists
        final Map<String, List<FinTransaction>> amountToTxns = new HashMap<>();
        for (FinTransaction tx : transactions) {
            final List<FinTransaction> txnsForAmount =
                    amountToTxns.getOrDefault(tx.amount(), Collections.emptyList());
            if (txnsForAmount.isEmpty()) {
                amountToTxns.put(tx.amount(), newList(tx));
            } else {
                txnsForAmount.add(tx);
            }
        }
        // sort each list by name for ease of searchability
        amountToTxns.values().forEach(txns -> txns.sort(PLACE_OR_PRODUCT));
        return amountToTxns;
    }

    private ImmutableList<FinTransaction> filterByDateRange(
            final ImmutableList<FinTransaction> txns, final DateRange dateRange) {
        if (dateRange.isUnbounded()) {
            return txns;
        } else {
            return txns.stream()
                    .filter(txn -> dateRange.contains(txn.date()))
                    .collect(ImmutableList.toImmutableList());
        }
    }

    private static List<FinTransaction> newList(final FinTransaction txn) {
        final ArrayList<FinTransaction> list = new ArrayList<>(2);
        list.add(txn);
        return list;
    }

    private static FileInfo buildFileInfo(
            final File file,
            final DigestResult digestResult,
            final ImmutableList<FinTransaction> txnsInDateRange,
            final ImmutableList<FinTransaction> missingFromOther) {
        return ImmutableFileInfo.builder()
                .name(file.getPath())
                .digestErrors(digestResult.errors())
                .numTransactions(digestResult.transactions().size())
                .numTransactionsInDateRange(txnsInDateRange.size())
                .missingFromOther(missingFromOther)
                .build();
    }
}
