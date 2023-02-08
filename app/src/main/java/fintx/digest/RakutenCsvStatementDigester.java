package fintx.digest;

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

public class RakutenCsvStatementDigester {
    private static final int NUM_HEADER_LINES = 1;

    private static final int NUM_REQUIRED_COLS = 7;

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

    static Result<FinTransaction> map(final int lineNumber, final String[] line) {
        if (line.length < NUM_REQUIRED_COLS) {
            final String message = String.format(
                "line does not have enough columns. required: %s, actual: %s,"
                        + " lineNumber: %s",
                NUM_REQUIRED_COLS, line.length, lineNumber);
            return Result.error(AppError.message(message));
        }

        final LocalDate date;
        try {
            date = LocalDate.parse(line[0], DATE_FORMAT);
        } catch (DateTimeParseException e) {
            final String message = String.format(
                    "line has a malformed date at column 0. value: %s, lineNumber: %s",
                    line[0], lineNumber
            );
            return Result.error(AppError.message(message));
        }
        return Result.value(
                ImmutableFinTransaction.builder()
                        .date(date)
                        .placeOrProduct(line[1])
                        .user(line[2])
                        .paymentMethod(line[3])
                        .amountSpent(line[4])
                        .commisionPaid(line[5])
                        .totalToPay(line[6])
                        .build());
    }
}
