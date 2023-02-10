# fintx-j
A personal Java project for reconciling financial transactions.


# Examples Commands

The examples below show how to invoke the application via Gradle, based on files used by unit tests.

## Example: No Transaction Discrepancies Found
```console
% ./gradlew run --args="-r=src/test/resources/ReconcilerTest/exactMatch/rakuten.csv -g=src/test/resources/ReconcilerTest/exactMatch/generic.csv"

> Task :app:run
===================================
Summary
===================================
Rakuten File: src/test/resources/ReconcilerTest/exactMatch/rakuten.csv
num transactions: 2
num transactions not found in Generic File: 0
num file errors: 0

Generic File: src/test/resources/ReconcilerTest/exactMatch/generic.csv
num transactions: 2
num transactions not found in Generic File: 0
num file errors: 0

===================================
Rakuten File Details
===================================
Transactions not found in Generic file:
(none)

File Errors:
(none)

===================================
Generic File Details
===================================
Transactions not found in Rakuten file:
(none)

File Errors:
(none)

BUILD SUCCESSFUL in 1s

```

## Example: Transaction Discrepancies Found  
```console
./gradlew run --args="-r=src/test/resources/ReconcilerTest/partialOverlap/rakuten.csv -g=src/test/resources/ReconcilerTest/partialOverlap/generic.csv"

> Task :app:run
===================================
Summary
===================================
Rakuten File: src/test/resources/ReconcilerTest/partialOverlap/rakuten.csv
num transactions: 2
num transactions not found in Generic File: 1
num file errors: 0

Generic File: src/test/resources/ReconcilerTest/partialOverlap/generic.csv
num transactions: 2
num transactions not found in Generic File: 1
num file errors: 0

===================================
Rakuten File Details
===================================
Transactions not found in Generic file:
line    date            amount                  place or item       
2       2022-12-30      7580                    item 1

File Errors:
(none)

===================================
Generic File Details
===================================
Transactions not found in Rakuten file:
line    date            amount                  place or item       
2       2023-02-03      1784                    eon

File Errors:
(none)
```
