package fintx.digest;

import static java.lang.Math.max;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fintx.model.AppError;
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
        default int placeOrProductIndex() {
            return 1;
        }

        @Value.Default
        default int amountIndex() {
            return 2;
        }

        @Value.Check
        default void check() {
            Preconditions.checkState(numHeaderLines() >= 0, "numHeaderLines must be >= 0");
            Preconditions.checkState(dateIndex() >= 0, "dateIndex must be >= 0");
            Preconditions.checkState(
                    placeOrProductIndex() >= 0, "placeOrProductIndex must be >= 0");
            Preconditions.checkState(amountIndex() >= 0, "amountIndex must be >= 0");
            Preconditions.checkState(amountIndex() >= 0, "indexes must be unique");
        }

        @Value.Derived
        default int maxIndex() {
            return max(dateIndex(), max(placeOrProductIndex(), amountIndex()));
        }
    }

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
    public static final Config RAKUTEN_CC = ImmutableConfig.builder().amountIndex(4).build();

    public CsvDigester(final Config config) {
        this.config = config;
    }

    private final Config config;

    private static final int NUM_HEADER_LINES = 1;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public DigestResult digest(final File file) {
        final List<String[]> lines;
        try (final CSVReader reader =
                new CSVReaderBuilder(new BufferedReader(new FileReader(file)))
                        .withSkipLines(NUM_HEADER_LINES)
                        .build()) {
            lines = reader.readAll();
        } catch (FileNotFoundException e) {
            return DigestResult.error(AppError.fileNotFound(file));
        } catch (IOException e) {
            return DigestResult.error(AppError.loadFileFailure(file, e));
        } catch (CsvException e) {
            return DigestResult.error(AppError.fileError("csv parsing failure", file, e));
        }
        final List<AppError> appErrors = new ArrayList<>(0);
        final List<FinTransaction> transactions = new ArrayList<>(0);
        for (int i = 0; i < lines.size(); i++) {
            final int lineNumber = NUM_HEADER_LINES + i;
            final Result<FinTransaction> transactionResult = map(lineNumber, lines.get(i));
            transactionResult.error().ifPresent(appErrors::add);
            transactionResult.value().ifPresent(transactions::add);
        }
        return ImmutableDigestResult.builder().errors(appErrors).transactions(transactions).build();
    }

    Result<FinTransaction> map(final int lineNumber, final String[] line) {
        if (line.length <= config.maxIndex()) {
            final String message =
                    String.format(
                            "line does not have enough columns. required: %s, actual: %s,"
                                    + " lineNumber: %s",
                            config.maxIndex() + 1, line.length, lineNumber);
            return Result.error(AppError.message(message));
        }

        final LocalDate date;
        try {
            date = LocalDate.parse(line[config.dateIndex()], DATE_FORMAT);
        } catch (DateTimeParseException e) {
            final String message =
                    String.format(
                            "line has a malformed date at column 0. value: %s, lineNumber: %s",
                            line[config.dateIndex()], lineNumber);
            return Result.error(AppError.message(message));
        }
        return Result.value(
                ImmutableFinTransaction.builder()
                        .date(date)
                        .placeOrProduct(line[config.placeOrProductIndex()])
                        .amount(line[config.amountIndex()])
                        .build());
    }
}
