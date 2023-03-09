package fintx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import fintx.digest.CsvDigester.Config;
import fintx.model.Result;
import java.io.IOException;

/** Creates a CsvDigester config from a JSON string. */
public class CsvDigesterConfigParser {

    private final ObjectMapper mapper;

    CsvDigesterConfigParser() {
        mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
    }

    Result<Config> parse(final String config) {
        try {
            return Result.value(mapper.reader(Config.class).readValue(config));
        } catch (IOException | RuntimeException e) {
            return Result.error(e);
        }
    }
}
