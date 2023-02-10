package fintx.digest;

import static java.lang.Math.max;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fintx.model.Err;
import fintx.model.FinTransaction;
import fintx.model.ImmutableFinTransaction;
import fintx.model.Result;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.immutables.value.Value;

/** Digests FinTransaction from a list of transactions in CSV format. */
public class CsvDigester {

    @Value.Immutable
    public interface Config {

        @Value.Default
        default int numHeaderLines() {
            return 1;
        }

        @Value.Default
        default int dateIndex() {
            return 0;
        }

        @Value.Default
        default int amountIndex() {
            return 1;
        }

        @Value.Default
        default int placeOrProductIndex() {
            return 2;
        }

        @Value.Default
        default String dateFormat() {
            return "M/d/yyyy";
        }

        @Value.Check
        default void check() {
            Preconditions.checkArgument(numHeaderLines() >= 0, "numHeaderLines must be >= 0");
            Preconditions.checkArgument(dateIndex() >= 0, "dateIndex must be >= 0");
            Preconditions.checkArgument(
                    placeOrProductIndex() >= 0, "placeOrProductIndex must be >= 0");
            Preconditions.checkArgument(amountIndex() >= 0, "amountIndex must be >= 0");

            // note: could validate uniquieness of indexes
        }

        @Value.Derived
        default int maxIndex() {
            return max(dateIndex(), max(placeOrProductIndex(), amountIndex()));
        }
    }

    /** A reusable instance for the default configuration. */
    public static final Config DEFAULT = ImmutableConfig.builder().build();

    /**
     * Config for digesting Rakuten credit card transactions in CSV format.
     *
     * <p>The CSV should have a header row on the first line with the following columns:
     *
     * <pre>
     *      Column Index    Column Name     (Unofficial) Column Description
     *      0               利用日           date of the transaction
     *      1               利用店名・商品名   either place of purchase or the product purchased
     *      2               家族             member of the account make the purchase (?)
     *      3               支払方法          method of payment
     *      4               利用金額          cost of the product
     *      5               支払手数料        additional commission paid
     *      6               支払総額          The total amount to be paid for the transaction
     * </pre>
     */
    public static final Config RAKUTEN_CC =
            ImmutableConfig.builder()
                    .dateFormat("yyyy/M/d")
                    .placeOrProductIndex(1)
                    .amountIndex(4)
                    .build();

    private final Config config;
    private final DateTimeFormatter dateFormat;

    public CsvDigester(final Config config) {
        this.config = config;
        this.dateFormat = DateTimeFormatter.ofPattern(config.dateFormat());
    }

    public DigestResult digest(final File file) {
        final List<String[]> lines;
        try (final CSVReader reader =
                new CSVReaderBuilder(new BufferedReader(new FileReader(file)))
                        .withSkipLines(config.numHeaderLines())
                        .build()) {
            lines = reader.readAll();
        } catch (FileNotFoundException e) {
            return DigestResult.error(Err.fileNotFound(file));
        } catch (IOException | CsvException e) {
            return DigestResult.error(Err.fileError("csv parsing failure", file, e));
        }
        final List<Err> appErrors = new ArrayList<>(0);
        final List<FinTransaction> transactions = new ArrayList<>(0);
        for (int i = 0; i < lines.size(); i++) {
            final int lineNumber = config.numHeaderLines() + i;
            final Result<FinTransaction> transactionResult = map(lineNumber, lines.get(i));
            transactionResult.error().ifPresent(appErrors::add);
            transactionResult.value().ifPresent(transactions::add);
        }
        return ImmutableDigestResult.builder().errors(appErrors).transactions(transactions).build();
    }

    private Result<FinTransaction> map(final int lineNumber, final String[] line) {
        if (line.length <= config.maxIndex()) {
            final String message =
                    String.format(
                            "line does not have enough columns. required: %s, actual: %s,"
                                    + " lineNumber: %s",
                            config.maxIndex() + 1, line.length, lineNumber);
            return Result.error(Err.message(message));
        }

        final LocalDate date;
        try {
            date = LocalDate.parse(line[config.dateIndex()], dateFormat);
        } catch (DateTimeParseException e) {
            final String message =
                    String.format(
                            "line has a malformed date. expectedFormat: %s, value: %s, line: %s,"
                                    + " column: %s",
                            config.dateFormat(),
                            line[config.dateIndex()],
                            lineNumber,
                            config.dateIndex());
            return Result.error(Err.message(message));
        }
        return Result.value(
                ImmutableFinTransaction.builder()
                        .lineNumber(lineNumber)
                        .date(date)
                        .placeOrProduct(line[config.placeOrProductIndex()])
                        .amount(line[config.amountIndex()])
                        .build());
    }
}
