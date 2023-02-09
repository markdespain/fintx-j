package fintx.report;

import fintx.digest.CsvDigester;
import fintx.digest.DigestResult;
import java.io.File;

public class Reconciler {

    public DigestResult reconcile(final File rakutenFile, final File genericFile) {
        final DigestResult rakuten = new CsvDigester(CsvDigester.RAKUTEN_CC).digest(rakutenFile);
        final DigestResult generic = new CsvDigester(CsvDigester.DEFAULT).digest(genericFile);
        return rakuten;
    }
}
