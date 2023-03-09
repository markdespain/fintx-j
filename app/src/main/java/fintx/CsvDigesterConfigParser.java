package fintx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import fintx.digest.CsvDigester;
import fintx.model.Err;
import fintx.model.Result;
import java.io.IOException;

public class CsvDigesterConfigParser {

    private final ObjectMapper mapper;

    CsvDigesterConfigParser() {
        mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
    }

    Result<CsvDigester.Config> parse(final String config) {
        try {
            return Result.value(mapper.reader(CsvDigester.Config.class).readValue(config));
        } catch (IOException | RuntimeException e) {
            return Result.error(Err.from(e));
        }
    }
}
