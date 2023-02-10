package fintx.report;

import com.google.common.collect.ImmutableList;
import fintx.digest.CsvDigester;
import fintx.digest.DigestResult;
import fintx.model.*;
import java.io.File;
import java.util.*;

/**
 * Produces a report that reconciles transactions contained within two files.
 */
public class Reconciler {

    private static final Comparator<FinTransaction> PLACE_OR_PRODUCT =
            Comparator.comparing(FinTransaction::placeOrProduct);

    /**
     * Produces a report that reconciles transactions contained within two files.
     *
     * @param rakutenFile file that is in the Rakuten CSV file format
     * @param genericFile file that is in the default generic CSV file format
     *
     * @see fintx.digest.CsvDigester#RAKUTEN_CC
     * @see fintx.digest.CsvDigester#DEFAULT
     */
    public Report reconcile(final File rakutenFile, final File genericFile) {
        final DigestResult rakutenDigest =
                new CsvDigester(CsvDigester.RAKUTEN_CC).digest(rakutenFile);
        final DigestResult genericDigest = new CsvDigester(CsvDigester.DEFAULT).digest(genericFile);

        // 1.  Find Rakuten transactions not within generic file
        final ImmutableList<FinTransaction> rakutenMissing =
                findMissingFromDigest(rakutenDigest.transactions(), genericDigest);

        // 1.  Find generic file transactions not within Rakuten file
        final ImmutableList<FinTransaction> genericMissing =
                findMissingFromDigest(genericDigest.transactions(), rakutenDigest);

        // 3.  build report
        return ImmutableReport.builder()
                .rakutenFileInfo(buildFileInfo(rakutenFile, rakutenDigest, rakutenMissing))
                .genericFileInfo(buildFileInfo(genericFile, genericDigest, genericMissing))
                .build();
    }

    private static FileInfo buildFileInfo(
            final File file,
            final DigestResult digestResult,
            final ImmutableList<FinTransaction> missingFromOther) {
        return ImmutableFileInfo.builder()
                .name(file.getAbsolutePath())
                .digestErrors(digestResult.errors())
                .numTransactions(digestResult.transactions().size())
                .missingFromOther(missingFromOther)
                .build();
    }


    /**
     * Returns a mutable Map of transactions, grouped by their amount. The list of transactions
     * for each amount is also mutable, as well as sorted by {@link #PLACE_OR_PRODUCT}.
     *
     * This returned value is intended to be used by {@link #findMissing(ImmutableList, Map)}, which
     * will modify the returned value.
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

    private static ImmutableList<FinTransaction> findMissingFromDigest(
            final ImmutableList<FinTransaction> txns, final DigestResult digestResult) {
        return findMissing(txns, groupByAmountSortByPlaceOrProduct(digestResult.transactions()));
    }

    /**
     * @see #groupByAmountSortByPlaceOrProduct(ImmutableList)
     */
    private static ImmutableList<FinTransaction> findMissing(
            final ImmutableList<FinTransaction> txns,
            final Map<String, List<FinTransaction>> amountToTxns) {
        final ImmutableList.Builder<FinTransaction> missing = ImmutableList.builder();
        for (FinTransaction tx : txns) {
            final List<FinTransaction> txnsForAmount = amountToTxns.get(tx.amount());
            if (txnsForAmount == null || txnsForAmount.isEmpty()) {
                missing.add(tx);
            } else {
                // remove the tranxaction... Note that this approach is flawed in the case of
                final int indexToRemove =
                        Collections.binarySearch(txnsForAmount, tx, PLACE_OR_PRODUCT);
                if (indexToRemove >= 0) {
                    // exact match, so the transaction can be removed
                    txnsForAmount.remove(indexToRemove);
                } else {
                    // just remove the first item in the list.  note that this approach is flawed in
                    // the case of
                    // multiple txns with the same amount, since a later transaction could have had
                    // an exact match.
                    // However, for the moment I'll consider it as good enough.
                    txnsForAmount.remove(0);
                }
            }
            if (txnsForAmount != null && txnsForAmount.isEmpty()) {
                amountToTxns.remove(tx.amount());
            }
        }
        return missing.build();
    }

    private static List<FinTransaction> newList(final FinTransaction txn) {
        final ArrayList<FinTransaction> list = new ArrayList<>(2);
        list.add(txn);
        return list;
    }
}
