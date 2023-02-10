# fintx-j
A personal Java project for reconciling financial transactions.


# Examples Commands

The examples below show how to invoke the application via Gradle, based on files used by unit tests.

## No Transaction Discrepancies Found
```console
% ./gradlew run --args="-r=src/test/resources/ReconcilerTest/exactMatch/rakuten.csv -g=src/test/resources/ReconcilerTest/exactMatch/generic.csv"
```

## Transaction Discrepancies Found  
```console
./gradlew run --args="-r=src/test/resources/ReconcilerTest/partialOverlap/rakuten.csv -g=src/test/resources/ReconcilerTest/partialOverlap/generic.csv"
```
