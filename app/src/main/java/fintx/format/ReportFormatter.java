package fintx.format;

import static fintx.format.Formats.DATE_FORMAT;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import fintx.model.Err;
import fintx.model.FinTransaction;
import fintx.model.Report;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.StringSubstitutor;

public class ReportFormatter {

    private static final String NO_ITEMS = "(none)";

    private static final String TX_FORMAT = "%-4s\t%-10s\t%-20s\t%-20s";

    private static final String TX_HEADER =
            String.format(TX_FORMAT, "line", "date", "amount", "place or item");

    private final String reportTemplate;

    public ReportFormatter() {
        reportTemplate = loadAsString("template/Report.template");
    }

    private static String loadAsString(final String resource) {
        try {
            return Resources.toString(Resources.getResource(resource), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to load report template", e);
        }
    }

    public String format(final Report report) {

        final ImmutableMap<String, String> values =
                ImmutableMap.<String, String>builder()
                        .put("dateRange", report.dateRange().toString())

                        // Rakuten file summary
                        .put("rakutenFile", report.rakutenFileInfo().name())
                        .put(
                                "numRakutenTransactions",
                                Integer.toString(report.rakutenFileInfo().numTransactions()))
                        .put(
                                "numRakutenTransactionsInDateRange",
                                Integer.toString(
                                        report.rakutenFileInfo().numTransactionsInDateRange()))
                        .put(
                                "numRakutenMissing",
                                Integer.toString(
                                        report.rakutenFileInfo().missingFromOther().size()))
                        .put(
                                "numRakutenErrors",
                                Integer.toString(report.rakutenFileInfo().digestErrors().size()))

                        // Rakuten file details
                        .put(
                                "rakutenMissing",
                                formatTxs(report.rakutenFileInfo().missingFromOther()))
                        .put("rakutenErrors", formatErrors(report.rakutenFileInfo().digestErrors()))

                        // Generic summary
                        .put("genericFile", report.genericFileInfo().name())
                        .put(
                                "numGenericTransactions",
                                Integer.toString(report.genericFileInfo().numTransactions()))
                        .put(
                                "numGenericTransactionsInDateRange",
                                Integer.toString(
                                        report.genericFileInfo().numTransactionsInDateRange()))
                        .put(
                                "numGenericMissing",
                                Integer.toString(
                                        report.genericFileInfo().missingFromOther().size()))
                        .put(
                                "numGenericErrors",
                                Integer.toString(report.genericFileInfo().digestErrors().size()))

                        // Generic file details
                        .put(
                                "genericMissing",
                                formatTxs(report.genericFileInfo().missingFromOther()))
                        .put("genericErrors", formatErrors(report.genericFileInfo().digestErrors()))
                        .build();

        return new StringSubstitutor(values).replace(reportTemplate);
    }

    private static String formatTxs(final ImmutableList<FinTransaction> txns) {
        if (txns.isEmpty()) {
            return NO_ITEMS;
        }
        final List<String> formatted =
                Stream.concat(Stream.of(TX_HEADER), txns.stream().map(ReportFormatter::formatTx))
                        .collect(Collectors.toList());
        return String.join("\n", formatted);
    }

    private static String formatTx(final FinTransaction tx) {
        final String formattedDate = DATE_FORMAT.format(tx.date());
        return String.format(
                "%-4s\t%-10s\t%-20s\t%s",
                tx.lineNumber(), formattedDate, tx.amount(), tx.placeOrProduct());
    }

    private static String formatErrors(final ImmutableList<Err> errors) {
        if (errors.isEmpty()) {
            return NO_ITEMS;
        }
        final List<String> formatted =
                errors.stream().map(Err::message).collect(Collectors.toList());
        return String.join("\n", formatted);
    }
}
